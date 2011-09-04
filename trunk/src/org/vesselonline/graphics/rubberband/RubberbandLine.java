package org.vesselonline.graphics.rubberband;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class RubberbandLine extends RubberbandShape {
  public RubberbandLine(Color color, int lineStyle, int lineWidth) {
    super(color, lineStyle, lineWidth, false);
  }

  public void drawRubberbandShape(Graphics2D g2d) {
    g2d.setPaint(color);
    g2d = RubberbandShape.setGraphicsStroke(this, g2d);
    g2d.draw(new Line2D.Float(anchorPt.x, anchorPt.y, stretchPt.x, stretchPt.y));
  }
}
