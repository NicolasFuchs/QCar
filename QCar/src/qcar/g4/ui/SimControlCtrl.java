package qcar.g4.ui;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import qcar.IQCar;
import qcar.IWorldManager;

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
  private IWorldManager refWM;


  @FXML
  void initialize() {
    Service<Void> playService = new Service<Void>(){
      @Override
      protected Task<Void> createTask() {
        return new Task<Void>(){
          @Override
          protected Void call() throws Exception {
            while(!isCancelled() && !refWM.isWarOver() && refWM.isSimulationOpened()) {
              refSim.simulateOneStep(300);
              Thread.sleep(600);
            }
            return null;
          }
        };
      }
    };

    btnPlay.selectedProperty().addListener((obs, oldVal, newVal) -> {
      if(newVal) {
        refSim.changeSimMode(true);
        playService.reset();
        playService.start();
      }
      else{
        playService.cancel();
        refSim.changeSimMode(false);
      }
    });
  }

  @FXML
  private void handleSimOneStepBtn(){
    refSim.simulateOneStep(1000);
  }

  @FXML
  private void handleStopBtn(){
    refSim.endSimulation();
  }


  public void setParentCtrl(SimulationCtrl refSim, IWorldManager wm){
    this.refSim = refSim;
    this.refWM = wm;
  }

  public boolean isPlayPressed(){
    return btnPlay.isSelected();
  }
}

