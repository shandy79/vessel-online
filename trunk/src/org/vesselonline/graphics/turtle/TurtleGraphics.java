package org.vesselonline.graphics.turtle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/** Homework 4, Problem 12.23 (p. 635)
 *  <p><b>Turtle Graphics</b></p>
 *  <p>Modify your solution to Exercise 7.21 to add a graphical user interface
 *   using <code>JTextFields</code> and <code>JButtons</code>.  Draw lines
 *   rather than asterisks.  Implement the drawing with the Java 2D API
 *   features.</p>
 *  
 *  @author Steven Handy
 */
public class TurtleGraphics extends JFrame implements ActionListener {
  private static final int DEFAULT_DISTANCE = 10;
  private static final int UP = 3;
  private static final int DOWN = 1;
  private static final int LEFT = 2;
  private static final int RIGHT = 0;
  private static final boolean CLOCKWISE = true;
  private static final boolean COUNTERCLOCKWISE = false;
  private static final int ROTATION_ANGLE = 90;

  private static final int COMP_SEP = 10;  // Spacer for layout purposes
  private static final boolean DEBUG = false;

  // GUI components
  private JRadioButton btnPenUp, btnPenDown;
  private JButton btnLeft, btnRight, btnMove;
  private JTextField txtMove;
  private JTextArea txtDebug;
  private DrawingPanel pnlDraw;

  // Properties of the turtle cursor controlled by the GUI
  private boolean write = false;
  private int dir = RIGHT;
  private int distance = DEFAULT_DISTANCE;

  /** Create the application with the given image as a cursor */
  public TurtleGraphics(Image img) {
    // Create the Drawing panel
    pnlDraw = new DrawingPanel(img);

    // Create a Font for the border titles and a size for each subpanel
    Font fnt = new Font("SansSerif", Font.BOLD, 11);
    Dimension dim = new Dimension(100, 100);

    JPanel pnlPen = createPenPanel(fnt, dim);
    JPanel pnlTurn = createTurnPanel(fnt, dim);
    JPanel pnlMove = createMovePanel(fnt, dim);
    JPanel pnlDebug = createDebugPanel(fnt, dim);

    // Create and layout the entire Input panel
    JPanel pnlInput = new JPanel();
    pnlInput.setLayout(new FlowLayout(FlowLayout.CENTER));
    pnlInput.setBorder(BorderFactory.createTitledBorder("Input Panel"));

    pnlInput.add(pnlPen);
    pnlInput.add(Box.createHorizontalStrut(COMP_SEP));
    pnlInput.add(pnlTurn);
    pnlInput.add(Box.createHorizontalStrut(COMP_SEP));
    pnlInput.add(pnlMove);
    if (DEBUG) {
      pnlInput.add(Box.createHorizontalStrut(COMP_SEP));
      pnlInput.add(pnlDebug); 
      printDebugInfo();
    }

    add(pnlDraw, BorderLayout.CENTER);
    add(pnlInput, BorderLayout.SOUTH);

    setTitle("Turtle Graphics - Homework 4, Problem 12.23");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(640, 640);
    this.setResizable(false);
    setVisible(true);
  }

  /**
   * Create and layout the Pen Position panel using the supplied
   * font for the border title and dimension for the preferred size.
   */
  private JPanel createPenPanel(Font f, Dimension d) {
    JPanel pnl = new JPanel();
    pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
    pnl.setBorder(BorderFactory.createTitledBorder("Pen Position"));
    ((TitledBorder) pnl.getBorder()).setTitleFont(f);
    pnl.setPreferredSize(d);

    btnPenUp = new JRadioButton("Pen Up");
    btnPenUp.addActionListener(this);

    btnPenDown = new JRadioButton("Pen Down");
    btnPenDown.addActionListener(this);

    ButtonGroup grpPen = new ButtonGroup();
    grpPen.add(btnPenUp);
    grpPen.add(btnPenDown);
    btnPenUp.setSelected(true);

    pnl.add(btnPenUp);
    pnl.add(Box.createVerticalStrut(COMP_SEP));
    pnl.add(btnPenDown);
    
    return pnl;
  }

