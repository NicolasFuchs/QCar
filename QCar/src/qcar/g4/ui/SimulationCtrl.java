package qcar.g4.ui;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.PopupBuilder;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import qcar.ICollision;
import qcar.IDecision;
import qcar.IQCar;
import qcar.IWorldManager;
import qcar.g4.ManualDriver;
import qcar.ui.QCarAnimationPane;
import simviou.AnimationPane;
import simviou.LogPanel;
import simviou.ObservationPolicy;
import simviou.UIOperations;
import simviou.UIOperationsWithDefaults;
import simviou.ViewPort;

public class SimulationCtrl {

  private IWorldManager worldManager;
  private ManualDriver manualDriver;
  private Stage stage;
  private ViewPort viewPort;
  private QCarAnimationPane world;
  private LogPanel logPanel;
  private UIOp uiOp;
  private SimControlCtrl headerCtrl;

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
  public void setWM(IWorldManager wm, ManualDriver manualDriver){

    worldManager = wm;
    this.manualDriver = manualDriver;

    try{
      HBox header = (manualDriver == null) ? getSimControl() : getManualControl();
      headerPane.getChildren().add(header);
    } catch (Exception e){
      e.printStackTrace();
    }

    Rectangle2D r = wm.boundingBox();
    viewPort = new ViewPort((int) r.getMinY(), (int) r.getMaxY(), (int) r.getMinX(), (int) r.getMaxX(), 610, 350, true);

    uiOp = new UIOp(wm, logPanel);

    world = new QCarAnimationPane(viewPort, Color.WHITE, uiOp, worldManager);
    paneQCar.getChildren().add(world);

    logPanel = new LogPanel(805, 100);
    paneConsole.getChildren().add(logPanel);

    ArrayList<IQCar> pilotedQcars = new ArrayList<>();

    for(IQCar q : wm.allQCars())
      if(q.nature().isDriven())
        pilotedQcars.add(q);

    ObservableList<IQCar> obQCars = FXCollections.observableList(pilotedQcars);
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
    headerCtrl = loader.getController();
    headerCtrl.setParentCtrl(this, worldManager);
    return header;
  }

  private HBox getManualControl() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/manualDriving.fxml"));
    HBox header = loader.load();
    ManualDrivingCtrl ctrl = (ManualDrivingCtrl) loader.getController();
    ctrl.setManualQcar(worldManager.allQCars().get(manualDriver.getQcarIndex()));
    ctrl.setParentCtrl(this);
    return header;
  }

  public void simulateOneStep(long ms){
    worldManager.simulateOneStep(ms);
    log();
    isSimulationOver();
  }

  public void simulateOneStep(IDecision manualDecision){
    manualDriver.sendDecision(manualDecision);
    simulateOneStep(1000);
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

  private void log(){
    String builder = "Step " + worldManager.stepNumber();
    if(worldManager.allNewCollisions().size() == 0)
      builder += " : no collision detected";
    else {
      builder += " : ";
      for(ICollision col : worldManager.allNewCollisions())
        builder += "\t QCar n°" + col.hittingQCarId() + " has hit QCar n°" + col.hitQCarId() + "\n";
    }
    logPanel.addEntry(builder);
  }

  public void changeSimMode(boolean isAnimationRunning){
    uiOp.setAnimationRunning(isAnimationRunning);
  }

  private void isSimulationOver(){
    if(worldManager.isWarOver()){
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Simulation finished");
      alert.setHeaderText(null);
      alert.setContentText("The war is over!");
      alert.showAndWait();
      endSimulation();
    }
  }

}
