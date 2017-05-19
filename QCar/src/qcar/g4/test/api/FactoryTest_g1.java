package qcar.g4.test.api;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import qcar.ApiTest;
import qcar.IFactory;
import qcar.IGameProvider;

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

public class FactoryTest_g1 extends ApiTest {

	public FactoryTest_g1(IFactory fact, IFactory aux) {
		super(fact, aux);
	}

	@Test
	public void allStylesExist() {
		IFactory f = factoryUnderTest;
		int nStyles = f.numberOfStyles();
		int nPlayers = 2;
		assertTrue("number of styles must be positive", nStyles > 0);
		for (int i = 0; i < nStyles; i++) {
			IGameProvider g = f.newGameProvider(i);
			// Now just to verify that it is able to build a game:
			assertTrue(g.nextGame(nPlayers) != null);
		}
	}

	@Test
	public void factoryReturnsInstances() {
		int style = 0;
		assertTrue(factoryUnderTest.newGameProvider(style) != null);
		assertTrue(factoryUnderTest.newWorldManager() != null);
		assertTrue(factoryUnderTest.newSmartDriver() != null);
	}

	// for those of us not using ides
	public static void main(String args[]) {
		org.junit.runner.JUnitCore.main(FactoryTest_g1.class.getName());
	}
}
