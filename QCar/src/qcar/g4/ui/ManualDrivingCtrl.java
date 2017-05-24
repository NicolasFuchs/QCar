package qcar.g4.ui;


import java.awt.geom.Point2D;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import qcar.IQCar;
import qcar.g4.Decision;

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

  private int sideId;
  private boolean isAngledMovement;
  private double requestedTranslation;

  @FXML
  void initialize() {
    sides = new ToggleGroup();
    movement = new ToggleGroup();

    radioRotation.setToggleGroup(movement);
    radioTranslation.setToggleGroup(movement);

    radioSide0.setToggleGroup(sides);
    radioSide1.setToggleGroup(sides);
    radioSide2.setToggleGroup(sides);
    radioSide3.setToggleGroup(sides);

    radioSide0.setSelected(true);
    radioRotation.setSelected(true);

    final Service<Void> moveService = new Service<Void>(){
      @Override
      protected Task<Void> createTask() {
        return new Task<Void>(){
          @Override
          protected Void call() throws Exception {
            System.out.println("move !");
            refSim.simulateOneStep(new Decision(isAngledMovement, sideId, requestedTranslation));
            return null;
          }
        };
      }
    };

    btnMove.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        moveService.cancel();
        moveService.reset();
        if(radioSide0.isSelected())
          sideId = 0;
        else if (radioSide1.isSelected())
          sideId = 1;
        else if (radioSide2.isSelected())
          sideId = 2;
        else
          sideId = 3;

        if(radioRotation.isSelected())
          isAngledMovement = true;
        else
          isAngledMovement = false;

        requestedTranslation = sliderSpan.getValue();
        moveService.start();
      }
    });

  }

  public void setManualQcar(IQCar qcar){
    this.qcar = qcar;
    // setMoveLimit();
  }

  public void setParentCtrl(SimulationCtrl refSim){
    this.refSim = refSim;
  }

  @FXML
  private void handleStopBtn(){
    refSim.endSimulation();
  }

  public void setMoveLimit(){
    // TODO calculate maxSpan and minSpan for every cases
    double maxSpan = 0;
    double minSpan = 0;
    sliderSpan.setMin(minSpan);
    sliderSpan.setMax(maxSpan);
    sliderSpan.setValue(0.0);
  }

}

