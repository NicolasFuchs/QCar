package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import qcar.ICollision;
import qcar.IDecision;
import qcar.IQCar;
import qcar.ISensors;

public class WorldManagerPhysicsHelper {

  // Stores the collisions with each driver
  //private static ArrayList<ICollision>[] driversColCache = null;
  private static HashMap<Integer, ArrayList<ICollision>> driversColCache;

  public static ISensors computeSensor(IQCar drivenQCar, List<IQCar> allQCars) {

    for (int i = 0; i < allQCars.size(); i++) {
      if (drivenQCar.nature().qCarId() == allQCars.get(i).nature().qCarId()) continue;
      
    }

    ISensors sensors = new Sensors(drivenQCar, null, null);
    return sensors;
  }

  // La dï¿½cision allDecision.get(i) s'applique au Qcar dirigï¿½ drivenQCars.get(i)
  public static List<ICollision> computeCollisions(List<IQCar> drivenQCars, List<IDecision> allDecisions, List<IQCar> allQCars) {
    if (drivenQCars == null || allDecisions == null || allQCars == null) return null;
    if (drivenQCars.size() != allDecisions.size()) return null; // Incohï¿½rence si les tailles de drivenQCars et allDecisions sont diffï¿½rentes
    driversColCache = new HashMap<>();
    for (int i = 0; i < allQCars.size(); i++) {
      driversColCache.put(allQCars.get(i).nature().qCarId(), new ArrayList<>());
    }
    List<ICollision> colList = new ArrayList<>();
    for (int i = 0; i < drivenQCars.size(); i++) {
      IQCar car = drivenQCars.get(i);
      if (!car.isAlive()) continue;
      IDecision decision = allDecisions.get(i);
      int sideId = decision.sideId();
      double requestedTranslation = decision.requestedTranslation();
      boolean isAngleMovement = decision.isAngleMovement();
      Point2D[] vertices = {car.vertex(0), car.vertex(1), car.vertex(2), car.vertex(3)};
      if (requestedTranslation == 0 || (!isAngleMovement && (((sideId == 0 || sideId == 1) && requestedTranslation > 0) || ((sideId == 2 || sideId == 3) && requestedTranslation < 0)))) continue; // Aucune collision gï¿½nï¿½rï¿½e car mouvemenent interne au QCar
      Line2D[] A1A2 = findAxes(decision, car);
      double A2_coor = Double.MAX_VALUE;    // La collision "finale" est celle dont la coordonnï¿½e A2 est la plus petite
      //colList.add(null);
      ICollision col = null;
      Line2D[] areaLines = findLines(decision, car, A1A2[1]);
      double[][] IMatrix = invertMatrix(computePMatrix(A1A2[0],A1A2[1]));
      Point2D origin = (!isAngleMovement || ((sideId == 0 || sideId == 3) && requestedTranslation < 0) || ((sideId == 1 || sideId == 2) && requestedTranslation > 0)) ? car.vertex((sideId+1)%4) : car.vertex(sideId);
      for (int j = 0; j < allQCars.size(); j++) {
        if (car == allQCars.get(j)) continue;
        IQCar hitCar = allQCars.get(j);
        if (!hitCar.isAlive()) continue;
        for (int k = 0; k < 4; k++) {
          Point2D PointInA = pointBaseXYToBaseA12(allQCars.get(j).vertex(k), origin, IMatrix);
          if (PointInA.getX() >= 0 && PointInA.getX() <= 1 && PointInA.getY() >= 0 && PointInA.getY() <= 1) { // cas oï¿½ le point est dans la zone balayï¿½e (y compris les limites)
            if (PointInA.getY() <= A2_coor) {
              int hittingID = collisionID(vertices, PointInA, origin, sideId, isAngleMovement);
              col = new Collision(hitCar.vertex(k), car.nature().qCarId(), hittingID, hitCar.nature().qCarId(), k, true);
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
                  int hittingID = collisionID(vertices, PointInA, origin, sideId, isAngleMovement);
                  col = new Collision(intersectionPoint, car.nature().qCarId(), hittingID, hitCar.nature().qCarId(), k, true);
                  A2_coor = PointInA.getY();
                }
              }
            }
          }
        }
      }
      if (col != null) {
        colList.add(col);
        driversColCache.get(col.hitQCarId()).add(col);
      }
    }
    return colList;
  }
  
  // retourne l'ID du vertex/side du QCar dirigï¿½ impliquï¿½ dans la collision
  private static int collisionID(Point2D[] vertices, Point2D PointInA, Point2D origin, int sideID, boolean isAngleMovement) {
    if (isAngleMovement) {
      if (PointInA.getX() == 0 && vertices[sideID] != origin) return (sideID+1)%4;
      else return sideID;
    } else {
      if (PointInA.getX() == 0) return (sideID+1)%4;
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

  // retourne 2 (angleMovement) ou 3 (!angleMovement) limites dï¿½limitant l'aire balayï¿½e par le mouvement
  private static Line2D[] findLines(IDecision decision, IQCar car, Line2D A2) {
    Line2D[] res = new Line2D[3]; Point2D p1,p2; int sideId = decision.sideId(); double requestedTranslation = decision.requestedTranslation();
    double shiftX = A2.getX2()-A2.getX1(); double shiftY = A2.getY2()-A2.getY1();
    if (decision.isAngleMovement()) {
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

        if (seg2.getY1() <= Math.max(seg1.getY1(), seg1.getY2()) && seg2.getY1() >= Math.min(seg1.getY1(), seg1.getY2())) return new Point2D.Double(seg2.getX1(),seg2.getY1());
        if (seg2.getY2() <= Math.max(seg1.getY1(), seg1.getY2()) && seg2.getY2() >= Math.min(seg1.getY1(), seg1.getY2())) return new Point2D.Double(seg2.getX2(),seg2.getY2());
        if (seg1.getY1() <= Math.max(seg2.getY1(), seg2.getY2()) && seg1.getY1() >= Math.min(seg2.getY1(), seg2.getY2())) return new Point2D.Double(seg1.getX1(),seg1.getY1());
        if (seg1.getY2() <= Math.max(seg2.getY1(), seg2.getY2()) && seg1.getY2() >= Math.min(seg2.getY1(), seg2.getY2())) return new Point2D.Double(seg1.getX2(),seg1.getY2());
      }
      if (seg1.getY1() == seg1.getY2() && seg2.getY1() == seg2.getY2() && seg1.getY1() == seg2.getY1()) { // constantes horizontales
        if (seg2.getX1() <= Math.max(seg1.getX1(), seg1.getX2()) && seg2.getX1() >= Math.min(seg1.getX1(), seg1.getX2())) return new Point2D.Double(seg2.getX1(),seg2.getY1());
        if (seg2.getX2() <= Math.max(seg1.getX1(), seg1.getX2()) && seg2.getX2() >= Math.min(seg1.getX1(), seg1.getX2())) return new Point2D.Double(seg2.getX2(),seg2.getY2());
        if (seg1.getX1() <= Math.max(seg2.getX1(), seg2.getX2()) && seg1.getX1() >= Math.min(seg2.getX1(), seg2.getX2())) return new Point2D.Double(seg1.getX1(),seg1.getY1());
        if (seg1.getX2() <= Math.max(seg2.getX1(), seg2.getX2()) && seg1.getX2() >= Math.min(seg2.getX1(), seg2.getX2())) return new Point2D.Double(seg1.getX2(),seg1.getY2());
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

  public static Point2D computePointFrom0XYToBaseA12(Point2D point_XY, double[][] p_1Matrix) {
    double x = (p_1Matrix[0][0]*point_XY.getX()) + (p_1Matrix[1][0]*point_XY.getY());
    double y = (p_1Matrix[0][1]*point_XY.getX()) + (p_1Matrix[1][1]*point_XY.getY());
    Point2D origin_A = new Point2D.Double(x, y);
    return origin_A;
  }

  public static Point2D pointBaseXYToBaseA12(Point2D point_XY, Point2D origin_XY, double[][] p_1Matrix) {
    Point2D p_O = new Point2D.Double(point_XY.getX()-origin_XY.getX(), point_XY.getY()-origin_XY.getY());   // Base origin substracted
    double x = computePointFrom0XYToBaseA12(p_O, p_1Matrix).getX();
    double y = computePointFrom0XYToBaseA12(p_O, p_1Matrix).getY();
    Point2D p_A = new Point2D.Double(x, y);
    return p_A;
  }

}
