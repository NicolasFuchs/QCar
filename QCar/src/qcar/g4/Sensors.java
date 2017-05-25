package qcar.g4;

import java.util.List;
import qcar.*;

/**
 * Describes what a QCar can see around him
 */
public class Sensors implements ISensors {
  
  QCar mySelf;
  List<ISeenVertex> seenVert;
  List<ICollision> colWithMe;
  IDistanceSensor distanceSensor;
  
  /**
   * Constructor of a Sensor
   * @param myself
   * @param sv
   * @param cwm
   * @param distanceSensor
   */
  public Sensors(IQCar myself, List<ISeenVertex> sv, List<ICollision> cwm, IDistanceSensor distanceSensor) {
    this.mySelf = (QCar)myself;
    this.seenVert = sv;
    this.colWithMe = cwm;
    this.distanceSensor = distanceSensor;
  }

  /**
   * @return List of collisions with me involved
   */
  @Override
  public List<ICollision> collisionsWithMe() {
    return this.colWithMe;
  }

  /**
   * @return List of vertices seen by the side0
   */
  @Override
  public List<ISeenVertex> seenVertices() {
    return this.seenVert;
  }

  /**
   * @return QCar who's having this sensor
   */
  @Override
  public IQCar mySelf() {
    return this.mySelf;
  }

  /**
   * @return the smallest distance between me and the closest QCar or boundingBox
   */
  @Override
  public IDistanceSensor distanceSensor() {
    return distanceSensor;
  }

}
