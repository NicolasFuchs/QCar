package qcar.g4;

import java.util.List;
import java.util.Random;

import qcar.*;

public class Driver implements IDriver {
  Random r = new Random();
  IPlayerChannel pc;
  volatile boolean finished = false;

  Thread driverThread = new Thread() {

    public void run() {
      while (!finished) {
        sensors = pc.play(takeDecision(sensors));
      }
    }
  };


  ISensors sensors;
  QCar myCar;
  int previousVertexCode = -1;
  int previousSideCode = -1;

  @Override
  public void startDriverThread(IPlayerChannel pc) {
    this.pc = pc;
    sensors = pc.play(MyDecision.IMMOBILE_DECISION);
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

  private IDecision takeDecision(ISensors sensors) {
    if (sensors != null) {
    if (sensors.collisionsWithMe().isEmpty()) {
      return freeDecision(sensors);
    } else {
      return collisionDecision(sensors.collisionsWithMe());
    }
    }
    return null;
  }

  // PRE : collisionWithMe is empty
  private IDecision freeDecision(ISensors sensors) {
    return MyDecision.randomDecision();
  }

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
            decision = MyDecision.REDUC_SIDE_0;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_2;
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
            decision = MyDecision.REDUC_SIDE_3;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_1;
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
            decision = MyDecision.REDUC_SIDE_1;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_3;
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
            decision = MyDecision.REDUC_SIDE_2;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_0;
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
            decision = MyDecision.REDUC_SIDE_1;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_3;
            previousSideCode = -1;
            return decision;
          }
        case 13: // all sides excepted the 2nd touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.REDUC_SIDE_0;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_2;
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
            decision = MyDecision.REDUC_SIDE_3;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_1;
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
            decision = MyDecision.REDUC_SIDE_0;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_2;
            previousSideCode = -1;
            return decision;
          }
        case 7: // all sides excepted the 0th touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.REDUC_SIDE_2;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_0;
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
            decision = MyDecision.REDUC_SIDE_1;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_3;
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
            decision = MyDecision.REDUC_SIDE_2;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_0;
            previousSideCode = -1;
            return decision;
          }
        case 1: // side 3 touch something
          if (previousSideCode != sideCode) {
            decision = MyDecision.REDUC_SIDE_3;
            break;
          } else {
            decision = MyDecision.INCR_SIDE_1;
            previousSideCode = -1;
            return decision;
          }
      }
      previousSideCode = sideCode;
      return decision;
    }
  }

  private static class MyDecision implements IDecision {
    public final static MyDecision IMMOBILE_DECISION = new MyDecision(false, 0, 0);

    public final static MyDecision REDUC_SIDE_0 =
        new MyDecision(false, 0, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision REDUC_SIDE_1 =
        new MyDecision(false, 1, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision REDUC_SIDE_2 =
        new MyDecision(false, 2, -GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision REDUC_SIDE_3 =
        new MyDecision(false, 3, -GameProvider.MAX_SIDE_LENGTH);

    public final static MyDecision INCR_SIDE_0 =
        new MyDecision(false, 0, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision INCR_SIDE_1 =
        new MyDecision(false, 1, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision INCR_SIDE_2 =
        new MyDecision(false, 2, GameProvider.MAX_SIDE_LENGTH);
    public final static MyDecision INCR_SIDE_3 =
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

  }

}
