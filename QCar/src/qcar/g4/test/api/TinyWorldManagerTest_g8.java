package qcar.g4.test.api;

import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.geom.Point2D;
import java.util.Arrays;
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

public class TinyWorldManagerTest_g8 extends ApiTest {
  private static final int NSTEPS=100;// just do this many for a run
  private static final long DELAY=100; // max wait time in micro seconds
  private static final int NVERTEX=4;  // only dealing with quadrilaterals
  private static final double TOLERANCE=1e-2;  // assume collision occurs within this distance
                                              // should really be measured in ulps

  public TinyWorldManagerTest_g8(IFactory fact, IFactory aux) {
    super(fact, aux);
  }

  @Test
  public void testWmReturnsNonNullValues() {
    IWorldManager wm=factoryUnderTest.newWorldManager();
    IGameDescription gd=auxiliaryFactory.newGameProvider(0).nextGame(1);
    wm.openNewSimulation(gd, Arrays.asList(new DummyDriver()));
    int nSteps=NSTEPS;
    for(int i=0; i<nSteps; i++) {
      assertTrue(wm.boundingBox() != null);
      assertTrue(wm.allDistanceSensors() != null);
      assertTrue(wm.allNewCollisions() != null);
      assertTrue(wm.allPhotoSensors() != null);
      assertTrue(wm.allQCars() != null);
      assertTrue(wm.stepNumber() == i);
      wm.simulateOneStep(DELAY);
    }
  }

  // test if there are "fake" collisions
  private static void testCollisions(List<IQCar> liqc, List<ICollision> lic) {
    Point2D coll; // point of collision
    IQCar iqc1, iqc2;  // collision is on one of these
    boolean found;     // I found the collision on one of the qcars

    for (int k = 0; k < lic.size(); ++k) {
      coll=lic.get(k).position();
      iqc1=liqc.get(lic.get(k).hittingQCarId());
      iqc2=liqc.get(lic.get(k).hitQCarId());
      double minDist=Double.POSITIVE_INFINITY;
      for (int kk=0; kk < NVERTEX; ++kk) {
        minDist = Math.min(minDist, iqc1.vertex(kk).distance(coll));
        minDist = Math.min(minDist, iqc2.vertex(kk).distance(coll));
      }
      found = minDist < TOLERANCE;
      assertTrue("There seems to be fake collisions in this world manager", found);
    }
  }

  @Test
  public void testWMstuff() {
    IFactory f=factoryUnderTest; // convenient shorthand
    IWorldManager iwm = f.newWorldManager();  // test this sucker
    int styles = f.numberOfStyles();   // test each one
    Qcn qc[];  // test QCar nature invariance
    List<IQCar> liqc;  // official list from iwm
    IQCar c1;    // short hand for construction below
    IQCarNature c1n; // another short hand

    IGameDescription igd;    // go through each one
    IDriver ida[];           // "smart" drivers from factory
    int tdiff= Thread.activeCount(); // see if we actually create some threads
    int kk;

    for (int k=0; k < styles; ++k) {
      igd = f.newGameProvider(k).nextGame(k+1);
      ida = new IDriver[k+1];
      for (kk=0; kk < k+1; ++kk)
         ida[kk] = f.newSmartDriver();
      
      assertTrue("Simulation is not yet open", !iwm.isSimulationOpened());
      iwm.openNewSimulation(igd, Arrays.asList(ida));
      assertTrue("Simulation is now open", iwm.isSimulationOpened());
      assertTrue("No new threads ?", tdiff < Thread.activeCount());

      liqc = iwm.allQCars(); // or igd.allQCar();
      qc= new Qcn[liqc.size()];
      for (kk=0; kk < qc.length; ++kk) {
        c1 = liqc.get(kk);
        c1n= c1.nature();
        assertTrue("qCarId should agree with list index", kk == c1n.qCarId());
        qc[kk]= new Qcn(c1n.qCarId(), c1n.isDriven(), c1n.maxSideLength(), c1n.minArea(),
           c1n.isParkingTarget(), c1n.isVertexTarget(), c1n.isSideTarget());
      }
      
      for (kk=0; kk < NSTEPS; ++kk)  {
        iwm.simulateOneStep(DELAY);
        testCollisions(iwm.allQCars(), iwm.allNewCollisions());
        for (IQCar iqc:iwm.allQCars()) {
           verifyThem(qc[iqc.nature().qCarId()], iqc.nature());
        }
        assertTrue("Step number mismatch", kk+1 == iwm.stepNumber());
        assertTrue("A qcar has dissappeared !", qc.length == iwm.allQCars().size());
        assertTrue("Simulation stopped prematurely", iwm.isSimulationOpened());
      }
      
      iwm.closeSimulation();
      assertTrue("Simulation is closed, but world manager doen't think so",
          !iwm.isSimulationOpened());
    }
  }
  
