package qcar.g4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultSingleSelectionModel;

import java.awt.geom.Point2D;

import qcar.*;

/**
 * this class represent the Driver of a QCar, it takes decision and uses a PlayerChannel to
 * communicate with the world manager
 */
public class Driver implements IDriver {
  /**
   * a list of decisions to be executed, for movement who require multiple steps
   */
  private List<IDecision> pendingDecisions;
  /**
   * Random object to take some choices randomly
   */
  private Random r = new Random();
  /**
   * the playerChannel used to reach the world manager
   */
  private IPlayerChannel pc;
  /**
   * to end the thread
   */
  private volatile boolean finished = false;

  /**
   * the driver thread by itself, at each steps take a decision in function of the sensors
   */
  Thread driverThread = new Thread() {
    public void run() {
      while (!finished) {
        System.out.println("before play");
        sensors = pc.play(takeDecision());
        if (myCar == null) {
          myCar = sensors.mySelf();
        }
      }
    }
  };

  /**
   * the sensors received by the driver at each step of the simulation
   */
  private ISensors sensors;
  /**
   * the QCar driven by the driver
   */
  private IQCar myCar;
  // codes used for historic of collisions
  private int previousVertexCode = -1;
  private int previousSideCode = -1;

  // attributes for the current Target of the driver
  private int targetId = -1;
  private boolean isTargetParking = false;


  /**
   * method to start the Thread,
   * 
   * @param pc the playerchannel used to communicate with the world manager
   */
  @Override
  public void startDriverThread(IPlayerChannel pc) {
    System.out.println("Start driver thread called");
    this.pc = pc;
    // sensors = pc.play(MyDecision.IMMOBILE_DECISION);

    // myCar = sensors.mySelf();
    pendingDecisions = new ArrayList<IDecision>();
    driverThread.start();
  }

  /**
   * method use to stop the driver thread
   */
  @Override
  public void stopDriverThread() {
    finished = true;
  }

