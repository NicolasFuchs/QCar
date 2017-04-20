package qcar.g4;

import java.awt.geom.Point2D;

public class DroSeg {

  private final double pente;
  private final double origin;
  private final Point2D p1;
  private final Point2D p2;

  public DroSeg(Point2D p1, Point2D p2) {
    if (p1.getX() < p2.getX()) {
      this.pente = (p2.getY()-p1.getY())/(p2.getX()-p1.getX());
    } else {
      this.pente = (p1.getY()-p2.getY())/(p1.getX()-p2.getX());
    }
    this.origin = p1.getY()-this.pente*p1.getX();
    this.p1 = p1;
    this.p2 = p2;
  }
  
  public double getPente() {
    return this.pente;
  }
  
  public double getOrigin() {
    return this.origin;
  }
  
  public Point2D getPoint(int index) {
    Point2D point = null;
    if (index == 1) {
      point =  this.p1;
    } else if (index == 2) {
      point = this.p2;
    }
    return point;
  }
  
  public Point2D intersect(DroSeg d) {
    Point2D res = null;
    if (Math.abs(this.pente - d.getPente()) > 0.0001) {
      double x = (d.getOrigin()-this.origin)/(this.pente-d.getPente());
      double y = (d.getPente()*x+d.getOrigin());
      res = new Point2D.Double(x,y);
    } else if (Math.abs(this.origin - d.getOrigin()) <= 0.0001) {
      res = new Point2D.Double(Double.NaN,Double.NaN);
    } else if (Math.abs(this.origin - d.getOrigin()) > 0.0001) {
      res = null;
    }
    return res;
  }
  
}
