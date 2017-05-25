package qcar.g4.ui;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

  private SimulationCtrl refSim;
  private IWorldManager refWM;
  private boolean doOnce;

  @FXML
  void initialize() {

    final Service<Void> playService = new Service<Void>(){
      @Override
      protected Task<Void> createTask() {
        return new Task<Void>(){
          @Override
          protected Void call() throws Exception {
            if(doOnce && !refWM.isWarOver() && refWM.isSimulationOpened()){
              refSim.simulateOneStep(300);
              doOnce = false;
            } else {
              while (!isCancelled() && !doOnce && !refWM.isWarOver() && refWM.isSimulationOpened()) {
                refSim.simulateOneStep(300);
                Thread.sleep(600);
              }
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

    btnOneStep.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        doOnce = true;
        refSim.changeSimMode(false);
        btnPlay.setSelected(false);
        playService.cancel();
        playService.reset();
        playService.start();
      }
    });
  }

  @FXML
  private void handleStopBtn(){
    refSim.endSimulation();
  }


  public void setParentCtrl(SimulationCtrl refSim, IWorldManager wm){
    this.refSim = refSim;
    this.refWM = wm;
  }
}

