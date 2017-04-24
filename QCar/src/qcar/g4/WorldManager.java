package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Rectangle2D;
import qcar.*;
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

    qcars = description.allQCar();
    this.players = players;
    this.isSimulationRunning = true;
    this.step = 0;

    // create a channel for each player
    for(int i  = 0; i < players.size(); i++){
      playerChannels.add(new PlayerChannel());
    }

    updateWorldState(); // update the world for the initial configuration

    for(int i = 0; i < players.size(); i++) {
      players.get(i).startDriverThread(playerChannels.get(i));
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
    // notifyAllWorldObserver(0);
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

  /*
        Update the state of the world according to the latest changes
   */
  private void updateWorldState(){

  }


}
