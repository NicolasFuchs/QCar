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
  private List<IQCar> qcars;                        // contains all the qcars

  private List<Line2D> photoSensors;
  private List<Line2D> distanceSensors;
  private Rectangle2D boundingBox;

  private List<? extends IDriver> players;

  @Override
  public void addWorldObserver(WorldChangeObserver o) {
    if(o != null && !observers.contains(o))
      this.observers.add(o);
  }

  @Override
  public void removeWorldObserver(WorldChangeObserver o) {
    observers.remove(o);
  }

  @Override
  public void openNewSimulation(IGameDescription description, List<? extends IDriver> players) {
    // TODO Auto-generated method stub
    qcars = description.allQCar();
    for(IQCar q : qcars){
      // TODO: get all sensors from qcar, check if is boundingBox
    }
    this.players = players;
    this.step = 0;
  }

  @Override
  public boolean isSimulationOpened() {
    return isSimulationRunning;
  }

  @Override
  public void simulateOneStep(long collectiveDelayInMicroSeconds) {
    // TODO Auto-generated method stub
    step++;
    // TODO for each player, play

    notifyAll();
  }

  @Override
  public void closeSimulation() {
    // TODO Auto-generated method stub
    isSimulationRunning = false;
    for(int i = 0; i < observers.size(); i++)
      observers.remove(i);
  }

  @Override
  public boolean isWarOver() {
    // TODO Auto-generated method stub
    return false;
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
    // TODO Auto-generated method stub
    return new ArrayList<Line2D>();

  }

  @Override
  public List<ICollision> allNewCollisions() {
    // TODO Auto-generated method stub
    return new ArrayList<ICollision>();

  }

  @Override
  public List<Line2D> allDistanceSensors() {
    // TODO Auto-generated method stub
    return new ArrayList<Line2D>();
  }

  public WorldManager(){
    this.observers = new ArrayList<WorldChangeObserver>();
    this.step = 0;
    isSimulationRunning = false;
  }

}
