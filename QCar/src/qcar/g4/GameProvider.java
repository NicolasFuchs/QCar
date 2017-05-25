package qcar.g4;

import java.awt.geom.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IQCar;

public class GameProvider implements IGameProvider {

  public static final int MAX_QCARS = 20;
  public static final int POSITION_DISPERSION = 300;
  public static final Random R = new Random();
  public static final double SPAWN_PROBABILITY = 0.3;
  public static final int PARALLELOGRAM_SCALE = 20;
  
  // to define the minimal side length of first side of QCar
  // SIDE_LENGTH_RATIO * nature.maxSideLength = minimal side length
  // ! Must be 0.0 < SIDE_LENGTH_RATIO < 1.0 !
  public static final double SIDE_LENGTH_RATIO = 0.01;
  
  public static final double MAX_SIDE_LENGTH = 7.4;
  
  // To define the minimal area ratio in function of maximal area given by (MAX_SIDE_LENGTH)^2
  // ! Must be 0.0 < MIN_AREA_RATIO < 1.0 !
  public static final double MIN_AREA_RATIO = 0.5;
  // ! 0.0 < MIN_AREA <= MAX_SIDE_LENGTH * MAX_SIDE_LENGTH !
  public static final double MIN_AREA = (MAX_SIDE_LENGTH * MAX_SIDE_LENGTH) * MIN_AREA_RATIO;
  
  private double max_dispersion = 300;

  private GameDescription.GameStyles game;
  
  // Map used to check the occupation of the current coordinates by a preexistant QCar
  private boolean[][] map;
  
  // Buffer for grid, avoid having a grid with the exact size of a QCar
  private double tol = 0.01 * MAX_SIDE_LENGTH; 
  
  /* Method to create a new GameProvider
   * Initialize the next game with the wanted game style
   * 
   *    @param  : a GameStyles, from GameDescription.GameStyles, the wanted GameStyles to be used
   *              when the function nextGame() is called
   */
  public GameProvider(GameDescription.GameStyles game){
    this.max_dispersion = 300;
    this.game = game;
  }
  
  /* Method to create a new game description
   * Generate randomly distributed QCars in function of wanted game style
   * 
   *    @param  : integer, the number of wanted driven QCars
   *    @return : a non-null new GameDescirption
   */
  @Override
  public IGameDescription nextGame(int nbOfDrivers) {
    
    ArrayList<IQCar> cars = new ArrayList<>();    
    int gridSide = 1;//(int)Math.sqrt(nbOfDrivers);
    map = new boolean[gridSide][gridSide];
    QCarNature.resetIDs();
    int maxCars = (nbOfDrivers>MAX_QCARS) ? nbOfDrivers : MAX_QCARS;
    max_dispersion = maxCars * ((2*MAX_SIDE_LENGTH + 4*tol) * 1.5); 
 
    // Static : new QCarNature(false, false, false, false, MAX_SIDE_LENGTH, MIN_AREA);
    // Driven : new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
    // Parking : new QCarNature(false, true, true, false, MAX_SIDE_LENGTH, MIN_AREA);
    
    switch (game){
      
      case MIXED_WITH_BORDERS: cars = standardStyle(nbOfDrivers); // 0
                               cars.add(obtainBorders());
                               break;
      case ONLY_DRIVERS_WITH_BORDERS: cars = onlyDrivers(nbOfDrivers); // 0
                                      cars.add(obtainBorders());
                                      break;                                                              
      case NO_STATICS_WITH_BORDERS: cars = noStatics(nbOfDrivers); // 1
                                  cars.add(obtainBorders());
                                  break;
      case NO_PARKINGS_WITH_BORDERS: cars = noParkings(nbOfDrivers); // 3
                                     cars.add(obtainBorders());
                                     break;

      case MIXED_WITHOUT_BORDERS: cars = standardStyle(nbOfDrivers); // 4
                                     break;
      case ONLY_DRIVERS_WITHOUT_BORDERS: cars = onlyDrivers(nbOfDrivers); // 0
                                         cars.add(obtainBorders());
                                         break;                                 
      case NO_STATICS_WITHOUT_BORDERS: cars = noStatics(nbOfDrivers); // 5
                                       break;
      case NO_PARKINGS_WITHOUT_BORDERS: cars = noParkings(nbOfDrivers); // 7
                                        break;

      default: break;    
    }
    
    return new GameDescription(cars);
  }
    
