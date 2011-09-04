package org.vesselonline.graphics.rubberband;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class RubberbandRectangle extends RubberbandShape {
  public RubberbandRectangle(Color color, int lineStyle, int lineWidth, boolean fill) {
    super(color, lineStyle, lineWidth, fill);
  }

  public void drawRubberbandShape(Graphics2D g2d) {
    Rectangle rect = getBounds();
    g2d.setPaint(color);
    g2d = RubberbandShape.setGraphicsStroke(this, g2d);

    if (! fill) { 
      g2d.draw(new Rectangle2D.Float(rect.x, rect.y, rect.width, rect.height));
    } else {
      g2d.fill(new Rectangle2D.Float(rect.x, rect.y, rect.width, rect.height));
    }
  }

  public Rectangle getBounds() {
    return new Rectangle(stretchPt.x < anchorPt.x ? stretchPt.x : anchorPt.x,
                         stretchPt.y < anchorPt.y ? stretchPt.y : anchorPt.y,
                         Math.abs(stretchPt.x - anchorPt.x), Math.abs(stretchPt.y - anchorPt.y));
  }
}
