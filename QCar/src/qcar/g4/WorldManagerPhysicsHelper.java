package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import qcar.ICollision;
import qcar.IDecision;
import qcar.IQCar;
import qcar.ISensors;

public class WorldManagerPhysicsHelper {

  public static void main(String[] args) {
    Point2D[] vertices1 = {new Point2D.Double(0, 0), new Point2D.Double(4, 2), new Point2D.Double(1, 6), new Point2D.Double(-3, 4)};
    Point2D[] vertices2 = {new Point2D.Double(5, 6), new Point2D.Double(7, 8), new Point2D.Double(4, 10), new Point2D.Double(2, 8)};
    List<IQCar> drivenQCars = new ArrayList<>(); drivenQCars.add(new QCar(new QCarNature(true, false, true, true, 50, 1), vertices1));
    List<IDecision> allDecisions = new ArrayList<>(); allDecisions.add(new Decision(false, 1, -Math.sqrt(20)));
    List<IQCar> allQCars = new ArrayList<>(); allQCars.add(new QCar(new QCarNature(true, false, true, true, 50, 1), vertices2));
    List<ICollision> colList = computeCollisions(drivenQCars, allDecisions, allQCars);
    Line2D[] axes = findAxes(allDecisions.get(0), drivenQCars.get(0));
    System.out.println("A1  -->  XFrom : " + axes[0].getX1() + " YFrom : " + axes[0].getY1() + " XTo : " + axes[0].getX2() + " YTo : " + axes[0].getY2());
    System.out.println("A2  -->  XFrom : " + axes[1].getX1() + " YFrom : " + axes[1].getY1() + " XTo : " + axes[1].getX2() + " YTo : " + axes[1].getY2());
    System.out.println();
    Line2D[] moveLines = findLines(allDecisions.get(0), drivenQCars.get(0), axes[1]);
    System.out.println("1st  -->  XFrom : " + moveLines[0].getX1() + " YFrom : " + moveLines[0].getY1() + " XTo : " + moveLines[0].getX2() + " YTo : " + moveLines[0].getY2());
    System.out.println("2nd  -->  XFrom : " + moveLines[1].getX1() + " YFrom : " + moveLines[1].getY1() + " XTo : " + moveLines[1].getX2() + " YTo : " + moveLines[1].getY2());
    System.out.println("3rd  -->  XFrom : " + moveLines[2].getX1() + " YFrom : " + moveLines[2].getY1() + " XTo : " + moveLines[2].getX2() + " YTo : " + moveLines[2].getY2());
    System.out.println();
    System.out.println("Collision point  -->  X : " + colList.get(0).position().getX() + " Y : " + colList.get(0).position().getY());
  }

  public static List<ISensors> computeSensor(IQCar drivenQCar, List<IQCar> allQCars) {
    //TODO define the kind of list we receive as parameter, implement
    return null;
  }

