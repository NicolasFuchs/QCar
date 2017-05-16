package qcar.g4.ui;

import java.util.Arrays;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import qcar.IQCar;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IWorldManager;
import qcar.g4.Factory;
import qcar.g4.QCar;
import qcar.ui.*;
import simviou.*;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    stage.setTitle("Table View Sample");
    stage.setWidth(1500);
    stage.setHeight(750);

    int wX0 = 0;      // top left corner x world area
    int wY0 = 0;      // top left corner y world area
    int wX1 = 100;       // bottom right corner x world area
    int wY1 = 100;       // bottom right corner y world area
    int sWidth = 750;   // screen area x-axis
    int sHeight = 500;  // screen area y-axis
    boolean keepRatio = true;
    ViewPort v = new ViewPort(wX0, wX1, wY0, wY1, sWidth, sHeight, keepRatio);

    UIOperationsWithDefaults uiOp = new UIOperationsWithDefaults() {
      @Override
      public Rectangle2D worldBoundingBox() {
        double minX = 0, minY = 0, width = 200, height = 200;
        return new Rectangle2D(minX,minY,width,height);
      }
      @Override
      public AnimationPane newAnimationPane(ViewPort vPort) {
        return null;
      }
      @Override
      public boolean isActive(int z) {
        return true;
      }
    };
    
    Factory fac = new Factory();
    IGameProvider gp = fac.newGameProvider(10);
    IWorldManager wm = fac.newWorldManager();
    IGameDescription desc = gp.nextGame(10);
    wm.openNewSimulation(desc, Arrays.asList(fac.newSmartDriver()));
    //wm.closeSimulation();

    try {
      FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("resources/fxml/simulation.fxml"));
      GridPane fxmlpane = (GridPane) fxmlloader.load();
      
      QCarAnimationPane pane = new QCarAnimationPane(v,Color.WHITE,uiOp,wm);
      ImageView arrow_top = (ImageView) fxmlloader.getNamespace().get("arrow_top");
      ImageView arrow_right = (ImageView) fxmlloader.getNamespace().get("arrow_right");
      ImageView arrow_bottom = (ImageView) fxmlloader.getNamespace().get("arrow_bottom");
      ImageView arrow_left = (ImageView) fxmlloader.getNamespace().get("arrow_left");
      Button translation = (Button) fxmlloader.getNamespace().get("translation");
      Button rotation = (Button) fxmlloader.getNamespace().get("rotation");
      Button simulOneStep = (Button) fxmlloader.getNamespace().get("simulOneStep");
      Pane qcarPane = (Pane) fxmlloader.getNamespace().get("qcarPane");
      qcarPane.getChildren().add(pane);
      
      arrow_top.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            System.out.println("arrow_top clicked!");
        }
      });
      arrow_right.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          System.out.println("arrow_right clicked!");
        }
      });
      arrow_bottom.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          System.out.println("arrow_bottom clicked!");
        }
      });
      arrow_left.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          System.out.println("arrow_left clicked!");
        }
      });
      translation.setOnMouseClicked(new EventHandler<MouseEvent>() {
        boolean is_trans_selected = false;
        @Override
        public void handle(MouseEvent event) {
          if (is_trans_selected) {
            is_trans_selected = false;
            rotation.setDisable(false);
          } else {
            is_trans_selected = true;
            rotation.setDisable(true);
          }
        }
      });
      rotation.setOnMouseClicked(new EventHandler<MouseEvent>() {
        boolean is_rot_selected = false;
        @Override
        public void handle(MouseEvent event) {
          if (is_rot_selected) {
            is_rot_selected = false;
            translation.setDisable(false);
          } else {
            is_rot_selected = true;
            translation.setDisable(true);
          }
        }
      });
      simulOneStep.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
          //wm.simulateOneStep(0);
          for (IQCar car : wm.allQCars()) {
            ((QCar)car).update(true, 0, 5);
          }
          pane.refreshView();
        }
      });
      
      Scene scene = new Scene(fxmlpane);
      stage.setScene(scene); stage.setHeight(850); stage.setWidth(1250);
      stage.show();
      
    } catch (Exception e) {
      System.out.println("The FXMLLoader has failed to load the fxml file view.fxml!");
      e.printStackTrace();
    }
  }

}