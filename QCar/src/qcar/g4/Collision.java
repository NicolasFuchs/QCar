package qcar.g4;

import java.awt.geom.Point2D;
import qcar.ICollision;

/**
 * This class represents a collision between two QCars
 * 
 */
public class Collision implements ICollision {
  private Point2D position;
  private int hittingQCarId;
  private int hittingSideOrVertexId;
  private int hitQCarId;
  private int hitSideOrVertexId;
  private boolean isAgainstVertex;

  /**
   * Constructor of Collision
   * 
   * @param position Point2D of the position of the collision
   * @param hittingQCarId int Id of the hitting QCar
   * @param hittingSideOrVertexId int side or vertex Id of the hitting QCar
   * @param hitQCarId int Id of the hit QCar
   * @param hitSideOrVertexId int side or vertex Id of the hit QCar
   * @param isAgainstVertex boolean true if hitting with a (moving) side against a vertex. false if
   *        hitting with a (moving) vertex against a side.
   * 
   */
  public Collision(Point2D position, int hittingQCarId, int hittingSideOrVertexId, int hitQCarId,
      int hitSideOrVertexId, boolean isAgainstVertex) {
    this.position = position;
    this.hittingQCarId = hittingQCarId;
    this.hittingSideOrVertexId = hittingSideOrVertexId;
    this.hitQCarId = hitQCarId;
    this.hitSideOrVertexId = hitSideOrVertexId;
    this.isAgainstVertex = isAgainstVertex;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Point2D position() {
    return this.position;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hittingQCarId() {
    return this.hittingQCarId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hittingSideOrVertexId() {
    return this.hittingSideOrVertexId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hitQCarId() {
    return this.hitQCarId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hitSideOrVertexId() {
    return this.hitSideOrVertexId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAgainstVertex() {
    return this.isAgainstVertex;
  }

}
