package qcar.g4;

import java.awt.geom.Point2D;

import qcar.IDistanceSensor;
import qcar.IQCarNature;

public class DistanceSensor implements IDistanceSensor {
  
  private boolean isSomethingDetected ;
  private QCarNature sensedSideNature ;
  private int sideId ;
  private Point2D rayEnd ;
  
  /**
   * @param isSomethingDetected
   * @param sensedSideNature
   * @param sideId
   * @param rayEnd
   */
  public DistanceSensor(boolean isSomethingDetected, QCarNature sensedSideNature, int sideId,
      Point2D rayEnd) {
    this.isSomethingDetected = isSomethingDetected;
    this.sensedSideNature = sensedSideNature;
    this.sideId = sideId;
    this.rayEnd = rayEnd;
  }  

  @Override
  public boolean isSomethingDetected() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public IQCarNature sensedSideNature() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int sideId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Point2D rayEnd() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean sideOffersBonus() {
    // TODO Auto-generated method stub
    return false;
  }

}
