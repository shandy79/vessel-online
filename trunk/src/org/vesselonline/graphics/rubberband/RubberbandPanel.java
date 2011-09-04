package org.vesselonline.graphics.rubberband;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

/**
 * This class extends a JPanel to provide a rubberbanded drawing
 * capability for figures which are displayed on the JPanel container
 * It includes methods which permit the selection of the figures to
 * be drawn and their properties such as color, line style and width, etc.
 * 
 * @author shandy
 */
public class RubberbandPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener, Printable {
  // Add additional constants here as other shapes and styles are added
  public static final int LINE = 0, CIRCLE = 1, RECTANGLE = 2, TRIANGLE = 3,
                          QUADRILATERAL = 4, FREEHAND = 5;
  public static final int SOLID = 0, DASHED = 1, DOTTED = 2;
  private static final int MAX_FIGURES = 20;
  private static final String FILE_EXT = "dwg";
  private final static int ONE_MINUTE = 60000;

  // Instance variables which store the number and references to all
  // shapes which have been drawn. This is needed to repaint the screen if it
  // becomes corrupted by an event (such as minimizing or resizing the window or
  // covering a portion of the drawing window).

  private RubberbandGraphics container;
  private RubberbandShape figures[];
  private int numFigures;
  private String fileName;
  private boolean saved = true;  // Set here to prevent initial display of Save Changes dialog
  private boolean autoSave = false;
  private Timer saveTimer;
  private boolean shiftReleased;

  private int currentShape = LINE;
  private Color currentColor = Color.WHITE;
  private int currentStyle = SOLID;
  private int currentWidth = 1;
  private boolean currentFill = false;

