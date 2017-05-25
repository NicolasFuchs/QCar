package qcar.g4;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import qcar.*;

public class WorldManagerPhysicsHelper {

  // stores the collisions for each driver
  private static HashMap<Integer, ArrayList<ICollision>> driversColCache;
  // stores the projected point of the collision on side A2
  public static HashMap<ICollision, Point2D> collisionOrigins;

  /**
   * @param drivenQCar : QCar driven manually or by a driver
   * @param allQCars : List of all QCar of the simulation
   * @return Sensor for the drivenQCar
   */
  public static ISensors computeSensor(IQCar drivenQCar, List<IQCar> allQCars) {
    if (driversColCache == null) {
      driversColCache = new HashMap<>();
      for (int i = 0; i < allQCars.size(); i++) {
        if (!allQCars.get(i).isAlive()) continue;
        driversColCache.put(allQCars.get(i).nature().qCarId(), new ArrayList<>());
      }
    }
    List<ISeenVertex> seenVertices = new ArrayList<>();
    Point2D eye = new Point2D.Double((drivenQCar.vertex(2).getX()+drivenQCar.vertex(3).getX())/2, (drivenQCar.vertex(2).getY()+drivenQCar.vertex(3).getY())/2);
    Point2D middle0 = new Point2D.Double((drivenQCar.vertex(0).getX()+drivenQCar.vertex(1).getX())/2, (drivenQCar.vertex(0).getY()+drivenQCar.vertex(1).getY())/2);
    Point2D start = (eye.getX() < middle0.getX()) ? eye : middle0;
    Point2D end = (eye.getX() < middle0.getX()) ? middle0 : eye;
    DistanceSensor closest = null;
    Point2D laserPointInA = null;
    Line2D laser = new Line2D.Double(start, end);
    Line2D A1 = new Line2D.Double(drivenQCar.vertex(0), eye); Line2D A2 = new Line2D.Double(drivenQCar.vertex(1), eye);
    double closestA2 = Double.MAX_VALUE;
    double[][] IMatrix = invertMatrix(computePMatrix(A1, A2));
    for (int i = 0; i < allQCars.size(); i++) {
      if (!allQCars.get(i).isAlive()) continue;
      for (int j = 0; j < 4; j++) {
        start = (allQCars.get(i).vertex(j).getX() < allQCars.get(i).vertex((j+1)%4).getX()) ? allQCars.get(i).vertex(j) : allQCars.get(i).vertex((j+1)%4);
        end = (allQCars.get(i).vertex(j).getX() < allQCars.get(i).vertex((j+1)%4).getX()) ? allQCars.get(i).vertex((j+1)%4) : allQCars.get(i).vertex(j);
        Line2D sideForDS = new Line2D.Double(start, end);
        Point2D pointForDS = findIntersection(laser, sideForDS, true);
        if (pointForDS != null && sideForDS.contains(pointForDS)) {
          laserPointInA = pointBaseXYToBaseA12(pointForDS, eye, invertMatrix(computePMatrix(new Line2D.Double(eye, drivenQCar.vertex(2)), laser)));
          if (laserPointInA.getY() >= 0 && laserPointInA.getY() < closestA2) {
            closestA2 = laserPointInA.getY();
            closest = new DistanceSensor(true, allQCars.get(i).vertexOffersBonus(j), (QCarNature)allQCars.get(i).nature(), j, pointForDS);
          }
        }
        Point2D PointInA = pointBaseXYToBaseA12(allQCars.get(i).vertex(j), eye, IMatrix);
        if (PointInA.getX() >= 0 && PointInA.getY() >= 0) {
          intersected : for (int k = 0; k < allQCars.size(); k++) {
            if (!allQCars.get(k).isAlive()) continue;
            for (int l = 0; l < 4; l++) {
              if (allQCars.get(k).vertex(l) == allQCars.get(i).vertex(j) || allQCars.get(k).vertex((l+1)%4) == allQCars.get(i).vertex(j)) continue;
              if (findIntersection(new Line2D.Double(allQCars.get(i).vertex(j), eye), new Line2D.Double(allQCars.get(k).vertex(l), allQCars.get(k).vertex((l+1)%4)), false) != null) {
                break intersected;
              }
              start = (drivenQCar.vertex(0).getX() < drivenQCar.vertex(1).getX())? drivenQCar.vertex(0) : drivenQCar.vertex(1);
              end = (drivenQCar.vertex(0).getX() < drivenQCar.vertex(1).getX())? drivenQCar.vertex(1) : drivenQCar.vertex(0);
              Line2D line0 = new Line2D.Double(start, end);
              start = (eye.getX() < allQCars.get(i).vertex(j).getX()) ? eye : allQCars.get(i).vertex(j);
              end = (eye.getX() < allQCars.get(i).vertex(j).getX()) ? allQCars.get(i).vertex(j) : eye;
              Line2D lineSeen = new Line2D.Double(start, end);
              seenVertices.add(new SeenVertex(j, (QCarNature)allQCars.get(i).nature(), allQCars.get(i).vertexOffersBonus(j), findIntersection(line0, lineSeen, true)));
            }
          }
        }
      }
    }
    if (closest == null) closest = new DistanceSensor(false, false, null, -1, null);
    ISensors sensors = new Sensors(drivenQCar, seenVertices, driversColCache.get(drivenQCar.nature().qCarId()), closest);
    return sensors;
  }

