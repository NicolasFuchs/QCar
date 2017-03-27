package qcar.g4;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
    
    WorldManager wm = new WorldManager();

    QCarAnimationPane pane = new QCarAnimationPane(v,Color.WHITE,uiOp,wm);
    pane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    
    Pane animPane = new Pane();
    animPane.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    animPane.setMinSize(750, 500);
    animPane.getChildren().add(pane);
    
    
    Scene scene = new Scene(animPane);
    stage.setScene(scene);
    stage.show();
  }

}