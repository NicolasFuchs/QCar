package qcar.g4;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IQCar;

public class GameProvider implements IGameProvider {

  public static final int MAX_QCARS = 5;
  public static final Random R = new Random();
  public static final double SPAWN_PROBABILITY = 0.3;
  public static final int PARALLELOGRAM_SCALE = 20;
  
  public static final double MIN_AREA = 1.3;
  public static final double MAX_SIDE_LENGTH = 7.4;

  private GameDescription.GameStyles game;
  private boolean[][] map;
  private double tol = 0.01 * MAX_SIDE_LENGTH; // Buffer for grid, avoid having a grid with the exact size of a QCar
  
  public GameProvider(GameDescription.GameStyles game){
    
    this.game = game;
  }
  
  @Override
  public IGameDescription nextGame(int nbOfDrivers) {
    
    ArrayList<IQCar> cars = new ArrayList<>();    
    int gridSide = (int)Math.sqrt(nbOfDrivers);
    map = new boolean[gridSide][gridSide];
    QCarNature.resetIDs();
    
    switch (game){
      
      case STANDARD_WITH_BORDERS: cars = standardStyle(nbOfDrivers); // 0
                                  cars.add(obtainBorders());
                                  break;
      case PARKINGS_WITH_BORDERS: cars = parkingsStyle(nbOfDrivers, 2); // 1
                                  cars.add(obtainBorders());
                                  break;
      case DEBUG_WITH_BORDERS: cars = debugStyle(); // 2
                               cars.add(obtainBorders());
                               break;
      case NO_PARKINGS_WITH_BORDERS: cars = noParkings(nbOfDrivers); // 3
                                     cars.add(obtainBorders());
                                     break;

      case STANDARD_WITHOUT_BORDERS: cars = standardStyle(nbOfDrivers); // 4
                                     break;
      case PARKINGS_WITHOUT_BORDERS: cars = parkingsStyle(nbOfDrivers, 2); // 5
                                     break;
      case DEBUG_WITHOUT_BORDERS: cars = debugStyle(); // 6
                                  break;
      case NO_PARKINGS_WITHOUT_BORDERS: cars = noParkings(nbOfDrivers); // 7
                                        break;

      default: break;    
    }
    
    return new GameDescription(cars);
  }
  
  private ArrayList<IQCar> noParkings(int nbOfDrivers){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature driven;
    QCar car;
    Point2D[] vertices;
    
    for(int i = 0; i<nbOfDrivers; i++){
      driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(driven); 
      allocateVertices(vertices);
      car = new QCar(driven, vertices);
      cars.add(car);
    }
    
    return cars;
  }
  
