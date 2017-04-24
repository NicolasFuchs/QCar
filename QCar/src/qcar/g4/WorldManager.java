package qcar.g4;

import java.awt.geom.Line2D;
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
  private ArrayList<IPlayerChannel> playerChannels;

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
    /*
     * create sensors
     * start driver threads
     * receive decisions (discard them)
     * compute sensors
     */
    qcars = description.allQCar();

    for(IQCar q : qcars){
      if(q.nature().isDriven()){
        // TODO create sensors for the pilotes

      }
    }

    this.players = players;

    playerChannels = new ArrayList<IPlayerChannel>();
    
    for(int i  = 0; i < players.size(); i++){
      playerChannels.add(new PlayerChannel());
    }

    this.isSimulationRunning = true;
    this.step = 0;
  }

  @Override
  public boolean isSimulationOpened() {
    return isSimulationRunning;
  }

  @Override
  public void simulateOneStep(long collectiveDelayInMicroSeconds) {
    /*
     * send sensors
     * receive decisions
     * execute decisions
     * compute collisions
     * compute sensors
     */
    // TODO for each player, play
    for(int i = 0; i < players.size(); i++){
      players.get(i).startDriverThread(playerChannels.get(i));
    }
    step++;
  }

  @Override
  public void closeSimulation() {
    /*
     * stop threads
     * send sensors
     */
    isSimulationRunning = false;
    for (int i = 0; i < observers.size(); i++)
      observers.remove(i);
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
  }

}
