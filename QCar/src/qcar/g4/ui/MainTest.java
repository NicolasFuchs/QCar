package qcar.g4.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainTest extends Application {

  private EditorCtrl ctrl;


  @Override
  public void start(Stage stage) {
    try{
      FXMLLoader loader =  new FXMLLoader(Main.class.getResource("resources/fxml/editor.fxml"));
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


  public static void main(String[] args) {
    launch(args);
  }

}