  private ArrayList<IQCar> parkingsStyle(int nbOfDrivers, int multiplier){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature parking, driven;
    
    Point2D[] vertices ;
    int parkings = nbOfDrivers * multiplier;
    int totalCars = nbOfDrivers + parkings;
    QCar car;
    
    for(int i = 0; i<nbOfDrivers; i++){
      driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(driven); 
      allocateVertices(vertices);
      car = new QCar(driven, vertices);
      cars.add(car);
    }
    for(int i = nbOfDrivers; i<totalCars; i++){
      parking = new QCarNature(false, true, true, false, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(parking); 
      allocateVertices(vertices);
      car = new QCar(parking, vertices);
      cars.add(car);
    }
    
    return cars;
  }
  
  private ArrayList<IQCar> standardStyle(int nbOfDrivers){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature parking, driven;
    
    Point2D[] vertices ;
    QCar car;
   
    int wantedQCars = (nbOfDrivers>MAX_QCARS) ? MAX_QCARS : nbOfDrivers;
    
    for(int i = 0; i<wantedQCars; i++){
      
      driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(driven); 
      allocateVertices(vertices);
      car = new QCar(driven, vertices);
      cars.add(car);
    }
    for(int i = wantedQCars; i<MAX_QCARS; i++){
      parking = new QCarNature(false, true, true, false, MAX_SIDE_LENGTH, MIN_AREA);
      vertices = randomAlignedPositions(parking); 
      allocateVertices(vertices);
      car = new QCar(parking, vertices);
      cars.add(car);
    }
    return cars;  
  }
  
  private ArrayList<IQCar> debugStyle(){
    
    ArrayList<IQCar> cars = new ArrayList<>();
    QCarNature nature = new QCarNature(true, false, true, true, MAX_SIDE_LENGTH, MIN_AREA);
    Point2D[] vertices = randomAlignedPositions(nature); 
    allocateVertices(vertices);
    QCar car = new QCar(nature, vertices);
    cars.add(car);
    return cars;
  }
  
  // Used for tests only
  private QCarNature randomNature (int currDrivers, int wantedDrivers) {
    
    boolean driven = currDrivers < wantedDrivers ? true : false;
    boolean parkingTarget = driven ? false : R.nextBoolean();
    boolean vertexTarget = R.nextBoolean();
    boolean sideTarget = R.nextBoolean();
    double maxSideLenght = MAX_SIDE_LENGTH;
    double minArea = MIN_AREA;
    
    QCarNature nature = new QCarNature(driven, parkingTarget, vertexTarget, sideTarget, maxSideLenght, minArea);
    
    return nature;
  }
  
  private Point2D[] randomAlignedPositions(QCarNature nature) {
    
    Point2D[] points = new Point2D[4];
    do{
      double verticalSideLenght = R.nextDouble()*PARALLELOGRAM_SCALE;
      verticalSideLenght = verticalSideLenght > nature.maxSideLength() ? verticalSideLenght % nature.maxSideLength() : verticalSideLenght;

      double horizontalSideLenght = R.nextDouble()*PARALLELOGRAM_SCALE;
      horizontalSideLenght = horizontalSideLenght > nature.maxSideLength() ? horizontalSideLenght % nature.maxSideLength() : horizontalSideLenght;

      boolean offsetVertical = R.nextBoolean();
      int positionFactor = R.nextInt(500);
      double offset = R.nextDouble()*(offsetVertical ? verticalSideLenght : horizontalSideLenght);

      double posX = R.nextDouble()*positionFactor;
      double posY = R.nextDouble()*positionFactor;
      points[1] = new Point2D.Double(posX, posY);

      if (offsetVertical) {
        posY = posY-verticalSideLenght;
        points[0] = new Point2D.Double(posX, posY);
        posY = posY - offset;
        posX = posX - Math.sqrt(Math.abs((horizontalSideLenght*horizontalSideLenght)-(offset*offset)));
        points[3] = new Point2D.Double(posX, posY);
        posY = posY + verticalSideLenght;
        points[2] = new Point2D.Double(posX, posY);
      } else {
        posX = posX-horizontalSideLenght;
        points[2] = new Point2D.Double(posX, posY);
        posX = posX - offset;
        posY = posY - Math.sqrt(Math.abs((verticalSideLenght*verticalSideLenght)-(offset*offset)));
        points[3] = new Point2D.Double(posX, posY);
        posX = posX + horizontalSideLenght;
        points[0] = new Point2D.Double(posX, posY);
      }
    }while(!checkEmplacementOnArena(points));

    return points;
  }
  
  // check occupation of this position
  // occupationMap = game map divided in an grid of square with side length equals to the max side length of a QCar
  //                 + buffer (tolerance on one side)
  private boolean checkEmplacementOnArena(Point2D[] vertices){
    
    double side = MAX_SIDE_LENGTH + 2*tol;
    for(int i = 0; i<vertices.length; i++){
      double x=vertices[i].getX(), y = vertices[i].getY();
      int occupX = (int)(x/side), occupY = (int)(y/side);
      if(occupX>= map.length || occupY >= map[0].length) map = augmentMapSize(occupX, occupY);
      if(map[occupX][occupY]) return false;
    }
    return true;
  }
  
  // Dynamic augmentation of game map grid, in function of emplacement of checked area of grid
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
  
  // Return a QCar with the coordinates of each corners of the map that contains all QCars
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
  
  // Update the map in function of the coordinates of the vertices
  private void allocateVertices(Point2D[] vertices){
    double x, y, side;
    int boolX, boolY;
    
    for(int i=0; i<vertices.length; i++){
      x = vertices[i].getX(); y = vertices[i].getY();
      side = (MAX_SIDE_LENGTH + tol *2);
      boolX = (int)(x/side); boolY = (int)(y/side);
      map[boolX][boolY] = true;
    }
  }
  
  private void printQCarsList(ArrayList<IQCar> cars){
    System.out.println("QCars List : ---------------");
    for(IQCar car: cars){
      System.out.println(car.toString());
      System.out.println(car.nature().toString());
      System.out.println();
    }
    System.out.println("----------------------------");
  }
  
}

  