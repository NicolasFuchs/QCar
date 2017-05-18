package qcar.g4;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IQCar;

public class GameProvider implements IGameProvider {

  public static final int MAX_QCARS = 256;
  public static final Random R = new Random();
  public static final double SPAWN_PROBABILITY = 0.3;
  public static final int PARALLELOGRAM_SCALE = 20;
  
  public static final double MIN_AREA = 1.3;
  public static final double MAX_SIDE_LENGHT = 7.4;

  private GameDescription.GameStyles game;
  private boolean[][] occupationMap;
  private double tol;
  
  // Create constructor for game styles
  // take enum from GameDescritpion as input and load wanted datas in GameProvider instance
  // game => int!!
  // faire correspondre les sytle en int avec enum de gameDescription
  // Attention au placement des QCARS, chevauchement! (solution de la grille, séparer le monde en carrés de tailles == MAX du QCAR possible?)
  // 
  // les QCARs driven sont en premiers index (piloté par IA ou manuel)
  // 
  
  public GameProvider(GameDescription.GameStyles game){
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    this.game = game;
    
  }
  
  @Override
  public IGameDescription nextGame(int nbOfDrivers) {
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    Point2D[] gameArena;
    
    
    
    
    switch (game){
      case STANDARD: cars = standardStyle(nbOfDrivers);
        break;
      case PARKINGS: cars = parkingsStyle(nbOfDrivers, 2);
        break;
      case DEBUG: boolean noBorders = true;
                  double tol = MAX_SIDE_LENGHT * 0.01;
                  cars = debugStyle(noBorders, tol);
        break;

      case WITHOUT_BORDERS:;
      break;

      // Bounding box is a QCAR without bonus, parking and driver
      case WITH_BORDERS:;
      break;

      
      case NO_PARKINGS:;
      break;

        
      default:;    
    }
    
    /*
    int drivers = 0;
    for (int i = 0; i < MAX_QCARS; i++) {
      
      if (R.nextDouble() > SPAWN_PROBABILITY) {
        QCarNature nature = randomNature(drivers++, nbOfDrivers);
        QCar car = new QCar(nature, randomAlignedPositions(nature));
        cars.add(car);
        //System.out.println(car);
      }
      
    }
    */
    return new GameDescription(cars);
  }
  
  private ArrayList<IQCar> withBorder(int nbOfDrivers, Point2D[] borderVertices){
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    QCarNature driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGHT, MIN_AREA);
    
    int totalDriven = 0, totalParkings = 0;
    int totalCars = nbOfDrivers ;
    QCar car;
    
    for(int i = 0; i<nbOfDrivers; i++){
      car = new QCar(driven, randomAlignedPositions(driven, occupationMap, false, tol));
      cars.add(car);
    }

    
    return cars;
  }
  
  private ArrayList<IQCar> noParkings(int nbOfDrivers){
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    QCarNature driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGHT, MIN_AREA);
    QCar car;
  
    for(int i = 0; i<nbOfDrivers; i++){
      car = new QCar(driven, randomAlignedPositions(driven, occupationMap, true, tol));
      cars.add(car);
    }
    
    return cars;
  }
  
  // corrected for QCAR order
  private ArrayList<IQCar> parkingsStyle(int nbOfDrivers, int multiplier){
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    QCarNature parking = new QCarNature(false, true, true, false, MAX_SIDE_LENGHT, MIN_AREA);
    QCarNature driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGHT, MIN_AREA);
    
    boolean noBorders = true;
    int totalDriven = 0, totalParkings = 0;
    int parkings = nbOfDrivers * multiplier;
    int totalCars = nbOfDrivers + parkings;
    QCar car;
    
    for(int i = 0; i<nbOfDrivers; i++){
      car = new QCar(driven, randomAlignedPositions(driven, occupationMap, noBorders, tol));
      cars.add(car);
    }
    for(int i = nbOfDrivers; i<totalCars; i++){
      car = new QCar(parking, randomAlignedPositions(parking, occupationMap, noBorders, tol));
      cars.add(car);
    }
    
    return cars;
  }
  
  private ArrayList<IQCar> standardStyle(int nbOfDrivers){
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    
    int drivers = 0;
    for (int i = 0; i < MAX_QCARS; i++) {
      
      if (R.nextDouble() > SPAWN_PROBABILITY) {
        QCarNature nature = randomNature(drivers++, nbOfDrivers);
        QCar car = new QCar(nature, randomAlignedPositions(nature, occupationMap, true, tol));
        cars.add(car);
        //System.out.println(car);
      }
    }
    return cars;  
  }
  
  private ArrayList<IQCar> debugStyle(boolean noBorders, double tol){
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    QCarNature nature = new QCarNature(true, false, true, true, MAX_SIDE_LENGHT, MIN_AREA);
    QCar car = new QCar(nature, randomAlignedPositions(nature, occupationMap, noBorders, tol));
    cars.add(car);
    return cars;
  }
  
  private QCarNature randomNature (int currDrivers, int wantedDrivers) {
    
    boolean driven = currDrivers < wantedDrivers ? true : false;
    boolean parkingTarget = driven ? false : R.nextBoolean();
    boolean vertexTarget = R.nextBoolean();
    boolean sideTarget = R.nextBoolean();
    double maxSideLenght = MAX_SIDE_LENGHT;
    double minArea = MIN_AREA;
    
    QCarNature nature = new QCarNature(driven, parkingTarget, vertexTarget, sideTarget, maxSideLenght, minArea);
    
    return nature;
  }
  
  private Point2D[] randomAlignedPositions(QCarNature nature, boolean[][] occupationMap, boolean dynamicReallocation, double tol) {
    
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
    }while(!checkEmplacementOnArena(points, occupationMap, tol, dynamicReallocation));
    
    
    return points;
  }
  
  // check occupation of this position
  // occupationMap = game map divided in an grid of square with side length equals to the max side length of a QCar
  //                 + buffer (tolerance on one side)
  private boolean checkEmplacementOnArena(Point2D[] vertices, boolean[][] occupationMap, double tol, boolean dynamicReallocation){
    
    double side = MAX_SIDE_LENGHT + 2*tol;
    for(int i = 0; i<vertices.length; i++){
      double x=vertices[i].getX(), y = vertices[i].getY();
      int occupX = (int)(x/side), occupY = (int)(y/side);
      if(dynamicReallocation &&(occupX>= occupationMap.length || occupY >= occupationMap[0].length))
        occupationMap = augmentMapSize(occupationMap, occupX, occupY);
      else return false;
      if(occupationMap[occupX][occupY]) return false;
    }
    
    return true;
  }
  
  // Dynamic augmentation of game map grid, in function of emplacement of checked area of grid
  private boolean[][] augmentMapSize(boolean[][] occupationMap, int occupX, int occupY){
    boolean[][] newMap;
    if(occupX >= occupationMap.length && occupY >= occupationMap[0].length)
      newMap = new boolean[occupX+1][occupY+1];
    else if(occupX >= occupationMap.length && occupY < occupationMap[0].length)
      newMap = new boolean[occupX+1][occupationMap[0].length];
    else
      newMap = new boolean[occupationMap.length][occupY+1];
    for(int x = 0; x<occupationMap.length; x++){
      for(int y = 0; y<occupationMap[x].length; y++) newMap[x][y] = occupationMap[x][y]; 
    }
    
    return newMap;
  }
  
}
