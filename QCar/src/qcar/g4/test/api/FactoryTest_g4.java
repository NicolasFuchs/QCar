package qcar.g4.test.api;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;
import qcar.*;

/*
    This class tests the api implementation of the Factory
    Author: group 4
 */

public class FactoryTest_g4 extends ApiTest {
  
  public FactoryTest_g4(IFactory fact, IFactory aux) {
    super(fact, aux);
  }
  
  @Test
  public void testNumberOfDrivers(){
    int nbPlayers = 10;
    IFactory f = factoryUnderTest;
    int nStyles = f.numberOfStyles();
    assertTrue("number of styles must be positive", nStyles > 0);
    IGameProvider g = f.newGameProvider(0);
    IGameDescription gd;
    for(int i=1; i < nbPlayers; i++){
      gd = g.nextGame(i);
      assertTrue(!gd.allQCar().isEmpty());
      int driverCount = 0;
      List<IQCar> qcars = gd.allQCar();
      for(int j = 0; j <qcars.size(); j++){
        if(qcars.get(j).nature().isDriven()) driverCount++;
      }
      assertTrue(i == driverCount);
    }
  }
  
  // ---------------------------------------------------------------------------
  // this tests have been recuperated from TinyFactoryTest class
  @Test
  public void allStylesExist(){
    IFactory f=factoryUnderTest;
    int nStyles = f.numberOfStyles();
    int nPlayers=2;
    assertTrue("number of styles must be positive", nStyles > 0);
    for(int i=0; i<nStyles; i++) {
      IGameProvider g = f.newGameProvider(i);
      // Now just to verify that it is able to build a game:
      assertTrue(g.nextGame(nPlayers) != null); 
    }
  }

  @Test
  public void testFactoryInstances(){
    int style=0;
    assertTrue(factoryUnderTest.newGameProvider(style)!=null);
    assertTrue(factoryUnderTest.newSmartDriver()!=null);
    assertTrue(factoryUnderTest.newWorldManager()!=null);
  }
  // ---------------------------------------------------------------------------

}
