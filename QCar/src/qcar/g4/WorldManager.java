package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javafx.geometry.Rectangle2D;
import qcar.*;
import qcar.ui.QCarAnimationPane;
import simviou.WorldChangeObserver;

public class WorldManager implements IWorldManager {

  private List<WorldChangeObserver> observers; // contains all currently observing objects

  private long step; // number of step played
  private boolean isSimulationRunning; // true if simulation is running else false
  private boolean isWarOver;
  private Rectangle2D boundingBox;

  private List<IQCar> drivenQCars;
  private List<IQCar> qcars; // contains all the qcars
  private List<Line2D> photoSensors;
  private List<ISensors> sensors;
  private List<Line2D> distanceSensors;

  private List<ICollision> collisions;

  private List<? extends IDriver> players;
  private ArrayList<PlayerChannel> playerChannels;

  private ArrayList<Point2D> allPoints;

  /*
   * Observers management
   */
  @Override
  public void addWorldObserver(WorldChangeObserver o) {
    if (o != null && !observers.contains(o))
      this.observers.add(o);
  }

  @Override
  public void removeWorldObserver(WorldChangeObserver o) {
    observers.remove(o);
  }

  private void notifyAllWorldObserver(int eventType) {
    for (WorldChangeObserver wo : observers) {
      wo.worldStateChanged(eventType, step);
    }
  }

  /*
   * Simulation setup/teardown
   */
  @Override
  public void openNewSimulation(IGameDescription description, List<? extends IDriver> players) {

    // add a specific QCar for testing
    // qcars.remove(0);
    // QCarNature nature = new QCarNature(true, false, true, true, 10000, 0);
    // Point2D[] vertices = {new Point2D.Double(52,84),new Point2D.Double(52,90),new
    // Point2D.Double(23,42),new Point2D.Double(23,36)};
    // QCar problem = new QCar(nature, vertices);
    // qcars.add(0,problem);

    this.isSimulationRunning = true;
    this.step = 0;
    drivenQCars = new ArrayList<>();
    this.players = players;

    for (IQCar q : description.allQCar()) {
      QCar myQCar = new QCar(q);
      qcars.add(myQCar);
    }

    for (IQCar q : qcars) {
      if (q.nature().isDriven()) {
        drivenQCars.add(q);
      }
      for (int i = 0; i < 4; i++) {
        allPoints.add(q.vertex(i));
      }
    }

    playerChannels = new ArrayList<>();
    for (int i = 0; i < players.size(); i++) {
      sensors.add(WorldManagerPhysicsHelper.computeSensor(drivenQCars.get(i), qcars));
      playerChannels.add(new PlayerChannel(sensors.get(i)));
    }
    for (int i = 0; i < players.size(); i++) {
      players.get(i).startDriverThread(playerChannels.get(i));
    }
    System.out.println("SIMULATION OPENED");

  }

  @Override
  public boolean isSimulationOpened() {
    return isSimulationRunning;
  }

