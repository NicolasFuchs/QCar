package qcar.g4.ui;


import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import qcar.IFactory;
import qcar.IGameDescription;
import qcar.IGameProvider;
import qcar.IQCar;

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
    btnPlay.setDisable(true);
    manualDriverIndex = NO_MANUAL_DRIVER;
    checkIsManual.setVisible(false);
  }

  @FXML
  private void handleBtnApply(){
    try{
      int numberOfQCar = Integer.parseInt(txtDriverNum.getText());
      nDrivers = numberOfQCar;
      int selectedStyle = (Integer) comboStyle.getSelectionModel().getSelectedItem();
      gameProvider = fact.newGameProvider(selectedStyle);
      gameDescription = gameProvider.nextGame(numberOfQCar);
      ObservableList<IQCar> obQCars = FXCollections.observableList(gameDescription.allQCar());
      listQCar.setItems(obQCars);
      btnPlay.setDisable(false);
      System.out.println("Apply for : " + numberOfQCar + " QCars !");
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
      ctrl.setParams(fact, gameProvider, gameDescription, nDrivers, manualDriverIndex);
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

}
