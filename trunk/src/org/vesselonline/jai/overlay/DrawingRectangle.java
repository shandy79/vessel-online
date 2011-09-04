package org.vesselonline.jai.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class DrawingRectangle {
  private Point anchorPoint, stretchPoint;
  private Color color;
  private int lineStyle, lineWidth;
  private boolean fill;

  public static final int SOLID = 0, DASHED = 1, DOTTED = 2;

  public DrawingRectangle() {
    setAnchorPoint(new Point(0, 0));
    setStretchPoint(new Point(0, 0));
    
    setColor(Color.RED);
    setLineStyle(SOLID);
    setLineWidth(1);
    setFill(false);
  }

  public DrawingRectangle(Color color, int lineStyle, int lineWidth, boolean fill)  {
    this();

    setColor(color);
    setLineStyle(lineStyle);
    setLineWidth(lineWidth);
    setFill(fill);
  }

  public Point getAnchorPoint() { return anchorPoint; }
  public void setAnchorPoint(Point anchorPoint) { this.anchorPoint = anchorPoint; }

  public Point getStretchPoint() { return stretchPoint; }
  public void setStretchPoint(Point stretchPoint) { this.stretchPoint = stretchPoint; }

  public Color getColor() { return color; }
  public void setColor(Color color) { this.color = color; }

  public int getLineStyle() { return lineStyle; }
  public void setLineStyle(int lineStyle) { if (lineStyle >= 0 && lineStyle <= 2) this.lineStyle = lineStyle; }

  public int getLineWidth() { return lineWidth; }
  public void setLineWidth(int lineWidth) { if (lineWidth > 0) this.lineWidth = lineWidth; }

  public boolean isFill() { return fill; }
  public void setFill(boolean fill) { this.fill = fill; }

  public void draw(Graphics2D g2d) {
    Rectangle rect = getBounds();
    g2d.setPaint(getColor());
    g2d.setStroke(createBasicStroke());

    if (! isFill()) { 
      g2d.draw(new Rectangle2D.Float(rect.x, rect.y, rect.width, rect.height));
    } else {
      g2d.fill(new Rectangle2D.Float(rect.x, rect.y, rect.width, rect.height));
    }
  }

  public Rectangle getBounds() {
    return new Rectangle(getStretchPoint().x < getAnchorPoint().x ? getStretchPoint().x : getAnchorPoint().x,
                         getStretchPoint().y < getAnchorPoint().y ? getStretchPoint().y : getAnchorPoint().y,
                         Math.abs(getStretchPoint().x - getAnchorPoint().x), Math.abs(getStretchPoint().y - getAnchorPoint().y));
  }

  /** Gets a stroke for the Graphics2D object using the current width and style. */
  private BasicStroke createBasicStroke() {
    BasicStroke stroke;

    if (getLineStyle() == DASHED) {
      stroke = new BasicStroke(getLineWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {10.0f, 5.0f}, 0);
    } else if (getLineStyle() == DOTTED) {
      stroke = new BasicStroke(getLineWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {2.0f, 5.0f}, 0);
    } else {
      stroke = new BasicStroke(getLineWidth());
    }

    return stroke;
  }
}
