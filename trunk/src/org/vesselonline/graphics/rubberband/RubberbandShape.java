package org.vesselonline.graphics.rubberband;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;

public abstract class RubberbandShape implements Serializable {
  protected Point anchorPt = new Point(0, 0);
  protected Point stretchPt = new Point(0, 0);

  // Default drawing values, should be the same as menu and RubberPanel defaults
  protected Color color = Color.WHITE;
  protected int lineStyle = RubberbandPanel.SOLID;
  protected int lineWidth = 1;
  protected boolean fill = false;

  /** Must be implemented by subclasses.  Specifies how shape is to be rendered. */
  public abstract void drawRubberbandShape(Graphics2D g);

  /** Called from subclasses with appropriate arguments. */
  public RubberbandShape(Color color, int lineStyle, int lineWidth, boolean fill)  {
    this.color = color;
    this.lineStyle = lineStyle;
    this.lineWidth = lineWidth;
    this.fill = fill;
  }

  public Point getAnchorPt() { return anchorPt; }
  public void setAnchorPt(Point anchorPt) { this.anchorPt = anchorPt; }

  public Point getStretchPt() { return stretchPt; }
  public void setStretchPt(Point stretchedPt) { this.stretchPt = stretchedPt; }

  public Color getColor() { return color; }
  public void setColor(Color color) { this.color = color; }

  public int getLineStyle() { return lineStyle; }
  public void setLineStyle(int lineStyle) { this.lineStyle = lineStyle; }

  public int getLineWidth() { return lineWidth; }
  public void setLineWidth(int lineWidth) { this.lineWidth = lineWidth; }

  public boolean isFill() { return fill; }
  public void setFill(boolean fill) { this.fill = fill; }

  /** Sets the stroke for the Graphics2D object using the current width and style. */
  public static final Graphics2D setGraphicsStroke(RubberbandShape rbs, Graphics2D g2d) {
    BasicStroke s;

    if (rbs.lineStyle == RubberbandPanel.DASHED) {
      s = new BasicStroke(rbs.lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {10.0f, 5.0f}, 0);
    } else if (rbs.lineStyle == RubberbandPanel.DOTTED) {
      s = new BasicStroke(rbs.lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {2.0f, 5.0f}, 0);
    } else {
      s = new BasicStroke(rbs.lineWidth);
    }

    g2d.setStroke(s);
    return g2d;
  }
}
