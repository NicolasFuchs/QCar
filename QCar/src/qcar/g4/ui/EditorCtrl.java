package qcar.g4.ui;


import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import qcar.IDriver;
import qcar.IFactory;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IQCar;
import qcar.IWorldManager;
import qcar.g4.Factory;

public class EditorCtrl {

  public static final int NO_MANUAL_DRIVER = -1;

  private IFactory fact;
  private IGameProvider gameProvider;
  private IGameDescription gameDescription;

  private int manualDriverIndex;
  private int nDrivers;

  private Stage stage;

  @FXML
  private ComboBox<?> comboStyle;

  @FXML
  private TextField txtDriverNum;

  @FXML
  private Button btnApply;

  @FXML
  private Button btnPlay;

  @FXML
  private ListView<IQCar> listQCar;

  @FXML
  private TextField txtQCarId;

  @FXML
  private TextField txtMaxSide;

  @FXML
  private TextField txtMinArea;

  @FXML
  private CheckBox checkDriven;

  @FXML
  private CheckBox checkParking;

  @FXML
  private CheckBox checkVertex;

  @FXML
  private CheckBox checkSide;

  @FXML
  private CheckBox checkIsManual;

  @FXML
  void initialize() {
    setFactory(new Factory());
    btnPlay.setDisable(true);
    manualDriverIndex = NO_MANUAL_DRIVER;
    checkIsManual.setVisible(false);
  }

  @FXML
  private void handleBtnApply(){
    try{
      nDrivers = Integer.parseInt(txtDriverNum.getText());
      int selectedStyle = (Integer) comboStyle.getSelectionModel().getSelectedItem();
      gameProvider = fact.newGameProvider(selectedStyle);
      gameDescription = gameProvider.nextGame(nDrivers);
      ObservableList<IQCar> obQCars = FXCollections.observableList(gameDescription.allQCar());
      listQCar.setItems(obQCars);
      emptyFields();
      btnPlay.setDisable(false);
      System.out.println("Apply for : " + nDrivers + " QCars !");
    } catch (NumberFormatException e) {
      System.out.println("Please enter a valid number of QCars");
    }
  }

  @FXML
  private void handleBtnPlay(){
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/simulation.fxml"));
      Scene scene = new Scene(loader.load());
      SimulationCtrl ctrl = loader.getController();
      stage.hide();

      IWorldManager wm  = fact.newWorldManager();

      List<IDriver> driverList = new ArrayList<>();
      for(int i = 0; i < nDrivers; i++)
        driverList.add(fact.newSmartDriver());

      wm.openNewSimulation(gameDescription, driverList);

      ctrl.setWM(wm, manualDriverIndex);
      ctrl.setStage(stage);
      stage.setTitle("Simulation");
      stage.setScene(scene);
      stage.show();
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  @FXML
  private void handleListSelection() {
    IQCar qcar = listQCar.getSelectionModel().getSelectedItem();

    txtQCarId.setText(Integer.toString(qcar.nature().qCarId()));
    txtMaxSide.setText(Double.toString(qcar.nature().maxSideLength()));
    txtMinArea.setText(Double.toString(qcar.nature().minArea()));

    checkDriven.setSelected(qcar.nature().isDriven());
    checkParking.setSelected(qcar.nature().isParkingTarget());
    checkSide.setSelected(qcar.nature().isSideTarget());
    checkVertex.setSelected(qcar.nature().isVertexTarget());

    checkIsManual.setVisible(qcar.nature().isDriven());

    if(manualDriverIndex == qcar.nature().qCarId())
      checkIsManual.setSelected(true);
    else
      checkIsManual.setSelected(false);
  }

  @FXML
  private void handleManualDriverSelection(){
    if(checkIsManual.isSelected())
      manualDriverIndex = listQCar.getSelectionModel().getSelectedItem().nature().qCarId();
    else
      manualDriverIndex = NO_MANUAL_DRIVER;
  }

  public void setFactory(IFactory fact){
    this.fact = fact;
    List<Integer> styles = new ArrayList<Integer>();
    for(int i = 0; i < fact.numberOfStyles(); i++)
      styles.add(i);
    ObservableList obStyles = FXCollections.observableList(styles);
    comboStyle.getItems().clear();
    comboStyle.setItems(obStyles);
    comboStyle.getSelectionModel().select(0);
  }

  public void setStage(Stage stage){
    this.stage = stage;
  }

  private void emptyFields(){
    txtQCarId.clear();
    txtMaxSide.clear();
    txtMinArea.clear();
    checkDriven.setSelected(false);
    checkParking.setSelected(false);
    checkSide.setSelected(false);
    checkVertex.setSelected(false);
    checkIsManual.setSelected(false);
    manualDriverIndex = NO_MANUAL_DRIVER;
    checkIsManual.setVisible(false);
  }

}
