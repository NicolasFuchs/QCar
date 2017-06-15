package qcar.g4.ui;


import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
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
import qcar.g4.ManualDriver;

/**
 * Controller bound to the editor view.
 */
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

  /**
   * Set up the view
   */
  @FXML
  void initialize() {
    setFactory(new Factory());
    btnPlay.setDisable(true);
    manualDriverIndex = NO_MANUAL_DRIVER;
    checkIsManual.setVisible(false);
  }

  /**
   * Handle the btnApply on click event.
   * Generate the game with the selected style and the number of driven
   * Qcar given by the user.
   * Add the generated QCars to the listView.
   */
  @FXML
  private void handleBtnApply(){
    try{
      nDrivers = Integer.parseInt(txtDriverNum.getText());
      int selectedStyle = (Integer) comboStyle.getSelectionModel().getSelectedItem();
      gameProvider = fact.newGameProvider(selectedStyle);
      gameDescription = gameProvider.nextGame(nDrivers);
      ObservableList<IQCar> obQCars = FXCollections.observableList(gameDescription.allQCar());
      listQCar.setItems(obQCars);
      listQCar.setCellFactory(param -> new ListCell<IQCar>() {
        @Override
        protected void updateItem(IQCar item, boolean empty) {
          super.updateItem(item, empty);
          if(empty){
            setText(null);
          } else {
            String builder = "Qcar n°" + item.nature().qCarId();
            if(item.nature().isDriven())
              builder += " - driven";
            if (item.nature().isParkingTarget())
              builder += " - parking";
            if (!item.nature().isDriven() && !item.nature().isParkingTarget()
                && !item.nature().isSideTarget() && !item.nature().isVertexTarget())
              builder += " - static";
            setText(builder);
          }
        }
      });
      emptyFields();
      btnPlay.setDisable(false);
    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Number of driver");
      alert.setHeaderText(null);
      alert.setContentText("Please input a valid amount of drivers");
      alert.showAndWait();
    }
  }

  /**
   * Handle the btnPlay onclick event.
   * Start a new Simulation, load the simulation view
   * and pass it the wm and the stage.
   */
  @FXML
  private void handleBtnPlay(){
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/simulation.fxml"));
      Scene scene = new Scene(loader.load());
      SimulationCtrl ctrl = loader.getController();
      stage.hide();

      IWorldManager wm  = fact.newWorldManager();
      List<IDriver> driverList = new ArrayList<>();
      ManualDriver manualDriver = null;

      for(int i = 0; i < nDrivers; i++) {
        if (i == manualDriverIndex) {
          manualDriver = new ManualDriver(manualDriverIndex);
          driverList.add(manualDriver);
        } else {
          driverList.add(fact.newSmartDriver());
        }
      }

      wm.openNewSimulation(gameDescription, driverList);

      ctrl.setWM(wm, manualDriver);
      ctrl.setStage(stage);
      stage.setTitle("Simulation");
      stage.setScene(scene);
      stage.show();
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Handle the listview onclick event. Display the selected QCar information
   * in the corresponding fields.
   */
  @FXML
  private void handleListSelection() {
    if(listQCar.getSelectionModel().isEmpty()) return;

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

  /**
   * Handle events of the checkbox allowing you to chose a QCar to drive manually
   */
  @FXML
  private void handleManualDriverSelection(){
    if(checkIsManual.isSelected())
      manualDriverIndex = listQCar.getSelectionModel().getSelectedItem().nature().qCarId();
    else
      manualDriverIndex = NO_MANUAL_DRIVER;
  }

  /**
   * Set the factory and every elements depending on it
   * @param fact factory used by the simulation
   */
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

  /**
   * Set the stage so we can pass it to the next view
   * @param stage the main window
   */
  public void setStage(Stage stage){
    this.stage = stage;
  }

  /**
   * Empty all the fields displaying QCar informations
   */
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
