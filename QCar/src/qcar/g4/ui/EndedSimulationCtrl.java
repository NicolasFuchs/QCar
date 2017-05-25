package qcar.g4.ui;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * Controller bound to the endedSimulation view.
 */
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

  /**
   * Set the final leaderboard from the simulation view
   * @param results
   */
  public void setFinalResults(ObservableList results){
    lstResults.setItems(results);
  }

  /**
   * Set the stage to pass it to the next view
   * @param stage
   */
  public void setStage(Stage stage){
    this.stage = stage;
  }

  /**
   * Handle onClick event on the newSimBtn, restart the program from the editor
   */
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

  /**
   * Handle onClick event on the exitBtn, close the program
   */
  @FXML
  private void handleExitBtn(){
    System.exit(0);
  }

}