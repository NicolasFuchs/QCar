package qcar.g4;

import java.awt.geom.Line2D;
import java.util.List;

import javafx.geometry.Rectangle2D;
import qcar.*;
import simviou.WorldChangeObserver;

public class WorldManager implements IWorldManager {

  @Override
  public void addWorldObserver(WorldChangeObserver o) {
    // TODO Auto-generated method stub

  }

  @Override
  public void removeWorldObserver(WorldChangeObserver o) {
    // TODO Auto-generated method stub

  }

  @Override
  public void openNewSimulation(IGameDescription description, List<? extends IDriver> players) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isSimulationOpened() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void simulateOneStep(long collectiveDelayInMicroSeconds) {
    // TODO Auto-generated method stub

  }

  @Override
  public void closeSimulation() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isWarOver() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public long stepNumber() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Rectangle2D boundingBox() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<IQCar> allQCars() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Line2D> allPhotoSensors() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ICollision> allNewCollisions() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Line2D> allDistanceSensors() {
    // TODO Auto-generated method stub
    return null;
  }

}
