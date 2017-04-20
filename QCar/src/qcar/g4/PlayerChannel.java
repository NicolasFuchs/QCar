package qcar.g4;

import qcar.IDecision;
import qcar.IPlayerChannel;
import qcar.ISensors;

public class PlayerChannel implements IPlayerChannel {
  IDecision decision ;
  ISensors sensors ;
  @Override
  public ISensors play(IDecision decision) {  
    // 1 : reçoit une décision du Driver
    // 2 : envoie la décision au WorldManager
    // 3 : BLOCK : attend les senseurs du WorldManager
    // 4 : envoie les senseurs au Driver
    return null;
  }

}
