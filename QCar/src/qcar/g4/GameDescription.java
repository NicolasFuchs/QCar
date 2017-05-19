package qcar.g4;

import java.util.ArrayList;
import java.util.List;

import qcar.IGameDescription;
import qcar.IQCar;

public class GameDescription implements IGameDescription{
  
  // correspondance avec style de factory -- enum est class à part avec se8
  public enum GameStyles {
    STANDARD_WITH_BORDERS, 
    PARKINGS_WITH_BORDERS, 
    DEBUG_WITH_BORDERS, 
    NO_PARKINGS_WITH_BORDERS, 
    STANDARD_WITHOUT_BORDERS, 
    PARKINGS_WITHOUT_BORDERS, 
    DEBUG_WITHOUT_BORDERS, 
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
