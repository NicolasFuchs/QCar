package qcar.g4.test.api;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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

public class TinyFactoryTest_g8 extends ApiTest {
  private IFactory f;                     // shorthand for object we're testing
  private int nS;                         // another shorthand
  private IGameProvider igp;              // test what it has
  private IGameDescription igd;           // test values in game description
  private List<IQCar> liqc;               // should get a list sized correctly
  private IQCar iqc;                      // shorthand for 1 of 'em
  private IWorldManager iwm;              // the wm we use
  private final int MAXP=4;               // games will have at most maxP drivers for testing

  public TinyFactoryTest_g8(IFactory fact, IFactory aux) {
    super(fact, aux);
  }

  // setup all the common variables I plan to use 
  // @Before
  public void setup() {
    f = factoryUnderTest; nS = f.numberOfStyles();
    assertTrue("number of styles must be > 0", nS > 0);
    iwm = f.newWorldManager();
  }

  @Test
  public void testAllStylesExist(){
    IFactory f=factoryUnderTest;
    int nStyles = f.numberOfStyles();
    assertTrue("number of styles must be positive", nStyles > 0);
    for(int i=0; i<nStyles; i++) {
      IGameProvider g = f.newGameProvider(i);
      testGameDescription(g.nextGame(i+1), i+1); // just checking it's able to build a game...
    }
  }

  // check we get corresponding elements
  private void testGameDescription(IGameDescription igd, int cnt) {
    List <IQCar> liqc = igd.allQCar();  // all qcars in the game
    int gcnt=0;                         // # of qcars driven according to igd

    for (int k=0; k < liqc.size(); ++k)
      if (liqc.get(k).nature().isDriven())
        ++gcnt;

    assertTrue("Game description doesn't have the specified number of drivers",
         gcnt == cnt);
 
    //System.out.printf("%d qcars, of which %d %s driven\n", liqc.size(), cnt,
       //cnt > 1 ? "are" : "is");

    for (int k=0; k < liqc.size(); ++k) {
      assertTrue("Id's should be listed in order", liqc.get(k).nature().qCarId() == k);
      assertTrue("Initial qcar score not zero", liqc.get(k).score() == 0);
      assertTrue("Some qcars start already dead", liqc.get(k).isAlive());
      assertTrue("minArea <= 0", liqc.get(k).nature().minArea() > 0);
      assertTrue("maxSideLength <= 0", liqc.get(k).nature().maxSideLength() > 0);
    }
  }
  
  @Test
  public void testFactoryReturnsInstances() {
    assertTrue(factoryUnderTest.newGameProvider(0) != null);
    assertTrue(factoryUnderTest.newWorldManager() != null);
    assertTrue(factoryUnderTest.newSmartDriver() != null);
  }

  @Test
  // check that values returned are consistent
  public void testConsistency() {
    setup();

    for (int k=0; k < nS; ++k) {
      igp = f.newGameProvider(k);
      for (int j=1; j <= MAXP; ++j) {
        igd = igp.nextGame(j);
        liqc = igd.allQCar();
        assertTrue("number of drivers exceeds list", liqc.size() >= j);
        for (int i=0; i < liqc.size(); ++i) {
           iqc = liqc.get(i);
           assertTrue("starting with a dead qcar ?", iqc.isAlive());
           assertTrue("starting with a non-zero score ?", iqc.score() == 0);
           assertTrue("what does a negative side length mean ?",
             iqc.nature().maxSideLength() >= 0.0);
           assertTrue("what does a negative area mean ?",
             iqc.nature().minArea() >= 0.0);

        }
        WMConsistency(igd, j);
      }
    }
  }

  // check world manager and qcar consistency
  public void WMConsistency(IGameDescription igdp, int drivers) {
    List<IDriver> liqcd = new ArrayList<IDriver>(drivers);
  
    for (int k=0; k < drivers; ++k)
      liqcd.add(f.newSmartDriver());

    iwm.openNewSimulation(igdp, liqcd);
    assertTrue("number of WM qcars not the same as GP", liqc.size()==iwm.allQCars().size());
    
  }
    
  // for those of us not using ides
  public static void main(String args[]) {
    org.junit.runner.JUnitCore.main(TinyFactoryTest_g8.class.getName());
  }
}
