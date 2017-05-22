/**
 * =========================================================================
 * Project :    "QCar - Parallelogrammes en vadrouille"
 * Year :       2016 - 2017
 * School :     College of Engineering and Architecture, Fribourg
 * <p>
 * File :       GameProvider.java
 * <p>
 * Authors :    Biselx Timothee
 * Diaconescu Stefan
 * Gabriel Michel
 * Meier Vladimir
 * Wenger Olivier
 * =========================================================================
 */
package qcar.g4.test.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.Test;

import qcar.ApiTest;
import qcar.ICollision;
import qcar.IDistanceSensor;
import qcar.IDriver;
import qcar.IFactory;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IPlayerChannel;
import qcar.IQCar;
import qcar.ISeenVertex;
import qcar.ISensors;

public class MyFactoryTest_g5 extends ApiTest {
   static final int    N         = 10;
   static final int    QCARLIMIT = 500;
   static final Random RDM       = new Random();

   public MyFactoryTest_g5(IFactory fact, IFactory aux) {
      super(fact, aux);
   }

   /**
    * @author Olivier
    *         this test test that the number of styles is the same as the
    *         existing ones
    */
   @Test
   public void testAllStylesExist() {
      IFactory f = factoryUnderTest;
      int nStyles = f.numberOfStyles();
      assertTrue("nStyles must be >0", nStyles > 0);
      for (int i = 0; i < nStyles; i++) {
         IGameProvider g = f.newGameProvider(i);
         assertTrue(g != null);
      }
   }

   /**
    * @author Olivier
    *         This verify that the IDriver is created, the thread started and stopped
    */
   //@Test
   public void testNewSmartDriver() {
      IFactory f = factoryUnderTest;
      IDriver d = f.newSmartDriver();
      assertTrue(d != null);
      assertTrue(Thread.activeCount() == 2);
      IPlayerChannel pc = null;
      try {
         pc = decision -> new ISensors() {
            @Override
            public List<ISeenVertex> seenVertices() {
               return null;
            }

            @Override
            public IQCar mySelf() {
               return null;
            }

            @Override
            public IDistanceSensor distanceSensor() {
               return null;
            }

            @Override
            public List<ICollision> collisionsWithMe() {
               return null;
            }
         };
         d.startDriverThread(pc);
      } catch (Exception e) {
         e.printStackTrace();
      }
      assertTrue(Thread.activeCount() == 3);
      d.stopDriverThread();
      assertTrue(Thread.activeCount() == 2);
   }

   /**
    * @author Olivier
    *         This test that all the QCars have the right id in QCarNature
    */
   @Test
   public void testQcarNature() {
      IFactory f = factoryUnderTest;
      IGameProvider gp;
      IGameDescription gd;
      int rdmNbr;
      for (int i = 0; i < f.numberOfStyles(); i++) {
         gp = f.newGameProvider(i);
         rdmNbr = RDM.nextInt(QCARLIMIT) + 1;
         gd = gp.nextGame(rdmNbr);
         for (int j = 0; j < rdmNbr; j++)
            assertEquals(gd.allQCar().get(j).nature().qCarId(), j);
      }
   }

   //

}
