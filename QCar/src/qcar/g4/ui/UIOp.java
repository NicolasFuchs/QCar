package qcar.g4.ui;

import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import qcar.IQCar;
import qcar.IWorldManager;
import qcar.ui.QCarAnimationPane;
import simviou.Action;
import simviou.AnimationPane;
import simviou.KeyCommand;
import simviou.LogPanel;
import simviou.MouseMode;
import simviou.ObservationPolicy;
import simviou.SimulatorMode;
import simviou.UIOperations;
import simviou.ViewPort;

/**
 * This class set SimViou behavior.
 */
public class UIOp implements UIOperations {

  private IWorldManager wm;
  private LogPanel logPanel;
  private SimulatorMode simMode;
  private boolean isMute;
  private HashMap<KeyCommand, Action> keyMap;

  /**
   * Get the world bounding box from the world manager
   * @return Bounding box in a Rectangle2D
   */
  @Override
  public Rectangle2D worldBoundingBox() {
    return wm.boundingBox();
  }

  /**
   * Return a new QCarAnimationPane
   * @param vPort container of the layers
   * @return QCarAnimationPane
   */
  @Override
  public AnimationPane newAnimationPane(ViewPort vPort) {
    return new QCarAnimationPane(vPort, Color.WHITE, this, wm);
  }

  //------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public MouseMode mouseMode() { return MouseMode.ZOOM; }

  /**
   * {@inheritDoc}
   */
  @Override
  public void actionRequest(double atX, double atY) { }

  /**
   * {@inheritDoc}
   */
  @Override
  public void infoRequest(double atX, double atY) { }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isActive(int zIndex) { return true; }

  /**
   * Make the QCarAnimationPane expand or contract according to the world bounding box
   * @return the observation policy
   */
  @Override
  public ObservationPolicy observationPolicy() {
    return ObservationPolicy.WORLD_BOUNDARY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SimulatorMode simulatorMode() {
    return simMode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void zoneSelected(double fromX, double fromY,
      double toX,   double toY) {}

  /**
   * Check if the point clicked is inside a QCar, if so
   * return a String with the qcarId, his maxSideLength and his minArea
   * @param atX x coordinate of the clicked point
   * @param atY y coordinate of the clicked point
   * @param mouseMode the mode of the mouse
   * @return tooltip string
   */
  @Override
  public String toolTipText(double atX, double atY, MouseMode mouseMode) {
    boolean result =false;
    for(IQCar q : wm.allQCars()){
      for(int i = 0, j = 3; i < 4; j = i++){
        if((q.vertex(i).getY() > atY) != (q.vertex(j).getY() > atY) &&
            (atX < (q.vertex(j).getX() - q.vertex(i).getX()) * (atY - q.vertex(i).getY()) /
                (q.vertex(j).getY() - q.vertex(i).getY()) + q.vertex(i).getX()))
          result = !result;
      }
      if(result) return "QCar " + q.nature().qCarId() + ": maxSideLength=" + q.nature().maxSideLength()
          + "\n minArea=" + q.nature().minArea();
    }
    return "Not a QCar";
  }

  /**
   * Tell if the simulation is muted or not.
   * @return true if muted, otherwise false
   */
  @Override
  public boolean mute() {
    return isMute;
  }


  /**
   * Get a hashmap containing the keybinds and their associated action
   * @param mouseMode the mode of the mouse
   * @return Hashmap
   */
  @Override
  public Map<KeyCommand, Action> keyActionMap(MouseMode mouseMode) {
    return keyMap;
  }

  /**
   * Add a string to the log panel
   * @param logText log text
   */
  @Override
  public void logEntry(String logText) {
    logPanel.addEntry(logText);
  }

  /**
   * Constructor
   * @param wm reference the worldmanager
   * @param logPanel  reference the logpanel
   */
  public UIOp(IWorldManager wm, LogPanel logPanel){
    this.wm = wm;
    this.logPanel = logPanel;
    this.isMute = false;
    this.simMode = SimulatorMode.STEP_BY_STEP;
    keyMap = new HashMap<>();
    final Action muteAction = new Action() {
      @Override
      public void execute() {
        toggleMute();
      }
    };
    keyMap.put(new KeyCommand(KeyCode.M), muteAction);
  }

  /**
   * This method toggle between mute and unmute.
   * The action is bound to the key "M".
   */
  public void toggleMute(){
    isMute = !isMute;
    logEntry(isMute ? "Sound muted" : "Sound unmuted");
  }

  /**
   * Toggle the Simulation mode between Animation_Running and Step_by_step
   * @param isAnimationRunning if true, set Animation_running else set step_by_step
   */
  public void setAnimationRunning(boolean isAnimationRunning){
    if(isAnimationRunning)
      simMode = SimulatorMode.ANIMATION_RUNNING;
    else
      simMode = SimulatorMode.STEP_BY_STEP;
  }
}

