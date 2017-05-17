package qcar.g4;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.BitSet;

import qcar.IDecision;
import qcar.IQCar;
import qcar.IQCarNature;

public class QCar implements IQCar {

  private Point2D[] vertices = new Point2D[4];
  private BitSet bonuses = new BitSet(); //ex: 9 bits: 0-3=vertices, 4-7=sides, 8=parking
  private int score = 0;
  private IQCarNature nature;

  //TODO check if we can pass vertices in the constructor
  public QCar(QCarNature nature, Point2D[] vertices) {
    this.nature = nature;
    this.vertices = vertices;

    bonuses.clear(0, 9);
    
    if(nature.isVertexTarget())
      bonuses.set(0, 4, true);
    if(nature.isSideTarget())
      bonuses.set(4, 8, true);
    if(nature.isParkingTarget())
      bonuses.set(8);
  }
  
  public void update(boolean isAngleMovement, int sideId, double requestedTranslation) {
    double[] vector = new double[2];
    if ((isAngleMovement && (sideId == 0 || sideId == 2)) || (!isAngleMovement && (sideId == 1 || sideId == 3))) {
      vector[0] = vertices[0].getX()-vertices[1].getX();
      vector[1] = vertices[0].getY()-vertices[1].getY();
    } else {
      vector[0] = vertices[3].getX()-vertices[0].getX();
      vector[1] = vertices[3].getY()-vertices[0].getY();
    }
    double unitVecDiv = Math.sqrt(Math.pow(vector[0],2)+Math.pow(vector[1],2)); //
    vector[0] = vector[0] / unitVecDiv * Math.abs(requestedTranslation);        // Transformation en vecteur unitaire puis multiplication par un scalaire
    vector[1] = vector[1] / unitVecDiv * Math.abs(requestedTranslation);        //
    int p1 = sideId;
    int p2 = (sideId+1)%4;
    if (requestedTranslation > 0) {
      vertices[p1] = new Point2D.Double(vertices[p1].getX()+vector[0],vertices[p1].getY()+vector[1]);
      vertices[p2] = new Point2D.Double(vertices[p2].getX()+vector[0],vertices[p2].getY()+vector[1]);
    } else {
      vertices[p1] = new Point2D.Double(vertices[p1].getX()-vector[0],vertices[p1].getY()-vector[1]);
      vertices[p2] = new Point2D.Double(vertices[p2].getX()-vector[0],vertices[p2].getY()-vector[1]);
    }
  }
  
  public void update(IDecision decision) {
    update(decision.isAngleMovement(), decision.sideId(), decision.requestedTranslation()) ;
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
    return !bonuses.isEmpty();
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
    return "QCar n°" + nature().qCarId() + " - (" + score + ")" ;
  }
  
//  public static double distance(Point2D a, Point2D b) {
//    return Math.sqrt(Math.sqrt(Math.abs(b.getX())-Math.abs(a.getX()) + Math.abs(b.getY())-Math.abs(a.getY())));
//  }
  
}