  // La décision allDecision.get(i) s'applique au Qcar dirigé drivenQCars.get(i)
  public static List<ICollision> computeCollisions(List<IQCar> drivenQCars, List<IDecision> allDecisions, List<IQCar> allQCars) {
    if (drivenQCars.size() != allDecisions.size()) return null; // Incohérence si les tailles de drivenQCars et allDecisions sont différentes
    if (drivenQCars == null || allDecisions == null || allQCars == null) return null;
    List<ICollision> colList = new ArrayList<>();
    for (int i = 0; i < drivenQCars.size(); i++) {
      IQCar car = drivenQCars.get(i);
      IDecision decision = allDecisions.get(i);
      int sideId = decision.sideId();
      double requestedTranslation = decision.requestedTranslation();
      boolean isAngleMovement = decision.isAngleMovement();
      Point2D[] vertices = {car.vertex(0), car.vertex(1), car.vertex(2), car.vertex(3)};
      if (requestedTranslation == 0 || (!isAngleMovement && (((sideId == 0 || sideId == 1) && requestedTranslation > 0) || ((sideId == 2 || sideId == 3) && requestedTranslation < 0)))) continue; // Aucune collision générée car mouvemenent interne au QCar
      Line2D[] A1A2 = findAxes(decision, car);
      double A2_coor = Double.MAX_VALUE;    // La collision "finale" est celle dont la coordonnée A2 est la plus petite
      colList.add(null);
      Line2D[] areaLines = findLines(decision, car, A1A2[1]);
      double[][] IMatrix = invertMatrix(computePMatrix(A1A2[0],A1A2[1]));
      Point2D origin = (!isAngleMovement || ((sideId == 0 || sideId == 3) && requestedTranslation < 0) || ((sideId == 1 || sideId == 2) && requestedTranslation > 0)) ? car.vertex((sideId+1)%4) : car.vertex(sideId);
      for (int j = 0; j < allQCars.size(); j++) {
        if (car == allQCars.get(j)) continue;
        IQCar hitCar = allQCars.get(j);
        for (int k = 0; k < 4; k++) {
          Point2D PointInA = pointBaseXYToBaseA12(allQCars.get(j).vertex(k), origin, IMatrix);
          if (PointInA.getX() >= 0 && PointInA.getX() <= 1 && PointInA.getY() >= 0 && PointInA.getY() <= 1) { // cas où le point est dans la zone balayée (y compris les limites)
            if (PointInA.getY() <= A2_coor) {
              colList.remove(colList.size()-1);
              int hittingID = CollisionID(vertices, PointInA, origin, sideId, isAngleMovement);
              colList.add(new Collision(hitCar.vertex(k), car.nature().qCarId(), hittingID, hitCar.nature().qCarId(), k, true));
              A2_coor = PointInA.getY();
            }
          }
          if (!hitCar.nature().isParkingTarget()) {
            Line2D carSide = new Line2D.Double(hitCar.vertex(k), hitCar.vertex((k+1)%4));
            int nLines = (isAngleMovement) ? 2 : 3;
            for (int l = 0; l < nLines; l++) {
              Point2D intersectionPoint = findIntersection(carSide, areaLines[l]);
              if (intersectionPoint != null) {
                PointInA = pointBaseXYToBaseA12(intersectionPoint, origin, IMatrix);
                if (PointInA.getY() <= A2_coor) {
                  colList.remove(colList.size()-1);
                  int hittingID = CollisionID(vertices, PointInA, origin, sideId, isAngleMovement);
                  colList.add(new Collision(intersectionPoint, car.nature().qCarId(), hittingID, hitCar.nature().qCarId(), k, true));
                  A2_coor = PointInA.getY();
                }
              }
            }
          }
        }
      }
    }
    return colList;
  }
  
  // retourne l'ID du vertex/side du QCar dirigé impliqué dans la collision
  private static int CollisionID(Point2D[] vertices, Point2D PointInA, Point2D origin, int sideID, boolean isAngleMovement) {
    if (isAngleMovement) {
      if (PointInA.getX() == 0 && vertices[sideID] != origin) return sideID+1;
      else return sideID;
    } else {
      if (PointInA.getX() == 0) return sideID+1;
      else return sideID;
    }
  }

  // retourne les axes a1 et a2 en fonction du mouvement et du QCar
  private static Line2D[] findAxes(IDecision decision, IQCar car) {
    Line2D[] res = new Line2D[2];
    Point2D p1,p2,p3,p4;
    int sideId = decision.sideId();
    if (decision.isAngleMovement()) {
      if (((sideId == 0 || sideId == 3) && decision.requestedTranslation() > 0) || ((sideId == 1 || sideId == 2) && decision.requestedTranslation() < 0)) {
        p1 = car.vertex((sideId+3)%4); p2 = car.vertex(sideId); p3 = car.vertex((sideId+1)%4); p4 = car.vertex(sideId);
      } else {
        p1 = car.vertex(sideId); p2 = car.vertex((sideId+3)%4); p3 = car.vertex(sideId); p4 = car.vertex((sideId+1)%4);
      }
    } else {
      p1 = car.vertex(sideId); p2 = car.vertex((sideId+1)%4); p3 = car.vertex((sideId+2)%4); p4 = car.vertex((sideId+1)%4);
    }
    double divider = Math.abs(decision.requestedTranslation())/Math.sqrt(Math.pow(p4.getX()-p3.getX(),2) + Math.pow(p4.getY()-p3.getY(),2));
    res[0] = new Line2D.Double(new Point2D.Double(p2.getX(),p2.getY()), new Point2D.Double(p1.getX(),p1.getY()));                                   // a1
    res[1] = new Line2D.Double(new Point2D.Double(p3.getX()*divider,p3.getY()*divider), new Point2D.Double(p4.getX()*divider,p4.getY()*divider));   // a2
    return res;
  }

