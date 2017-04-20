package qcar.g4;

import java.util.List;
import qcar.*;

public class Sensors implements ISensors {
  
  QCar mySelf;
  List<ISeenVertex> seenVert;
  List<ICollision> colWithMe;
  
  public Sensors(IQCar myself, List<ISeenVertex> sv, List<ICollision> cwm) {
    this.mySelf = (QCar)myself;
    this.seenVert = sv;
    this.colWithMe = cwm;
  }

  @Override
  public List<ICollision> collisionsWithMe() {
    return this.colWithMe;
  }

  @Override
  public List<ISeenVertex> seenVertices() {
    return this.seenVert;
  }

  @Override
  public IQCar mySelf() {
    return this.mySelf;
  }

  @Override
  public IDistanceSensor distanceSensor() {
    // TODO Auto-generated method stub
    return null;
  }

}
