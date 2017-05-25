package qcar.g4.ui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import qcar.IDecision;
import qcar.IQCar;
import qcar.g4.Decision;

/**
 * Controller bound to the header of the simulation if there is a manual driver
 */
public class ManualDrivingCtrl {

  @FXML
  private RadioButton radioSide0;

  @FXML
  private RadioButton radioSide1;

  @FXML
  private RadioButton radioSide2;

  @FXML
  private RadioButton radioSide3;

  @FXML
  private RadioButton radioRotation;

  @FXML
  private RadioButton radioTranslation;

  @FXML
  private Slider sliderSpan;

  @FXML
  private Button btnMove;

  private ToggleGroup sides;
  private ToggleGroup movement;
  private SimulationCtrl refSim;
  private IQCar qcar;

  /**
   * Setup the view elements
   */
  @FXML
  void initialize() {
    sides = new ToggleGroup();
    movement = new ToggleGroup();

    radioRotation.setToggleGroup(movement);
    radioTranslation.setToggleGroup(movement);

    radioRotation.setUserData(true);
    radioTranslation.setUserData(false);

    radioSide0.setToggleGroup(sides);
    radioSide1.setToggleGroup(sides);
    radioSide2.setToggleGroup(sides);
    radioSide3.setToggleGroup(sides);

    radioSide0.setUserData(0);
    radioSide1.setUserData(1);
    radioSide2.setUserData(2);
    radioSide3.setUserData(3);

    radioRotation.setSelected(true);
    radioSide0.setSelected(true);

    final Service<Void> moveService = new Service<Void>(){
      @Override
      protected Task<Void> createTask() {
        return new Task<Void>(){
          @Override
          protected Void call() throws Exception {
            refSim.simulateOneStep(getCurrentDecision());
            setMoveLimit();
            return null;
          }
        };
      }
    };

    btnMove.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        moveService.cancel();
        moveService.reset();
        moveService.start();
      }
    });
  }

  /**
   * Set the qcar to be controlled manually
   * @param qcar manually controlled QCar
   */
  public void setManualQcar(IQCar qcar){
    this.qcar = qcar;

    EventHandler radioChangeEvent = new EventHandler() {
      @Override
      public void handle(Event event) {
        setMoveLimit();
      }
    };

    radioSide0.setOnAction(radioChangeEvent);
    radioSide1.setOnAction(radioChangeEvent);
    radioSide2.setOnAction(radioChangeEvent);
    radioSide3.setOnAction(radioChangeEvent);
    radioTranslation.setOnAction(radioChangeEvent);
    radioRotation.setOnAction(radioChangeEvent);

    setMoveLimit();
  }

  /**
   * Set the reference of the parent controller
   * @param refSim parent controller
   */
  public void setParentCtrl(SimulationCtrl refSim){
    this.refSim = refSim;
  }

  /**
   * Handle the end of the simulation
   */
  @FXML
  private void handleStopBtn(){
    refSim.endSimulation();
  }

  /**
   * Set the slider values according the maximum and
   * minimum translation possible for the driven QCar
   */
  private void setMoveLimit(){
    double min = Decision.minAllowedTranslation(qcar, (int) sides.getSelectedToggle().getUserData(),
        (boolean) movement.getSelectedToggle().getUserData());
    double max = Decision.maxAllowedTranslation(qcar, (int) sides.getSelectedToggle().getUserData(),
        (boolean) movement.getSelectedToggle().getUserData());
    sliderSpan.setMin(min);
    sliderSpan.setMax(max);
    sliderSpan.setValue(0.0);
  }

  /**
   * Create a decision with the side, movement type and span currently selected
   * @return manual driver decision
   */
  private IDecision getCurrentDecision(){
    return new Decision((boolean) movement.getSelectedToggle().getUserData(),
        (int) sides.getSelectedToggle().getUserData(),
        sliderSpan.getValue());
  }

}