  // retourne 2 (angleMovement) ou 3 (!angleMovement) limites délimitant l'aire balayée par le mouvement
  private static Line2D[] findLines(IDecision decision, IQCar car, Line2D A2) {
    Line2D[] res = new Line2D[3]; Point2D p1,p2; int sideId = decision.sideId(); double requestedTranslation = decision.requestedTranslation();
    double shiftX = A2.getX2()-A2.getX1(); double shiftY = A2.getY2()-A2.getY1();
    if (decision.isAngleMovement()) {
//      p1 = car.vertex(sideId); p2 = car.vertex((sideId+1)%4);
//      if (p1.getX() >= p2.getX()) res[0] = new Line2D.Double(p2.getX()+shiftX, p2.getY()+shiftY, p1.getX()+shiftX, p1.getY()+shiftY);
//      else res[0] = new Line2D.Double(p1.getX()+shiftX, p1.getY()+shiftY, p2.getX()+shiftX, p2.getY()+shiftY);
//      if (((sideId == 0 || sideId == 3) && requestedTranslation > 0) || ((sideId == 1 || sideId == 2) && requestedTranslation < 0)) {
//        p1 = car.vertex(sideId); p2 = car.vertex((sideId+3)%4);
//        if (p1.getX()+shiftX >= p2.getX()) res[1] = new Line2D.Double(p2.getX()+shiftX, p2.getY()+shiftY, p1.getX()+shiftX, p1.getY()+shiftY);
//        else res[1] = new Line2D.Double(p1.getX()+shiftX, p1.getY()+shiftY, p2.getX()+shiftX, p2.getY()+shiftY);     
//      } else {
//        p1 = car.vertex((sideId+1)%4); p2 = car.vertex((sideId+2)%4);
//        if (p1.getX()+shiftX >= p2.getX()) res[1] = new Line2D.Double(p2.getX()+shiftX, p2.getY()+shiftY, p1.getX()+shiftX, p1.getY()+shiftY);
//        else res[1] = new Line2D.Double(p1.getX()+shiftX, p1.getY()+shiftY, p2.getX()+shiftX, p2.getY()+shiftY);
//      }
      if (((sideId == 0 || sideId == 3) && requestedTranslation > 0) || ((sideId == 1 || sideId == 2) && requestedTranslation < 0)) {
        p1 = car.vertex(sideId); p2 = car.vertex((sideId+1)%4);
        if (p1.getX()+shiftX >= p2.getX()) res[0] = new Line2D.Double(p1.getX(), p1.getY(), p1.getX()+shiftX, p1.getY()+shiftY);
        else res[0] = new Line2D.Double(p1.getX()+shiftX, p1.getY()+shiftY, p1.getX(), p1.getY());
        p1 = car.vertex(sideId); p2 = car.vertex((sideId+3)%4);
        if (p1.getX()+shiftX >= p2.getX()) res[1] = new Line2D.Double(p2.getX(), p2.getY(), p1.getX()+shiftX, p1.getY()+shiftY);
        else res[1] = new Line2D.Double(p1.getX()+shiftX, p1.getY()+shiftY, p2.getX(), p2.getY());     
      } else {
        p1 = car.vertex(sideId); p2 = car.vertex((sideId+1)%4);
        if (p1.getX()+shiftX >= p2.getX()) res[0] = new Line2D.Double(p2.getX()+shiftX, p2.getY()+shiftY, p2.getX(), p2.getY());
        else res[0] = new Line2D.Double(p2.getX(), p2.getY(), p2.getX()+shiftX, p2.getY()+shiftY);
        p1 = car.vertex((sideId+1)%4); p2 = car.vertex((sideId+2)%4);
        if (p1.getX()+shiftX >= p2.getX()) res[1] = new Line2D.Double(p2.getX(), p2.getY(), p1.getX()+shiftX, p1.getY()+shiftY);
        else res[1] = new Line2D.Double(p1.getX()+shiftX, p1.getY()+shiftY, p2.getX(), p2.getY());
      }
    } else {
      p1 = car.vertex((sideId+1)%4); p2 = car.vertex((sideId+2)%4);
      if (p1.getX() >= p2.getX()) res[0] = new Line2D.Double(p1.getX(), p1.getY(), p1.getX()+shiftX, p1.getY()+shiftY);
      else res[0] = new Line2D.Double(p1.getX()+shiftX, p1.getY()+shiftY, p1.getX(), p1.getY());
      p1 = car.vertex(sideId); p2 = car.vertex((sideId+1)%4);
      if (p1.getX() >= p2.getX()) res[1] = new Line2D.Double(p2.getX()+shiftX, p2.getY()+shiftY, p1.getX()+shiftX, p1.getY()+shiftY);
      else res[1] = new Line2D.Double(p1.getX()+shiftX, p1.getY()+shiftY, p2.getX()+shiftX, p2.getY()+shiftY);
      p1 = car.vertex((sideId+3)%4); p2 = car.vertex(sideId);
      if (p1.getX() >= p2.getX()) res[2] = new Line2D.Double(p2.getX()+shiftX, p2.getY()+shiftY, p2.getX(), p2.getY());
      else res[2] = new Line2D.Double(p2.getX(), p2.getY(), p2.getX()+shiftX, p2.getY()+shiftY);
    }
    return res;
  }

