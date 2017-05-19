package qcar.g4.test.api;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import qcar.*;

public class DriverTest_g1 extends ApiTest {

	private static final int NB_OF_DRIVERS = 10;

	private int nbOfThreadsBefore;
	private IWorldManager worldManager;

	public DriverTest_g1(IFactory fact, IFactory aux) {
		super(fact, aux);
	}

	@Before
	public void initDrivers() {
		worldManager = factoryUnderTest.newWorldManager();
		IGameProvider gameProvider = factoryUnderTest.newGameProvider(0);
		List<IDriver> drivers = new ArrayList<>();
		for (int i = 0; i < NB_OF_DRIVERS; i++) {
			drivers.add(factoryUnderTest.newSmartDriver());
		}
		nbOfThreadsBefore = Thread.activeCount();
		worldManager.openNewSimulation(gameProvider.nextGame(NB_OF_DRIVERS), drivers);
	}

	@Test
	public void startDriverThreadTest() {
		assertTrue(Thread.activeCount() - nbOfThreadsBefore == NB_OF_DRIVERS);
		worldManager.closeSimulation();
	}

//	@Test(timeout = 5000) // Timeout after 5 seconds!
//	public void stopDriverThreadTest() {
//		nbOfThreadsBefore = Thread.activeCount();
//		worldManager.closeSimulation();
//		while (Thread.activeCount() != nbOfThreadsBefore - NB_OF_DRIVERS) {
//			Thread.yield();
//		}
//		assertTrue(true);
//	}

	// for those of us not using ides
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main(DriverTest_g1.class.getName());
	}
}
