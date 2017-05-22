package qcar.g4.test.api;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import qcar.*;

public class WorldManagerTest_g3 extends ApiTest {
   
   private IGameProvider gp;
   private IGameDescription gd;
   private final int STYLE = 0;
   private final int NBDRIVERS = 5;
   private IWorldManager wm;
   
   public WorldManagerTest_g3(IFactory fact, IFactory aux) {
      super(fact, aux);
   }
   
   @Before
   public void setup(){
       gp = factoryUnderTest.newGameProvider(STYLE);
       gd = gp.nextGame(NBDRIVERS);
       wm = factoryUnderTest.newWorldManager();
   }

   @Test
   public void testSimulation() {
      ArrayList<IDriver> players = new ArrayList<>();
      for(int i=0; i<NBDRIVERS; i++)
        players.add(factoryUnderTest.newSmartDriver());
      // Start the simulation
      wm.openNewSimulation(gd, players);
      assertTrue(wm.isSimulationOpened());
      // Execute 2 step
      for (int i = 0; i < 2; i++) {
         wm.simulateOneStep(10);
      }
      assertTrue(wm.stepNumber() == 2);
      // Close the simulation
      wm.closeSimulation();
      assertFalse(wm.isSimulationOpened());
   }
}
