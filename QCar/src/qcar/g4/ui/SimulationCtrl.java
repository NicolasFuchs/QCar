package qcar.g4.ui;

import java.io.IOException;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import qcar.IDecision;
import qcar.IQCar;
import qcar.IWorldManager;
import qcar.ui.QCarAnimationPane;
import simviou.AnimationPane;
import simviou.LogPanel;
import simviou.ObservationPolicy;
import simviou.UIOperationsWithDefaults;
import simviou.ViewPort;

public class SimulationCtrl {

  private IWorldManager worldManager;
  private int manualCarIndex;
  private Stage stage;
  private ViewPort viewPort;
  private QCarAnimationPane world;
  private LogPanel logPanel;

  @FXML
  private Pane headerPane;

  @FXML
  private ListView<IQCar> lstLeaderboard;

  @FXML
  private Pane paneConsole;

  @FXML
  private Pane paneQCar;

  @FXML
  void initialize() {

  }


  /**
   * This method get all the objects needed for the simulation from the previous view
   */
  public void setWM(IWorldManager wm, int manualCarIndex){

    worldManager = wm;
    this.manualCarIndex = manualCarIndex;

    try{
      HBox header = (manualCarIndex == -1) ? getSimControl() : getManualControl();
      headerPane.getChildren().add(header);
    } catch (Exception e){
      e.printStackTrace();
    }

    Rectangle2D r = wm.boundingBox();
    viewPort = new ViewPort((int) r.getMinY(), (int) r.getMaxY(), (int) r.getMinX(), (int) r.getMaxX(), 610, 350, true);

    UIOperationsWithDefaults uiOp = new UIOperationsWithDefaults() {
      @Override
      public Rectangle2D worldBoundingBox() {
        return wm.boundingBox();
      }
      @Override
      public AnimationPane newAnimationPane(ViewPort vPort) {
        return null;
      }
      @Override
      public boolean isActive(int z) {
        return true;
      }
      @Override
      public ObservationPolicy observationPolicy(){
        return ObservationPolicy.WORLD_BOUNDARY;
      }
    };

    world = new QCarAnimationPane(viewPort, Color.WHITE, uiOp, worldManager);
    paneQCar.getChildren().add(world);

    logPanel = new LogPanel(805, 100);
    paneConsole.getChildren().add(logPanel);

    ObservableList<IQCar> obQCars = FXCollections.observableList(worldManager.allQCars());
    SortedList sortedQCars = obQCars.sorted(
        Comparator.<IQCar>comparingInt(p1 -> p1.score()).thenComparing(p2 ->p2.score()));
    lstLeaderboard.setItems(sortedQCars);
  }

  public void setStage(Stage stage){
    this.stage = stage;
  }

  private HBox getSimControl() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/simControl.fxml"));
    HBox header = loader.load();
    SimControlCtrl ctrl = loader.getController();
    ctrl.setParentCtrl(this);
    return header;
  }

  private HBox getManualControl() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/manualDriving.fxml"));
    HBox header = loader.load();
    ManualDrivingCtrl ctrl = (ManualDrivingCtrl) loader.getController();
    ctrl.setParentCtrl(this);
    return header;
  }

  public void simulateOneStep(){

  }

  public void simulateOneStep(long ms){
    Runnable r = new Runnable() {
      @Override
      public void run() {
        worldManager.simulateOneStep(ms);
      }
    };
    new Thread(r).start();
  }

  public void simulateOneStep(IDecision manualDecision){
  }

  public void endSimulation(){
    worldManager.closeSimulation();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/endedSimulation.fxml"));
    try{
      Scene scene = new Scene(loader.load());
      EndedSimulationCtrl ctrl = loader.getController();
      ctrl.setFinalResults(lstLeaderboard.getItems());
      ctrl.setStage(stage);
      stage.setScene(scene);
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  @FXML
  private void handleListQCarClick(){
  }
}
