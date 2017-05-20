package qcar.g4;

import qcar.IDriver;
import qcar.IFactory;
import qcar.IGameProvider;
import qcar.IWorldManager;

public class Factory implements IFactory {

  @Override
  public int numberOfStyles() {
    return GameDescription.GameStyles.values().length;
  }

  @Override
  public IGameProvider newGameProvider(int style) {
    return new GameProvider();
  }

  @Override
  public IDriver newSmartDriver() {
    return new Driver();
  }

  @Override
  public IWorldManager newWorldManager() {
    return new WorldManager();
  }

}
