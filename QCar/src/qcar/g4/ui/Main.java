package qcar.g4.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import qcar.IDriver;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IWorldManager;
import qcar.g4.Factory;
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
    IGameProvider gp = fac.newGameProvider(0);
    IWorldManager wm = fac.newWorldManager();
    IGameDescription desc = gp.nextGame(1);
    List<IDriver> dl = new ArrayList<IDriver>(); dl.add(fac.newSmartDriver());
    wm.openNewSimulation(desc, dl);
    while(!wm.isWarOver()) {
    //  wm.simulateOneStep(collectiveDelayInMicroSeconds);
    }
    wm.closeSimulation();
    
    GridPane bigGrid = new GridPane();
    bigGrid.setPadding(new Insets(25));
    bigGrid.setBorder(new Border(new BorderStroke(Color.ORANGE, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    
    QCarAnimationPane pane = new QCarAnimationPane(v,Color.WHITE,uiOp,wm);
    pane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    
    bigGrid.add(pane,1,0);
    
    Scene scene = new Scene(bigGrid);
    stage.setScene(scene); stage.setHeight(850); stage.setWidth(1250);
    stage.show();
    
  }
}