  /* Method to create a list of new randomly distributed QCars containing only driven QCars
   * 
   * PRECONDITION : to have called nextGame() at least one time
   * 
   *    @param  : integer, the number of wanted driven QCars
   *    @return : ArrayList<IQCar>, list of wanted QCars
   */
  private ArrayList<IQCar> onlyDrivers(int nbOfDrivers){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature driven;
    Point2D[] vertices ;
    
    max_dispersion = nbOfDrivers * ((2*MAX_SIDE_LENGTH + 4*tol) * 1.5);
    QCar car;
    
    for(int i = 0; i<nbOfDrivers; i++){
      driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(driven); 
      allocateVertices(vertices);
      car = new QCar(driven, vertices);
      cars.add(car);
    }

    return cars;
  }

  /* Method to create a list of new randomly distributed QCars with only driven and statics QCars
   * 
   * Return a list containing the specified number of driven QCars nbOfDrivers, and :
   * if nbOfDrivers<MAX_QCARS : "MAX_QCARS-number of driven QCars" static QCars 
   * if nbOfDrivers>MAX_QCARS : "nbOfDrivers/2" (rounded at lowest integer) static QCars
   * 
   * PRECONDITION : to have called nextGame() at least one time
   * 
   *    @param  : integer, the number of wanted driven QCars
   *    @return : ArrayList<IQCar>, list of wanted QCars with a minimum of MAX_QCARS QCars
   */
  private ArrayList<IQCar> noParkings(int nbOfDrivers){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature driven, staticNature;
    QCar car;
    Point2D[] vertices;
   
    int wantedQCars = (nbOfDrivers>MAX_QCARS) ? ((int)(nbOfDrivers*1.5)) : MAX_QCARS;    
    max_dispersion = wantedQCars * ((2*MAX_SIDE_LENGTH + 4*tol) * 1.5); 
    
    for(int i = 0; i<nbOfDrivers; i++){
      driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(driven); 
      allocateVertices(vertices);
      car = new QCar(driven, vertices);
      cars.add(car);
    }
    
    for(int i = cars.size(); i<wantedQCars; i++){
      staticNature = new QCarNature(false, false, false, false, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(staticNature); 
      allocateVertices(vertices);
      car = new QCar(staticNature, vertices);
      cars.add(car);
    }
    
    return cars;
  }
    
  /* Method to create a list of new randomly distributed QCars with only driven and parking QCars
   * 
   * Return a list containing the specified number of driven QCars nbOfDrivers, and :
   * if nbOfDrivers<MAX_QCARS : "MAX_QCARS-number of driven QCars" parking QCars 
   * if nbOfDrivers>MAX_QCARS : "nbOfDrivers/2" (rounded at lowest integer) parking QCars
   * 
   * PRECONDITION : to have called nextGame() at least one time
   * 
   *    @param  : integer, the number of wanted driven QCars
   *    @return : ArrayList<IQCar>, list of wanted QCars with a minimum of MAX_QCARS QCars
   */
  private ArrayList<IQCar> noStatics(int nbOfDrivers){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature driven, parking;

    Point2D[] vertices ;
    
    int wantedQCars = (nbOfDrivers>MAX_QCARS) ? ((int)(nbOfDrivers*1.5)) : MAX_QCARS;    
    max_dispersion = wantedQCars * ((2*MAX_SIDE_LENGTH + 4*tol) * 1.5); 
    QCar car;
    
    for(int i = 0; i<nbOfDrivers; i++){
      driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(driven); 
      allocateVertices(vertices);
      car = new QCar(driven, vertices);
      cars.add(car);
    }

    for(int i = cars.size(); i<wantedQCars; i++){
      parking = new QCarNature(false, true, true, false, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(parking); 
      allocateVertices(vertices);
      car = new QCar(parking, vertices);
      cars.add(car);
    }
    
    return cars;
  }

  /* Method to create a list of new randomly distributed QCars of mixed natures
   * 
   * Return a list containing the specified number of driven QCars nbOfDrivers, and :
   * if nbOfDrivers<MAX_QCARS : "MAX_QCARS-number of driven QCars" non-driven QCars 
   * if nbOfDrivers>MAX_QCARS : "nbOfDrivers/2" (rounded at lowest integer) non-driven QCars
   * Thoses non-driven QCars have randomly distributed nature chosen as either parking, either
   * static
   * 
   * PRECONDITION : to have called nextGame() at least one time
   * 
   *    @param  : integer, the number of wanted driven QCars
   *    @return : ArrayList<IQCar>, list of wanted QCars with a minimum of MAX_QCARS QCars
   */
  private ArrayList<IQCar> standardStyle(int nbOfDrivers){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature randomNature, driven;
    
    Point2D[] vertices ;
    QCar car;
   
    int wantedQCars = (nbOfDrivers>MAX_QCARS) ? ((int)(nbOfDrivers*1.5)) : MAX_QCARS;    
    max_dispersion = wantedQCars * ((2*MAX_SIDE_LENGTH + 4*tol) * 1.5); 
    
    for(int i = 0; i<nbOfDrivers; i++){
      
      driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(driven); 
      allocateVertices(vertices);
      car = new QCar(driven, vertices);
      cars.add(car);
    }
    for(int i = cars.size(); i<wantedQCars; i++){
      if(R.nextBoolean()) randomNature = new QCarNature(false, true, true, false, MAX_SIDE_LENGTH, MIN_AREA);
      else randomNature = new QCarNature(false, false, false, false, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(randomNature); 
      allocateVertices(vertices);
      car = new QCar(randomNature, vertices);
      cars.add(car);
    }
    return cars;  
  }
  
  /* Method to create a list containing a single driven QCar
   * 
   * PRECONDITION : to have called nextGame() at least one time
   * 
   *    @param  : -
   *    @return : ArrayList<IQCar>, list of wanted containing a single QCar
   */ 
  private ArrayList<IQCar> debugStyle(){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature nature = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
    Point2D[] vertices = randomAlignedPositions(nature); 
    allocateVertices(vertices);
    QCar car = new QCar(nature, vertices);
    cars.add(car);
    return cars;
  }
  
  /* Method to randomly generate the vertices coordinates of a valid QCar given his nature
   * 
   * Method generate coordinates where the QCar cannot be superposed with others
   * preexistant QCars
   * Use the 2-dimensional array of boolean "map" to check and validate coordinates
   * Coordinates are represented with Point2D.Double with non-negativ double coordinates
   * 
   * PRECONDITION : to have called nextGame() at least one time
   * 
   *    @param  : QCarNature, the nature of the QCar
   *    @return : Point2D.Double array, the coordinates of the vertices of the QCar
   */ 
  private Point2D[] randomAlignedPositions(QCarNature nature) {

    System.out.println("ENTER in randomAlignedPosition...");
    Point2D[] points = new Point2D[4];
    do{
      Point2D[] tmp = new Point2D[4];
      
      // Take random value for the position of the vertice 0 of the QCar
      double posX = R.nextDouble()*max_dispersion;
      double posY = R.nextDouble()*max_dispersion;
      points[0] = new Point2D.Double(posX, posY);

      // Calculate a minimal length of first side of QCar to avoid side == 0.0 case
      double minSideLength = SIDE_LENGTH_RATIO * nature.maxSideLength();
      double minH = nature.maxSideLength();
      double side1 = minSideLength;
      
      do{
        // Calculate a random value for the base side
        side1 = minSideLength + R.nextDouble()*(nature.maxSideLength()-minSideLength); 

        // Minimal height of parallelogram to respect minArea
        minH = nature.minArea()/side1;

      } while (minH>=nature.maxSideLength());

      // Calculate random second side of parallelogram in respect of maxSideLength and minArea
      double side2 = minH + R.nextDouble() * Math.abs((nature.maxSideLength()-minH));

      // Calculate the maximum possible offset to respect minArea
      double maxOffset = Math.sqrt(Math.abs(side2*side2 - minH * minH)); 

      // Choose a random offset within given parameters
      double offset = R.nextDouble() * maxOffset;

      // Calculate real height of parallelogram
      double H = Math.sqrt(Math.abs(side2*side2 - offset*offset));

      // Set offset orientation (true: offset is horizontal, false: offset is vertical)
      boolean offsetHorizontal = R.nextBoolean();

      // Set direction of offset
      // offsetUp: for use when offset orientation is vertical
      // true: orientation of offset is vertical upward, false: orientation of offset is vertical downward
      // offsetRight: for use when offset orientation is horizontal
      // true: orientation of offset is to the right, false: orientation of offset is to the left
      boolean offsetUp = R.nextBoolean(), offsetRight = R.nextBoolean();

      // Create parallelogram
      if(offsetHorizontal){
        points[1] = new Point2D.Double(posX+side1, posY);
        if(offsetRight){
          points[2] = new Point2D.Double(posX+side1+offset, posY+H);
          points[3] = new Point2D.Double(posX+offset, posY+H);
        }
        else{
          points[2] = new Point2D.Double((posX+side1)-offset, posY+H);
          points[3] = new Point2D.Double(posX-offset, posY+H);
        }
      }
      else{
        points[3] = new Point2D.Double(posX, posY+side1);
        if(offsetUp){
          points[1] = new Point2D.Double(posX+H, posY+offset);
          points[2] = new Point2D.Double(posX+H, posY+side1+offset);
        }
        else{
          points[1] = new Point2D.Double(posX+H, posY-offset);
          points[2] = new Point2D.Double(posX+H, (posY+side1)-offset);
        }
      }
      
      // Find the center of the current parallelogram, which is the center of rotation
      double dX = Math.abs(points[2].getX()-points[0].getX());
      double dY = Math.abs(points[2].getY()-points[0].getY());
      dX = dX/((double)2); dY = dY/((double)2);
      double rotationCenterX = points[0].getX()+dX;
      double rotationCenterY = points[0].getY()+dY;
      
      // Define a random angle of rotation 0.0 >= angle > 2*PI
      double angle = R.nextDouble()*2*Math.PI;
      
      // Create a rotation matrix and apply rotation
      AffineTransform rotation = new AffineTransform();
      rotation.rotate(angle, rotationCenterX, rotationCenterY);
      rotation.transform(points, 0, tmp, 0, points.length);
      points = tmp;

      // Check if the parallelogram vertices coordinates are negativ or not
      boolean hasNegativXCoordinates = false;
      boolean hasNegativYCoordinates = false;
      double negativXOffset = 0.0, negativYOffset = 0.0;
      double X, Y, absolut;

      for(Point2D pt: points){
        X = pt.getX(); Y = pt.getY();
        if(X<0) {
          hasNegativXCoordinates = true;
          absolut = Math.abs(X);
          if(absolut>negativXOffset) negativXOffset = absolut;
        }
        if(Y<0){
          hasNegativYCoordinates = true;
          absolut = Math.abs(Y);
          if(absolut>negativXOffset) negativYOffset = absolut;
        }
      }
      
      // If the parallelogram has negativ coordinates, make a translation to have
      // only positiv coordinates
      if(hasNegativXCoordinates || hasNegativYCoordinates){
        tmp = new Point2D[4];
        AffineTransform translation = new AffineTransform();
        translation.translate(negativXOffset, negativYOffset);
        translation.transform(points, 0, tmp, 0, points.length);
        points = tmp;
      }

    }while(!checkEmplacementOnArena(points));

    System.out.println(" -- EXIT --");
    
    return points;
  }  
  
  /* Method check occupation of the wanted emplacement for this QCar using "map" array
   * 
   * The game map is divided in an grid of square with side length equals to "maxSideLength+2*tol"
   * of a QCar
   * The occupation of thoses squares is represented by a 2-dimensional array of boolean where the
   * position is set to "true" if the emplacement is occupied
   * 
   * PRECONDITION : to have called nextGame() at least one time
   * 
   *    @param  : Point2D.double array, representing the 4 vertices of a QCar
   *    @return : boolean, true is the emplacement of this QCar is occupied
   */                 
  private boolean checkEmplacementOnArena(Point2D[] vertices){
    
    double side = MAX_SIDE_LENGTH + 2*tol;
    double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
    double ptX = 0, ptY = 0;
    for(Point2D pt : vertices){
      ptX = pt.getX(); ptY = pt.getY();
      if(ptX<minX) minX = ptX;
      if(ptX>maxX) maxX = ptX;
      if(ptY<minY) minY = ptY;
      if(ptY>maxY) maxY = ptY;
    }
    int beginX = (int)(minX/side), endX = (int)(maxX/side);
    int beginY = (int)(minY/side), endY = (int)(maxY/side);
    for(int x = beginX; x<=(endX+1); x++){
      for(int y = beginY; y<=(endY+1); y++){
        if(x>= map.length || y >= map[0].length) map = augmentMapSize(x, y);
        if(map[x][y]) return false;
      }
    }
    
    return true;
  }
  
  /* Dynamic augmentation of game map grid, in function of emplacement of checked area of grid
   * and returning the new game map with all previous values of occupied emplacements
   * 
   * PRECONDITION : to have called nextGame at least once before
   * 
   *    @param  : integer: coordinate x of the current checked area
   *              integre: coordinate y of the current checked area
   *    @return : a new 2-dimensional array of booleans
   */
  private boolean[][] augmentMapSize(int occupX, int occupY){
    boolean[][] newMap;
    if(occupX >= map.length && occupY >= map[0].length)
      newMap = new boolean[occupX+1][occupY+1];
    else if(occupX >= map.length && occupY < map[0].length)
      newMap = new boolean[occupX+1][map[0].length];
    else
      newMap = new boolean[map.length][occupY+1];
    for(int x = 0; x<map.length; x++){
      for(int y = 0; y<map[x].length; y++) newMap[x][y] = map[x][y]; 
    }
    return newMap;
  }
  
  /* Return a QCar with the coordinates of each corners of the map that contains all QCars
   * 
   * PRECONDITION : to have called nextGame at least once before
   * 
   *    @param  : -
   *    @return : a single Qcar
   */
  private QCar obtainBorders(){
    
    int side = Math.max(map.length, map[0].length);
    double sideX = map.length * (MAX_SIDE_LENGTH + tol *2);
    double sideY = map[0].length * (MAX_SIDE_LENGTH + tol *2);
    double maxSideLength = side * (MAX_SIDE_LENGTH + tol *2);
    QCarNature nature = new QCarNature(false, false, false, false, maxSideLength, sideX * sideY);

    Point2D[] borderVertices = {
        new Point2D.Double(0, 0),
        new Point2D.Double(sideX, 0),
        new Point2D.Double(sideX, sideY),
        new Point2D.Double(0, sideY)
    };
    
    return new QCar(nature, borderVertices);
  }
  
  /* Update the map in function of the coordinates of the vertices given in parameters
   * 
   * PRECONDITION : to have called nextGame at least once before
   * 
   *    @param  : array of Point2D.double
   *    @return : -
   */
  private void allocateVertices(Point2D[] vertices){
    
    double side = MAX_SIDE_LENGTH + 2*tol;
    double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
    double ptX = 0, ptY = 0;
    for(Point2D pt : vertices){
      ptX = pt.getX(); ptY = pt.getY();
      if(ptX<minX) minX = ptX;
      if(ptX>maxX) maxX = ptX;
      if(ptY<minY) minY = ptY;
      if(ptY>maxY) maxY = ptY;
    }
    int beginX = (int)(minX/side), endX = (int)(maxX/side);
    int beginY = (int)(minY/side), endY = (int)(maxY/side);
    for(int x = beginX; x<=(endX+1); x++){
      for(int y = beginY; y<=(endY+1); y++){
        map[x][y] = true;
      }
    }
  }
}



  