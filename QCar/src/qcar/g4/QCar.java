package qcar.g4;

import java.awt.geom.Point2D;

import qcar.IQCar;
import qcar.IQCarNature;

public class QCar implements IQCar{

  @Override
  public Point2D vertex(int vertexId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int score() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isAlive() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean vertexOffersBonus(int vertexId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean sideOffersBonus(int sideId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean parkOffersBonus() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public IQCarNature nature() {
    // TODO Auto-generated method stub
    return null;
  }

}
