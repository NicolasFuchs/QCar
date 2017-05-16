package qcar.g4.ui;

    import java.net.URL;
    import java.util.ArrayList;
    import java.util.ResourceBundle;
    import javafx.fxml.FXML;
    import javafx.geometry.Rectangle2D;
    import javafx.scene.control.ListView;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.VBox;
    import javafx.stage.Stage;
    import qcar.IFactory;
    import qcar.IGameDescription;
    import qcar.IGameProvider;
    import qcar.IWorldManager;
    import simviou.AnimationPane;
    import simviou.LogPanel;
    import simviou.UIOperationsWithDefaults;
    import simviou.ViewPort;

public class SimulationCtrl {

  private IFactory fact;
  private IGameDescription gameDescription;
  private IGameProvider gameProvider;
  private IWorldManager worldManager;

  private int manualCarIndex;

  private Stage stage;

  private ViewPort world;
  private LogPanel logPanel;


  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private BorderPane borderPane;

  @FXML
  private Pane headerPane;

  @FXML
  private ListView<?> lstLeaderboard;

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
  public void setParams(IFactory fact, IGameProvider gameProvider,
      IGameDescription gameDescription, int nDriver, int manualCarIndex){

    this.fact = fact;
    this.gameProvider = gameProvider;
    this.gameDescription = gameDescription;
    this.manualCarIndex = manualCarIndex;

    VBox header = (manualCarIndex == -1) ? new SimControlCtrl() : new ManualDrivingCtrl();
    headerPane.getChildren().add(header);

    worldManager = fact.newWorldManager();

    world = new ViewPort(0, 0, 100, 100, 750, 500, true);

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

    // List drivers = new ArrayList<>()
    // for(int i = 0; i < nDriver; i++)


  }

  public void setStage(Stage stage){
    this.stage = stage;
  }
}
