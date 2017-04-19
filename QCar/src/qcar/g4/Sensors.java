package qcar.g4;

import java.util.List;

import qcar.*;

public class Sensors implements ISensors {  
  QCar mySelf;  

  @Override
  public List<ICollision> collisionsWithMe() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ISeenVertex> seenVertices() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IQCar mySelf() {
    // TODO Auto-generated method stub
    return this.mySelf;
  }

  @Override
  public IDistanceSensor distanceSensor() {
    // TODO Auto-generated method stub
    return null;
  }

}
