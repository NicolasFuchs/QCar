package qcar.g4;

import qcar.IDriver;
import qcar.IFactory;
import qcar.IGameProvider;
import qcar.IWorldManager;

/**
 * This class generate different elements needed for the game.
 */
public class Factory implements IFactory{

  /**
   * @return the number of style of the gameDescription
   */
  @Override
  public int numberOfStyles() {
    return GameDescription.GameStyles.values().length;
  }

  /**
   * Create a new game provider
   * @param style selected style
   * @return the gameProvider
   */
  @Override
  public IGameProvider newGameProvider(int style) {
    return new GameProvider(GameDescription.GameStyles.values()[style]);
  }

  /**
   * @return a new driver
   */
  @Override
  public IDriver newSmartDriver() {
    return new Driver();
  }

  /**
   * @return a new world manager
   */
  @Override
  public IWorldManager newWorldManager() {
    return new WorldManager();
  }

}
