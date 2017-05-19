package qcar.g4.ui;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class EndedSimulationCtrl {

  private Stage stage;

  @FXML
  private ListView<?> lstResults;

  @FXML
  private Button btnNewSim;

  @FXML
  private Button btnExit;

  @FXML
  void initialize() {}

  public void setFinalResults(ObservableList results){
    lstResults.setItems(results);
  }

  public void setStage(Stage stage){
    this.stage = stage;
  }

  @FXML
  private void handleNewSimBtn(){
    FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/editor.fxml"));
    try{
      Scene scene = new Scene(loader.load());
      EditorCtrl ctrl = loader.getController();
      ctrl.setStage(stage);
      stage.setScene(scene);
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  @FXML
  private void handleExitBtn(){
    System.exit(0);
  }

}