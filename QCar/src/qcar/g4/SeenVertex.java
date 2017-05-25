package qcar.g4;

import java.awt.geom.Point2D;

import qcar.IQCarNature;
import qcar.ISeenVertex;

/**
 * Represents a seen vertex. Used by a Driver to make decisions.
 */
public class SeenVertex implements ISeenVertex {
  
  //QCar mySelf;

  private QCarNature nature;
  private int vertexId;
  private boolean offersBonus;
  private Point2D projectionLocation;

  /**
   * Constructor of the class
   * @param vertexId id of the vertex from the seen QCar
   * @param nature nature of the seen QCar
   * @param offersBonus defines if the seen vertex offers a bonus
   * @param projectionLocation point where the vertex is projected on the PhotoSensor
   */
  public SeenVertex(int vertexId, QCarNature nature, boolean offersBonus, Point2D projectionLocation){
    this.nature = nature;
    this.vertexId = vertexId;
    this.offersBonus = offersBonus;
    this.projectionLocation = projectionLocation;
  }

  /**
   * Getter for the nature of the seen QCar
   * @return the seen QCar's nature
   */
  @Override
  public IQCarNature nature() {
    return nature;
  }

  /**
   * Getter for the seen vertex's ID
   * @return the id of the seen vertex
   */
  @Override
  public int vertexId() {
    return vertexId;
  }

  /**
   * Getter for offersBonus
   * @return true if the vertex offers a bonus
   */
  @Override
  public boolean offersBonus() {
    return offersBonus;
  }

  /**
   * Getter for the projection location
   * @return the projection of the seen QCar point on the photo sensor
   */
  @Override
  public Point2D projectionLocation() {
    return projectionLocation;
  }

}
