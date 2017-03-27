package qcar.g4;

import java.util.ArrayList;
import java.util.List;

import qcar.IGameDescription;
import qcar.IQCar;

public class GameDescription implements IGameDescription{
  
  private ArrayList<IQCar> cars;
  
  public GameDescription(ArrayList<IQCar> cars) {
    this.cars = cars;
  }
  
  public void addQCar(IQCar qCar) {
    if (qCar != null)
      cars.add(qCar);
    else 
      throw new NullPointerException("qCar is null");
  }
  
  @Override
  public List<IQCar> allQCar() {
    return cars;
  }

}
