package qcar.g4;

import qcar.IDecision;
import qcar.IDriver;
import qcar.IPlayerChannel;

public class ManualDriver implements IDriver {

  private IPlayerChannel pc;
  private int qcarIndex;

  @Override
  public void startDriverThread(IPlayerChannel iPlayerChannel) {
    this.pc = iPlayerChannel;
  }

  @Override
  public void stopDriverThread() {
  }

  public void sendDecision(IDecision decision){
    pc.play(decision);
    System.out.println("Decision sent");
  }

  public ManualDriver(int qcarIndex){
    this.qcarIndex = qcarIndex;
  }

  public int getQcarIndex(){
    return qcarIndex;
  }
}
