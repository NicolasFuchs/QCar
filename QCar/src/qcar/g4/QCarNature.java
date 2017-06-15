package qcar.g4;

import qcar.IQCarNature;

/**
 * Describes the nature of each QCar
 */
public class QCarNature implements IQCarNature {

  private static int ID_COUNTER = 0;
  
  private int id;
  private boolean driven;
  private boolean parkingTarget;
  private boolean vertexTarget;
  private boolean sideTarget;
  private double maxSideLenght;
  private double minArea;
  
  /**
   * Constructor of the nature
   * @param driven if the QCar is driven
   * @param parkingTarget if the QCar is a parking
   * @param vertexTarget if the QCar's vertices give points
   * @param sideTarget if the QCar's sides give points
   * @param maxSideLenght the QCar's max side length
   * @param minArea the QCar's min area
   */
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

  /**
   * @return id of the QCar
   */
  @Override
  public int qCarId() {
    return id;
  }

  /**
   * @return if the QCar is driven or not
   */
  @Override
  public boolean isDriven() {
    return driven;
  }

  /**
   * @return the maximal length of the sides of QCar
   */
  @Override
  public double maxSideLength() {
    return maxSideLenght;
  }

  /**
   * @return the minimal area of the QCar
   */
  @Override
  public double minArea() {
    return minArea;
  }

  /**
   * @return if the QCar is a parking or not
   */
  @Override
  public boolean isParkingTarget() {
    return parkingTarget;
  }

  /**
   * @return if the QCar will give bonuses on the vertices
   */
  @Override
  public boolean isVertexTarget() {
    return vertexTarget;
  }

  /**
   * @return if the QCar will give bonuses on the sides
   */
  @Override
  public boolean isSideTarget() {
    return sideTarget;
  }
  
  /**
   * print the information about the QCar
   */
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
  
  /**
   * sets the ID counter back to zero
   */
  public static void resetIDs(){
    ID_COUNTER=0;
  }
}