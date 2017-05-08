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
  
  // Create constructor for game styles
  // take enum from GameDescritpion as input and load wanted datas in GameProvider instance
  public GameProvider(GameDescription.GameStyles game){
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();

    this.game = game;
    /*
    switch (game){
      case STANDARD:;
        
        break;
      case PARKINGS:;
        break;
      case DEBUG:
        QCarNature nature = new QCarNature(true, false, true, true, MAX_SIDE_LENGHT, MIN_AREA);
        QCar car = new QCar(nature, randomAlignedPositions(nature));
        cars.add(car);
        
      break;

      case WITHOUT_BORDERS:;
      break;

      case WITH_BORDERS:;
      break;

      case NO_PARKINGS:;
      break;

      case DRIVERS_ONLY:;
      break;
        
      default:;    
    }
    */
  }
  
  
  @Override
  public IGameDescription nextGame(int nbOfDrivers) {
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    switch (game){
      case STANDARD: cars = standardStyle(nbOfDrivers);
        break;
      case PARKINGS: cars = parkingsStyle(nbOfDrivers, 2);
        break;
      case DEBUG: cars = debugStyle();
        break;

      case WITHOUT_BORDERS:;
      break;

      case WITH_BORDERS:;
      break;

      case NO_PARKINGS:;
      break;

      case DRIVERS_ONLY:;
      break;
        
      default:;    
    }
    
    
    
    
    
    
    int drivers = 0;
    for (int i = 0; i < MAX_QCARS; i++) {
      
      if (R.nextDouble() > SPAWN_PROBABILITY) {
        QCarNature nature = randomNature(drivers++, nbOfDrivers);
        QCar car = new QCar(nature, randomAlignedPositions(nature));
        cars.add(car);
        //System.out.println(car);
      }
      
    }
    
    return new GameDescription(cars);
  }
  
  
  private ArrayList<IQCar> parkingsStyle(int nbOfDrivers, int multiplier){
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    QCarNature parking = new QCarNature(false, true, true, false, MAX_SIDE_LENGHT, MIN_AREA);
    QCarNature driven = new QCarNature(true, false, true, true, MAX_SIDE_LENGHT, MIN_AREA);
    
    int totalDriven = 0, totalParkings = 0;
    int parkings = nbOfDrivers * multiplier;
    int totalCars = nbOfDrivers + parkings;
    QCar car;
    
    for(int i = 0; i<totalCars; i++){
      if(totalParkings<parkings && R.nextBoolean()){
        car = new QCar(parking, randomAlignedPositions(parking));
        totalParkings++;
      }
      else{
        car = new QCar(driven, randomAlignedPositions(driven));
        totalDriven++;
      }
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
        QCar car = new QCar(nature, randomAlignedPositions(nature));
        cars.add(car);
        //System.out.println(car);
      }
    }
    return cars;  
  }
  
  private ArrayList<IQCar> debugStyle(){
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    QCarNature nature = new QCarNature(true, false, true, true, MAX_SIDE_LENGHT, MIN_AREA);
    QCar car = new QCar(nature, randomAlignedPositions(nature));
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
  
  private Point2D[] randomAlignedPositions(QCarNature nature) {
    Point2D[] points = new Point2D[4];
    
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
    
    return points;
  }
  
}
