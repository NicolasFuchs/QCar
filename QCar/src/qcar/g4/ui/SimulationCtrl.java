package qcar.g4.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
import javafx.stage.Stage;
import qcar.ICollision;
import qcar.IDecision;
import qcar.IQCar;
import qcar.IWorldManager;
import qcar.g4.ManualDriver;
import qcar.ui.QCarAnimationPane;
import simviou.LogPanel;
import simviou.ViewPort;

/**
 * Controller bound to the simulation view.
 */
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
   * This methods retrieve the information needed for the view from the world manager
   * and set up the simviou panels and the header.
   * @param wm World Manager
   * @param manualDriver Manual Driver thread
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

    logPanel = new LogPanel(805, 100);
    paneConsole.getChildren().add(logPanel);

    uiOp = new UIOp(wm, logPanel);

    world = new QCarAnimationPane(viewPort, Color.WHITE, uiOp, worldManager);
    paneQCar.getChildren().add(world);

    ArrayList<IQCar> pilotedQcars = new ArrayList<>();

    for(IQCar q : wm.allQCars())
      if(q.nature().isDriven())
        pilotedQcars.add(q);

    ObservableList<IQCar> obQCars = FXCollections.observableList(pilotedQcars);
    SortedList sortedQCars = obQCars.sorted(
        Comparator.<IQCar>comparingInt(p1 -> p1.score()).thenComparing(p2 ->p2.score()));
    lstLeaderboard.setItems(sortedQCars);
  }

  /**
   * Set the stage so we can pass it to the next view later
   * @param stage current stage
   */
  public void setStage(Stage stage){
    this.stage = stage;
  }

  /**
   * This method load the header for a simulation without manually controlled QCar
   * @return HBox containing all the controls
   * @throws IOException exception with the fxml file
   */
  private HBox getSimControl() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/simControl.fxml"));
    HBox header = loader.load();
    headerCtrl = loader.getController();
    headerCtrl.setParentCtrl(this, worldManager);
    return header;
  }

  /**
   * This method load the header for a simulation with a manually controlled QCar
   * @return HBox containing all the controls
   * @throws IOException exception with the fxml file
   */
  private HBox getManualControl() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/manualDriving.fxml"));
    HBox header = loader.load();
    ManualDrivingCtrl ctrl = (ManualDrivingCtrl) loader.getController();
    ctrl.setManualQcar(worldManager.allQCars().get(manualDriver.getQcarIndex()));
    ctrl.setParentCtrl(this);
    return header;
  }

  /**
   * Simulate one step, log the changes after the step and test if the simulation is finished.
   * @param ms collectiveDelayInMicroSeconds
   */
  public void simulateOneStep(long ms){
    worldManager.simulateOneStep(ms);
    log();
    isSimulationOver();
  }

  /**
   * Simulate one step with a decision manually taken
   * @param manualDecision Decision taken by the user
   */
  public void simulateOneStep(IDecision manualDecision){
    manualDriver.sendDecision(manualDecision);
    simulateOneStep(100);
  }

  /**
   * End the current simulation and go to the leaderboard view.
   */
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

  /**
   * After each step, find the new events and add them to the logPane
   */
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

  /**
   * Toggle between step by step mode and simulation running
   * @param isAnimationRunning if true set ANIMATION_RUNNING in the UIOp
   */
  public void changeSimMode(boolean isAnimationRunning){
    uiOp.setAnimationRunning(isAnimationRunning);
  }

  /**
   * Test if the simulation is still going on, if not inform the user
   * and end the simulation
   */
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
