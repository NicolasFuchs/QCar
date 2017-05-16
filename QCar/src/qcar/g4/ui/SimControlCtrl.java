package qcar.g4.ui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SimControlCtrl extends VBox {

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private HBox panelHeader;

  @FXML
  private Button btnOneStep;

  @FXML
  private ToggleButton btnPlay;

  @FXML
  private ToggleButton btnPause;

  @FXML
  private ToggleButton btnStop;

  @FXML
  private Slider sliderSpeed;

  @FXML
  void initialize() {
    System.out.println("SimControlCtrl instancied");
  }

  public SimControlCtrl(){
    FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/simControl.fxml"));
    loader.setController(this);
    try{
      loader.load();
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}

