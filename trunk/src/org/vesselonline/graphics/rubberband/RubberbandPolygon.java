package org.vesselonline.graphics.rubberband;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;

/**
 * This class is used to implement triangle, quadrilateral, and any other
 * polygon that is to be drawn using the "shift key to add point" method.
 * The number of sides for the polygon is set using the constructor.
 * 
 * @author handy
 */
public class RubberbandPolygon extends RubberbandShape {
  private Point[] points;
  private int numPoints;

  public RubberbandPolygon(int vertices, Color color, int lineStyle, int lineWidth, boolean fill) {
    super(color, lineStyle, lineWidth, fill);

    points = new Point[vertices];
    numPoints = 0;
  }

  public int getNumPoints() { return numPoints; }
  public int getVertices() { return points.length; }

  public void addPoint(Point p) {
    if (numPoints < getVertices()) {
      points[numPoints] = p;
      numPoints++;
    }
  }

  /** 
   * Override the RubberbandShape version to make sure a point is
   * added to the array tracking all points in the polygon.
   */
  public void setAnchorPt(Point p) {
    super.setAnchorPt(p);
    addPoint(p);
  }

  /** 
   * Use a GeneralPath object to create a point-to-point construction of the
   * polygon.  The path must be closed at the end.
   */
  public void drawRubberbandShape(Graphics2D g2d) {
    g2d.setPaint(color);
    g2d = RubberbandShape.setGraphicsStroke(this, g2d);

    GeneralPath polygon = new GeneralPath();
    polygon.moveTo(points[0].x, points[0].y);

    if (numPoints == getVertices()) {
      for (int i = 1; i < getVertices(); i++) {
        polygon.lineTo(points[i].x, points[i].y);
      }
    } else {
      for (int i = 1; i < numPoints; i++) {
        polygon.lineTo(points[i].x, points[i].y);
      }

      polygon.lineTo(stretchPt.x, stretchPt.y);
    }

    polygon.closePath();

    if (! fill || numPoints == 1) { 
      g2d.draw(polygon);
    } else {
      g2d.fill(polygon);
    }
  }
}
