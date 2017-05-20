package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
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

  private List<IQCar> qcars;                        // contains all the qcars
  private List<Line2D> photoSensors;
  private List<Line2D> distanceSensors;

  private List<ICollision> collisions;

  private List<? extends IDriver> players;
  private ArrayList<PlayerChannel> playerChannels;

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
    qcars = new ArrayList<>();
//    Point2D[] vertices1 = {new Point2D.Double(0, 0), new Point2D.Double(4, 2), new Point2D.Double(1, 6), new Point2D.Double(-3, 4)};
//    qcars.add(new QCar(new QCarNature(true, false, true, true, 50, 1), vertices1));
//    Point2D[] vertices2 = {new Point2D.Double(5, 6), new Point2D.Double(7, 8), new Point2D.Double(4, 10), new Point2D.Double(2, 8)};
//    qcars.add(new QCar(new QCarNature(false, false, true, true, 50, 1), vertices2));
    Point2D[] vertices1 = {new Point2D.Double(0, 0), new Point2D.Double(4, 0), new Point2D.Double(4, 4), new Point2D.Double(0, 4)};
    qcars.add(new QCar(new QCarNature(true, false, true, true, 50, 1), vertices1));
    Point2D[] vertices2 = {new Point2D.Double(0, 6), new Point2D.Double(4, 6), new Point2D.Double(4, 10), new Point2D.Double(0, 10)};
    qcars.add(new QCar(new QCarNature(false, false, true, true, 50, 1), vertices2));
    //qcars = description.allQCar();
    this.players = players;
    this.isSimulationRunning = true;
    this.step = 0;

    // create a channel for each player
    for(int i  = 0; i < players.size(); i++){
      playerChannels.add(new PlayerChannel());
    }

    //updateWorldState(); // update the world for the initial configuration

    for(int i = 0; i < players.size(); i++) {
      //players.get(i).startDriverThread(playerChannels.get(i));
    }

    for(int i = 0; i < players.size(); i++) {
      playerChannels.get(i).sendSensors(null);
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
//    for (IQCar car : allQCars()) {
//      updateMove(car, false, 2, 5);
//    }
//    notifyAllWorldObserver(QCarAnimationPane.STATE_CHANGE_EVENT);
    updateWorldState();
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
    boundingBox = new Rectangle2D(-20,-20,20,20);
    return boundingBox;
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
  private void updateWorldState() {
    List<IDecision> allDecisions = new ArrayList<>(); allDecisions.add(new Decision(false, 2, 1));
    List<IQCar> drivenQCars = new ArrayList<>();
    for (int q = 0; q < allQCars().size(); q++) {
      if (allQCars().get(q).nature().isDriven()) {
        drivenQCars.add(allQCars().get(q));
      }
    }
    collisions = WorldManagerPhysicsHelper.computeCollisions(drivenQCars, allDecisions, allQCars());
    for (int q = 0; q < drivenQCars.size(); q++) {
      try {
        Thread.sleep(3000);
        double requestedTranslation = allDecisions.get(q).requestedTranslation();
        if (!collisions.isEmpty()) {
        ICollision collision = collisions.get(q);
          if (collision != null) {
            requestedTranslation = Math.signum(requestedTranslation)*Math.sqrt(Math.pow(collision.position().getX()-allQCars().get(q).vertex(collision.hittingSideOrVertexId()).getX(), 2)+Math.pow(collision.position().getY()-allQCars().get(q).vertex(collision.hittingSideOrVertexId()).getY(), 2));
            notifyAllWorldObserver(QCarAnimationPane.COLLISION_EVENT);
          }
        }
        updateMove(drivenQCars.get(q), allDecisions.get(q).isAngleMovement(), allDecisions.get(q).sideId(), requestedTranslation);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


}
