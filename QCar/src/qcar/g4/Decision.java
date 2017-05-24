package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import jdk.nashorn.internal.ir.RuntimeNode.Request;
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
    double maxSideLength = qcar.nature().maxSideLength();
    double minArea = qcar.nature().minArea();
    int side = decision.sideId();

    if (decision.isAngleMovement()) {
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
      double maxDepl = maxSideLength / Math.asin(maxSideLength / height) + dx;
      if (decision.requestedTranslation() > maxDepl) {
        return new Decision(decision.isAngleMovement(), decision.sideId(), maxDepl);
      }
      if (-decision.requestedTranslation() < -maxDepl) {
        return new Decision(decision.isAngleMovement(), decision.sideId(), -maxDepl);
      }

    } else {
      if (decision.requestedTranslation() > 0) {
        // AUGMENTATION : vérifier maxSideLength
        double lengthAdjSide = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 2) % 4));
        if (decision.requestedTranslation() + lengthAdjSide > maxSideLength) {
          return new Decision(decision.isAngleMovement(), decision.sideId(),
              maxSideLength - lengthAdjSide);
        }


      } else {
        // REDUCTION : vérifier minArea
        // la hauteur latérale (en prenant comme base le coté directement à droite de celui désiré)
        // ne change pas.
        // on calcule l'aire en réduisant le coté adjaçant
        double lengthAdjSide = qcar.vertex((side + 1) % 4).distance(qcar.vertex((side + 2) % 4));
        Line2D sideLine =
            new Line2D.Double(qcar.vertex((side + 1) % 4), qcar.vertex((side + 2) % 4));
        double height = sideLine.ptLineDist(qcar.vertex(side));
        double minimumLengthSide = minArea / height;
        // /!\ requestedTranslation < 0
        if (lengthAdjSide + decision.requestedTranslation() < minimumLengthSide) {
          return new Decision(decision.isAngleMovement(), decision.sideId(),
              minimumLengthSide - lengthAdjSide);
        }
      }
    }
    return decision;
  }



}
