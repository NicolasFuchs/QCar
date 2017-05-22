package qcar.g4.ui;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Rectangle2D;
import qcar.IWorldManager;
import simviou.Action;
import simviou.AnimationPane;
import simviou.KeyCommand;
import simviou.LogPanel;
import simviou.MouseMode;
import simviou.ObservationPolicy;
import simviou.SimulatorMode;
import simviou.UIOperations;
import simviou.ViewPort;

/** You should strongly consider overriding at least two methods:
 *  - worldBoundingBox()
 *  - newAnimationPane()
 */
public class UIOp implements UIOperations {

  private IWorldManager wm;
  private LogPanel logPanel;

  @Override
  public Rectangle2D worldBoundingBox() {
    // This one is not reasonable (should depend on the current game...)
    return wm.boundingBox();
  }

  @Override
  public AnimationPane newAnimationPane(ViewPort vPort) {
    // This one is not reasonable (could cause NullPointerException)
    System.out.print("newAnimationPane called");
    return null;
  }

  //------------------------------------------------------------------

  @Override
  public MouseMode mouseMode() { return MouseMode.ZOOM; }

  @Override
  public void actionRequest(double atX, double atY) { }

  @Override
  public void infoRequest(double atX, double atY) { }

  @Override
  public boolean isActive(int zIndex) { return true; }

  @Override
  public ObservationPolicy observationPolicy() {
    return ObservationPolicy.WORLD_BOUNDARY;
  }

  @Override
  public SimulatorMode simulatorMode() {
    return SimulatorMode.STEP_BY_STEP;
  }

  @Override
  public void zoneSelected(double fromX, double fromY,
      double toX,   double toY) { }

  @Override
  public String toolTipText(double atX, double atY, MouseMode mouseMode) {
    return "";
  }

  @Override
  public boolean mute() { return false; }

  @Override
  public Map<KeyCommand, Action> keyActionMap(MouseMode mouseMode) {
    return new HashMap<>();
  }

  @Override
  public void logEntry(String logText) {
    logPanel.addEntry(logText);
  }

  public UIOp(IWorldManager wm, LogPanel logPanel){
    this.wm = wm;
    this.logPanel = logPanel;
  }

}

