package qcar.g4;

import java.awt.geom.Point2D;

import qcar.IQCar;
import qcar.IQCarNature;

public class QCar implements IQCar {
  

  private Point2D[] vertex ;
  private int score ;
  private boolean isAlive ;
  private boolean[] vertexOffersBonus ;
  private boolean[] sieOffersBonus ;
  private boolean parkOffersBonus ;  
  private QCarNature nature ;
  
  /**
   * @param vertex, array of vertexes the 4 edges of the QCar
   * @param score, the current score of the QCar
   * @param isAlive, true if the QCar is alive
   * @param vertexOffersBonus, array of booleans, true if the concerned vertex offers bonus
   * @param sieOffersBonus, array of booleans, true if the concerned side offers bonus
   * @param parkOffersBonus, true if park offers bonus
   * @param nature, the nature of the QCar
   */
  public QCar(Point2D[] vertex, int score, boolean isAlive, boolean[] vertexOffersBonus,
      boolean[] sieOffersBonus, boolean parkOffersBonus, QCarNature nature) {
    this.vertex = vertex;
    this.score = score;
    this.isAlive = isAlive;
    this.vertexOffersBonus = vertexOffersBonus;
    this.sieOffersBonus = sieOffersBonus;
    this.parkOffersBonus = parkOffersBonus;
    this.nature = nature;
  }
  

  @Override
  public Point2D vertex(int vertexId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int score() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean isAlive() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean vertexOffersBonus(int vertexId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean sideOffersBonus(int sideId) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean parkOffersBonus() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public IQCarNature nature() {
    // TODO Auto-generated method stub
    return null;
  }

}
