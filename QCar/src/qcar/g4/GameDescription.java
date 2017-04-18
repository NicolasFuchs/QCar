package qcar.g4;

import java.util.ArrayList;
import java.util.List;

import qcar.IGameDescription;
import qcar.IQCar;

public class GameDescription implements IGameDescription{
  
  public enum GameStyles {
    WITHOUT_BORDERS, WITH_BORDERS
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
