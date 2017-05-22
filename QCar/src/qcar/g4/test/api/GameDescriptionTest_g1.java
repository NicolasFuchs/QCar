package qcar.g4.test.api;

import org.junit.Test;
import org.junit.Assert;

import qcar.*;

public class GameDescriptionTest_g1 extends ApiTest {

    private IGameProvider gp;

    public GameDescriptionTest_g1(IFactory fact, IFactory aux) {
        super(fact, aux);
        this.gp = factoryUnderTest.newGameProvider(0);
    }

    @Test
    public void allQCarsTest() {
//        IGameDescription gd;
//        for(int i = 0; i < 10000; i++) {
//            gd = this.gp.nextGame(i);
//            Assert.assertEquals(i, gd.allQCar().size());
//        }
    }

    // for those of us not using ides
    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(GameDescriptionTest_g1.class.getName());
    }
}
