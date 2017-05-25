package qcar.g4.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.*;

import org.junit.Test;

import javafx.geometry.Rectangle2D;
/**
 * Classe de test pour le WorldManager
 * @author Groupe 2 - QCar
 *
 */
import qcar.*;
//import qcar.g2.worker.GameDescription;
//import qcar.g2.worker.QCar;
//import qcar.g2.worker.QCarNature;
//import qcar.g2.worker.SmartDriverIa;

public class WorldManagerTest_g2 extends ApiTest {

  private final IWorldManager wm;

  public WorldManagerTest_g2(IFactory fact, IFactory aux) {
    super(fact, aux);
    wm = factoryUnderTest.newWorldManager();
    // TODO Auto-generated constructor stub
  }

  @Test
  public void testWmReturnsNonNullValues() {
    IGameDescription gd = auxiliaryFactory.newGameProvider(0).nextGame(1);
    wm.openNewSimulation(gd, Arrays.asList(new DummyDriver()));
    int nSteps = 10;
    int timeoutInUs = 100;
    for (int i = 0; i < nSteps; i++) {
      assertTrue(wm.boundingBox() != null);
      assertTrue(wm.allDistanceSensors() != null);
      assertTrue(wm.allNewCollisions() != null);
      assertTrue(wm.allPhotoSensors() != null);
      assertTrue(wm.allQCars() != null);
      assertTrue(""+wm.stepNumber()+" "+i, wm.stepNumber() == i);
      wm.simulateOneStep(timeoutInUs);
    }
    wm.closeSimulation();
  }

  @Test
  public void testAllThreadsDestroyed() {
    int nThreads = Thread.activeCount();
    IGameDescription gd = auxiliaryFactory.newGameProvider(0).nextGame(2);
    wm.openNewSimulation(gd, Arrays.asList(factoryUnderTest.newSmartDriver(),
        factoryUnderTest.newSmartDriver()));
    int nSteps = 100;
    int timeoutInUs = 10;
    for (int i = 0; i < nSteps; i++) {
      wm.simulateOneStep(timeoutInUs);
    }
    wm.closeSimulation();
    int someTerminalDelayInMs = 500;
    try {
      Thread.sleep(someTerminalDelayInMs);
    } catch (InterruptedException e) {
    } // here nothing to do
    assertEquals(nThreads, Thread.activeCount());
  }

  // ===============================================================
  private static class DummyDriver implements IDriver {
    @Override
    public void startDriverThread(IPlayerChannel pc) {
    }

    @Override
    public void stopDriverThread() {
    }
  }

  /**
   * Test si la méthode isSimulationOpened() fonctionne et retourne
   * correctement sa valeur
   */
  @Test
  public void testIsSimulationOpened() {
    // Valeur inutile afin de pas lever une exception
    IGameDescription gd = auxiliaryFactory.newGameProvider(0).nextGame(2);
    assertFalse(wm.isSimulationOpened());
    wm.openNewSimulation(gd, Arrays.asList(factoryUnderTest.newSmartDriver(),
        factoryUnderTest.newSmartDriver()));
    int nSteps = 100;
    int timeoutInUs = 10;
    for (int i = 0; i < nSteps; i++) {
      wm.simulateOneStep(timeoutInUs);
      assertTrue(wm.isSimulationOpened());
    }
    wm.simulateOneStep(0);
    assertTrue(wm.isSimulationOpened());
    wm.closeSimulation();
    assertFalse(wm.isSimulationOpened());
  }


  /**
   * Test la methode boundingBox() fonctionne et retourne correctement sa valeur
   * - pas fonctionnel
   */
  @Test
  public void testboundingBox() {

    IGameDescription gd = auxiliaryFactory.newGameProvider(0)
        .nextGame(2);
    wm.openNewSimulation(gd, Arrays.asList(factoryUnderTest.newSmartDriver(),
        factoryUnderTest.newSmartDriver()));
    Rectangle2D rect = wm.boundingBox();
    double tol= (rect.getHeight()+rect.getWidth())/1E6;
    rect=new Rectangle2D(rect.getMinX()-tol,
        rect.getMinY()-tol,
        rect.getWidth()+2*tol,
        rect.getHeight()+2*tol
        );

    for (IQCar car : gd.allQCar()) {
      for (int i = 0; i < 4; i++) {
        Point2D point = car.vertex(i);
        boolean inside=rect.contains(point.getX(), point.getY());
        if(!inside) {
          //System.out.println(rect);
          //System.out.println(point);
        }
        assertTrue(inside);
      }
    }
  }
}
