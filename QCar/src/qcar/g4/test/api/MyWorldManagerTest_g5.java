/**
 * =========================================================================
 * Project :    "QCar - Parallelogrammes en vadrouille"
 * Year :       2016 - 2017
 * School :     College of Engineering and Architecture, Fribourg
 * <p>
 * File :       Driver.java
 * <p>
 * Authors :    Biselx Timothée
 *              Diaconescu Stefan
 *              Gabriel Michel
 *              Meier Vladimir
 *              Wenger Olivier
 * =========================================================================
 */
package qcar.g4.test.api;

import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import javafx.geometry.Rectangle2D;
import qcar.ApiTest;
import qcar.IDriver;
import qcar.IFactory;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IQCar;
import qcar.IWorldManager;

public class MyWorldManagerTest_g5 extends ApiTest {


   static final int          N                      = 10;
   static final int          QCARLIMIT              = 10;
   private static final long STEP_DURATION_LIMIT_US = 1_000_000;
   private static final int  MAX_STEPS              = 200;
   private static final long EPSILON                = 1000;
   static final Random       RDM                    = new Random();

   IFactory                  f                      = factoryUnderTest;
   IGameProvider             gp                     = f.newGameProvider(0);
    
   public MyWorldManagerTest_g5(IFactory fact, IFactory aux) {
        super(fact, aux);
    }

   /**
    * Test if the qcar length are correct regarding the qcarNature maxSideLength.
    *
    * @author Michel
    */
   @Test
   public void testArea() {
      IGameDescription gd;
      int rdmNbr;
      for (int i = 0; i < f.numberOfStyles(); i++) {
         gp = f.newGameProvider(i);
         List<IDriver> players = new ArrayList<>();
         rdmNbr = 1+RDM.nextInt(QCARLIMIT);
         for (int j = 0; j < rdmNbr; j++)
            players.add(f.newSmartDriver());
         gd = gp.nextGame(players.size());
         for (IQCar car : gd.allQCar()) {
           double ml=car.nature().maxSideLength();
           ml *= 1+1E06;
            for (int j = 0; j < 4; j++) {
               Point2D from = car.vertex(j % 4);
               Point2D to = car.vertex((j + 1) % 4);
               double sideLength = getSideLength(from, to);
               assertTrue("QCar side length "+sideLength+
                   " must be lesser than maxSideLength "+ml, 
                          sideLength <= ml);
            }
         }
      }
   }
     
    /**
     * @author Olivier
     * This test that the worldManager simulateOneStep, that there is as much Photosensors as QCars
     */
    @Test
    public void testWorldManager()   {
        IWorldManager wm = f.newWorldManager();
        IGameDescription gd;
        int rdmNbr;
        for (int i = 0; i < f.numberOfStyles(); i++) {
            //System.out.printf("Testing style number: %d\n", i);
            gp = f.newGameProvider(i);      
            List<IDriver> players = new ArrayList<>();
            rdmNbr = RDM.nextInt(QCARLIMIT)+1;
            for(int j=0; j<rdmNbr; j++)    {
                players.add(f.newSmartDriver());
            }
            gd = gp.nextGame(players.size());
            wm.openNewSimulation(gd, players);
            assertTrue(wm.isSimulationOpened());

            int steps = 0;
            while(!wm.isWarOver() && steps++ < MAX_STEPS) {
                wm.simulateOneStep(STEP_DURATION_LIMIT_US);
            }
            assertTrue(wm.stepNumber()>= 0);
            //assertEquals(wm.allPhotoSensors().size(), wm.allQCars().size()); 
            
            for(int j = 0 ; j < wm.allQCars().size() ; j++){
              assertTrue(wm.allQCars().get(j) != null);
              //assertTrue(wm.allPhotoSensors().get(j) != null);
            }
            
            wm.closeSimulation();
            players.clear();
        }
    }


   private static double getSideLength(Point2D from, Point2D to) {
      double x = from.getX() - to.getX();
      double y = from.getY() - to.getY();
      return Math.sqrt(x * x + y * y);
   }





