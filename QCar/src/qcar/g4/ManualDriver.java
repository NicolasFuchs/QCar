package qcar.g4;

import qcar.IDecision;
import qcar.IDriver;
import qcar.IPlayerChannel;

/**
 * This class act as driver but for the qcar manually controlled by the IHM
 */
public class ManualDriver implements IDriver {

  private IPlayerChannel pc;
  private int qcarIndex;
  private IDecision currentDecision;
  private int i = 0;

  private volatile boolean finished = false;

  /**
   * This thread act as an AI driver
   */
  Thread manualDriverThread = new Thread() {
    public void run() {
      while (!finished) {
        pc.play(currentDecision);
      }
    }
  };

  /**
   * Start the driver thread
   * @param iPlayerChannel playerChannel for this driver
   */
  @Override
  public void startDriverThread(IPlayerChannel iPlayerChannel) {
    this.pc = iPlayerChannel;
    manualDriverThread.start();
  }

  /**
   * Stop the driver thread
   */
  @Override
  public void stopDriverThread() {
    finished = true;
  }

  /**
   * Pass the decision to the thread from the IHM
   * @param decision decision made manually
   */
  public void sendDecision(IDecision decision){
    this.currentDecision = decision;
    i++;
    //System.out.println(i + " - MPC: Decision received");
  }

  /**
   * Constructor of the class
   * @param qcarIndex Index of the QCar manually driven
   */
  public ManualDriver(int qcarIndex){
    this.qcarIndex = qcarIndex;
  }

  /**
   * @return manually driver QCar index
   */
  public int getQcarIndex(){
    return qcarIndex;
  }
}
