package qcar.g4;

import java.util.ArrayList;
import java.util.List;

import qcar.IGameDescription;
import qcar.IQCar;

public class GameDescription implements IGameDescription{
  
  public enum GameStyles {
    STANDARD, PARKINGS, DEBUG,
    WITHOUT_BORDERS, WITH_BORDERS, NO_PARKINGS, DRIVERS_ONLY
  }
  
  private ArrayList<IQCar> cars;
  
  public GameDescription(ArrayList<IQCar> cars) {
    this.cars = cars;
  }
  
  @Override
  public List<IQCar> allQCar() {
    return cars;
  }

}
