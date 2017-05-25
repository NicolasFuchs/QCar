package qcar.g4;

import qcar.IDecision;
import qcar.IDriver;
import qcar.IPlayerChannel;

public class ManualDriver implements IDriver {

  private IPlayerChannel pc;
  private int qcarIndex;
  private IDecision currentDecision;
  private int i = 0;

  private volatile boolean finished = false;

  Thread manualDriverThread = new Thread() {
    public void run() {
      while (!finished) {
        pc.play(currentDecision);
      }
    }
  };

  @Override
  public void startDriverThread(IPlayerChannel iPlayerChannel) {
    this.pc = iPlayerChannel;
    manualDriverThread.start();
  }

  @Override
  public void stopDriverThread() {
    finished = true;
  }

  public void sendDecision(IDecision decision){
    this.currentDecision = decision;
    i++;
    System.out.println(i + " - MPC: Decision received");
  }

  public ManualDriver(int qcarIndex){
    this.qcarIndex = qcarIndex;
  }

  public int getQcarIndex(){
    return qcarIndex;
  }
}
