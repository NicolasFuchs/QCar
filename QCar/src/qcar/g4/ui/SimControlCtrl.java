package qcar.g4.ui;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

public class SimControlCtrl {

  @FXML
  private Button btnOneStep;

  @FXML
  private ToggleButton btnPlay;

  @FXML
  private Button btnStop;

  @FXML
  private Slider sliderSpeed;

  private SimulationCtrl refSim;



  @FXML
  void initialize() {
  }

  @FXML
  private void handleSimOneStepBtn(){
    refSim.simulateOneStep(1000);
    System.out.println("Simulate one step pressed!");
  }

  @FXML
  private void handlePlayBtn(){

  }

  @FXML
  private void handleStopBtn(){
    refSim.endSimulation();
  }

  public void setParentCtrl(SimulationCtrl refSim){
    this.refSim = refSim;
  }
}

