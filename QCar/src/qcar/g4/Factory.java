package qcar.g4;

import qcar.IDriver;
import qcar.IFactory;
import qcar.IGameProvider;
import qcar.IWorldManager;

public class Factory implements IFactory{

  @Override
  public int numberOfStyles() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public IGameProvider newGameProvider(int style) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDriver newSmartDriver() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IWorldManager newWorldManager() {
    // TODO Auto-generated method stub
    return null;
  }

}
