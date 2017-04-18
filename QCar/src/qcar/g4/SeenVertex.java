package qcar.g4;

import java.awt.geom.Point2D;

import qcar.IQCarNature;
import qcar.ISeenVertex;

public class SeenVertex implements ISeenVertex {
  
  QCar mySelf;

  private QCarNature nature;
  private int vertexId;
  private boolean offersBonus;

  public SeenVertex(int vertexId, QCarNature nature){
    this.nature = nature;
    this.vertexId = vertexId;
  }

  @Override
  public IQCarNature nature() {
    // TODO Auto-generated method stub
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
    // TODO Auto-generated method stub
    return null;
  }

}
