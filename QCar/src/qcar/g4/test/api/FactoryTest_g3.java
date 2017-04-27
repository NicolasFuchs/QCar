package qcar.g4.test.api;

import static org.junit.Assert.*;

import org.junit.Test;

import qcar.ApiTest;
import qcar.IDriver;
import qcar.IFactory;
import qcar.IGameProvider;
import qcar.IWorldManager;

public class FactoryTest_g3 extends ApiTest {

  public FactoryTest_g3(IFactory fact, IFactory aux) {
    super(fact, aux);
  }

  @Test
  public void testNewGameProvider(){
     IFactory f = factoryUnderTest;
     IGameProvider gp = f.newGameProvider(0);
     assertNotNull(gp);
  }
  
  @Test
  public void testNewSmartDriver(){
     IFactory f = factoryUnderTest;
     IDriver d = f.newSmartDriver();
     assertNotNull(d);
  }
  
  @Test
  public void testNewWorldManager(){
     IFactory f = factoryUnderTest;
     IWorldManager wm = f.newWorldManager();
     assertNotNull(wm);
  }
  
  @Test
  public void testAllSytlesExist() {
    IFactory f = factoryUnderTest;
    int nbOfStyles = f.numberOfStyles();
    assertTrue("nStyles must be >0", nbOfStyles > 0);
    for (int i = 0; i < nbOfStyles; i++) {
      IGameProvider g = f.newGameProvider(i);
      assertTrue(g != null);
    }
  }
  
}
