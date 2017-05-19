package qcar.g4.test.api;

import org.junit.Test;
import org.junit.Assert;

import qcar.ApiTest;
import qcar.IFactory;
import qcar.IGameProvider;

public class GameProviderTest_g1 extends ApiTest {

    private IGameProvider gp;

    public GameProviderTest_g1(IFactory fact, IFactory aux) {
        super(fact, aux);
        this.gp = factoryUnderTest.newGameProvider(0);
    }

    @Test
    public void nextGameTest() {
        for(int i = 1; i < 100; i++) {
            Assert.assertNotNull(this.gp.nextGame(i));
        }
    }

    // for those of us not using ides
    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(GameProviderTest_g1.class.getName());
    }
}