  /**
   * @param drivenQCars : QCar driven manually or by a driver
   * @param allDecisions : The Decision at index i is applied to the driven QCar at index i
   * @return List of all Collision created
   */
  public static List<ICollision> computeCollisions(List<IQCar> drivenQCars, List<IDecision> allDecisions, List<IQCar> allQCars) {
    if (drivenQCars == null || allDecisions == null || allQCars == null) return null;
    if (drivenQCars.size() != allDecisions.size()) return null; // Incoherence if the drivenQCars and allDecisions sizes are different
    driversColCache = new HashMap<>();
    collisionOrigins = new HashMap<>();
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
      if (!isAngleMovement && requestedTranslation <= 0) continue; // No generated collision (inner movement)
      Line2D[] A1A2 = findAxes(decision, car);
      double ratio = (Math.pow(A1A2[1].getX2()-A1A2[1].getX1(), 2)+Math.pow(A1A2[1].getY2()-A1A2[1].getY1(), 2))/(Math.pow(A1A2[0].getX2()-A1A2[0].getX1(), 2)+Math.pow(A1A2[0].getY2()-A1A2[0].getY1(), 2));
      double A2_coor = (isAngleMovement) ? ratio : 1;    // The final collision is the one with the smallest A2 coordinate (!isAngleMovement) or the the smallest ratio A2/A1 (isAngleMovement)
      ICollision col = null;
      List<ICollision> colCandidates = new ArrayList<>();
      Line2D[] areaLines = findLines(decision, car, A1A2);
      double[][] Matrix = computePMatrix(A1A2[0],A1A2[1]);
      double[][] IMatrix = invertMatrix(Matrix);
      Point2D origin = null;
      if (!isAngleMovement) {
        origin = car.vertex((sideId+1)%4);
      } else {
        if (requestedTranslation > 0) {
          origin = car.vertex((sideId+1)%4);
        } else {
          origin = car.vertex(sideId);
        }
      }
      Point2D PointInA = null, thePointInA = null;
      for (int j = 0; j < allQCars.size(); j++) {
        if (car == allQCars.get(j)) continue;
        IQCar hitCar = allQCars.get(j);
        if (!hitCar.isAlive()) continue;
        for (int k = 0; k < 4; k++) {
          PointInA = pointBaseXYToBaseA12(allQCars.get(j).vertex(k), origin, IMatrix);
          if (PointInA.getX() >= 0 && PointInA.getX() <= 1 && PointInA.getY() >= 0 && PointInA.getY() <= 1) { // case where the point stands in the swept area (borders included)
            if ((!isAngleMovement && PointInA.getY() <= A2_coor) || ((PointInA.getY())/(1-PointInA.getX()) <= A2_coor)) {
              if ((!isAngleMovement && PointInA.getY() < A2_coor) || ((PointInA.getY())/(1-PointInA.getX()) < A2_coor)) colCandidates.clear();
              int hittingID = collisionID(vertices, PointInA, origin, sideId, isAngleMovement);
              col = new Collision(hitCar.vertex(k), car.nature().qCarId(), hittingID, hitCar.nature().qCarId(), k, true);
              colCandidates.add(col);
              thePointInA = PointInA;
              A2_coor = (isAngleMovement) ? (PointInA.getY())/(1-PointInA.getX()) : PointInA.getY();

            }
          }
          if (!hitCar.nature().isParkingTarget()) {
            Line2D carSide = new Line2D.Double(hitCar.vertex(k), hitCar.vertex((k+1)%4));
            int nLines = (isAngleMovement) ? 2 : 3;
            for (int l = 0; l < nLines; l++) {
              Point2D intersectionPoint = findIntersection(carSide, areaLines[l], false);
              if (intersectionPoint != null) {
                PointInA = pointBaseXYToBaseA12(intersectionPoint, origin, IMatrix);
                if ((!isAngleMovement && PointInA.getY() <= A2_coor) || ((PointInA.getY())/(1-PointInA.getX()) <= A2_coor)) {
                  if ((!isAngleMovement && PointInA.getY() < A2_coor) || ((PointInA.getY())/(1-PointInA.getX()) < A2_coor)) colCandidates.clear();
                  int hittingID = collisionID(vertices, PointInA, origin, sideId, isAngleMovement);
                  col = new Collision(intersectionPoint, car.nature().qCarId(), hittingID, hitCar.nature().qCarId(), k, false);
                  colCandidates.add(col);
                  thePointInA = PointInA;
                  A2_coor = (isAngleMovement) ? (PointInA.getY())/(1-PointInA.getX()) : PointInA.getY();
                }
              }
            }
          }
        }
      }
      if (!colCandidates.isEmpty()) {
        colList.addAll(colCandidates);
        driversColCache.get(col.hitQCarId()).addAll(colCandidates);
        if (isAngleMovement) {
          double A2coor = thePointInA.getY()/(1-thePointInA.getX())*Math.sqrt(Math.pow(A1A2[0].getY2()-A1A2[0].getY1(), 2) + Math.pow(A1A2[0].getX2()-A1A2[0].getX1(), 2));
          collisionOrigins.put(col, new Point2D.Double(origin.getX()+A2coor*(A1A2[1].getX2()-A1A2[1].getX1()), origin.getY()+A2coor*(A1A2[1].getY2()-A1A2[1].getY1())));
        } else {
          double[] projection = {(A1A2[0].getX2()-A1A2[0].getX1())*thePointInA.getX(), (A1A2[0].getY2()-A1A2[0].getY1())*thePointInA.getX()};
          collisionOrigins.put(col, new Point2D.Double(col.position().getX()-projection[0], col.position().getY()-projection[1]));
        }
      }
    }
    return colList;
  }

  /**
   * @param vertices : points of the QCar
   * @param PointInA : vertex transformed into A1A2 axes
   * @param origin : center of the axes
   * @param sideID : side moved
   * @param isAngleMovement
   * @return ID of the vertex/side of the driven QCar involved in the collision
   */
  private static int collisionID(Point2D[] vertices, Point2D PointInA, Point2D origin, int sideID, boolean isAngleMovement) {
      if ((isAngleMovement && PointInA.getX() == 0 && vertices[sideID] != origin) || (!isAngleMovement && PointInA.getX() == 0)) return (sideID+1)%4;
      else return sideID;
  }

  /**
   * @param decision : move to apply to de QCar
   * @param car : QCar moving
   * @return axes A1 and A2 according to the movement and the QCar
   */
  private static Line2D[] findAxes(IDecision decision, IQCar car) {
    Line2D[] res = new Line2D[2];
    Point2D p1,p2,p3,p4;
    int sideId = decision.sideId();
    if (decision.isAngleMovement()) {
      if (decision.requestedTranslation() > 0) {
        p1 = car.vertex((sideId+1)%4); p2 = car.vertex((sideId+2)%4); p3 = car.vertex(sideId); p4 = car.vertex((sideId+1)%4);
      } else {
        p1 = car.vertex(sideId); p2 = car.vertex((sideId+3)%4); p3 = car.vertex((sideId+1)%4); p4 = car.vertex(sideId);
      }
    } else {
      if (decision.requestedTranslation() > 0) {
        p1 = car.vertex((sideId+1)%4); p2 = car.vertex(sideId); p3 = car.vertex((sideId+2)%4); p4 = car.vertex((sideId+1)%4);
      } else {
        return null;
      }
    }
    double divider = Math.abs(decision.requestedTranslation())/Math.sqrt(Math.pow(p4.getX()-p3.getX(),2) + Math.pow(p4.getY()-p3.getY(),2));
    res[0] = new Line2D.Double(new Point2D.Double(p1.getX(),p1.getY()), new Point2D.Double(p2.getX(),p2.getY()));                                                               // a1
    res[1] = new Line2D.Double(new Point2D.Double(p4.getX(),p4.getY()), new Point2D.Double(p4.getX()+(p4.getX()-p3.getX())*divider,p4.getY()+(p4.getY()-p3.getY())*divider));   // a2
    return res;
  }

  /**
   * @param decision : move to apply to de QCar
   * @param car : QCar moving
   * @param A1A2 : array of axes
   * @return 2 (angleMovement) or 3 (!angleMovement) lines delimiting the area swept by the movement
   */
  private static Line2D[] findLines(IDecision decision, IQCar car, Line2D[] A1A2) {
    Line2D[] res = new Line2D[3];
    if (decision.isAngleMovement()) {
      //Ligne de l'axe A2
      Point2D start = (A1A2[1].getX1() < A1A2[1].getX2()) ? A1A2[1].getP1() : A1A2[1].getP2() ;
      Point2D end = (A1A2[1].getX1() > A1A2[1].getX2()) ? A1A2[1].getP1() : A1A2[1].getP2() ;
      res[0] = new Line2D.Double(start, end);

      //Ligne de l'axe A2_P2 - A1_P2 (hypothenuse des axes)
      start = (A1A2[1].getX2() < A1A2[0].getX2()) ? A1A2[1].getP2() : A1A2[0].getP2() ;
      end = (A1A2[1].getX2() > A1A2[0].getX2()) ? A1A2[1].getP2() : A1A2[0].getP2() ;
      res[1] = new Line2D.Double(start, end);
    } else {
      //Ligne de l'axe A2
      Point2D start = (A1A2[1].getX1() < A1A2[1].getX2()) ? A1A2[1].getP1() : A1A2[1].getP2() ;
      Point2D end = (A1A2[1].getX1() > A1A2[1].getX2()) ? A1A2[1].getP1() : A1A2[1].getP2() ;
      res[0] = new Line2D.Double(start, end);

      //Ligne de l'axe A1 decale
      start = (A1A2[1].getX2() < (A1A2[0].getX2() + (A1A2[1].getX2() - A1A2[1].getX1()))) ? A1A2[1].getP2() : new Point2D.Double((A1A2[0].getX2() + (A1A2[1].getX2() - A1A2[1].getX1())), (A1A2[0].getY2() + (A1A2[1].getY2() - A1A2[1].getY1())));
      end = (A1A2[1].getX2() > (A1A2[0].getX2() + (A1A2[1].getX2() - A1A2[1].getX1()))) ? A1A2[1].getP2() : new Point2D.Double((A1A2[0].getX2() + (A1A2[1].getX2() - A1A2[1].getX1())), (A1A2[0].getY2() + (A1A2[1].getY2() - A1A2[1].getY1())));
      res[1] = new Line2D.Double(start, end);

      //Ligne de l'axe A2 decale
      start = (A1A2[0].getX2() < (A1A2[0].getX2() + (A1A2[1].getX2() - A1A2[1].getX1()))) ? A1A2[0].getP2() : new Point2D.Double((A1A2[0].getX2() + (A1A2[1].getX2() - A1A2[1].getX1())), (A1A2[0].getY2() + (A1A2[1].getY2() - A1A2[1].getY1())));
      end = (A1A2[0].getX2() > (A1A2[0].getX2() + (A1A2[1].getX2() - A1A2[1].getX1()))) ? A1A2[0].getP2() : new Point2D.Double((A1A2[0].getX2() + (A1A2[1].getX2() - A1A2[1].getX1())), (A1A2[0].getY2() + (A1A2[1].getY2() - A1A2[1].getY1())));
      res[2] = new Line2D.Double(start, end);

    }
    return res;
  }

  /**
   * In case of overlap, the intersection will be on one of the 2 Points of hittingQCar, otherwise on one of the 2 Points of hitQCar
   * @param seg1 : first line to find intersection
   * @param seg2 : second line to find intersection
   * @param seenLines : defines whether we are calculating intersections or seenvertices
   * @return Point if an intersection is found, otherwise null
   */
  private static Point2D findIntersection(Line2D seg1, Line2D seg2, boolean seenLines) {
    if (seenLines || seg1.intersectsLine(seg2)) {
      // 2 crossed constant lines
      if (seg1.getX1() == seg1.getX2() && seg2.getY1() == seg2.getY2()) {   // 1 horizontal line and 1 vertical line
        return new Point2D.Double(seg1.getX1(), seg2.getY1());
      }
      if (seg1.getY1() == seg1.getY2() && seg2.getX1() == seg2.getX2()) {   // 1 vertical line and 1 horizontal line
        return new Point2D.Double(seg2.getX1(), seg1.getY1());
      }
      // 2 overlapped constant lines
      if ((seg1.getX1() == seg1.getX2() && seg2.getX1() == seg2.getX2() && seg1.getX1() == seg2.getX1()) || (seg1.getY1() == seg1.getY2() && seg2.getY1() == seg2.getY2() && seg1.getY1() == seg2.getY1())) { // 2 vertical constant lines
        if (seg1.contains(seg2.getP1())) return new Point2D.Double(seg2.getX1(),seg2.getY1());  //seg2.getY1() <= Math.max(seg1.getY1(), seg1.getY2()) && seg2.getY1() >= Math.min(seg1.getY1(), seg1.getY2())
        if (seg1.contains(seg2.getP2())) return new Point2D.Double(seg2.getX2(),seg2.getY2());  //seg2.getY2() <= Math.max(seg1.getY1(), seg1.getY2()) && seg2.getY2() >= Math.min(seg1.getY1(), seg1.getY2())
        if (seg2.contains(seg1.getP1())) return new Point2D.Double(seg1.getX1(),seg1.getY1());  //seg1.getY1() <= Math.max(seg2.getY1(), seg2.getY2()) && seg1.getY1() >= Math.min(seg2.getY1(), seg2.getY2())
        if (seg2.contains(seg1.getP2())) return new Point2D.Double(seg1.getX2(),seg1.getY2());  //seg1.getY2() <= Math.max(seg2.getY1(), seg2.getY2()) && seg1.getY2() >= Math.min(seg2.getY1(), seg2.getY2())
      }
//      if (seg1.getY1() == seg1.getY2() && seg2.getY1() == seg2.getY2() && seg1.getY1() == seg2.getY1()) { // 2 horizontal constant lines
//        if (seg1.contains(seg2.getP1())) return new Point2D.Double(seg2.getX1(),seg2.getY1());  //seg2.getX1() <= Math.max(seg1.getX1(), seg1.getX2()) && seg2.getX1() >= Math.min(seg1.getX1(), seg1.getX2())
//        if (seg1.contains(seg2.getP2())) return new Point2D.Double(seg2.getX2(),seg2.getY2());  //seg2.getX2() <= Math.max(seg1.getX1(), seg1.getX2()) && seg2.getX2() >= Math.min(seg1.getX1(), seg1.getX2())
//        if (seg2.contains(seg1.getP1())) return new Point2D.Double(seg1.getX1(),seg1.getY1());  //seg1.getX1() <= Math.max(seg2.getX1(), seg2.getX2()) && seg1.getX1() >= Math.min(seg2.getX1(), seg2.getX2())
//        if (seg2.contains(seg1.getP2())) return new Point2D.Double(seg1.getX2(),seg1.getY2());  //seg1.getX2() <= Math.max(seg2.getX1(), seg2.getX2()) && seg1.getX2() >= Math.min(seg2.getX1(), seg2.getX2())
//      }
      double m1 = (seg1.getY2()-seg1.getY1())/(seg1.getX2()-seg1.getX1()); double d1 = seg1.getY1()-m1*seg1.getX1();
      double m2 = (seg2.getY2()-seg2.getY1())/(seg2.getX2()-seg2.getX1()); double d2 = seg2.getY1()-m2*seg2.getX1();
      // 1 constant line
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
      // 2 ordinary lines
      } else {
        double x = (d1-d2)/(m2-m1); double y = m1*x + d1;
        return new Point2D.Double(x,y);
      }
    }
    return null;
  }

  /**
   * @param a1_XY : A1 axe in base XY
   * @param 21_XY : A2 axe in base XY
   * @return matrix as an array, first index is column, second is line
   */
  public static double[][] computePMatrix(Line2D a1_XY, Line2D a2_XY) {
    double[][] pMatrix = new double[2][2];
    pMatrix[0][0] = a1_XY.getX2()-a1_XY.getX1();
    pMatrix[0][1] = a1_XY.getY2()-a1_XY.getY1();
    pMatrix[1][0] = a2_XY.getX2()-a2_XY.getX1();
    pMatrix[1][1] = a2_XY.getY2()-a2_XY.getY1();
    return pMatrix;
  }

  /**
   * @param pMatrix : matrix of passage
   * @return the inverted matrix given as parameter
   */
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

  /**
   * @param point_XY : point to convert
   * @return Point in XY coordinates to the same Point in A12 coordinates. A1 and A2 are generated from origin (0,0)
   */
  public static Point2D computePointFrom0XYToBaseA12(Point2D point_XY, double[][] p_1Matrix) {
    double x = (p_1Matrix[0][0]*point_XY.getX()) + (p_1Matrix[1][0]*point_XY.getY());
    double y = (p_1Matrix[0][1]*point_XY.getX()) + (p_1Matrix[1][1]*point_XY.getY());
    Point2D origin_A = new Point2D.Double(x, y);
    return origin_A;
  }

  /**
   * @param point_XY : point to convert
   * @param origin_XY : specified origin in XY coordinates
   * @param p_1Matrix : inverted matrix
   * @return Point in XY coordinates to the same Point in A12 coordinates from the specified origin
   */
  public static Point2D pointBaseXYToBaseA12(Point2D point_XY, Point2D origin_XY, double[][] p_1Matrix) {
    Point2D p_O = new Point2D.Double(point_XY.getX()-origin_XY.getX(), point_XY.getY()-origin_XY.getY());   // Base origin subtracted
    double x = computePointFrom0XYToBaseA12(p_O, p_1Matrix).getX();
    double y = computePointFrom0XYToBaseA12(p_O, p_1Matrix).getY();
    Point2D p_A = new Point2D.Double(x, y);
    return p_A;
  }
  
  /**
   * @param p1 : Point 1
   * @param p2 : Point 2
   * @param p3 : Point 3
   * @return the sign area of the triangle
   */
  public static double signedArea(Point2D p1, Point2D p2, Point2D p3) {
    return (p2.getX()-p1.getX())*(p3.getY()-p1.getY()) - (p3.getX()-p1.getX())*(p2.getY()-p1.getY());
    // negative if clockwise; twice the area of the triangle
  }
  
  /**
   * @param p1 : Point 1
   * @param p2 : Point 2
   * @param p3 : Point 3
   * @return the sign corresponding to the clockwise or counter-clockwise order
   */
  public static int ccw(Point2D p1, Point2D p2, Point2D p3) {
      double a = signedArea(p1, p2, p3);
      if (a<0) return -1;
      if (a>0) return +1;
      return 0;
  }

}
