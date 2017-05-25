package qcar.g4.ui;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import qcar.IWorldManager;

/**
 * Controller bound to the header of the simulation if there are no manual driver
 */
public class SimControlCtrl {

  @FXML
  private Button btnOneStep;

  @FXML
  private ToggleButton btnPlay;

  private SimulationCtrl refSim;
  private IWorldManager refWM;
  private boolean doOnce;   // used when btnOneStep is pressed

  /**
   * Bind action to the different elements
   */
  @FXML
  void initialize() {

    // This service continuously call simulateOneStep if playBtn is selected
    // or call simulateOneStep once if btnOneStep is clicked
    final Service<Void> playService = new Service<Void>(){
      @Override
      protected Task<Void> createTask() {
        return new Task<Void>(){
          @Override
          protected Void call() throws Exception {
            if(doOnce && !refWM.isWarOver() && refWM.isSimulationOpened()){
              btnOneStep.setDisable(true);
              refSim.simulateOneStep(10);
              Thread.sleep(100);
              btnOneStep.setDisable(false);
              doOnce = false;
            } else {
              while (!isCancelled() && !doOnce && !refWM.isWarOver() && refWM.isSimulationOpened()) {
                refSim.simulateOneStep(10);
                Thread.sleep(40);
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

  /**
   * Button stop on click handler
   */
  @FXML
  private void handleStopBtn(){
    refSim.endSimulation();
  }


  /**
   * Set up a reference to the worldmanager and to the parent view's controller
   * @param refSim parent view's controller
   * @param wm world manager
   */
  public void setParentCtrl(SimulationCtrl refSim, IWorldManager wm){
    this.refSim = refSim;
    this.refWM = wm;
  }
}

