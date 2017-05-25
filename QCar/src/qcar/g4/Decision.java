package qcar.g4;

import java.awt.geom.Line2D;
import qcar.IDecision;
import qcar.IQCar;

/**
 * This class represent a decision taken by a Driver
 */
public class Decision implements IDecision {

  private boolean isAngleMovement;
  private int sideId;
  private double requestedTranslation;

  /**
   * Constructor of Decision
   * 
   * @param isAngleMovement true if sideId remains on its supporting line (angles change)
   * @param sideId side i contains points p(i) and p((i+1) mod 4)
   * @param requestedTranslation the distance requested >0 means "to the left" or "forwards", when
   *        standing on that side and facing the outside world
   */
  public Decision(boolean isAngleMovement, int sideId, double requestedTranslation) {
    this.isAngleMovement = isAngleMovement;
    this.sideId = sideId;
    this.requestedTranslation = requestedTranslation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAngleMovement() {
    return isAngleMovement;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int sideId() {
    return sideId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double requestedTranslation() {
    return requestedTranslation;
  }

  /** 
   * Checks and correct the Decision if it doesn't respect the constraints given by the QCarNature   * 
   * @param decision the decision to be checked
   * @param qcar the QCar whom Driver requested the Decision
   * 
   */
  public static IDecision validDecision(IDecision decision, IQCar qcar) {
    
    
      // détermine si la décision dépasse la longueur maximale du Qcar, et dans ce cas renvoie une
      // nouvelle décision
      if (decision.requestedTranslation() > 0) {
        double maxTranslation =
            maxAllowedTranslation(qcar, decision.sideId(), decision.isAngleMovement());
        System.out.println("max " +maxTranslation);
        if (decision.requestedTranslation() > maxTranslation) {
          return new Decision(decision.isAngleMovement(), decision.sideId(), maxTranslation);
        }
      } else {
        double minTranslation =
            minAllowedTranslation(qcar, decision.sideId(), decision.isAngleMovement());
        System.out.println("min " + minTranslation);
        if (decision.requestedTranslation() < minTranslation) {
          return new Decision(decision.isAngleMovement(), decision.sideId(), minTranslation);
        }
      }     
    return decision;
  }

  /**
   * return the maximum double for an allowed Decision for a QCar and a side
   * @param QCar the QCar concerned
   * @param side the sideId of the concerned QCar
   * @param isAngleMovement true if the request is an angle movement
   */
  public static double maxAllowedTranslation(IQCar qcar, int side, boolean isAngleMovement) {
    double maxSideLength = qcar.nature().maxSideLength();
    if (isAngleMovement) {
      // détermine si la décision dépasse la longueur maximale du Qcar, et dans ce cas renvoie une
      // nouvelle décision
      Line2D sideLine = new Line2D.Double(qcar.vertex(side), qcar.vertex((side + 1) % 4));
      double height = sideLine.ptLineDist(qcar.vertex((side + 2) % 4));
      double adjsideLength = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 2) % 4));
      double dx = Math.sqrt(Math.pow(adjsideLength, 2) - Math.pow(height, 2));
      double lengthDiagonale02 = qcar.vertex((side) % 4).distance(qcar.vertex((side + 2) % 4));
      double lengthDiagonale13 = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 3) % 4));
      // détermine l'orientation du QCar, "penché vers la droite ou la gauche"
      if (lengthDiagonale13 > lengthDiagonale02) {
        dx = -dx;
      }
      double maxDepl = Math.cos(Math.asin(height / maxSideLength)) * maxSideLength;
      return maxDepl + dx;
    } else {
      double lengthAdjSide = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 2) % 4));
      return maxSideLength - lengthAdjSide;
    }
  }

  /**
   * return the maximum negative double (minimum) for an allowed Decision for a QCar and a side
   * @param QCar the QCar concerned
   * @param side the sideId of the concerned QCar
   * @param isAngleMovement true if the request is an angle movement
   */
  public static double minAllowedTranslation(IQCar qcar, int side, boolean isAngleMovement) {
    double maxSideLength = qcar.nature().maxSideLength();
    double minArea = qcar.nature().minArea();
    if (isAngleMovement) {
      Line2D sideLine = new Line2D.Double(qcar.vertex(side), qcar.vertex((side + 1) % 4));
      double height = sideLine.ptLineDist(qcar.vertex((side + 2) % 4));
      double adjsideLength = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 2) % 4));
      double dx = Math.sqrt(Math.pow(adjsideLength, 2) - Math.pow(height, 2));
      double lengthDiagonale02 = qcar.vertex((side) % 4).distance(qcar.vertex((side + 2) % 4));
      double lengthDiagonale13 = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 3) % 4));
      // détermine l'orientation du QCar, "penché vers la droite ou la gauche"
      if (lengthDiagonale13 > lengthDiagonale02) {
        dx = -dx;
      }
      double maxDepl = Math.cos(Math.asin(height / maxSideLength)) * maxSideLength;
      System.out.println("maxDepl = "+ maxDepl);
      return -maxDepl + dx;
    } else {
      double lengthAdjSide = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 2) % 4));
      Line2D sideLine = new Line2D.Double(qcar.vertex((side + 1) % 4), qcar.vertex((side + 2) % 4));
      double height = sideLine.ptLineDist(qcar.vertex(side));
      double minimumLengthSide = minArea / height;
      // /!\ requestedTranslation < 0
      return -(minimumLengthSide - lengthAdjSide);
    }
  }
}
