package qcar.g4.test.api;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import qcar.ApiTest;
import qcar.IFactory;
import qcar.IGameDescription;
import qcar.IGameProvider;

public class MyGameProviderTest_g5 extends ApiTest{
    static final int N = 10;
    static final int QCARLIMIT = 500;
    static final Random  RDM = new Random();

    IFactory f = factoryUnderTest;

    public MyGameProviderTest_g5(IFactory fact, IFactory aux) {
        super(fact, aux);

    }
    
    /**
     * @author Olivier
     * this test verify that the number of QCar created is the same as the 
     * one given as parameters in nextGame(int)
     */
    @Test
    public void testNumberOfQcar()  {
        int nStyles = f.numberOfStyles();
        IGameProvider gp;
        IGameDescription gd;
        
        int rdmNumber;
        for (int j=0; j < N; j++)   {
            rdmNumber = RDM.nextInt(QCARLIMIT)+1;
            for (int i=0; i<nStyles; i++)   {
                gp = f.newGameProvider(i);
                gd = gp.nextGame(rdmNumber);
                assertTrue(gd.allQCar().size() >= rdmNumber);
            }  
        }
    }

}
