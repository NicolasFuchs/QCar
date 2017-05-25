package qcar.g4;

import java.util.concurrent.Semaphore;
import qcar.IDecision;
import qcar.IPlayerChannel;
import qcar.ISensors;

/**
 * This class serve as a communication channel between the driver thread and the world
 * manager.
 */
public class PlayerChannel implements IPlayerChannel {

  private IDecision decision ;
  private ISensors sensors ;
  private Semaphore sem;
  private Semaphore wmSem;

  /**
   * This methods is called after every decision by the driver thread.
   * The driver thread is locked until the wm sends the sensors to every driver
   * then the wm release the driver who will call the method again with his decision
   * and release one permit to signal to the wm he's taken a decision.
   * @param decision Decision made by the driver for the current step
   * @return sensors generated for the current step
   */
  @Override
  public ISensors play(IDecision decision) {
    this.decision = decision;
    wmSem.release(1);
    sem.acquireUninterruptibly(); // locked until the wm send the new sensors
    return sensors;
  }

  /**
   * Get the driver's decision. Called by the world manager.
   * @return driver's current decision
   */
  public IDecision getDecision(){
    return decision;
  }

  /**
   * Give the sensor to the driver. Called by the world manager.
   * @param sensors sensors for current step
   */
  public void sendSensors(ISensors sensors){
    this.sensors = sensors;
  }

  /**
   * Release the driver from the lock.
   */
  public void release(){
    sem.release();
  }

  /**
   * Constructor
   * @param sensors sensors for step 0
   * @param wmSem semaphore used by the worldmanager to wait on the driver for their decisions
   */
  public PlayerChannel(ISensors sensors, Semaphore wmSem){
    this.sensors = sensors;
    this.wmSem = wmSem;
    sem = new Semaphore(0);
  }
  
}
