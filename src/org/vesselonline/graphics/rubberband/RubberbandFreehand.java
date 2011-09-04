package org.vesselonline.graphics.rubberband;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/**
 * This class was "discovered" while working on the RubberbandPolygon
 * class, so I added it for kicks.  Works on the same principle, rendering
 * a GeneralPath composed of many points.  However, points are added at
 * every mouse drag event.
 *  
 * @author handy
 */
public class RubberbandFreehand extends RubberbandShape {
  private ArrayList<Point> points;

  public RubberbandFreehand(Color color, int lineStyle, int lineWidth) {
    super(color, lineStyle, lineWidth, false);

    points = new ArrayList<Point>(100);
  }

  // Override the RubberbandShape methods so that a new point is added
  // to the path at every mouse drag event.
  public void setAnchorPt(Point p) { points.add(0, p); }
  public void setStretchPt(Point p) { points.add(p); }

  public void drawRubberbandShape(Graphics2D g2d) {
    g2d.setPaint(color);
    g2d = RubberbandShape.setGraphicsStroke(this, g2d);

    GeneralPath freehand = new GeneralPath();
    boolean init = false;

    for (Point p : points) {
      if (! init) {
        freehand.moveTo(p.x, p.y);
        init = true;
      }

      freehand.lineTo(p.x, p.y);
    }

    if (! fill) { 
      g2d.draw(freehand);
    } else {
      g2d.fill(freehand);
    }
  }
}
