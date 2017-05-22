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
  public Decision(boolean isAngleMovement, int sideId, double requestedTranslation) {
    this.isAngleMovement = isAngleMovement;
    this.sideId = sideId;
    this.requestedTranslation = requestedTranslation;
  }
  

  @Override
  public boolean isAngleMovement() {
    return isAngleMovement;
  }

  @Override
  public int sideId() {
    return sideId;
  }

  @Override
  public double requestedTranslation() {
    return requestedTranslation;
  }

  

}