   /**
    * @author Olivier
    *         This test that the QCar are placed within the rounding box and that there is no colision at start
    */
   @Test
   public void testLocation() {
      IWorldManager wm = f.newWorldManager();
      IGameDescription gd;
      Rectangle2D r;
      int rdmNbr;
      for (int i = 0; i < f.numberOfStyles(); i++) {
         gp = f.newGameProvider(i);
         List<IDriver> players = new ArrayList<>();
         rdmNbr = 1+RDM.nextInt(QCARLIMIT);
         for (int j = 0; j < rdmNbr; j++)
            players.add(f.newSmartDriver());
         gd = gp.nextGame(players.size());
         wm.openNewSimulation(gd, players);
         r = wm.boundingBox();
         r = withTolerance(r);
         gd.allQCar();
         for (IQCar qcar : gd.allQCar())
            for (int k = 0; k < 4; k++) {
              Point2D p=qcar.vertex(k);
              boolean inside=r.contains(p.getX(), p.getY());
              if(!inside) {
                //System.out.println(r);
                //System.out.println(p);
              }
              assertTrue(inside);
            }
         assertTrue(wm.isSimulationOpened());
         wm.closeSimulation();
         assertTrue(wm.allNewCollisions().isEmpty());
      }
   }
   
   static Rectangle2D withTolerance(Rectangle2D rect) {
     double tol= (rect.getHeight()+rect.getWidth())/1E6;
     rect=new Rectangle2D(
         rect.getMinX()-tol,
         rect.getMinY()-tol,
         rect.getWidth()+2*tol,
         rect.getHeight()+2*tol
         );
     return rect;
   }


   /**
    * @author Olivier
    *         This test the condition that with one remaining player the war should be over
    *         WRONG: the score can still change !!
    */
//   @Test
//   public void testIsWarOver() {
//      IWorldManager wm = f.newWorldManager();
//      List<IDriver> players = new ArrayList<>();
//      players.add(f.newSmartDriver());
//      IGameDescription gd = gp.nextGame(1);
//      wm.openNewSimulation(gd, players);
//      assertTrue(wm.isWarOver());
//   }

   /**
    * @author Olivier
    *         This tests that all QCars are alive at start
    */
   @Test
   public void isAlive() {
      IGameDescription gd;
      int rdmNbr;
      for (int i = 0; i < f.numberOfStyles(); i++) {
         //System.out.printf("Testing style number: %d\n", i);
         gp = f.newGameProvider(i);
         List<IDriver> players = new ArrayList<>();
         rdmNbr = 1+RDM.nextInt(QCARLIMIT);
         for (int j = 0; j < rdmNbr; j++)
            players.add(f.newSmartDriver());
         gd = gp.nextGame(players.size());
         gd.allQCar();
         for (IQCar qcar : gd.allQCar())
             assertTrue(qcar.isAlive());
      }
   }

   /**
    * @author Olivier
    *         This tests that oneStepsimulation doesn't last more than the STEP_DURATION_Limit
    */
   @Test
   public void oneStepTest() {
      IWorldManager wm = f.newWorldManager();
      IGameDescription gd;
      int rdmNbr;
      long startTime;
      long stopTime;
      for (int i = 0; i < N; i++) {
         gp = f.newGameProvider(0);
         List<IDriver> players = new ArrayList<>();
         rdmNbr = 1+RDM.nextInt(QCARLIMIT);
         for (int j = 0; j < rdmNbr; j++)
            players.add(f.newSmartDriver());
         gd = gp.nextGame(players.size());
         gd.allQCar();
         wm.openNewSimulation(gd, players);
         startTime = System.nanoTime();
         wm.simulateOneStep(STEP_DURATION_LIMIT_US);
         stopTime = System.nanoTime();
         double d=(stopTime - startTime)/1000;
         String s=""+d+" vs "+STEP_DURATION_LIMIT_US;
         //System.out.println(d/1000);
         assertTrue(s, d <= (STEP_DURATION_LIMIT_US + EPSILON));
         wm.closeSimulation();
      }
   }

}