  /**
   * method called by the driver thread to take one decision take the next cached decision if there
   * is one if there is no collision, if it doesn't have a target, call aquireTarget to acquire one,
   * otherwise, call followTargetDecision if there is a collision, call collisionDecision
   * 
   */
  private IDecision takeDecision() {
    if (myCar != null) {
      if (!pendingDecisions.isEmpty()) {
        return pendingDecisions.remove(0);
      }

      pendingDecisions.add(MyDecision.sideDecision(true, 0, getSideLength(1)));
      return MyDecision.sideDecision(true, 2, getSideLength(1));
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


  /**
   * @return decision to try to follow its target, in two steps
   * 
   *         1 : orientate toward the target 2 : advance in the direction of side 2 (where is the
   *         "eye")
   */
  private IDecision followTargetDecision() {
    pendingDecisions.add(advanceDirection2());
    return orientateTowardTargetAcquired();
  }

  /**
   * @return decision to orientate toward a point given
   * 
   * @param vertexProjection is a point on the side 0 of the QCar, the decision is taken to
   *        orientate the QCar in direction of the target
   */
  private IDecision orientateTo(Point2D vertexProjection) {
    if (myCar.vertex(0).distance(vertexProjection) < myCar.vertex(1).distance(vertexProjection)) {
      return MyDecision.angleDecision(true, 0, vertexProjection.distance(getMiddleOfSensor()));
    } else {
      return MyDecision.angleDecision(false, 0, vertexProjection.distance(getMiddleOfSensor()));
    }
  }

  /**
   * @return decision to orientate toward the acquired target
   */
  private IDecision orientateTowardTargetAcquired() {
    Point2D[] target = new Point2D[4];
    int i = 0;
    for (ISeenVertex vertex : sensors.seenVertices()) {
      if (vertex.nature().qCarId() == targetId) {
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

  /**
   * @return decision to acquire a new target and orientate toward it
   */
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

  /**
   * return randomly the decision to take a quarterTurn left or right (in 3 steps)
   */
  private IDecision randomQuarterTurn() {
    if (r.nextBoolean()) {
      return quarterTurnLeft();
    }
    return quarterTurnRight();
  }

  // ----------------------------------------

  /**
   * return a decision in function of the sides or vertexes who are in a collision, computes the
   * collisionsCode to call decisionFromCodes *
   */
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

  /**
   * called from collisionDecision
   * 
   */
  // TODO : simplify !
  private IDecision decisionFromCodes(int vertexCode, int sideCode) {
    MyDecision decision = MyDecision.IMMOBILE_DECISION;
    if (vertexCode != 0) {
      switch (vertexCode) {
        case 15: // all vertices touch something -> try to reduce my size
          // reduce one on the biggest side
          int side = getSideLength(0) > getSideLength(1) ? 0 : 1;
          if (r.nextBoolean()) {
            side += 2;
          }
          decision = MyDecision.sideDecision(false, side, getSideLength((side + 1) % 4));
          break;
        case 14: // all vertices excepted the 3rd touch something
          decision = MyDecision.Side_3_to_right(getSideLength(3));
          break;
        case 13: // all vertices excepted the 2nd touch something
          decision = MyDecision.Side_2_to_right(getSideLength(3));
          break;
        case 12: // vertices 0 and 1 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.Side_0_decr(getSideLength(1));
            break;
          } else {
            decision = MyDecision.Side_2_incr(getSideLength(1));
            previousVertexCode = -1;
            return decision;
          }
        case 11: // all vertices excepted the 1st touch something
          decision = MyDecision.Side_1_to_right(getSideLength(1));
          break;
        case 10: // vertices 0 and 2 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.Side_2_to_left(getSideLength(2));
          } else {
            decision = MyDecision.Side_0_to_left(getSideLength(0));
          }
          break;
        case 9: // vertices 0 and 3 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.Side_3_decr(getSideLength(0));
            break;
          } else {
            decision = MyDecision.Side_1_incr(getSideLength(0));
            previousVertexCode = -1;
            return decision;
          }
        case 8: // vertice 0 touch something
          decision = MyDecision.Side_0_to_left(getSideLength(0));
          break;
        case 7: // all vertices excepted the 0th touch something
          decision = MyDecision.Side_0_to_right(getSideLength(0));
          break;
        case 6: // vertices 1 and 2 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.Side_1_decr(getSideLength(0));
            break;
          } else {
            decision = MyDecision.Side_3_incr(getSideLength(0));
            previousVertexCode = -1;
            return decision;
          }
        case 5: // vertice 1 and 3 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.Side_1_to_left(getSideLength(1));
          } else {
            decision = MyDecision.Side_3_to_left(getSideLength(3));
          }
          break;
        case 4: // vertice 1 touch something
          decision = MyDecision.Side_1_to_left(getSideLength(1));;
          break;
        case 3: // vertices 2 and 3 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.Side_2_decr(getSideLength(1));
            break;
          } else {
            decision = MyDecision.Side_0_incr(getSideLength(1));
            previousVertexCode = -1;
            return decision;
          }
        case 2: // vertex 2 touch something
          decision = MyDecision.Side_2_to_left(getSideLength(2));
          break;
        case 1: // vertex 3 touch something
          decision = MyDecision.Side_3_to_left(getSideLength(3));
          break;
      }
      previousVertexCode = vertexCode;
      return decision;
    } else {
      switch (sideCode) {
        case 15: // all vertices touch something -> try to reduce my size
          // reduce one on the biggest side
          int side = getSideLength(0) > getSideLength(1) ? 0 : 1;
          if (r.nextBoolean()) {
            side += 2;
          }
          decision = MyDecision.sideDecision(false, side, getSideLength((side + 1) % 4));
          break;
        case 14: // all sides excepted the 3rd touch something
        case 4: // side 1 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.Side_1_decr(getSideLength(0));
            break;
          } else {
            decision = MyDecision.Side_3_incr(getSideLength(0));
            previousVertexCode = -1;
            return decision;
          }
        case 13:// all sides excepted the 2nd touch something
        case 8: // side 0 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.Side_0_decr(getSideLength(1));
            break;
          } else {
            decision = MyDecision.Side_2_incr(getSideLength(1));
            previousVertexCode = -1;
            return decision;
          }
        case 12: // sides 0 and 1 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.Side_2_to_left(getSideLength(2));
          } else {
            decision = MyDecision.Side_3_to_right(getSideLength(3));
          }
          break;
        case 11: // all sides excepted the 1st touch something
        case 1: // side 3 touch something
          if (previousVertexCode != vertexCode) {
            decision = MyDecision.Side_3_decr(getSideLength(0));
            break;
          } else {
            decision = MyDecision.Side_1_incr(getSideLength(0));
            previousVertexCode = -1;
            return decision;
          }
        case 10: // sides 0 and 2 touch something
          if (myCar.sideOffersBonus(1)) {// try to gain distance from collisions
            if (r.nextBoolean()) {
              decision = MyDecision.Side_0_to_left(getSideLength(0));
            } else {
              decision = MyDecision.Side_2_to_right(getSideLength(2));
            }
          } else {
            if (r.nextBoolean()) {
              decision = MyDecision.Side_0_to_right(getSideLength(0));
            } else {
              decision = MyDecision.Side_2_to_left(getSideLength(2));
            }
          }
          break;
        case 9: // sides 0 and 3 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.Side_1_to_left(getSideLength(1));
          } else {
            decision = MyDecision.Side_2_to_right(getSideLength(2));
          }
          break;

        case 7: // all sides excepted the 0th touch something
        case 2: // side 2 touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.Side_2_decr(getSideLength(1));
            break;
          } else {
            decision = MyDecision.Side_0_incr(getSideLength(1));
            previousSideCode = -1;
            return decision;
          }
        case 6: // sides 1 and 2 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.Side_3_to_left(getSideLength(3));
          } else {
            decision = MyDecision.Side_0_to_right(getSideLength(0));
          }
          break;
        case 5: // sides 1 and 3 touch something
          if (myCar.sideOffersBonus(2)) { // try to gain distance from collisions
            if (r.nextBoolean()) {
              decision = MyDecision.Side_1_to_left(getSideLength(1));
            } else {
              decision = MyDecision.Side_3_to_right(getSideLength(3));
            }
          } else {
            if (r.nextBoolean()) {
              decision = MyDecision.Side_1_to_right(getSideLength(1));
            } else {
              decision = MyDecision.Side_3_to_left(getSideLength(3));
            }
          }
          break;


        case 3: // sides 2 and 3 touch something
          if (r.nextBoolean()) {
            decision = MyDecision.Side_0_to_left(getSideLength(0));
          } else {
            decision = MyDecision.Side_1_to_right(getSideLength(1));
          }
          break;
      }
    }


    previousSideCode = sideCode;
    return decision;

  }

  // ----------------------------------------

  /**
   * 2-steps to make an advance in direction 0 (down)
   */
  private IDecision advanceDirection0() {
    pendingDecisions.add(MyDecision.Side_2_decr(getSideLength(1)));
    return MyDecision.Side_0_incr(myCar.nature().maxSideLength());
  }

  // ----------------------------------------

  /**
   * 2-steps to make an advance in direction 1 (right)
   */
  private IDecision advanceDirection1() {
    pendingDecisions.add(MyDecision.Side_3_decr(getSideLength(0)));
    return MyDecision.Side_1_incr(myCar.nature().maxSideLength());
  }

  // ----------------------------------------

  /**
   * 2-steps to make an advance in direction 2 (up)
   */
  private IDecision advanceDirection2() {
    pendingDecisions.add(MyDecision.Side_0_decr(getSideLength(1)));
    return MyDecision.Side_2_incr(myCar.nature().maxSideLength());
  }

  // ----------------------------------------

  /**
   * 2-steps to make an advance in direction 3 (left)
   */
  private IDecision advanceDirection3() {
    pendingDecisions.add(MyDecision.Side_1_decr(getSideLength(0)));
    return MyDecision.Side_3_incr(myCar.nature().maxSideLength());
  }

  // ----------------------------------------

  /**
   * 3-steps to make a quarter turn left
   */
  private IDecision quarterTurnLeft() {
    pendingDecisions.add(MyDecision.Side_1_to_left(getSideLength(1)));
    pendingDecisions.add(MyDecision.Side_2_to_left(getSideLength(2)));
    return MyDecision.Side_3_to_left(getSideLength(3));
  }

  // ----------------------------------------

  /**
   * 3-steps to make a quarter turn right
   */
  private IDecision quarterTurnRight() {
    pendingDecisions.add(MyDecision.angleDecision(false, 1, getSideLength(1)));
    pendingDecisions.add(MyDecision.angleDecision(false, 2, getSideLength(2)));
    return MyDecision.angleDecision(false, 3, getSideLength(3));
  }

  // ----------------------------------------

  /**
   * method to get the length of a side from the driven QCar
   * 
   * @param sideId
   * @return the length of the requested side
   */
  private double getSideLength(int sideId) {
    return myCar.vertex(sideId).distance(myCar.vertex((sideId + 1) % 4));
  }

  // ----------------------------------------


  /**
   * @return the point in the middle of an array of points
   */
  private Point2D middle(Point2D... points) {
    double x = 0, y = 0;
    for (Point2D p : points) {
      x += p.getX();
      y += p.getY();
    }
    return new Point2D.Double(x / points.length, y / points.length);
  }

  // ----------------------------------------

  /**
   * @return the point in the middle of the side0
   */
  private Point2D getMiddleOfSensor() {
    return middle(myCar.vertex(0), myCar.vertex(1));
  }

  // ----------------------------------------
  // ----------------------------------------
  // ----------------------------------------

  /**
   * Decision generator with hardcoded ones
   */

  public static class MyDecision implements IDecision {
    public final static MyDecision IMMOBILE_DECISION = new MyDecision(false, 0, 0);

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

    /**
     * @return a randomDecision for a QCar
     */
    public static MyDecision randomDecision(IQCar qcar) {
      return new MyDecision(r.nextBoolean(), r.nextInt(4),
          r.nextDouble() * qcar.nature().maxSideLength());
    }

    public static MyDecision Side_0_to_left(double requestedTranslation) {
      return angleDecision(true, 0, requestedTranslation);
    }

    public static MyDecision Side_0_to_right(double requestedTranslation) {
      return angleDecision(false, 0, requestedTranslation);
    }

    public static MyDecision Side_0_incr(double requestedTranslation) {
      return sideDecision(true, 0, requestedTranslation);
    }

    public static MyDecision Side_0_decr(double requestedTranslation) {
      return sideDecision(false, 0, requestedTranslation);
    }

    public static MyDecision Side_1_to_left(double requestedTranslation) {
      return angleDecision(true, 1, requestedTranslation);
    }

    public static MyDecision Side_1_to_right(double requestedTranslation) {
      return angleDecision(false, 1, requestedTranslation);
    }

    public static MyDecision Side_1_incr(double requestedTranslation) {
      return sideDecision(true, 1, requestedTranslation);
    }

    public static MyDecision Side_1_decr(double requestedTranslation) {
      return sideDecision(false, 1, requestedTranslation);
    }

    public static MyDecision Side_2_to_left(double requestedTranslation) {
      return angleDecision(true, 2, requestedTranslation);
    }

    public static MyDecision Side_2_to_right(double requestedTranslation) {
      return angleDecision(false, 2, requestedTranslation);
    }

    public static MyDecision Side_2_incr(double requestedTranslation) {
      return sideDecision(true, 2, requestedTranslation);
    }

    public static MyDecision Side_2_decr(double requestedTranslation) {
      return sideDecision(false, 2, requestedTranslation);
    }

    public static MyDecision Side_3_to_left(double requestedTranslation) {
      return angleDecision(true, 3, requestedTranslation);
    }

    public static MyDecision Side_3_to_right(double requestedTranslation) {
      return angleDecision(false, 0, requestedTranslation);
    }

    public static MyDecision Side_3_incr(double requestedTranslation) {
      return sideDecision(true, 3, requestedTranslation);
    }

    public static MyDecision Side_3_decr(double requestedTranslation) {
      return sideDecision(false, 3, requestedTranslation);
    }


    /**
     * @return an angle decision
     */
    public static MyDecision angleDecision(boolean left, int side, double length) {
      int coeff = -1;
      if (left) {
        coeff = 1;
      }
      return new MyDecision(true, side, length * coeff);
    }

    /**
     * @return an side decision
     */
    public static MyDecision sideDecision(boolean increase, int side, double length) {
      int coeff = -1;
      if (increase) {
        coeff = 1;
      }
      return new MyDecision(false, side, coeff * length);
    }
  }

  // ----------------------------------------
  // ----------------------------------------
  // ----------------------------------------


}
