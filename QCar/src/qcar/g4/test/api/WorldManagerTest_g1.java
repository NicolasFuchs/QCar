package qcar.g4.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import qcar.*;

/* Please look at qcar.ApiTest documentation. Excerpt: 
   
   The idea is to write subclasses, such as qcar.g3.test.api.MyTest.
   When running those tests, you have two possibilities:
   - run normally, and it will test against the corresponding factory, 
     like new qcar.g3.Factory() in the example above
   - run and define the factories to test via a property: 
         java -Dqcar.groups=134 ...
     it will test against each of the factories qcar.g1.Factory
                                                qcar.g3.Factory
                                                qcar.g4.Factory
     (each tested with each other as "auxiliary" factory)
     
     ApiTest defines two attributes: 
 
     - factoryUnderTest:  The factory that is being tested 

     - auxiliaryFactory:  A factory (can be the same as factoryUnderTest)
       that is used to provide any other needed components; e.g. when testing 
       factoryUnderTest.newWorldManager(), it could be useful to rely on 
       auxiliaryFactory.newGameProvider() 
*/

public class WorldManagerTest_g1 extends ApiTest {
	private final IWorldManager wm;

	public WorldManagerTest_g1(IFactory fact, IFactory aux) {
		super(fact, aux);
		wm = factoryUnderTest.newWorldManager();
	}

//	@Test
//	public void testNbDrivers() {
//		List<IDriver> drivers;
//		for(int i = 0; i < 50; i++) {
//			drivers = new ArrayList<>();
//			for(int j = 0; j < i+1; j++) {
//				drivers.add(factoryUnderTest.newSmartDriver());
//			}
//			wm.openNewSimulation(auxiliaryFactory.newGameProvider(0).nextGame(i+1), drivers);
//
//			assertEquals(i+1, wm.allDistanceSensors().size());
//			assertEquals(i+1, wm.allPhotoSensors().size());
//			assertEquals(i+1, wm.allQCars().size());
//
//			wm.closeSimulation();
//		}
//	}

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
			assertTrue(wm.stepNumber() == i);
			wm.simulateOneStep(timeoutInUs);
		}
	}

	@Test
	public void testAllThreadsDestroyed() {
		int nThreads = Thread.activeCount();
		IGameDescription gd = auxiliaryFactory.newGameProvider(0).nextGame(2);
		wm.openNewSimulation(gd, Arrays.asList(factoryUnderTest.newSmartDriver(), factoryUnderTest.newSmartDriver()));
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

		public void startDriverThread(IPlayerChannel pc) {

		}

		public void stopDriverThread() {

		}
	}
}