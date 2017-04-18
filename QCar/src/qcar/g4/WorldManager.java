package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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
    Point2D[] vertices = {new Point2D.Double(0,0), new Point2D.Double(10,20), new Point2D.Double(30,20), new Point2D.Double(20,0)};
    boolean[] vertexOffersBonus = {true, true, true, true};
    boolean[] sideOffersBonus = {true, true, true, true};
    boolean parkOffersBonus = false;
    QCarNature qCarNature = new QCarNature(true, false, true, true, 10, 100);
    
    QCar car1 = new QCar(qCarNature, vertices);
    List<IQCar> listQCars = new ArrayList<IQCar>();
    listQCars.add(car1);
    return listQCars;
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

}
