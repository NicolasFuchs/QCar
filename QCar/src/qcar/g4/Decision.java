package qcar.g4;

import java.awt.geom.Line2D;
import qcar.IDecision;
import qcar.IQCar;

public class Decision implements IDecision {


  private boolean isAngleMovement;
  private int sideId;
  private double requestedTranslation;

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

  // vérifie et corrige la décision si elle ne respecte pas les contraintes de la nature du QCar
  public static IDecision validDecision(IDecision decision, IQCar qcar) {
    if (decision.isAngleMovement()) {
      // détermine si la décision dépasse la longueur maximale du Qcar, et dans ce cas renvoie une
      // nouvelle décision
      if (decision.requestedTranslation() > 0) {
        double maxTranslation =
            maxAllowedTranslation(qcar, decision.sideId(), decision.isAngleMovement());
        if (decision.requestedTranslation() > maxTranslation) {
          return new Decision(decision.isAngleMovement(), decision.sideId(), maxTranslation);
        }
      } else {
        double minTranslation =
            minAllowedTranslation(qcar, decision.sideId(), decision.isAngleMovement());
        if (decision.requestedTranslation() < minTranslation) {
          return new Decision(decision.isAngleMovement(), decision.sideId(), minTranslation);
        }
      }
    }
    return decision;
  }

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
      double maxDepl = maxSideLength / Math.asin(maxSideLength / height);
      return maxDepl + dx;
    } else {
      double lengthAdjSide = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 2) % 4));
      return maxSideLength - lengthAdjSide;
    }
  }

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
      double maxDepl = maxSideLength / Math.asin(maxSideLength / height);
      return -maxDepl + dx;
    } else {
      double lengthAdjSide = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 2) % 4));
      Line2D sideLine = new Line2D.Double(qcar.vertex((side + 1) % 4), qcar.vertex((side + 2) % 4));
      double height = sideLine.ptLineDist(qcar.vertex(side));
      double minimumLengthSide = minArea / height;
      // /!\ requestedTranslation < 0
      return minimumLengthSide - lengthAdjSide;
    }
  }
}