  // retourne un Point si une intersection est trouvée, sinon null
  // Si les droites se chevauchent, l'intersection se trouvera sur l'un des deux Points du hittingQCar
  private static Point2D findIntersection(Line2D seg1, Line2D seg2) {
    if (seg1.intersectsLine(seg2)) {
      // 2 droites constantes croisées
      if (seg1.getX1() == seg1.getX2() && seg2.getY1() == seg2.getY2()) {   // cas d'une ligne horizontale et d'une ligne verticale
        return new Point2D.Double(seg1.getX1(), seg2.getY1());
      }
      if (seg1.getY1() == seg1.getY2() && seg2.getX1() == seg2.getX2()) {   // cas d'une ligne verticale et d'une ligne horizontale
        return new Point2D.Double(seg2.getX1(), seg1.getY1());
      }
      // 2 droites constantes superposées
      if (seg1.getX1() == seg1.getX2() && seg2.getX1() == seg2.getX2() && seg1.getX1() == seg2.getX1()) { // constantes verticales
//        if (seg1.getY1() <= Math.max(seg2.getY1(), seg2.getY2()) && seg1.getY1() >= Math.min(seg2.getY1(), seg2.getY2())) return new Point2D.Double(seg1.getX1(),seg1.getY1());
//        if (seg1.getY2() <= Math.max(seg2.getY1(), seg2.getY2()) && seg1.getY2() >= Math.min(seg2.getY1(), seg2.getY2())) return new Point2D.Double(seg1.getX2(),seg1.getY2());
        if (seg2.getY1() <= Math.max(seg1.getY1(), seg1.getY2()) && seg2.getY1() >= Math.min(seg1.getY1(), seg1.getY2())) return new Point2D.Double(seg2.getX1(),seg2.getY1());
        if (seg2.getY2() <= Math.max(seg1.getY1(), seg1.getY2()) && seg2.getY2() >= Math.min(seg1.getY1(), seg1.getY2())) return new Point2D.Double(seg2.getX2(),seg2.getY2());
      }
      if (seg1.getY1() == seg1.getY2() && seg2.getY1() == seg2.getY2() && seg1.getY1() == seg2.getY1()) { // constantes horizontales
//        if (seg1.getX1() <= Math.max(seg2.getX1(), seg2.getX2()) && seg1.getX1() >= Math.min(seg2.getX1(), seg2.getX2())) return new Point2D.Double(seg1.getX1(),seg1.getY1());
//        if (seg1.getX2() <= Math.max(seg2.getX1(), seg2.getX2()) && seg1.getX2() >= Math.min(seg2.getX1(), seg2.getX2())) return new Point2D.Double(seg1.getX2(),seg1.getY2());
        if (seg2.getX1() <= Math.max(seg1.getX1(), seg1.getX2()) && seg2.getX1() >= Math.min(seg1.getX1(), seg1.getX2())) return new Point2D.Double(seg2.getX1(),seg2.getY1());
        if (seg2.getX2() <= Math.max(seg1.getX1(), seg1.getX2()) && seg2.getX2() >= Math.min(seg1.getX1(), seg1.getX2())) return new Point2D.Double(seg2.getX2(),seg2.getY2());
      }
      // 1 droite constante
      double m1 = (seg1.getY2()-seg1.getY1())/(seg1.getX2()-seg1.getX1()); double d1 = seg1.getY1()-m1*seg1.getX1();
      double m2 = (seg2.getY2()-seg2.getY1())/(seg2.getX2()-seg2.getX1()); double d2 = seg2.getY1()-m2*seg2.getX1();
      if (seg1.getX1() == seg1.getX2() || seg1.getY1() == seg1.getY2()) {
        if (seg1.getX1() == seg1.getX2()) {
          double y = m2*seg1.getX1()+d2;
          return new Point2D.Double(seg1.getX1(), y);
        }
        if (seg1.getY1() == seg1.getY2()) {
          double x = (seg1.getY1()-d2)/m2;
          return new Point2D.Double(x, seg1.getY1());
        }
      } else if (seg2.getX1() == seg2.getX2() || seg2.getY1() == seg2.getY2()) {
        if (seg2.getX1() == seg2.getX2()) {
          double y = m1*seg2.getX1()+d1;
          return new Point2D.Double(seg2.getX1(), y);
        }
        if (seg2.getY1() == seg2.getY2()) {
          double x = (seg2.getY1()-d1)/m1;
          return new Point2D.Double(x, seg2.getY1());
        }
      // 2 droites quelconques
      } else {
        double x = (d1-d2)/(m2-m1); double y = m1*x + d1;
        return new Point2D.Double(x,y);
      }
    }
    return null;
  }

