package org.vesselonline.graphics.rubberband;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class RubberbandCircle extends RubberbandShape {
  public RubberbandCircle(Color color, int lineStyle, int lineWidth, boolean fill) {
    super(color, lineStyle, lineWidth, fill);
  }

  public void drawRubberbandShape(Graphics2D g2d) {
    Rectangle rect = getBounds();
    g2d.setPaint(color);
    g2d = RubberbandShape.setGraphicsStroke(this, g2d);

    if (! fill) { 
      g2d.draw(new Ellipse2D.Float(rect.x, rect.y, rect.width, rect.height));
    } else {
      g2d.fill(new Ellipse2D.Float(rect.x, rect.y, rect.width, rect.height));
    }
  }

  /** 
   * Draw the circle as expanding from the anchor point, with the stretch point marking
   * the distance of the circle's radius.
   */
  public Rectangle getBounds() {
    int radius = (int) anchorPt.distance(stretchPt);
    return new Rectangle(anchorPt.x - radius, anchorPt.y - radius, radius * 2, radius * 2);
  }
}
