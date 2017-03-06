package qcar.g4;

import qcar.IDecision;

public class Decision implements IDecision {
  private boolean isAngleMovement ;
  private int sideId ;
  private double requestedTranslation ;
  
  

  @Override
  public boolean isAngleMovement() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int sideId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double requestedTranslation() {
    // TODO Auto-generated method stub
    return 0;
  }

}