    @Override
    public void simulateOneStep(long collectiveDelayInMicroSeconds) {

    /*
     * This method needs to : - get each driver decision and apply it to their qcar - update the
     * state of the world (sensors, collision, isWarOver, ...) - send the sensors the qcar drivers -
     * notify the view of the change - increment the number of step
     */
        // notifyAllWorldObserver(0);

        // TODO uncomment the next line and send all decisions
        List<IDecision> decisions = new ArrayList<>();
        for (int i = 0; i < sensors.size(); i++) {
            playerChannels.get(i).sendSensors(sensors.get(i));
        }
        for (int i = 0; i < sensors.size(); i++) {
            playerChannels.get(i).release();
        }
        try {
            Thread.sleep(collectiveDelayInMicroSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (PlayerChannel pc : playerChannels) {
            decisions.add(pc.getDecision());
        }
        for (int i = 0 ; i < decisions.size() ; i++) {
          IDecision d = decisions.get(i) ;
          d = Decision.validDecision(d, drivenQCars.get(i)) ;
          decisions.set(i,d) ;
        }
//        decisions = new ArrayList<>(); decisions.add(new Decision(false, 2, -Math.sqrt(20*20+25)));
        updateWorldState(decisions);
        fetchSensors();
        step++;
        notifyAllWorldObserver(QCarAnimationPane.STATE_CHANGE_EVENT);

    /*
     * //Random random = new Random(); for (int i = 0; i < drivenQCars.size(); i++) { //
     * decisions.add(new Decision(random.nextBoolean(), random.nextInt(4), //
     * (random.nextBoolean()?1:-1)*random.nextInt(5))); decisions.add(new Decision(false, 2,
     * Math.sqrt(3145))); }
     */

    /*
     * for (int i = 0; i < drivenQCars.size(); i++) { ISensors sensor =
     * WorldManagerPhysicsHelper.computeSensor(drivenQCars.get(i), qcars); if (sensor != null) {
     * sensors.add(sensor); Line2D line0 = new Line2D.Double(sensor.mySelf().vertex(0),
     * sensor.mySelf().vertex(1)); // FIX // ME // photoSensors.add(new
     * Line2D.Double(sensor.mySelf(), // WorldManagerPhysicsHelper.findIntersection(line0, lineSeen,
     * true))); } }
     */
    }

    private void fetchSensors() {
        List<Line2D> newPhotoSensors = new ArrayList<>();
        List<Line2D> newDistanceSensors = new ArrayList<>();
        List<ISensors> newSensors = new ArrayList<>();
        for (int i = 0; i < drivenQCars.size(); i++) {
            IQCar drivenQCar = drivenQCars.get(i);
            ISensors sensor = WorldManagerPhysicsHelper.computeSensor(drivenQCar, qcars);
            if (sensor != null) {
                newSensors.add(sensor);
                // DistanceSensors
                if (sensor.distanceSensor().isSomethingDetected()) {
                    Line2D distSensor = new Line2D.Double(new Point2D.Double((drivenQCar.vertex(0).getX()+drivenQCar.vertex(1).getX())/2, (drivenQCar.vertex(0).getY()+drivenQCar.vertex(1).getY())/2), sensor.distanceSensor().rayEnd());
                    newDistanceSensors.add(distSensor);
                }
                // PhotoSensors
                for (ISeenVertex v : sensor.seenVertices()) {
                    IQCar chosen = null;
                    for (IQCar car : qcars) {
                        if (car.nature().qCarId() == v.nature().qCarId()) {
                            chosen = car; break;
                        }
                    }
                    Line2D photoS = new Line2D.Double(v.projectionLocation(), chosen.vertex(v.vertexId()));
                    if (chosen.nature().qCarId() != v.nature().qCarId()) {
                        throw new Error("IDs mismatch!");
                    }
                    newPhotoSensors.add(photoS);
                }
            }
        }
        photoSensors = newPhotoSensors;
        distanceSensors = newDistanceSensors;
        sensors = newSensors;
    }

    @Override
    public void closeSimulation() {
        // stop each player's thread and release them from the chan
        for (int i = 0; i < players.size(); i++) {
            players.get(i).stopDriverThread();
            playerChannels.get(i).release();
        }
        isSimulationRunning = false;
    }

  /*
   * Snapshot of the current state. Common PRE-condition: isSimulationOpened()
   */

  @Override
  public boolean isWarOver() {
    return isWarOver;
  }

  @Override
  public long stepNumber() {
    return step;
  }

  @Override
  public Rectangle2D boundingBox() {
    // todo go throught the qcar list and find the furthest apart points
    double minX = Double.MAX_VALUE;
    double minY = Double.MAX_VALUE;
    double maxX = Double.MAX_VALUE * -1;
    double maxY = Double.MAX_VALUE * -1;
    for (Point2D candidate : allPoints) {
      double x = candidate.getX();
      double y = candidate.getY();
      if (minY > y)
        minY = y;
      if (minX > x)
        minX = x;
      if (maxY < y)
        maxY = y;
      if (maxX < x)
        maxX = x;
    }
    return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
  }

  @Override
  public List<IQCar> allQCars() {
    return qcars;
  }

  @Override
  public List<Line2D> allPhotoSensors() {
    return photoSensors;
  }

  @Override
  public List<ICollision> allNewCollisions() {
    return collisions;
  }

  @Override
  public List<Line2D> allDistanceSensors() {
    return distanceSensors;
  }

  public WorldManager() {
    this.observers = new ArrayList<>();
    this.photoSensors = new ArrayList<>();
    this.distanceSensors = new ArrayList<>();
    this.collisions = new ArrayList<>();
    this.playerChannels = new ArrayList<>();
    this.allPoints = new ArrayList<>();
    this.qcars = new ArrayList<>();
    this.sensors = new ArrayList<>();
  }

  // ======== Private methods =======================================

    private void updateMove(IQCar car, boolean isAngleMovement, double requestedTranslation, int sideId, ICollision collision) {
        if (requestedTranslation == 0) return;
        if (collision == null) {
            double[] vector = new double[2];
            if (isAngleMovement) {
                if (requestedTranslation > 0) {
                    vector[0] = car.vertex((sideId+1)%4).getX() - car.vertex(sideId).getX();
                    vector[0] = car.vertex((sideId+1)%4).getY() - car.vertex(sideId).getY();
                } else {
                    vector[0] = car.vertex(sideId).getX() - car.vertex((sideId+1)%4).getX();
                    vector[0] = car.vertex(sideId).getY() - car.vertex((sideId+1)%4).getY();
                }
            } else {
                if (requestedTranslation > 0) {
                    vector[0] = car.vertex((sideId+1)%4).getX() - car.vertex((sideId+2)%4).getX();
                    vector[1] = car.vertex((sideId+1)%4).getY() - car.vertex((sideId+2)%4).getY();
                } else {
                    vector[0] = car.vertex((sideId+2)%4).getX() - car.vertex((sideId+1)%4).getX();
                    vector[1] = car.vertex((sideId+2)%4).getY() - car.vertex((sideId+1)%4).getY();
                }
            }
            double unitVecDiv = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));   //
            vector[0] = vector[0] / unitVecDiv * Math.abs(requestedTranslation);              // Transformation en vecteur unitaire puis multiplication par un scalaire
            vector[1] = vector[1] / unitVecDiv * Math.abs(requestedTranslation);              //
            int p1 = sideId;
            int p2 = (sideId + 1) % 4;
            Point2D point1 = car.vertex(p1);
            Point2D point2 = car.vertex(p2);
            point1.setLocation(point1.getX() + vector[0], point1.getY() + vector[1]);
            point2.setLocation(point2.getX() + vector[0], point2.getY() + vector[1]);
        } else {
            Point2D endPoint = WorldManagerPhysicsHelper.collisionOrigins.get(collision);
            Point2D origin = (!isAngleMovement || requestedTranslation > 0) ? car.vertex((sideId + 1) % 4) : car.vertex(sideId);
            double shiftX = endPoint.getX() - origin.getX();
            double shiftY = endPoint.getY() - origin.getY();
            car.vertex(sideId).setLocation(car.vertex(sideId).getX() + shiftX, car.vertex(sideId).getY() + shiftY);
            car.vertex((sideId + 1) % 4).setLocation(car.vertex((sideId + 1) % 4).getX() + shiftX, car.vertex((sideId + 1) % 4).getY() + shiftY);
            collisions.add(collision);
            notifyAllWorldObserver(QCarAnimationPane.COLLISION_EVENT);
        }
        notifyAllWorldObserver(QCarAnimationPane.STATE_CHANGE_EVENT);
    }

  //Update the state of the world according to the latest changes
   private void updateWorldState(List<IDecision> allDecisions) {
//     collisions = WorldManagerPhysicsHelper.computeCollisions(drivenQCars, allDecisions, allQCars());
     for (int q = 0; q < drivenQCars.size(); q++) {
       try {
         collisions = WorldManagerPhysicsHelper.computeCollisions(drivenQCars, allDecisions, allQCars());
         double requestedTranslation = allDecisions.get(q).requestedTranslation();
         ICollision collision = null;
         for (int i = 0; i < collisions.size(); i++) {
           if (collisions.get(i).hittingQCarId() == drivenQCars.get(q).nature().qCarId()) collision = collisions.get(i);
         }
//         if (Math.abs(requestedTranslation) < Math.pow(10, -5)) requestedTranslation = 0;
         updateMove(drivenQCars.get(q), allDecisions.get(q).isAngleMovement(), requestedTranslation, allDecisions.get(q).sideId(), collision);
       } catch (Exception e) {
         e.printStackTrace();
       }
     }
   }

}
