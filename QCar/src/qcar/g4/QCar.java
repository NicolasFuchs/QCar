package qcar.g4;

import java.awt.geom.Point2D;
import java.util.BitSet;
import qcar.IDecision;
import qcar.IQCar;
import qcar.IQCarNature;

public class QCar implements IQCar {

  private Point2D[] vertices = new Point2D[4];
  private BitSet bonuses = new BitSet(); //ex: 9 bits: 0-3=vertices, 4-7=sides, 8=parking
  private int score = 0;
  private IQCarNature nature;

  /**
   * Constructor used by our gameProvider
   * @param nature nature of the new qcar
   * @param vertices points of the new qcar
   */
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

  /**
   * Consume the bonus from the Vertice if it's not already gone
   *
   * @param vertice the vertice to use
   *
   * @return true if the Vertice still offers point, else false
   */
  public boolean useVertice(int vertice){
    boolean use = bonuses.get(vertice);
    bonuses.clear(vertice);
    return use;
  }

  /**
   * Consume the bonus from the Side if it's not already gone
   *
   * @param side the side to use
   *
   * @return true if the side offers still point, else false
   */
  public boolean useSide(int side){
    boolean use = bonuses.get(side+4);
    bonuses.clear(side+4);
    return use;
  }

  /**
   * Consume the bonus from the parking if it's not already gone
   * @return true if the parking offers still point, else false
   */
  public boolean useParking(){
    boolean use = bonuses.get(8);
    bonuses.clear(8);
    return use;
  }

  /**
   * Apply a movement to a QCar
   * @param isAngleMovement type of movement
   * @param sideId side targeted by the movement
   * @param requestedTranslation movement size
   */
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

  /**
   * Apply a decision to a qcar (not used)
   * @param decision decision to apply
   */
  public void update(IDecision decision) {
    update(decision.isAngleMovement(), decision.sideId(), decision.requestedTranslation()) ;
  }

  /**
   * Increment the QCar score by 1
   */
  public void incrementScore(){
    score++;
  }

  /**
   * PRE: vertexId &lt; 4
   * @param vertexId the id of the vertex
   * @return selected vertex
   */
  @Override
  public Point2D vertex(int vertexId) {
    if(vertexId >= 0 && vertexId < vertices.length)
      return vertices[vertexId];
    else
      throw new IndexOutOfBoundsException("vertexId: "+vertexId+" should be >= 0 but < "+vertices.length);
  }

  /**
   * @return qcar's score
   */
  @Override
  public int score() {
    return score;
  }

  /**
   * @return true if the QCar still has bonuses to offer or if it's static
   */
  @Override
  public boolean isAlive() {
    return !bonuses.isEmpty() || (!nature().isDriven() && !nature().isParkingTarget()
        && !nature().isSideTarget() && !nature().isVertexTarget());
  }

  /**
   * PRE: vertexId &lt; 4
   * @param vertexId the id of the vertex
   * @return true if vertex offers bonus else false
   */
  @Override
  public boolean vertexOffersBonus(int vertexId) {
    if (vertexId >= 0 && vertexId < vertices.length)
      return bonuses.get(vertexId);
    else 
      throw new IndexOutOfBoundsException("vertexId: "+vertexId+" should be >= 0 but < "+vertices.length);
  }

  /**
   * PRE: sideId &lt; 4
   * @param sideId the id of the side
   * @return true if side offers a bonus else false
   */
  @Override
  public boolean sideOffersBonus(int sideId) {
    if (sideId >= 0 && sideId < vertices.length)
      return bonuses.get(sideId+vertices.length);
    else 
      throw new IndexOutOfBoundsException("sideId: "+sideId+" should be >= 0 but < "+vertices.length);
  }

  /**
   * @return true if park offers bonus else false
   */
  @Override
  public boolean parkOffersBonus() {
    return bonuses.get(8);
  }

  /**
   * @return QCar's nature
   */
  @Override
  public IQCarNature nature() {
    return nature;
  }

  /**
   * Generate a string with the qcar id and his current score
   * @return generated string
   */
  @Override
  public String toString() {
    String builder = "Qcar n°" + nature().qCarId();
    if(nature().isParkingTarget())
      builder += " - parking";
    if(!nature().isDriven() && !nature().isParkingTarget()
        && !nature().isSideTarget() && !nature().isVertexTarget())
      builder += " - static";
    builder += " (" + score + ")";
    return builder;
  }

  /**
   * Constructor, create a new QCar from another qcar
   * @param qcar qcar to recreate (coming from a gameDescription)
   */
  public QCar(IQCar qcar){
    for(int i = 0; i < 4; i++)
      vertices[i] = qcar.vertex(i);
    this.nature = qcar.nature();
    this.score = 0;
    bonuses.clear(0, 9);
    if(nature.isVertexTarget())
      bonuses.set(0, 4, true);
    if(nature.isSideTarget())
      bonuses.set(4, 8, true);
    if(nature.isParkingTarget())
      bonuses.set(8);
  }

  /**
   * Test if the qcar's current position respect the maxSideLength
   * @return true if maxsidelength is respected, else false
   */
  public boolean isSideLengthValid(){
    if(vertices[0].distance(vertices[1]) > nature.maxSideLength())
      return false;
    if(vertices[1].distance(vertices[2]) > nature.maxSideLength())
      return false;
    if(vertices[2].distance(vertices[3]) > nature.maxSideLength())
      return false;
    if(vertices[3].distance(vertices[0]) > nature.maxSideLength())
      return false;
    return true;
  }

  /**
   * Test if the current qcar area is bigger than minArea
   * @return true if bigger, else false
   */
  public boolean isMinAreaValid(){
    double area = vertex(0).getX() * vertex(1).getY() - vertex(0).getY() * vertex(1).getX();
    area += vertex(1).getX() * vertex(2).getY() - vertex(1).getY() * vertex(2).getX();
    area += vertex(2).getX() * vertex(3).getY() - vertex(2).getY() * vertex(3).getX();
    area += vertex(3).getX() * vertex(0).getY() - vertex(3).getY() * vertex(0).getX();
    area = area / 2;
    return area > nature.minArea();
  }
  
}
