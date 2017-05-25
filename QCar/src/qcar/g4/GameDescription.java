package qcar.g4;

import java.util.ArrayList;
import java.util.List;

import qcar.IGameDescription;
import qcar.IQCar;

public class GameDescription implements IGameDescription{
  
  public enum GameStyles {
    MIXED_WITH_BORDERS,
    ONLY_DRIVERS_WITH_BORDERS,
    NO_STATICS_WITH_BORDERS, 
    NO_PARKINGS_WITH_BORDERS, 
    MIXED_WITHOUT_BORDERS,
    ONLY_DRIVERS_WITHOUT_BORDERS,
    NO_STATICS_WITHOUT_BORDERS, 
    NO_PARKINGS_WITHOUT_BORDERS
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
