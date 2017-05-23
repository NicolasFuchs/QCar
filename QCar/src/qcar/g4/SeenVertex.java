package qcar.g4;

import java.awt.geom.Point2D;

import qcar.IQCarNature;
import qcar.ISeenVertex;

public class SeenVertex implements ISeenVertex {
  
  QCar mySelf;

  private QCarNature nature;
  private int vertexId;
  private boolean offersBonus;
  private Point2D projectionLocation;

  public SeenVertex(int vertexId, QCarNature nature, boolean offersBonus, Point2D projectionLocation){
    this.nature = nature;
    this.vertexId = vertexId;
    this.offersBonus = offersBonus;
    this.projectionLocation = projectionLocation;
  }

  @Override
  public IQCarNature nature() {
    return this.mySelf.nature();
  }

  @Override
  public int vertexId() {
    return vertexId;
  }

  @Override
  public boolean offersBonus() {
    return offersBonus;
  }

  @Override
  public Point2D projectionLocation() {
    return projectionLocation;
  }

}
