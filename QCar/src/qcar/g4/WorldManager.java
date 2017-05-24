package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Rectangle2D;
import qcar.*;
import qcar.ui.QCarAnimationPane;
import simviou.WorldChangeObserver;

public class WorldManager implements IWorldManager {

  private List<WorldChangeObserver> observers;      // contains all currently observing objects

  private long step;                                // number of step played
  private boolean isSimulationRunning;              // true if simulation is running else false
  private boolean isWarOver;
  private Rectangle2D boundingBox;

  private List<IQCar> drivenQCars;
  private List<IQCar> qcars;                        // contains all the qcars
  private List<Line2D> photoSensors;
  private List<Line2D> distanceSensors;

  private List<ICollision> collisions;

  private List<? extends IDriver> players;
  private ArrayList<PlayerChannel> playerChannels;

  private ArrayList<Point2D> allPoints;

  /*
  *   Observers management
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

  private void notifyAllWorldObserver(int eventType){
    for(WorldChangeObserver wo : observers){
      wo.worldStateChanged(eventType, step);
    }
  }

  /*
  *   Simulation setup/teardown
  */
  @Override
  public void openNewSimulation(IGameDescription description, List<? extends IDriver> players) {

    drivenQCars = new ArrayList<>();
    qcars = description.allQCar();
    this.players = players;
    this.isSimulationRunning = true;
    this.step = 0;

//    // create a channel for each player
//    for(int i  = 0; i < players.size(); i++){
//      playerChannels.add(new PlayerChannel());
//    }

    //updateWorldState(); // update the world for the initial configuration

    for(IQCar q : qcars){
      if (q.nature().isDriven()) {
        drivenQCars.add(q);
      }
      for(int i = 0; i < 4; i++) {
        allPoints.add(q.vertex(i));
      }
    }

    for(int i = 0; i < players.size(); i++) {
      //players.get(i).startDriverThread(playerChannels.get(i));
    }

//    for(int i = 0; i < players.size(); i++) {
//      playerChannels.get(i).sendSensors(null);
//    }

    List<ISensors> sensors = new ArrayList<>();
    for (IQCar driver : drivenQCars) {
      sensors.set(driver.nature().qCarId(), WorldManagerPhysicsHelper.computeSensor(driver, qcars));
    }

    // create a channel for each player
    for(int i  = 0; i < players.size(); i++){
      playerChannels.add(new PlayerChannel(sensors.get(i)));
    }

  }

  @Override
  public boolean isSimulationOpened() {
    return isSimulationRunning;
  }

  @Override
  public void simulateOneStep(long collectiveDelayInMicroSeconds) {

    /*
        This method needs to :
          - get each driver decision and apply it to their qcar
          - update the state of the world (sensors, collision, isWarOver, ...)
          - send the sensors the qcar drivers
          - notify the view of the change
          - increment the number of step
     */

    step++;
    // notifyAllWorldObserver(0);

    //TODO uncomment the next line and send all decisions
    List<IDecision> decisions = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < drivenQCars.size(); i++) {
//      decisions.add(new Decision(random.nextBoolean(), random.nextInt(4), (random.nextBoolean()?1:-1)*random.nextInt(5)));
      decisions.add(new Decision(false,2,50));

    }
    updateWorldState(decisions);
  }

  @Override
  public void closeSimulation() {

    // stop each player's thread and release them from the chan
    for(int i = 0; i < players.size(); i++) {
      players.get(i).stopDriverThread();
      playerChannels.get(i).sendSensors(null);
    }

    isSimulationRunning = false;
  }

  /*
  *   Snapshot of the current state. Common PRE-condition: isSimulationOpened()
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
    double maxX = Double.MAX_VALUE*-1;
    double maxY = Double.MAX_VALUE*-1;
    for(Point2D candidate : allPoints){
      double x = candidate.getX();
      double y = candidate.getY();
      if(minY > y)
        minY = y;
      if(minX > x)
        minX = x;
      if(maxY < y)
        maxY = y;
      if(maxX < x)
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

  public WorldManager(){
    this.observers = new ArrayList<WorldChangeObserver>();
    this.photoSensors = new ArrayList<Line2D>();
    this.distanceSensors = new ArrayList<Line2D>();
    this.collisions = new ArrayList<ICollision>();
    this.playerChannels = new ArrayList<PlayerChannel>();
    this.allPoints = new ArrayList<Point2D>();
  }

  // ======== Private methods =======================================

  private void updateMove(IQCar car, boolean isAngleMovement, int sideId, double requestedTranslation) {
    double[] vector = new double[2];
    if ((isAngleMovement && (sideId == 0 || sideId == 2)) || (!isAngleMovement && (sideId == 1 || sideId == 3))) {
      vector[0] = car.vertex(0).getX()-car.vertex(1).getX();
      vector[1] = car.vertex(0).getY()-car.vertex(1).getY();
    } else {
      vector[0] = car.vertex(3).getX()-car.vertex(0).getX();
      vector[1] = car.vertex(3).getY()-car.vertex(0).getY();
    }
    double unitVecDiv = Math.sqrt(Math.pow(vector[0],2)+Math.pow(vector[1],2)); //
    vector[0] = vector[0] / unitVecDiv * Math.abs(requestedTranslation);        // Transformation en vecteur unitaire puis multiplication par un scalaire
    vector[1] = vector[1] / unitVecDiv * Math.abs(requestedTranslation);        //
    int p1 = sideId;
    int p2 = (sideId+1)%4;
    Point2D point1 = car.vertex(p1);
    Point2D point2 = car.vertex(p2);
    if (requestedTranslation > 0) {
      point1.setLocation(point1.getX()+vector[0], point1.getY()+vector[1]);
      point2.setLocation(point2.getX()+vector[0], point2.getY()+vector[1]);
    } else {
      point1.setLocation(point1.getX()-vector[0], point1.getY()-vector[1]);
      point2.setLocation(point2.getX()-vector[0], point2.getY()-vector[1]);
    }
    notifyAllWorldObserver(QCarAnimationPane.STATE_CHANGE_EVENT);
  }

  /*
        Update the state of the world according to the latest changes
   */
  private void updateWorldState(List<IDecision> allDecisions) {
    collisions = WorldManagerPhysicsHelper.computeCollisions(drivenQCars, allDecisions, allQCars());
    for (int q = 0; q < drivenQCars.size(); q++) {
      try {
        //Thread.sleep(2000);
        double requestedTranslation = allDecisions.get(q).requestedTranslation();
        //if (!collisions.isEmpty()) {
          ICollision collision = null;
          for (int i = 0; i < collisions.size(); i++) {
            if (collisions.get(i).hittingQCarId() == drivenQCars.get(q).nature().qCarId()) collision = collisions.get(i);
          }
          //ICollision collision = collisions.get(q);
          //if (collision.hittingQCarId() == drivenQCars.get(q).nature().qCarId()) {
          if (collision != null) {
            requestedTranslation = Math.signum(requestedTranslation)*Math.sqrt(Math.pow(collision.position().getX()-allQCars().get(q).vertex(collision.hittingSideOrVertexId()).getX(), 2)+Math.pow(collision.position().getY()-allQCars().get(q).vertex(collision.hittingSideOrVertexId()).getY(), 2));
            notifyAllWorldObserver(QCarAnimationPane.COLLISION_EVENT);
          }
        //}
        updateMove(drivenQCars.get(q), allDecisions.get(q).isAngleMovement(), allDecisions.get(q).sideId(), requestedTranslation);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