  /** Sets RubberbandPanel to process own mouse and key events, creates save timer. */
  public RubberbandPanel(RubberbandGraphics container) {
    this.container = container;
    addMouseListener(this);
    addMouseMotionListener(this);
    addKeyListener(this);

    saveTimer = new Timer(ONE_MINUTE, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (! saved) savePanel(false);
      }    
    });

    clearPanel();
  }

  public RubberbandPanel(RubberbandGraphics container, int currentShape, Color currentColor, int currentStyle,
                         int currentWidth, boolean currentFill) {
    this(container);

    this.currentShape = currentShape;
    this.currentColor = currentColor;
    this.currentStyle = currentStyle;
    this.currentWidth = currentWidth;
    this.currentFill = currentFill;
  }

  /** Resets panel to default state.  Used for initialization, New, and Exit operations. */
  public void clearPanel() {
    promptToSave();

    figures = new RubberbandShape[MAX_FIGURES];
    numFigures = -1;
    fileName = "Untitled";
    saved = true;
    shiftReleased = false;

    container.setFrameFileName(getFileName());
    repaint();
  }

  /** Load a saved file into the panel.  Used for Open operation. */
  public void loadPanel() {
    File f = null;
    promptToSave();

    JFileChooser openDialog = new JFileChooser(".");
    openDialog.setMultiSelectionEnabled(false);
    openDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
    openDialog.setFileFilter(new DrawingFileFilter());

    if (! fileName.equals("Untitled")) {
      openDialog.setCurrentDirectory(new File(fileName).getParentFile());
    }

    int openResult = openDialog.showOpenDialog(this);
    if (openResult == JFileChooser.APPROVE_OPTION) {
      f = openDialog.getSelectedFile();
      fileName = f.getAbsolutePath();
    } else {
      return;
    }

    try {
      ObjectInputStream openIn = new ObjectInputStream(new FileInputStream(f));
      figures = (RubberbandShape[]) openIn.readObject();
      numFigures = openIn.readInt();
      openIn.close();
    } catch (Exception ioe) {
      ioe.printStackTrace();
      JOptionPane.showMessageDialog(this, "An error occurred while attempting to open your file!\n" +
                                    "Please try opening " + fileName + " again.",
                                    "File Not Opened", JOptionPane.ERROR_MESSAGE);
      clearPanel();
      return;
    }

    saved = true;
    container.setFrameFileName(getFileName());
    repaint();
  }

  /** Save the panel to a file.  Used in the Save and Save As operations. */
  public void savePanel(boolean saveAs) {
    File f = null;
    if (saved && ! saveAs) return;
    
    if (fileName.equals("Untitled") || saveAs) {
      JFileChooser saveDialog = new JFileChooser(".");
      saveDialog.setMultiSelectionEnabled(false);
      saveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
      saveDialog.setFileFilter(new DrawingFileFilter());

      if (! fileName.equals("Untitled")) {
        saveDialog.setCurrentDirectory(new File(fileName).getParentFile());
      }

      int saveResult = saveDialog.showSaveDialog(this);
      if (saveResult == JFileChooser.APPROVE_OPTION) {
        f = saveDialog.getSelectedFile();
        fileName = f.getAbsolutePath();

        if (! f.getName().toLowerCase().endsWith("." + FILE_EXT)) {
          fileName += "." + FILE_EXT;
          f = new File(fileName);
        }
      } else {
        return;
      }
    } else {
      f = new File(fileName);
    }

    try {
      ObjectOutputStream saveOut = new ObjectOutputStream(new FileOutputStream(f));
      saveOut.writeObject(figures);
      saveOut.writeInt(numFigures);
      saveOut.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
      JOptionPane.showMessageDialog(this, "An error occurred while attempting to save your file!\n" +
                                    "Please try saving " + fileName + " again.",
                                    "File Not Saved", JOptionPane.ERROR_MESSAGE);
      saved = false;
      return;
    }

    saved = true;
    container.setFrameFileName(getFileName());
  }

  /** Adapted from http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html */
  public void printPanel() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog()) 
      try {
        printJob.print();
      } catch(PrinterException pe) {
        pe.printStackTrace();
        JOptionPane.showMessageDialog(this, "An error occurred while attempting to print your file!\n" +
                                      "Please try printing " + fileName + " again.",
                                      "File Not Printed", JOptionPane.ERROR_MESSAGE);
        return;
      }
  }

  /** 
   * If the current drawing has unsaved changes, prompt user to save them before
   * switching to a new drawing.
   */
  private void promptToSave() {
    if (saved) return;

    int promptResult = JOptionPane.showConfirmDialog(this, "You have unsaved changes.  Would you like to save " +
                                                     "them before continuing?", "Save Changes?",
                                                     JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (promptResult == JOptionPane.YES_OPTION) savePanel(false);
  }

  /** Return the file name for display in the frame title bar. */
  public String getFileName() { return new File(fileName).getName(); }

  public boolean isSaved() { return saved; }

  public boolean isAutoSave() { return autoSave; }

  /** Start or stop the save time depending on the auto-save setting. */
  public void setAutoSave(boolean autoSave) {
    this.autoSave = autoSave;

    if (this.autoSave) saveTimer.start();
    else saveTimer.stop();
  }

  public int getCurrentShape() { return currentShape; }
  public void setCurrentShape(int currentShape) { this.currentShape = currentShape; }

  public Color getCurrentColor() { return currentColor; }
  public void setCurrentColor(Color currentColor) { this.currentColor = currentColor; }

  public int getCurrentStyle() { return currentStyle; }
  public void setCurrentStyle(int currentStyle) { this.currentStyle = currentStyle; }

  public int getCurrentWidth() { return currentWidth; }
  public void setCurrentWidth(int currentWidth) { this.currentWidth = currentWidth; }

  public boolean isCurrentFill() { return currentFill; }
  public void setCurrentFill(boolean currentFill) { this.currentFill = currentFill; }

  /**
   * Repaint all the final figures upon command from
   * the operating system. This method is called when any area
   * of the window becomes invalid and then is restored.
   * Examples are when the window is minimized and then
   * restored, or a portion of the window becomes covered by
   * another window and then is restored to its uncovered state.
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    for (int i = 0; i <= numFigures; i++) {
      figures[i].drawRubberbandShape((Graphics2D) g);
    }
  }

  /**
   * Responsible for selecting the shape to be drawn and for setting all the relevant
   * properties for the shape object.  Then sets the anchor point for the new shape.
   */
  public void mousePressed(MouseEvent e) {
    if (numFigures >= MAX_FIGURES - 1) {
      JOptionPane.showMessageDialog(this, "Your masterpiece is complete, no need to continue adding to it!",
                                    "Canvas Complete", JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (currentShape == CIRCLE) {
      figures[++numFigures] = new RubberbandCircle(currentColor, currentStyle, currentWidth, currentFill);
    } else if (currentShape == RECTANGLE) {
      figures[++numFigures] = new RubberbandRectangle(currentColor, currentStyle, currentWidth, currentFill);
    } else if (currentShape == TRIANGLE) {
      figures[++numFigures] = new RubberbandPolygon(3, currentColor, currentStyle, currentWidth, currentFill);
    } else if (currentShape == QUADRILATERAL) {
      figures[++numFigures] = new RubberbandPolygon(4, currentColor, currentStyle, currentWidth, currentFill);
    } else if (currentShape == FREEHAND) {
      figures[++numFigures] = new RubberbandFreehand(currentColor, currentStyle, currentWidth);
    } else {  // currentShape == LINE
      figures[++numFigures] = new RubberbandLine(currentColor, currentStyle, currentWidth);
    }

    // Set the anchor point for the currently active figure
    figures[numFigures].setAnchorPt(e.getPoint());
    saved = false;
    // Allows JPanel to fire KeyEvents needed for RubberbandPolygon drawing
    requestFocus();
  }

  /** Called when the mouse is released upon the completion of drawing a figure. */
  public void mouseReleased(MouseEvent e) {
    figures[numFigures].setStretchPt(e.getPoint());
    repaint();
  }

  /** Tracks the current stretch point for drawing a figure. */
  public void mouseDragged(MouseEvent e) {
    figures[numFigures].setStretchPt(e.getPoint());

    if (shiftReleased && figures[numFigures] instanceof RubberbandPolygon) {
      ((RubberbandPolygon) figures[numFigures]).addPoint(e.getPoint());
      shiftReleased = false;
    }
 
    repaint();
  }

  /** Listens for Shift key releases to add points to RubberbandPolygons. */
  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SHIFT) shiftReleased = true;
  }

  /** Adapted from http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html */
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      this.setBackground(Color.LIGHT_GRAY);
      RepaintManager repaintMgr = RepaintManager.currentManager(this);
      repaintMgr.setDoubleBufferingEnabled(false);
      this.paint(g2d);
      repaintMgr.setDoubleBufferingEnabled(true);
      this.setBackground(Color.BLACK);
      return(PAGE_EXISTS);
    }
  }
  
  // Empty methods required to implement interfaces
  public void mouseClicked(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }
  public void mouseMoved(MouseEvent e) { }
  public void keyTyped(KeyEvent e) { }
  public void keyPressed(KeyEvent e) { }

  /** Create a file filter that allows one to select a directory or a .dwg file. */
  private class DrawingFileFilter extends FileFilter {
    public boolean accept(File pathname) {
      if (pathname.isDirectory() || pathname.getName().toLowerCase().endsWith("." + FILE_EXT)) {
        return true;
      } else {
        return false;
      }
    }

    public String getDescription() { return "Rubberband Drawing Files (*.dwg)"; }
  }
}
