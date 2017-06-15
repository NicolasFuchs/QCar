package qcar.g4.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class of the program.
 */
public class Main extends Application {

  private EditorCtrl ctrl;

  /**
   * This method start the IHM. It loads the editor pass him the stage.
   * @param stage the main window
   */
  @Override
  public void start(Stage stage) {
    try{
      FXMLLoader loader =  new FXMLLoader(getClass().getResource("resources/fxml/editor.fxml"));
      Scene scene = new Scene(loader.load());
      ctrl = loader.getController();
      ctrl.setStage(stage);
      stage.setScene(scene);
      stage.setTitle("Editor");
      stage.show();
    } catch (Exception ex){
      ex.printStackTrace();
    }
  }

  /**
   * Starting point of the program
   * @param args the command line params
   */
  public static void main(String[] args) {
    launch(args);
  }

}