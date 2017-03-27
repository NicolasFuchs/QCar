package qcar.g4;

import java.awt.geom.Point2D;
import qcar.IQCar;
import qcar.IQCarNature;

public class QCar implements IQCar {

  private Point2D[] vertex ;
  private int score ;
  private boolean isAlive ;
  private boolean[] vertexOffersBonus ;
  private boolean[] sideOffersBonus ;
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
      boolean[] sideOffersBonus, boolean parkOffersBonus, QCarNature nature) {
    this.vertex = vertex;
    this.score = score;
    this.isAlive = isAlive;
    this.vertexOffersBonus = vertexOffersBonus;
    this.sideOffersBonus = sideOffersBonus;
    this.parkOffersBonus = parkOffersBonus;
    this.nature = nature;
  }
  

  @Override
  public Point2D vertex(int vertexId) {
    return vertex[vertexId];
  }

  @Override
  public int score() {
    return score;
  }

  @Override
  public boolean isAlive() {
    return this.isAlive;
  }

  @Override
  public boolean vertexOffersBonus(int vertexId) {
    return this.vertexOffersBonus[vertexId];
  }

  @Override
  public boolean sideOffersBonus(int sideId) {
    return this.sideOffersBonus[sideId];
  }

  @Override
  public boolean parkOffersBonus() {
    return this.parkOffersBonus;
  }

  @Override
  public IQCarNature nature() {
    return this.nature;
  }

}
