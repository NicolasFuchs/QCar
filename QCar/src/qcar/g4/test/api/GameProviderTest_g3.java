package qcar.g4.test.api;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import qcar.*;

public class GameProviderTest_g3 extends ApiTest {

   private final int STYLE = 0;
   private final int NBDRIVERS = 5;
   private IGameProvider gp;
   private IGameDescription gd;

   public GameProviderTest_g3(IFactory fact, IFactory aux) {
      super(fact, aux);
   }
   
   @Before
   public void setup(){
       gp = factoryUnderTest.newGameProvider(STYLE);
       gd = gp.nextGame(NBDRIVERS);
   }

   @Test
   public void testNbOfDrivers() {
      int drivenCars = 0;
      for (IQCar qcar : gd.allQCar()) {
         if(qcar.nature().isDriven())
            drivenCars++;
      }
      assertTrue(drivenCars == NBDRIVERS);
   }
   
   @Test
   public void testNbOfStyles() {
      IFactory f = factoryUnderTest;
      for (int i = 0; i < f.numberOfStyles(); i++) {
         IGameProvider gameProvider = f.newGameProvider(i);
         assertNotNull(gameProvider);
      }
   }

}
