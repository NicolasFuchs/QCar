package qcar.g4;

import java.awt.geom.Point2D;

import qcar.IDistanceSensor;
import qcar.IQCarNature;

public class DistanceSensor implements IDistanceSensor {
  
  private boolean isSomethingDetected ;
  private boolean sideOffersBonus ;
  private QCarNature sensedSideNature ;
  private int sideId ;
  private Point2D rayEnd ;

  public DistanceSensor(boolean isSomethingDetected, boolean sideOffersBonus, QCarNature sensedSideNature, int sideId, Point2D rayEnd) {
    this.isSomethingDetected = isSomethingDetected;
    this.sideOffersBonus = sideOffersBonus;
    this.sensedSideNature = sensedSideNature;
    this.sideId = sideId;
    this.rayEnd = rayEnd;
  }  

  @Override
  public boolean isSomethingDetected() {
    return isSomethingDetected;
  }

  @Override
  public IQCarNature sensedSideNature() {
    return sensedSideNature;
  }

  @Override
  public int sideId() {
    return sideId;
  }

  @Override
  public Point2D rayEnd() {
    return rayEnd;
  }

  @Override
  public boolean sideOffersBonus() {
    return sideOffersBonus;
  }

}
