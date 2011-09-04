package org.vesselonline.jai.overlay;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RepaintManager;
import com.sun.media.jai.widget.DisplayJAI;

public class CompositeImageDisplayJAI extends DisplayJAI implements Printable, SelectableImage {
  private DrawingRectangle rect;
  private List<SelectableImageListener> listeners;

  public CompositeImageDisplayJAI() {
    super();

    listeners = new ArrayList<SelectableImageListener>();

    addMouseListener(this);
    addMouseMotionListener(this);
  }


  /**
   * Repaint the image and drawing rectangle upon command from
   * the operating system. This method is called when any area
   * of the window becomes invalid and then is restored.
   * Examples are when the window is minimized and then
   * restored, or a portion of the window becomes covered by
   * another window and then is restored to its uncovered state.
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (rect != null) {
      rect.draw((Graphics2D) g);
    }
    notifySelectionUpdated();
  }

  /** Responsible for instantiating and setting the anchor point for the new rectangle. */
  public void mousePressed(MouseEvent e) {
    rect = new DrawingRectangle();
    rect.setAnchorPoint(e.getPoint());
  }

  /**
   * Called when the mouse is released upon the completion of drawing a rectangle.
   * If the release is sufficiently close to the press, consider it a clear.
   */
  public void mouseReleased(MouseEvent e) {
    if (Math.abs(e.getPoint().x - rect.getAnchorPoint().x) < 3 && Math.abs(e.getPoint().y - rect.getAnchorPoint().y) < 3) {
      rect = null;
    } else {
      rect.setStretchPoint(e.getPoint());
    }
    repaint();
  }

  /** Tracks the current stretch point for drawing a rectangle. */
  public void mouseDragged(MouseEvent e) {
    rect.setStretchPoint(e.getPoint());
    repaint();
  }


  /** Adapted from http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html */
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return NO_SUCH_PAGE;
    } else {
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

      Color bgColor = this.getBackground();
      this.setBackground(Color.WHITE);

      RepaintManager repaintMgr = RepaintManager.currentManager(this);
      repaintMgr.setDoubleBufferingEnabled(false);
      this.paint(g2d);
      repaintMgr.setDoubleBufferingEnabled(true);

      this.setBackground(bgColor);
      return PAGE_EXISTS;
    }
  }


  public Rectangle getSelection() {
    if (rect == null) {
      return new Rectangle(0, 0, 0, 0);
    } else {
      return rect.getBounds();
    }
  }

  public void setSelection(Rectangle rect) {
    if (rect == null) {
      this.rect = null;
    } else {
      if (this.rect == null) this.rect = new DrawingRectangle();
      this.rect.setAnchorPoint(new Point(rect.x, rect.y));
      this.rect.setStretchPoint(new Point(rect.x + rect.width, rect.y + rect.height));
    }
    repaint();
  }

  public final void addSelectableImageListener(SelectableImageListener sil) {
    listeners.add(sil);
  }

  public final void removeSelectableImageListener(SelectableImageListener sil) {
    listeners.remove(sil);
  }

  public void notifySelectionUpdated() {
    for (SelectableImageListener sil : listeners) {
      sil.selectionUpdated();
    }
  }


  // Empty methods required to implement interfaces
  public void mouseClicked(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }
  public void mouseMoved(MouseEvent e) { }
}
