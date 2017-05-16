package qcar.g4.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import qcar.IFactory;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.g4.Factory;

public class MainTest extends Application {

  private Stage stage;
  private static IFactory factory;
  private EditorCtrl ctrl;


  @Override
  public void start(Stage stage) {
    try{
      FXMLLoader loader =  new FXMLLoader(Main.class.getResource("resources/fxml/editor.fxml"));
      Scene scene = new Scene(loader.load());
      ctrl = loader.getController();
      ctrl.setFactory(factory);
      ctrl.setStage(stage);
      stage.setScene(scene);
      stage.setTitle("Editor");
      stage.show();
    } catch (Exception ex){
      ex.printStackTrace();
    }
  }

  public static void handleNextView(IGameDescription gameDescription, IGameProvider gameProvider,
      int manualControllerIndex){


  }

  public static void main(String[] args) {
    factory = new Factory();
    launch(args);
  }

}