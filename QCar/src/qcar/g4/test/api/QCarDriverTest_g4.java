package qcar.g4.test.api;

import org.junit.Test;
import static org.junit.Assert.*;
import qcar.*;

/*
    This class tests the api implementation of the smart driver
    Author: group 4
 */

public class QCarDriverTest_g4 extends ApiTest{

  public QCarDriverTest_g4(IFactory fact, IFactory aux) {
    super(fact, aux);
  }

  @Test
  public void driverTest(){
//    int nThreads = Thread.activeCount();
//    IFactory f = factoryUnderTest;
//    IDriver driver = f.newSmartDriver();
//    driver.startDriverThread(new DummyPlayerChannel());
//    assertEquals(nThreads + 1, Thread.activeCount());
//    driver.stopDriverThread();
//    assertEquals(nThreads, Thread.activeCount());
  }

  private static class DummyPlayerChannel implements IPlayerChannel{
    @Override public ISensors play(IDecision d){return null;}
  }

}
