package qcar.g4.test.api;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import qcar.*;

public class WorldManagerTest extends ApiTest{

  private final IWorldManager wm;

  public WorldManagerTest(IFactory fact, IFactory aux){
    super(fact, aux);
    wm = factoryUnderTest.newWorldManager();
  }

  @Test
  public void testSimulationSetup(){
    IGameDescription gd = factoryUnderTest.newGameProvider(0).nextGame(1);
    wm.openNewSimulation(gd, Arrays.asList(factoryUnderTest.newSmartDriver()));
    assertTrue(wm.isSimulationOpened());
    assertTrue(wm.stepNumber() == 0);
  }

  @Test
  public void testIsWarOver(){
    IGameDescription gd = factoryUnderTest.newGameProvider(0).nextGame(1);
    wm.openNewSimulation(gd, Arrays.asList(factoryUnderTest.newSmartDriver()));
    int timeoutInUs=10;
    wm.simulateOneStep(timeoutInUs);
    assertTrue(wm.isWarOver());
  }

  @Test
  public void testSimulationTearDown(){
    IGameDescription gd = factoryUnderTest.newGameProvider(0).nextGame(1);
    wm.openNewSimulation(gd, Arrays.asList(factoryUnderTest.newSmartDriver()));
    wm.closeSimulation();
    assertTrue(!wm.isSimulationOpened());
  }

  // Ce test provient de la classe TinyWorldManagerTest
  @Test
  public void testWmReturnsNonNullValues(){
    IGameDescription gd=auxiliaryFactory.newGameProvider(0).nextGame(1);
    wm.openNewSimulation(gd, Arrays.asList(factoryUnderTest.newSmartDriver()));
    int nSteps=10;
    int timeoutInUs=100;
    for(int i=0; i<nSteps; i++) {
      assertTrue(wm.boundingBox() != null);
      assertTrue(wm.allDistanceSensors() != null);
      assertTrue(wm.allNewCollisions() != null);
      assertTrue(wm.allPhotoSensors() != null);
      assertTrue(wm.allQCars() != null);
      assertTrue(wm.stepNumber() == i);
      wm.simulateOneStep(timeoutInUs);
    }
  }

  // Ce test provient de la classe TinyWorldManagerTest
  @Test
  public void testAllThreadsDestroyed(){
    int nThreads=Thread.activeCount();
    IGameDescription gd=auxiliaryFactory.newGameProvider(0).nextGame(2);
    wm.openNewSimulation(gd, Arrays.asList(
        factoryUnderTest.newSmartDriver(),
        factoryUnderTest.newSmartDriver()
    ));
    int nSteps=100;
    int timeoutInUs=10;
    for(int i=0; i<nSteps; i++) {
      wm.simulateOneStep(timeoutInUs);
    }
    wm.closeSimulation();
    int someTerminalDelayInMs=500;
    try {
      Thread.sleep(someTerminalDelayInMs);
    } catch (InterruptedException e) {} // here nothing to do
    assertEquals(nThreads, Thread.activeCount());
  }

}
