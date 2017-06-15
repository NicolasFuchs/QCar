package qcar.g4;

import java.util.ArrayList;
import java.util.List;

import qcar.IGameDescription;
import qcar.IQCar;

public class GameDescription implements IGameDescription{
  
  /** Enumeration of the styles of games available
   * 
   * The first QCars are always those being driven
   * 
   * MIXED_WITH_BORDERS, game with mixed driven, static and parking QCars and the last QCar
   *                     is a static QCar representing the border
   * ONLY_DRIVERS_WITH_BORDERS, game with only driven QCars and the last QCar is a static QCar 
   *                            representing the border
   * NO_STATICS_WITH_BORDERS, game with only driven and parking QCars and the last QCar is a static
   *                          QCar representing the border 
   * NO_PARKINGS_WITH_BORDERS, game with only driven and static QCars and the last QCar is a static
   *                           QCar representing the border 
   * MIXED_WITHOUT_BORDERS, game with mixed driven, static and parking QCars
   * ONLY_DRIVERS_WITHOUT_BORDERS, game with only driven QCars
   * NO_STATICS_WITHOUT_BORDERS, game with only driven and parking QCars
   * NO_PARKINGS_WITHOUT_BORDERS, game with only driven and static QCars
   * 
   */ 
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
  
  /** Method to create a new GameDescription
   * Initialize the GameDescription with list of the QCar given in parameters
   * 
   *    @param cars : ArrayList of {@link IQCar}, the list of the QCars representing this game
   */
  public GameDescription(ArrayList<IQCar> cars) {
    this.cars = cars;
  }
  
  /** Method to return the list of QCars describing this game
   *
   *    @return : List of {@link IQCar}, the list of the QCars representing this game
   */
  @Override
  public List<IQCar> allQCar() {
    return cars;
  }

}
