package qcar.g4;

import qcar.IDecision;
import qcar.IDriver;
import qcar.IPlayerChannel;

/**
 * Created by Karim on 22.05.17.
 */
public class ManualDriver implements IDriver {

  IPlayerChannel pc;

  @Override
  public void startDriverThread(IPlayerChannel iPlayerChannel) {
    this.pc = iPlayerChannel;
  }

  @Override
  public void stopDriverThread() {

  }

  public void sendDecision(IDecision decision){
    pc.play(decision);
  }
}
