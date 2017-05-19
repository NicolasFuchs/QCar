package qcar.g4;

import qcar.IQCarNature;

public class QCarNature implements IQCarNature {

  private static int ID_COUNTER = 0;
  
  private int id;
  private boolean driven;
  private boolean parkingTarget;
  private boolean vertexTarget;
  private boolean sideTarget;
  private double maxSideLenght;
  private double minArea;
  
  public QCarNature(boolean driven, boolean parkingTarget, boolean vertexTarget, boolean sideTarget, double maxSideLenght, double minArea) {
    super();
    this.id = ID_COUNTER++;
    this.driven = driven;
    this.parkingTarget = parkingTarget;
    this.vertexTarget = vertexTarget;
    this.sideTarget = sideTarget;
    this.maxSideLenght = maxSideLenght;
    this.minArea = minArea;
  }

  @Override
  public int qCarId() {
    return id;
  }

  @Override
  public boolean isDriven() {
    return driven;
  }

  @Override
  public double maxSideLength() {
    return maxSideLenght;
  }

  @Override
  public double minArea() {
    return minArea;
  }

  @Override
  public boolean isParkingTarget() {
    return parkingTarget;
  }

  @Override
  public boolean isVertexTarget() {
    return vertexTarget;
  }

  @Override
  public boolean isSideTarget() {
    return sideTarget;
  }
  
  @Override
  public String toString() {
    StringBuilder bld = new StringBuilder();
    
    bld.append("| ------- Nature ------- \n");
    bld.append("| id: "+id+"\n");
    bld.append("| driven: "+driven+"\n");
    bld.append("| parkingTarget: "+parkingTarget+"\n");
    bld.append("| vertexTarget: "+vertexTarget+"\n");
    bld.append("| sideTarget: "+sideTarget+"\n");
    bld.append("| maxSideLenght: "+maxSideLenght+"\n");
    bld.append("| minArea: "+minArea+"\n");
    
    return bld.toString();
  }
  
  public static void resetIDs(){
    ID_COUNTER=0;
  }
}