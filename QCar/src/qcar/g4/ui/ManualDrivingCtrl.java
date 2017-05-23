package qcar.g4.ui;


import java.awt.geom.Point2D;
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
  }

  public void setManualQcar(IQCar qcar){
    this.qcar = qcar;
    // setMoveLimit();
  }

  public void setParentCtrl(SimulationCtrl refSim){
    this.refSim = refSim;
  }

  public void setMoveLimit(){
    // TODO calculate maxSpan and minSpan for every cases
    double maxSpan = 0;
    double minSpan = 0;
    if (radioRotation.isSelected()) {   // case rotation

      if(radioSide0.isSelected()){

      } else if (radioSide1.isSelected()) {

      } else if (radioSide2.isSelected()) {

      } else if (radioSide3.isSelected()) {

      }

    } else {    // case Translation

      if(radioSide0.isSelected() || radioSide2.isSelected()){
        Point2D p0 =qcar.vertex(0);
        Point2D p3 = qcar.vertex(3);
        maxSpan = qcar.nature().maxSideLength() - p0.distance(p3);
      } else if (radioSide1.isSelected() || radioSide3.isSelected()) {
        Point2D p0 =qcar.vertex(0);
        Point2D p1 = qcar.vertex(1);
        maxSpan = qcar.nature().maxSideLength() - p0.distance(p1);
      }

    }
    sliderSpan.setMin(minSpan);
    sliderSpan.setMax(maxSpan);
    sliderSpan.setValue(0.0);
    System.out.println("");
  }

  @FXML
  private void handleMoveButton(){
    int sideId;
    boolean isAngledMovement;
    double requestedTranslation;

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

    refSim.simulateOneStep(new Decision(isAngledMovement, sideId, requestedTranslation));
  }
}

