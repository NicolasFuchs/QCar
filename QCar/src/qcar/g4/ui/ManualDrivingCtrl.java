package qcar.g4.ui;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class ManualDrivingCtrl extends VBox {

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

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

  @FXML
  void initialize() {
    assert radioSide0 != null : "fx:id=\"radioSide0\" was not injected: check your FXML file 'manualDriving.fxml'.";
    assert radioSide1 != null : "fx:id=\"radioSide1\" was not injected: check your FXML file 'manualDriving.fxml'.";
    assert radioSide2 != null : "fx:id=\"radioSide2\" was not injected: check your FXML file 'manualDriving.fxml'.";
    assert radioSide3 != null : "fx:id=\"radioSide3\" was not injected: check your FXML file 'manualDriving.fxml'.";
    assert radioRotation != null : "fx:id=\"radioRotation\" was not injected: check your FXML file 'manualDriving.fxml'.";
    assert radioTranslation != null : "fx:id=\"radioTranslation\" was not injected: check your FXML file 'manualDriving.fxml'.";
    assert sliderSpan != null : "fx:id=\"sliderSpan\" was not injected: check your FXML file 'manualDriving.fxml'.";
    assert btnMove != null : "fx:id=\"btnMove\" was not injected: check your FXML file 'manualDriving.fxml'.";

  }

  public ManualDrivingCtrl(){
  }
}

