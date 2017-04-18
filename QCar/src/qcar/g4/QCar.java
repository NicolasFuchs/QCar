package qcar.g4;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.BitSet;

import qcar.IQCar;
import qcar.IQCarNature;

public class QCar implements IQCar {

  private Point2D[] vertices = new Point2D[4];
  private BitSet bonuses = new BitSet(vertices.length*2+1); //ex: 9 bits: 0-3=vertices, 4-7=sides, 8=parking
  private int score = 0;
  private IQCarNature nature;

  //TODO check if we can pass vertices in the constructor
  public QCar(QCarNature nature, Point2D[] vertices) {
    this.nature = nature;
    this.vertices = vertices;
    if(nature.isParkingTarget()){
      bonuses.set(0, 4, true);
      bonuses.set(8);
      bonuses.set(4, 8, false);
    }
    else{
      bonuses.set(0, 8, true);
      bonuses.clear(8);
    }
  }
  
  @Override
  public Point2D vertex(int vertexId) {
    if(vertexId >= 0 && vertexId < vertices.length)
      return vertices[vertexId];
    else
      throw new IndexOutOfBoundsException("vertexId: "+vertexId+" should be >= 0 but < "+vertices.length);
  }

  @Override
  public int score() {
    return score;
  }

  @Override
  public boolean isAlive() {
    return bonuses.isEmpty();
  }

  @Override
  public boolean vertexOffersBonus(int vertexId) {
    if (vertexId >= 0 && vertexId < vertices.length)
      return bonuses.get(vertexId);
    else 
      throw new IndexOutOfBoundsException("vertexId: "+vertexId+" should be >= 0 but < "+vertices.length);
  }

  @Override
  public boolean sideOffersBonus(int sideId) {
    if (sideId >= 0 && sideId < vertices.length)
      return bonuses.get(sideId+vertices.length);
    else 
      throw new IndexOutOfBoundsException("sideId: "+sideId+" should be >= 0 but < "+vertices.length);
  }

  @Override
  public boolean parkOffersBonus() {
    return bonuses.get(vertices.length);
  }

  @Override
  public IQCarNature nature() {
    return nature;
  }

  @Override
  public String toString() {
    StringBuilder bld = new StringBuilder();
    
    if (vertices[0].getX() == vertices[1].getX()) { //offsetVertical
      bld.append("          -----*\n");
      bld.append("     -----     |\n");
      bld.append("*-----         |\n");
      bld.append("|              |\n");
      bld.append("|              |\n");
      bld.append("|         -----*\n");
      bld.append("|    -----      \n");
      bld.append("*-----           \n\n\n");
    } else {
      bld.append("*--------------*\n");
      bld.append(" |              |\n");
      bld.append("   |              |\n");
      bld.append("     |              |\n");
      bld.append("      *--------------*\n");
    }
    bld.append(Arrays.toString(vertices)+"\n");
    bld.append(nature);
    bld.append("\n\n");
    
    return bld.toString();
  }
  
//  public static double distance(Point2D a, Point2D b) {
//    return Math.sqrt(Math.sqrt(Math.abs(b.getX())-Math.abs(a.getX()) + Math.abs(b.getY())-Math.abs(a.getY())));
//  }
  
}
