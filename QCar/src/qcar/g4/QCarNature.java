package qcar.g4;

import qcar.IQCarNature;

public class QCarNature implements IQCarNature{
  
  private int qCarId ;
  private boolean isDriven ;
  private double maxSideLength ;
  private double minArea ;
  private boolean isParkingTarget ;
  private boolean isVertexTarget ;
  private boolean isSideTarget ;  

  /**
   * @param qCarId, the Id of the QCar
   * @param isDriven, true if the QCar is driven
   * @param maxSideLength, the maximum sideLength allowed for that nature
   * @param minArea, the minimum area allowed for that nature
   * @param isParkingTarget, true if this nature gives points for parking
   * @param isVertexTarget, true if this nature gives points for touching his vertexes
   * @param isSideTarget, true if this nature gives points for touching his sides
   */
  public QCarNature(int qCarId, boolean isDriven, double maxSideLength, double minArea,
      boolean isParkingTarget, boolean isVertexTarget, boolean isSideTarget) {
    this.qCarId = qCarId;
    this.isDriven = isDriven;
    this.maxSideLength = maxSideLength;
    this.minArea = minArea;
    this.isParkingTarget = isParkingTarget;
    this.isVertexTarget = isVertexTarget;
    this.isSideTarget = isSideTarget;
  }
  
  @Override
  public int qCarId() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isDriven() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public double maxSideLength() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double minArea() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isParkingTarget() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isVertexTarget() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isSideTarget() {
    // TODO Auto-generated method stub
    return false;
  }

}