  /**
   * Create and layout the Turn Direction panel using the supplied
   * font for the border title and dimension for the preferred size.
   */
  private JPanel createTurnPanel(Font f, Dimension d) {
    JPanel pnl = new JPanel();
    pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
    pnl.setBorder(BorderFactory.createTitledBorder("Turn Direction"));
    ((TitledBorder) pnl.getBorder()).setTitleFont(f);
    pnl.setPreferredSize(d);

    btnLeft = new JButton("Left");
    btnLeft.addActionListener(this);
    
    btnRight = new JButton("Right");
    btnRight.addActionListener(this);

    pnl.add(btnLeft);
    pnl.add(Box.createVerticalStrut(COMP_SEP));
    pnl.add(btnRight);

    return pnl;
  }

  /**
   * Create and layout the Move Control panel using the supplied
   * font for the border title and dimension for the preferred size.
   */
  private JPanel createMovePanel(Font f, Dimension d) {
    JPanel pnl = new JPanel();
    pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
    pnl.setBorder(BorderFactory.createTitledBorder("Move Control"));
    ((TitledBorder) pnl.getBorder()).setTitleFont(f);
    pnl.setPreferredSize(d);

    txtMove = new JTextField(new Integer(DEFAULT_DISTANCE).toString());
    txtMove.addActionListener(this);
    
    btnMove = new JButton("Move");
    btnMove.addActionListener(this);

    pnl.add(txtMove);
    pnl.add(Box.createVerticalStrut(COMP_SEP));
    pnl.add(btnMove);

    return pnl;
  }

  /**
   * Create and layout the Debug Info panel using the supplied
   * font for the border title and dimension for the preferred size.
   */
  private JPanel createDebugPanel(Font f, Dimension d) {
    JPanel pnl = new JPanel();
    if (DEBUG) {
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
      pnl.setBorder(BorderFactory.createTitledBorder("Debug Info"));
      ((TitledBorder) pnl.getBorder()).setTitleFont(f);
      pnl.setPreferredSize(d);

      txtDebug = new JTextArea();
      txtDebug.setEditable(false);
      txtDebug.setFont(f);

      pnl.add(txtDebug);
    }

    return pnl;
  }

  /** Updates the text for the debug panel with the cursor status */
  private void printDebugInfo() {
    String txt = "Write = " + write + "\nDir = " + dir + " (" + pnlDraw.currentRotateAngle +
                 ")\nDistance = " + distance + "\nX,Y = " + pnlDraw.posX + "," + pnlDraw.posY +
                 "\nMax = " + pnlDraw.maxX + "," + pnlDraw.maxY;   
    txtDebug.setText(txt);
  }

  /** Handles all the button presses, text entry events for the GUI */
  public void actionPerformed(ActionEvent e) {
    // Pen up
    if (e.getSource() == btnPenUp) {
      write = false;

    // Pen down
    } else if (e.getSource() == btnPenDown) {
      write = true;
    
    // Turn left (handle wrap from 0 to 3)
    } else if (e.getSource() == btnLeft) {
      dir = (dir - 1 < 0) ? 3 : dir - 1;
      pnlDraw.rotate(COUNTERCLOCKWISE);

    // Turn right (handle wrap from 3 to 0)
    } else if (e.getSource() == btnRight) {
      dir = (dir + 1) % 4;
      pnlDraw.rotate(CLOCKWISE);  

    // Move the turtle cursor
    } else if (e.getSource() == txtMove || e.getSource() == btnMove) {
      try {
        distance = Integer.parseInt(txtMove.getText());
      } catch (NumberFormatException nfe) {
        distance = DEFAULT_DISTANCE;
        txtMove.setText(new Integer(DEFAULT_DISTANCE).toString());
      }

      pnlDraw.moveTurtle();
    }

    if (DEBUG) printDebugInfo();
  }
  
