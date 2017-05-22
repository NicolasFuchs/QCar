package qcar.g4;

import java.util.concurrent.Semaphore;
import qcar.IDecision;
import qcar.IPlayerChannel;
import qcar.ISensors;

public class PlayerChannel implements IPlayerChannel {

  private IDecision decision ;
  private ISensors sensors ;
  private Semaphore sem;
  private Semaphore mutex;

  /*
    1 : reçoit une décision du Driver
    2 : envoie la décision au WorldManager (il la récupère)
    3 : BLOCK : attend les senseurs du WorldManager
    (le world manager setSensors) + unlock
    4 : envoie les senseurs au Driver
   */


  @Override
  public ISensors play(IDecision decision) {
    this.decision = decision;
    sem.acquireUninterruptibly(); // locked until the wm send the new sensors
    return sensors;
  }

  public IDecision getDecision(){
    return decision;
  }

  public void sendSensors(ISensors sensors){
    this.sensors = sensors;
  }

  public void release(){
    sem.release();
  }

  public PlayerChannel(ISensors sensors){
    this.sensors = sensors;
    sem = new Semaphore(0);
  }
  
}