  //return matrix as an array, first index is column, second is line
  public static double[][] computePMatrix(Line2D a1_XY, Line2D a2_XY) {
    double[][] pMatrix = new double[2][2];
    pMatrix[0][0] = a1_XY.getX2()-a1_XY.getX1();
    pMatrix[0][1] = a1_XY.getY2()-a1_XY.getY1();
    pMatrix[1][0] = a2_XY.getX2()-a2_XY.getX1();
    pMatrix[1][1] = a2_XY.getY2()-a2_XY.getY1();
    return pMatrix;
  }

  public static double[][] invertMatrix(double[][] pMatrix) {
    assert(pMatrix.length == 2 && pMatrix[0].length == 2);
    double[][] p_1Matrix = new double[2][2];
    double determinant = 1/((pMatrix[0][0]*pMatrix[1][1])-(pMatrix[0][1]*pMatrix[1][0]));
    p_1Matrix[0][0] = pMatrix[1][1]*determinant;
    p_1Matrix[0][1] = (-pMatrix[0][1])*determinant;
    p_1Matrix[1][0] = (-pMatrix[1][0])*determinant;
    p_1Matrix[1][1] = pMatrix[0][0]*determinant;
    return p_1Matrix;
  }

  //  public static Point2D computeOriginXYToBaseA12(Point2D origin_XY, double[][] p_1Matrix) {
  //    double x = (p_1Matrix[0][0]*origin_XY.getX()) + (p_1Matrix[1][0]*origin_XY.getY());
  //    double y = (p_1Matrix[0][1]*origin_XY.getX()) + (p_1Matrix[1][1]*origin_XY.getY());
  //    Point2D origin_A = new Point2D.Double(x, y);
  //    return origin_A;
  //  }

  private static Point2D computePointFrom0XYToBaseA12(Point2D point_XY, double[][] p_1Matrix) {
    double x = (p_1Matrix[0][0]*point_XY.getX()) + (p_1Matrix[1][0]*point_XY.getY());
    double y = (p_1Matrix[0][1]*point_XY.getX()) + (p_1Matrix[1][1]*point_XY.getY());
    Point2D origin_A = new Point2D.Double(x, y);
    return origin_A;
  }

  //  public static Point2D pointBaseXYToBaseA12(Point2D p_XY, double[][] p_1Matrix, Point2D origin_A) {
  //    double x = (p_1Matrix[0][0]*p_XY.getX()) + (p_1Matrix[1][0]*p_XY.getY()) - origin_A.getX();
  //    double y = (p_1Matrix[0][1]*p_XY.getX()) + (p_1Matrix[1][1]*p_XY.getY()) - origin_A.getY();
  //    Point2D p_A = new Point2D.Double(x, y);
  //    return p_A;
  //  }

  public static Point2D pointBaseXYToBaseA12(Point2D point_XY, Point2D origin_XY, double[][] p_1Matrix) {
    Point2D p_O = new Point2D.Double(point_XY.getX()-origin_XY.getX(), point_XY.getY()-origin_XY.getY());   // Base origin substracted
    double x = computePointFrom0XYToBaseA12(p_O, p_1Matrix).getX();
    double y = computePointFrom0XYToBaseA12(p_O, p_1Matrix).getY();
    Point2D p_A = new Point2D.Double(x, y);
    return p_A;
  }

}
