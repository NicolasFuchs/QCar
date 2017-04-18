package qcar.g4;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import qcar.IDriver;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IWorldManager;
import qcar.ui.*;
import simviou.*;

public class TestGUI extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    stage.setTitle("Table View Sample");
    stage.setWidth(1500);
    stage.setHeight(750);

    int wX0 = -50;      // top left corner x world area
    int wY0 = -50;      // top left corner y world area
    int wX1 = 50;       // bottom right corner x world area
    int wY1 = 50;       // bottom right corner y world area
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
    List<IDriver> dl = new ArrayList<IDriver>(); dl.add(fac.newSmartDriver());
    wm.openNewSimulation(desc, dl);
    
    GridPane bigGrid = new GridPane();
    bigGrid.setPadding(new Insets(25));
    bigGrid.setBorder(new Border(new BorderStroke(Color.ORANGE, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    
    QCarAnimationPane pane = new QCarAnimationPane(v,Color.WHITE,uiOp,wm);
    pane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    
    GridPane controls = new GridPane();
    ImageView arrow_top =       new ImageView(new Image("qcar/g4/arrow_top.png")); arrow_top.setFitHeight(100); arrow_top.setFitWidth(100);
    ImageView arrow_right =     new ImageView(new Image("qcar/g4/arrow_right.png")); arrow_right.setFitHeight(100); arrow_right.setFitWidth(100);
    ImageView arrow_bottom =    new ImageView(new Image("qcar/g4/arrow_bottom.png")); arrow_bottom.setFitHeight(100); arrow_bottom.setFitWidth(100);
    ImageView arrow_left =      new ImageView(new Image("qcar/g4/arrow_left.png")); arrow_left.setFitHeight(100); arrow_left.setFitWidth(100);
    controls.add(arrow_top,1,0); controls.add(arrow_right,2,1); controls.add(arrow_bottom,1,2); controls.add(arrow_left,0,1);
    
    Slider slider = new Slider();
    
    VBox trans_rot = new VBox();
    Button translation = new Button("TRANSLATION");
    Button rotation = new Button("ROTATION");
    trans_rot.getChildren().add(translation); trans_rot.getChildren().add(rotation);

    bigGrid.add(pane,1,0); bigGrid.add(controls,0,1); bigGrid.add(trans_rot,0,0); bigGrid.add(slider,1,1);

    Scene scene = new Scene(bigGrid);
    stage.setScene(scene); stage.setHeight(850); stage.setWidth(1250);
    stage.show();
    
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
          for (qcar.IQCar car : wm.allQCars()) {
            
            for (int vertexId = 0; vertexId < 4; vertexId++) {
              //System.out.println("current location : x = " + car.vertex(vertexId).getX() + " y = " + car.vertex(vertexId).getY());
              //car.
              //System.out.println("new location : x = " + car.vertex(vertexId).getX() + " y = " + car.vertex(vertexId).getY());
            }
          }
          pane.updateLayersTransforms();
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
  }

}