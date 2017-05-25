package qcar.g4;

import java.awt.geom.Point2D;

import qcar.IDistanceSensor;
import qcar.IQCarNature;

/**
 * this class represent a Distance sensor from one driven QCar this is a straight line from the
 * middle of the side to the first thing it intersects passing by the middle of side 2
 */
public class DistanceSensor implements IDistanceSensor {

  private boolean isSomethingDetected;
  private boolean sideOffersBonus;
  private QCarNature sensedSideNature;
  private int sideId;
  private Point2D rayEnd;

  /**
   * @param isSomethingDetected true if something is detected
   * @param sideOffersBonus if the side detected offers bonus
   * @param sensedSideNature the QCar nature of the side detected
   * @param sideId the id of the side selected
   * @param rayEnd the Point2D of the intersection between the side detected and the sensor
   */
  public DistanceSensor(boolean isSomethingDetected, boolean sideOffersBonus,
      QCarNature sensedSideNature, int sideId, Point2D rayEnd) {
    this.isSomethingDetected = isSomethingDetected;
    this.sideOffersBonus = sideOffersBonus;
    this.sensedSideNature = sensedSideNature;
    this.sideId = sideId;
    this.rayEnd = rayEnd;
  }


  /**
   * @return true if something is detected
   */
  @Override
  public boolean isSomethingDetected() {
    return isSomethingDetected;
  }

  
  /**
   * @return the nature of the side detected
   */
  @Override
  public IQCarNature sensedSideNature() {
    return sensedSideNature;
  }

  /**
   * @return the id of the side detected
   */
  @Override
  public int sideId() {
    return sideId;
  }

  /**
   * @return the Point2D of the intersection between the side detected and the sensor
   */
  @Override
  public Point2D rayEnd() {
    return rayEnd;
  }

  /**
   * @return true if the side detected offers bonus
   */
  @Override
  public boolean sideOffersBonus() {
    return sideOffersBonus;
  }

}
