package qcar.g4;

import qcar.IQCarNature;

public class QCarNature implements IQCarNature{
  
  private int qCarId;
  private boolean isDriven;
  private double maxSideLength;
  private double minArea;
  private boolean isParkingTarget;
  private boolean isVertexTarget;
  private boolean isSideTarget;  

  /**
   * @param qCarId, the Id of the QCar
   * @param isDriven, true if the QCar is driven
   * @param maxSideLength, the maximum sideLength allowed for that nature
   * @param minArea, the minimum area allowed for that nature
   * @param isParkingTarget, true if this nature gives points for parking
   * @param isVertexTarget, true if this nature gives points for touching his vertexes
   * @param isSideTarget, true if this nature gives points for touching his sides
   */
  public QCarNature(int qCarId, boolean isDriven, double maxSideLength, double minArea, boolean isParkingTarget, boolean isVertexTarget, boolean isSideTarget) {
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
    return this.qCarId;
  }

  @Override
  public boolean isDriven() {
    return this.isDriven;
  }

  @Override
  public double maxSideLength() {
    return this.maxSideLength;
  }

  @Override
  public double minArea() {
    return minArea;
  }

  @Override
  public boolean isParkingTarget() {
    return this.isParkingTarget;
  }

  @Override
  public boolean isVertexTarget() {
    return this.isVertexTarget;
  }

  @Override
  public boolean isSideTarget() {
    return this.isSideTarget;
  }

}
