package qcar.g4.ui;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;

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
  }

  public ManualDrivingCtrl(){
  }

  public void setParentCtrl(SimulationCtrl refSim){
    this.refSim = refSim;
  }
}

