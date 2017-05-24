package qcar.g4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.awt.geom.Point2D;

import qcar.*;

public class Driver implements IDriver {
  // a list of decisions to be executed, for movement who require multiple steps
  List<IDecision> pendingDecisions;
  // Random object to take some choices randomly
  Random r = new Random();
  // the playerChannel used to reach the world manager
  IPlayerChannel pc;
  // to end the thread
  volatile boolean finished = false;

  // the driver thread by itself, at each steps take a decision in function of the sensors
  Thread driverThread = new Thread() {
    public void run() {
      while (!finished) {
        sensors = pc.play(takeDecision());
      }
    }
  };

  ISensors sensors;
  IQCar myCar;
  // codes used for historic of collisions
  int previousVertexCode = -1;
  int previousSideCode = -1;

  // attributes for the current Target of the driver
  int targetId = -1;
  boolean isTargetParking = false;

  @Override
  public void startDriverThread(IPlayerChannel pc) {
    this.pc = pc;
    sensors = pc.play(MyDecision.IMMOBILE_DECISION);
    myCar = sensors.mySelf();
    pendingDecisions = new ArrayList<IDecision>();
    driverThread.start();
  }

  @Override
  public void stopDriverThread() {
    try {
      finished = true;
      driverThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> TEST

  public IDecision testDecision() {
    int side = 0;
    boolean left = true;
    return MyDecision.sideDecisionMax(left, side);
  }

  public void giveQCar(IQCar iqCar) {
    myCar = iqCar;
  }
  // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<



  // method called by the driver thread to take one decision
  private IDecision takeDecision() {
    if (!pendingDecisions.isEmpty()) {
      return pendingDecisions.remove(0);
    }
    if (sensors != null) {
      if (sensors.collisionsWithMe().isEmpty()) {
        if (targetId == -1) {
          return acquireTarget();
        } else {
          return followTargetDecision();
        }

      } else {
        return collisionDecision(sensors.collisionsWithMe());
      }
    }
    return MyDecision.IMMOBILE_DECISION;
  }

  // ----------------------------------------

  private IDecision followTargetDecision() {
    pendingDecisions.add(advanceDirection0());
    return orientateTowardTargetAcquired();
  }

  // ----------------------------------------

  private IDecision orientateTo(Point2D vertexProjection) {
    if (myCar.vertex(0).distance(vertexProjection) < myCar.vertex(1).distance(vertexProjection)) {
      return MyDecision.angleDecision(true, 0, vertexProjection.distance(getMiddleOfSensor()));
    } else {
      return MyDecision.angleDecision(false, 0, vertexProjection.distance(getMiddleOfSensor()));
    }
  }

  // ----------------------------------------

  private IDecision orientateTowardTargetAcquired() {
    Point2D[] target = new Point2D[4];
    int i = 0;
    for (ISeenVertex vertex : sensors.seenVertices()) {
      if (vertex.vertexId() == targetId) {
        target[i] = vertex.projectionLocation();
        if (vertex.nature().isParkingTarget()) {
          isTargetParking = true;
          i++;
        } else {
          if (vertex.offersBonus()) {
            target[0] = vertex.projectionLocation();
            break;
          }
        }
      }
    }
    if (i != 0) {
      // Decision to orientate in direction of the target
      // if this is a parking, target the center else target the 1st point found with bonus
      if (isTargetParking) {
        return orientateTo(middle(target));
      } else {
        return orientateTo(target[0]);
      }
    } else {
      targetId = -1;
      if (!pendingDecisions.isEmpty()) {
        return pendingDecisions.remove(0);
      }
      return randomQuarterTurn();
    }
  }

  // ----------------------------------------

  // PRE : collisionWithMe is empty
  private IDecision acquireTarget() {
    // mapping from an ID to a list of seen vertexes
    HashMap<Integer, ArrayList<ISeenVertex>> vertexesFromSameId = new HashMap<>();
    // listing of interesting vertexes and gathering all vertexes from a certain ID
    for (ISeenVertex v : sensors.seenVertices()) {
      int id = v.vertexId();
      // create the entry in the map in not present
      if (!vertexesFromSameId.containsKey(id)) {
        ArrayList<ISeenVertex> vertexes = new ArrayList<>();
        vertexesFromSameId.put(id, vertexes);
      }
      // add the vertex to the list of the same id
      vertexesFromSameId.get(id).add(v);
    }
    if (!vertexesFromSameId.isEmpty()) {
      int max = 0;
      int bestId = -1;
      for (int id : vertexesFromSameId.keySet()) {
        int points = 0;
        for (ISeenVertex vertex : vertexesFromSameId.get(id)) {
          if (vertex.offersBonus()) {
            points++;
          }
        }
        if (points > max) {
          bestId = id;
          max = points;
        }
      }
      targetId = bestId;
      return orientateTowardTargetAcquired();
    } else {
      // if there is no interresting vertexes, we make a quarter turn left or right randomly choosen
      return randomQuarterTurn();
    }
  }

  // ----------------------------------------

  // return randomly the decision to take a quarterTurn left or right (in 3 steps)
  private IDecision randomQuarterTurn() {
    if (r.nextBoolean()) {
      return quarterTurnLeft();
    }
    return quarterTurnRight();
  }

  // ----------------------------------------

  // PRE : collisionWithMe is not empty
  private IDecision collisionDecision(List<ICollision> collisionsWithMe) {
    int sideCode = 0;
    int vertexCode = 0;
    for (ICollision col : collisionsWithMe) {
      if (col.isAgainstVertex()) {
        sideCode += (int) Math.pow(2, col.hitSideOrVertexId());
      } else {
        vertexCode += (int) Math.pow(2, col.hitSideOrVertexId());
      }
    }
    return decisionFromCodes(vertexCode, sideCode);
  }

  // ----------------------------------------

  // called from collisionDecision
  // TODO : simplify !
  private IDecision decisionFromCodes(int vertexCode, int sideCode) {
    MyDecision decision = MyDecision.IMMOBILE_DECISION;
    if (vertexCode != 0) {
      switch (vertexCode) {
        case 15: // all vertices touch something -> try to reduce my size
          decision = new MyDecision(false, r.nextInt(4), -GameProvider.MAX_SIDE_LENGTH);
          break;
        case 14: // all vertices excepted the 3rd touch something
          decision = MyDecision.ANGLE_NEG_SIDE_3;
          break;
        case 13: // all vertices excepted the 2nd touch something
          decision = MyDecision.ANGLE_NEG_SIDE_2;
          break;
        case 12: // vertices 0 and 1 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.GROWTH_SIDE_0;
            break;
          } else {
            decision = MyDecision.GROWTH_SIDE_2;
            previousVertexCode = -1;
            return decision;
          }
        case 11: // all vertices excepted the 1st touch something
          decision = MyDecision.ANGLE_NEG_SIDE_1;
          break;
        case 10: // vertices 0 and 2 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.ANGLE_POS_SIDE_2;
          } else {
            decision = MyDecision.ANGLE_POS_SIDE_0;
          }
          break;
        case 9: // vertices 0 and 3 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.REDUCTION_SIDE_3;
            break;
          } else {
            decision = MyDecision.REDUCTION_SIDE_1;
            previousVertexCode = -1;
            return decision;
          }
        case 8: // vertice 0 touch something
          decision = MyDecision.ANGLE_POS_SIDE_0;
          break;
        case 7: // all vertices excepted the 0th touch something
          decision = MyDecision.ANGLE_NEG_SIDE_0;
          break;
        case 6: // vertices 1 and 2 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.GROWTH_SIDE_1;
            break;
          } else {
            decision = MyDecision.GROWTH_SIDE_3;
            previousVertexCode = -1;
            return decision;
          }
        case 5: // vertice 1 and 3 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.ANGLE_POS_SIDE_1;
          } else {
            decision = MyDecision.ANGLE_POS_SIDE_3;
          }
          break;
        case 4: // vertice 1 touch something
          decision = MyDecision.ANGLE_POS_SIDE_1;
          break;
        case 3: // vertices 2 and 3 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.REDUCTION_SIDE_2;
            break;
          } else {
            decision = MyDecision.REDUCTION_SIDE_0;
            previousVertexCode = -1;
            return decision;
          }
        case 2: // vertex 2 touch something
          decision = MyDecision.ANGLE_POS_SIDE_2;
          break;
        case 1: // vertex 3 touch something
          decision = MyDecision.ANGLE_POS_SIDE_3;
          break;
      }
      previousVertexCode = vertexCode;
      return decision;
    } else {
      switch (sideCode) {
        case 15: // all sides touch something -> try to reduce my size
          decision = new MyDecision(false, r.nextInt(4), -GameProvider.MAX_SIDE_LENGTH);
          break;
        case 14: // all sides excepted the 3rd touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.GROWTH_SIDE_1;
            break;
          } else {
            decision = MyDecision.GROWTH_SIDE_3;
            previousSideCode = -1;
            return decision;
          }
        case 13: // all sides excepted the 2nd touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.GROWTH_SIDE_0;
            break;
          } else {
            decision = MyDecision.GROWTH_SIDE_2;
            previousSideCode = -1;
            return decision;
          }
        case 12: // sides 0 and 1 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.ANGLE_POS_SIDE_2;
          } else {
            decision = MyDecision.ANGLE_NEG_SIDE_3;
          }
          break;
        case 11: // all sides excepted the 1st touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.REDUCTION_SIDE_3;
            break;
          } else {
            decision = MyDecision.REDUCTION_SIDE_1;
            previousSideCode = -1;
            return decision;
          }
        case 10: // sides 0 and 2 touch something
          if (myCar.sideOffersBonus(1)) {// try to gain distance from collisions
            if (r.nextBoolean()) {
              decision = MyDecision.ANGLE_POS_SIDE_0;
            } else {
              decision = MyDecision.ANGLE_NEG_SIDE_2;
            }
          } else {
            if (r.nextBoolean()) {
              decision = MyDecision.ANGLE_NEG_SIDE_0;
            } else {
              decision = MyDecision.ANGLE_POS_SIDE_2;
            }
          }
          break;
        case 9: // sides 0 and 3 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.ANGLE_POS_SIDE_1;
          } else {
            decision = MyDecision.ANGLE_NEG_SIDE_2;
          }
          break;
        case 8: // side 0 touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.GROWTH_SIDE_0;
            break;
          } else {
            decision = MyDecision.GROWTH_SIDE_2;
            previousSideCode = -1;
            return decision;
          }
        case 7: // all sides excepted the 0th touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.REDUCTION_SIDE_2;
            break;
          } else {
            decision = MyDecision.REDUCTION_SIDE_0;
            previousSideCode = -1;
            return decision;
          }
        case 6: // sides 1 and 2 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.ANGLE_POS_SIDE_3;
          } else {
            decision = MyDecision.ANGLE_NEG_SIDE_0;
          }
          break;
        case 5: // sides 1 and 3 touch something
          if (myCar.sideOffersBonus(2)) { // try to gain distance from collisions
            if (r.nextBoolean()) {
              decision = MyDecision.ANGLE_POS_SIDE_1;
            } else {
              decision = MyDecision.ANGLE_NEG_SIDE_3;
            }
          } else {
            if (r.nextBoolean()) {
              decision = MyDecision.ANGLE_NEG_SIDE_1;
            } else {
              decision = MyDecision.ANGLE_POS_SIDE_3;
            }
          }
          break;
        case 4: // side 1 touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.GROWTH_SIDE_1;
            break;
          } else {
            decision = MyDecision.GROWTH_SIDE_3;
            previousSideCode = -1;
            return decision;
          }
        case 3: // sides 2 and 3 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.ANGLE_POS_SIDE_0;
          } else {
            decision = MyDecision.ANGLE_NEG_SIDE_1;
          }
          break;
        case 2: // side 2 touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.REDUCTION_SIDE_2;
            break;
          } else {
            decision = MyDecision.REDUCTION_SIDE_0;
            previousSideCode = -1;
            return decision;
          }
        case 1: // side 3 touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.REDUCTION_SIDE_3;
            break;
          } else {
            decision = MyDecision.REDUCTION_SIDE_1;
            previousSideCode = -1;
            return decision;
          }
      }
      previousSideCode = sideCode;
      return decision;
    }
  }

  // ----------------------------------------

  // 2-steps to make an advance in direction 0 (down)
  private IDecision advanceDirection0() {
    pendingDecisions.add(MyDecision.REDUCTION_SIDE_2);
    return MyDecision.GROWTH_SIDE_0;
  }

  // ----------------------------------------

  // 2-steps to make an advance in direction 1 (right)
  private IDecision advanceDirection1() {
    pendingDecisions.add(MyDecision.REDUCTION_SIDE_3);
    return MyDecision.GROWTH_SIDE_1;
  }

  // ----------------------------------------

  // 2-steps to make an advance in direction 2 (up)
  private IDecision advanceDirection2() {
    pendingDecisions.add(MyDecision.REDUCTION_SIDE_0);
    return MyDecision.GROWTH_SIDE_2;
  }

  // ----------------------------------------

  // 2-steps to make an advance in direction 3 (left)
  private IDecision advanceDirection3() {
    pendingDecisions.add(MyDecision.REDUCTION_SIDE_1);
    return MyDecision.GROWTH_SIDE_3;
  }

  // ----------------------------------------

  // 3-steps to make a quarter turn left
  private IDecision quarterTurnLeft() {
    pendingDecisions.add(MyDecision.angleDecision(true, 1, getSideLength(1)));
    pendingDecisions.add(MyDecision.angleDecision(true, 2, getSideLength(2)));
    return MyDecision.angleDecision(true, 3, getSideLength(3));
  }

  // ----------------------------------------

  // 3-steps to make a quarter turn right
  private IDecision quarterTurnRight() {
    pendingDecisions.add(MyDecision.angleDecision(false, 1, getSideLength(1)));
    pendingDecisions.add(MyDecision.angleDecision(false, 2, getSideLength(2)));
    return MyDecision.angleDecision(false, 3, getSideLength(3));
  }

  // ----------------------------------------
  
  private double getSideLength(int sideId) {    
    return myCar.vertex(sideId).distance(myCar.vertex((sideId+1%4))) ;    
  }

  //----------------------------------------


  private Point2D middle(Point2D... points) {
    double x = 0, y = 0;
    for (Point2D p : points) {
      x += p.getX();
      y += p.getY();
    }
    return new Point2D.Double(x / points.length, y / points.length);
  }

  // ----------------------------------------

  private Point2D getMiddleOfSensor() {
    return middle(myCar.vertex(0), myCar.vertex(1));
  }

  // ----------------------------------------
  // ----------------------------------------
  // ----------------------------------------

  // Decision generator with hardcoded ones
  public static class MyDecision implements IDecision {
    public final static MyDecision IMMOBILE_DECISION = new MyDecision(false, 0, 0);

    public final static MyDecision REDUCTION_SIDE_0 =
        new MyDecision(false, 0, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision REDUCTION_SIDE_1 =
        new MyDecision(false, 1, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision REDUCTION_SIDE_2 =
        new MyDecision(false, 2, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision REDUCTION_SIDE_3 =
        new MyDecision(false, 3, -GameProvider.MAX_SIDE_LENGTH);


    public final static MyDecision GROWTH_SIDE_0 =
        new MyDecision(false, 0, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision GROWTH_SIDE_1 =
        new MyDecision(false, 1, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision GROWTH_SIDE_2 =
        new MyDecision(false, 2, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision GROWTH_SIDE_3 =
        new MyDecision(false, 3, GameProvider.MAX_SIDE_LENGTH);

    public final static MyDecision ANGLE_POS_SIDE_0 =
        new MyDecision(true, 0, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision ANGLE_POS_SIDE_1 =
        new MyDecision(true, 1, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision ANGLE_POS_SIDE_2 =
        new MyDecision(true, 2, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision ANGLE_POS_SIDE_3 =
        new MyDecision(true, 3, GameProvider.MAX_SIDE_LENGTH);

    public final static MyDecision ANGLE_NEG_SIDE_0 =
        new MyDecision(true, 0, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision ANGLE_NEG_SIDE_1 =
        new MyDecision(true, 1, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision ANGLE_NEG_SIDE_2 =
        new MyDecision(true, 2, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision ANGLE_NEG_SIDE_3 =
        new MyDecision(true, 3, -GameProvider.MAX_SIDE_LENGTH);

    private boolean isAngleMovement;
    private int sideId;
    private double requestedTranslation;
    private static Random r = new Random();

    private MyDecision(boolean isAngleMovement, int sideId, double requestedTranslation) {
      this.isAngleMovement = isAngleMovement;
      this.sideId = sideId;
      this.requestedTranslation = requestedTranslation;
    }

    @Override
    public boolean isAngleMovement() {
      return isAngleMovement;
    }

    @Override
    public int sideId() {
      return sideId;
    }

    @Override
    public double requestedTranslation() {
      return requestedTranslation;
    }

    public static MyDecision randomDecision() {
      return new MyDecision(r.nextBoolean(), r.nextInt(4),
          r.nextDouble() * GameProvider.MAX_SIDE_LENGTH);
    }

    // PRE: left is -1 or 1
    public static MyDecision angleDecision(boolean left, int side, double length) {
      int coeff = -1;
      if (left) {
        coeff = 1;
      }
      return new MyDecision(true, side, length * coeff);
    }

    public static MyDecision sideDecision(boolean increase, int side, double length) {
      int coeff = -1;
      if (increase) {
        coeff = 1;
      }
      return new MyDecision(false, side, coeff * length);
    }

    public static MyDecision sideDecisionMax(boolean increase, int side) {
      return sideDecision(increase, side, GameProvider.MAX_SIDE_LENGTH);
    }
  }

  // ----------------------------------------
  // ----------------------------------------
  // ----------------------------------------


}