  public static void main(String[] args) {
    // Make sure we have nice window decorations
    JFrame.setDefaultLookAndFeelDecorated(true);
    
    Image i = new ImageIcon("turtle.gif").getImage();
    new TurtleGraphics(i);
  }

  /**
   * Handles rendering of the drawn path and cursor, as well as maintains
   * all status information regarding its orientation and position.
   */
  private class DrawingPanel extends JPanel {
    private int posX, posY;  // Current position of the cursor
    private int minX, maxX, minY, maxY, imgPad;  // Used for bounding the drawing area
    private Image img;  // Graphic for the cursor
    private GeneralPath pathDraw;  // Accumulated drawing path
    private AffineTransform rotation;  // Used for orienting the cursor
    private int currentRotateAngle = 90;  // Initial direction is RIGHT
    private int imgWidth, imgHeight;

    private DrawingPanel(Image img) {
      this.img = img;
      imgWidth = img.getWidth(this);
      imgHeight = img.getHeight(this); 

      // Create a pad to the borders of the drawing area based on the
      // size of the cursor image.  Can't set maxX and maxY here because
      // the DrawingPanel has not been rendered yet, thus set these
      // values when the component is painted.
      imgPad = Math.abs(imgWidth - imgHeight);
      minX = minY = imgPad;

      // Set initial position of the cursor to the upper left bounds
      posX = minX;
      posY = minY;

      // Initialize the drawing path and set it to the cursor's current pen point
      pathDraw = new GeneralPath();
      pathDraw.moveTo(getPenPoint().x, getPenPoint().y);

      rotation = new AffineTransform();
    }

    public void paintComponent(Graphics g) {
      maxX = getWidth() - (imgWidth + imgPad);
      maxY = getHeight() - (imgHeight + imgPad);

      Graphics2D g2d = (Graphics2D) g;

      // Paint the background white
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, getWidth(), getHeight());

      // Moves the cursor to its current position
      rotation.setToTranslation(posX, posY);

      // Rotates the cursor with the rotation point as the center of the image
      rotation.rotate(Math.toRadians(currentRotateAngle), imgWidth / 2, imgHeight / 2);

      // Draw the accumulated path using black
      g2d.setColor(Color.BLACK);
      g2d.draw(pathDraw);

      // Draw the image using the AffineTransform
      g2d.drawImage(img, rotation, this);
    }

    /** Calculate the angle at which to draw the rotation of the cursor */
    private void rotate(boolean clockwise) {
      if (clockwise) currentRotateAngle += ROTATION_ANGLE;
      else currentRotateAngle -= ROTATION_ANGLE;

      // Keep degrees of rotation constrained from 0-359
      currentRotateAngle %= 360;
      if (currentRotateAngle < 0) currentRotateAngle += 360; 

      repaint();
    }

    /**
     * For each direction, reduce the distance to not exceed the bounds of
     * the floor, then move the turtle, writing if pen is down.
     */
    private void moveTurtle() {
      switch (dir) {
        case RIGHT:  // initial direction
          if (posX + distance > maxX) distance = maxX - posX;
          posX += distance;
          break;

        case DOWN:
          if (posY + distance > maxY) distance = maxY - posY;
          posY += distance;
          break;

        case LEFT:
          if (posX - distance < minX) distance = posX - minX;
          posX -= distance;
          break;

        case UP:
          if (posY - distance < minY) distance = posY - minY;
          posY -= distance;
          break;       

        default:
          break;
      }    

      if (write) {
        pathDraw.lineTo(getPenPoint().x, getPenPoint().y);
      } else {
        pathDraw.moveTo(getPenPoint().x, getPenPoint().y);
      }

      repaint();
    }

    /** 
     * Ensures the pen point is from approximately the center of the cursor,
     * but use width for both coordinates to ensure consistency of line connections. 
     */
    private Point getPenPoint() {
      return new Point(posX + (imgWidth / 2), posY + (imgWidth / 2));
    }
  }
}