  static void verifyThem(Qcn qcn, IQCarNature n) {
     assertTrue("Unchangeable property id has changed !",
        qcn.qCarId() == n.qCarId());
     assertTrue("Unchangeable property isDriven has changed !",
        qcn.isDriven() == n.isDriven());
     assertTrue("Unchangeable property maxSideLength has changed !",
        qcn.maxSideLength() == n.maxSideLength());
     assertTrue("Unchangeable property minArea has changed !",
        qcn.minArea() == n.minArea());
     assertTrue("Unchangeable property isParkingTarget has changed !",
        qcn.isParkingTarget() == n.isParkingTarget());
     assertTrue("Unchangeable property isVertexTarget has changed !",
        qcn.isVertexTarget() == n.isVertexTarget());
     assertTrue("Unchangeable property isSideTarget has changed !",
        qcn.isSideTarget() == n.isSideTarget());
  }
    
  @Test
  public void testAllThreadsDestroyed() {
    IWorldManager wm=factoryUnderTest.newWorldManager();
    int nThreads=Thread.activeCount();
    IGameDescription gd=auxiliaryFactory.newGameProvider(0).nextGame(2);
    wm.openNewSimulation(gd, Arrays.asList(
        factoryUnderTest.newSmartDriver(), 
        factoryUnderTest.newSmartDriver()
        ));
    int nSteps=100;
    for(int i=0; i<nSteps; i++) {
      wm.simulateOneStep(10);
    }
    wm.closeSimulation();
    try { 
      Thread.sleep(500);
    } catch (InterruptedException e) {} // here nothing to do
    assertEquals(nThreads, Thread.activeCount());
  }
    
  // for those of us not using ides
  public static void main(String args[]) {
    org.junit.runner.JUnitCore.main("qcar.g0.test.api.TinyWorldManagerTest");
  }

  //===============================================================
  static class DummyDriver implements IDriver {
    @Override public void startDriverThread(IPlayerChannel pc) {}
    @Override public void stopDriverThread() {}
  }
  //===============================================================
  //a quick and dirty qcar nature debug class
  static class Qcn implements IQCarNature {
    int     qid;               // variables taken from interface
    boolean driven;
    double  maxSideL, minA;
    boolean parkTarget, vertexTarget, sideTarget;

    boolean debug;             // for additional print info

    // the constructors
    public Qcn(int q) { 
      init (q, false, Double.MAX_VALUE, Double.MIN_VALUE, false, false, false, false);
    }
    public Qcn(int q, boolean d) {
      init(q, d, Double.MAX_VALUE, Double.MIN_VALUE, false, false, false, false);
    }
    public Qcn(int q, boolean d, double mS) {
      init (q, d, mS, Double.MIN_VALUE, false, false, false, false);
    }
    public Qcn(int q, boolean d, double mS, double mA) {
      init (q, d, mS, mA, false, false, false, false);
    }
    public Qcn(int q, boolean d, double mS, double mA, boolean pT) {
      init(q, d, mS, mA, pT, false, false, false);
    }
    public Qcn(int q, boolean d, double mS, double mA, boolean pT,
        boolean vT) {
      init(q, d, mS, mA, pT, vT, false, false);
    }
    public Qcn(int q, boolean d, double mS, double mA, boolean pT,
        boolean vT, boolean sT) {
      init(q, d, mS, mA, pT, vT, sT, false);
    }
    public Qcn(int q, boolean d, double mS, double mA, boolean pT,
        boolean vT, boolean sT, boolean de) {
      init(q, d, mS, mA, pT, vT, sT, de);
    }

    private void init(int q, boolean d, double mS, double mA, boolean pT,
        boolean vT, boolean sT, boolean de) {
      qid=q; driven=d; maxSideL=mS; minA=mA; parkTarget=pT;
      vertexTarget=vT; sideTarget=sT; debug=de;
      if (de) { dump(); }
    }

    // getters
    public int qCarId() { return qid; }
    public boolean isDriven() { return driven; }
    public double maxSideLength() { return maxSideL; }
    public double minArea() { return minA; }
    public boolean isParkingTarget() { return parkTarget; }
    public boolean isVertexTarget() { return vertexTarget; }
    public boolean isSideTarget() { return sideTarget; }

    // utility function(s)
    void dump() {
      System.err.printf("new qcn: %d, %b, %g, %g, %d, %d, %d\n",
          qid, driven, maxSideL, minA, parkTarget, vertexTarget, sideTarget);
    }
  }
}
