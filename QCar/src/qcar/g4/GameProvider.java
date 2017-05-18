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
  public static final double SPAWN_PROBABILITY = 0.9;
  public static final int PARALLELOGRAM_SCALE = 20;
  
  public static final double MIN_AREA = 1.3;
  public static final double MAX_SIDE_LENGHT = 7.4;
  

  @Override
  public IGameDescription nextGame(int nbOfDrivers) {
    
    ArrayList<IQCar> cars = new ArrayList<IQCar>();
    
    int drivers = 0;
    //for (int i = 0; i < 2; i++) {
      
     // if (R.nextDouble() > SPAWN_PROBABILITY) {
        QCarNature nature = randomNature(drivers++, nbOfDrivers);
        QCar car = new QCar(nature, randomAlignedPositions(nature));
        cars.add(car);
<<<<<<< HEAD
      }
=======
        //System.out.println(car);
     // }
>>>>>>> refs/remotes/origin/develop_pilot_2
      
   // }
    
    return new GameDescription(cars);
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
