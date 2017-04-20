package qcar.g4;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

import qcar.ICollision;
import qcar.IDecision;
import qcar.IQCar;
import qcar.ISensors;

public class WorldManagerPhysicsHelper {

  public static List<ISensors> computeSensor(IQCar drivenQCar, List<IQCar> allQCars) {
    //TODO define the kind of list we receive as parameter, implement
    return null;
  }
  
  public static List<ICollision> computeCollisions(List<IQCar> drivenQCars, List<IDecision> allDecisions, List<IQCar> allQCars) {
    //TODO define the kind of lists we receive as parameter, implement
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
  
  public static Point2D computeOriginXYToBaseA12(Point2D origin_XY, double[][] p_1Matrix) {
    double x = (p_1Matrix[0][0]*origin_XY.getX()) + (p_1Matrix[1][0]*origin_XY.getY());
    double y = (p_1Matrix[0][1]*origin_XY.getX()) + (p_1Matrix[1][1]*origin_XY.getY());
    Point2D origin_A = new Point2D.Double(x, y);
    return origin_A;
  }
  
  public static Point2D pointBaseXYToBaseA12(Point2D p_XY, double[][] p_1Matrix, Point2D origin_A) {
    double x = (p_1Matrix[0][0]*p_XY.getX()) + (p_1Matrix[1][0]*p_XY.getY()) - origin_A.getX();
    double y = (p_1Matrix[0][1]*p_XY.getX()) + (p_1Matrix[1][1]*p_XY.getY()) - origin_A.getY();
    Point2D p_A = new Point2D.Double(x, y);
    return p_A;
  }
  
}
