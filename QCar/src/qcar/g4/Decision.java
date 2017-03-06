package qcar.g4;

import qcar.IDecision;

public class Decision implements IDecision {
  private boolean isAngleMovement ;
  private int sideId ;
  private double requestedTranslation ;
  
  /**
   * @param isAngleMovement
   * @param sideId
   * @param requestedTranslation
   */
  private Decision(boolean isAngleMovement, int sideId, double requestedTranslation) {
    this.isAngleMovement = isAngleMovement;
    this.sideId = sideId;
    this.requestedTranslation = requestedTranslation;
  }
  

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
