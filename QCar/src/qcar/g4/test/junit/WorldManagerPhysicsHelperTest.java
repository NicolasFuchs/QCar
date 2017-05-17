package qcar.g4.test.junit;

import static org.junit.Assert.*;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.junit.Test;

import qcar.g4.WorldManagerPhysicsHelper;

class WorldManagerPhysicsHelperTest {

  @Test
  public void testComputePMatrix() {
    double[][] pMatrixExpected = {{2, -2},{0, 1}};
    
    Line2D a1_XY = new Line2D.Double(new Point2D.Double(2, 6), new Point2D.Double(4, 4));
    Line2D a2_XY = new Line2D.Double(new Point2D.Double(2, 6), new Point2D.Double(2, 7));
    
    double[][] pMatrix = WorldManagerPhysicsHelper.computePMatrix(a1_XY, a2_XY);
    for (int i = 0; i < pMatrix.length; i++) {
      assertArrayEquals(pMatrixExpected[i], pMatrix[i], 0.0001);
    }
  }
  
  @Test
  public void testInvertMatrix() {
    double[][] p_1MatrixExpected = {{0.5, 1},{0, 1}};

    double[][] pMatrix = {{2, -2},{0, 1}};
    double[][] p_1Matrix = WorldManagerPhysicsHelper.invertMatrix(pMatrix);
    for (int i = 0; i < pMatrix.length; i++) {
      assertArrayEquals(p_1MatrixExpected[i], p_1Matrix[i], 0.0001);
    }
  }

  @Test
  public void testComputeOriginXYToBaseA12() {
    double[][] p_1Matrix = {{0.5, 1},{0, 1}};
    Point2D origin_AExpected = new Point2D.Double(1, 8);
    Point2D origin_XY = new Point2D.Double(2, 6);
    
    Point2D origin_A = WorldManagerPhysicsHelper.computeOriginXYToBaseA12(origin_XY, p_1Matrix);
    
    assertEquals(origin_AExpected.getX(), origin_A.getX(), 0.0001);
    assertEquals(origin_AExpected.getY(), origin_A.getY(), 0.0001);
  }
  
  @Test
  public void testPointBaseXYToBaseA12() {
    double[][] p_1Matrix = {{0.5, 1},{0, 1}};
    Point2D origin_A = new Point2D.Double(1, 8);
    Point2D p_XY = new Point2D.Double(3, 8);
    Point2D p_AExpected = new Point2D.Double(0.5, 3);
    
    Point2D p_A = WorldManagerPhysicsHelper.pointBaseXYToBaseA12(p_XY, p_1Matrix, origin_A);
    
    assertEquals(p_AExpected.getX(), p_A.getX(), 0.0001);
    assertEquals(p_AExpected.getY(), p_A.getY(), 0.0001);
  }
}
