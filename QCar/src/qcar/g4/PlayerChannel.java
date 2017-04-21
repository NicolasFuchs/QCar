package qcar.g4;

import qcar.IDecision;
import qcar.IPlayerChannel;
import qcar.ISensors;

public class PlayerChannel implements IPlayerChannel {

  IDecision decision ;
  ISensors sensors ;

  @Override
  public ISensors play(IDecision decision) {  
    // 1 : re�oit une d�cision du Driver
    this.decision = decision ;
    // 2 : envoie la d�cision au WorldManager (il la r�cup�re)    
    // 3 : BLOCK : attend les senseurs du WorldManager 
    // (le world manager setSensors) + unlock
    // 4 : envoie les senseurs au Driver
    return sensors;
  }
  
}
