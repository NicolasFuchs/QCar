package qcar.g4;

import java.awt.geom.Point2D;

import qcar.ICollision;

public class Collision implements ICollision {
  private Point2D position;
  private int hittingQCarId;
  private int hittingSideOrVertexId;
  private int hitQCarId;
  private int hitSideOrVertexId;
  private boolean isAgainstVertex;
   
  /**
   * @param position
   * @param hittingQCarId
   * @param hittingSideOrVertexId
   * @param hitQCarId
   * @param hitSideOrVertexId
   * @param isAgainstVertex
   */
  private Collision(Point2D position, int hittingQCarId, int hittingSideOrVertexId, int hitQCarId,
      int hitSideOrVertexId, boolean isAgainstVertex) {
    this.position = position;
    this.hittingQCarId = hittingQCarId;
    this.hittingSideOrVertexId = hittingSideOrVertexId;
    this.hitQCarId = hitQCarId;
    this.hitSideOrVertexId = hitSideOrVertexId;
    this.isAgainstVertex = isAgainstVertex;
  }  

  @Override
  public Point2D position() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int hittingQCarId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hittingSideOrVertexId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hitQCarId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int hitSideOrVertexId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isAgainstVertex() {
    // TODO Auto-generated method stub
    return false;
  }

  

}
