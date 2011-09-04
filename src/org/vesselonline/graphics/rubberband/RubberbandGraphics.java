package org.vesselonline.graphics.rubberband;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class RubberbandGraphics extends JFrame {
  private static final String APP_TITLE = "CS750 MiniCAD";
  private final Color colorValues[] = {Color.WHITE, Color.RED, Color.GREEN, Color.BLUE};
  private RubberbandPanel drawingPanel;

  private JMenu fileMenu;
  private FileHandler fileHandler;
  private JMenuItem fileItems[];

  private JMenu drawMenu;
  private DrawHandler drawHandler;
  private JRadioButtonMenuItem drawItems[];
  private ButtonGroup drawButtonGroup;

  private JMenu colorMenu;
  private JRadioButtonMenuItem colorItems[];
  private ButtonGroup colorButtonGroup;

  private JMenu optionMenu;
  private OptionHandler optionHandler;
  private JRadioButtonMenuItem styleItems[];
  private ButtonGroup styleButtonGroup;
  private JRadioButtonMenuItem widthItems[];
  private ButtonGroup widthButtonGroup;
  private JCheckBoxMenuItem fillItem;
  private JCheckBoxMenuItem autoSaveItem;

  // no-argument constructor set up GUI
  public RubberbandGraphics() {
    super(APP_TITLE + ":  Untitled");

    fileMenu = createFileMenu();
    drawMenu = createDrawMenu();
    colorMenu = createColorMenu();
    optionMenu = createOptionMenu();;

    JMenu helpMenu = createHelpMenu();

    JMenuBar bar = new JMenuBar();
    setJMenuBar(bar);
    bar.add(fileMenu);
    bar.add(drawMenu);
    bar.add(colorMenu);
    bar.add(optionMenu);
    bar.add(helpMenu);
// The setHelpMenu() method is not yet implemented in JSE 5.0 and throws an exception
//    bar.setHelpMenu(helpMenu);

    drawingPanel = new RubberbandPanel(this);
    drawingPanel.setBackground(Color.BLACK);

    add(drawingPanel, BorderLayout.CENTER);
  }

  /** Create file menu available for menubar usage. */
  private JMenu createFileMenu() {
    JMenu fMenu = new JMenu("File");
    fMenu.setMnemonic('F');

    // Array listing file operations
    String files[] = {"New", "Open...", "Save", "Save As...", "Print", "Exit"};

    fileHandler = new FileHandler();
    fileItems = new JMenuItem[files.length];

    // Create menu items for file operations
    for (int i = 0; i < files.length; i++) {
      fileItems[i] = new JMenuItem(files[i]);
      fileItems[i].setMnemonic(files[i].charAt(0));
      fileItems[i].addActionListener(fileHandler);
      fMenu.add(fileItems[i]);
    }
    // Exceptions for mnemonics for Save As and Exit, which should use 'A' and 'x' 
    fileItems[3].setMnemonic('A');
    fileItems[5].setMnemonic('x');

    // Add the action listener to check whether Save should be disabled or enabled
    fMenu.addMenuListener(fileHandler);
    return fMenu;
  }

  /** Create draw menu available for menubar or popup menu usage. */
  private JMenu createDrawMenu() {
    JMenu dMenu = new JMenu("Draw");
    dMenu.setMnemonic('D');

    // Array listing drawing shapes
    String shapes[] = {"Line", "Circle", "Rectangle", "Triangle", "Quadrilateral", "Freehand"};

    drawHandler = new DrawHandler();
    drawItems = new JRadioButtonMenuItem[shapes.length];
    drawButtonGroup = new ButtonGroup();

    // Create menu items for drawing shape
    for (int i = 0; i < shapes.length; i++) {
      drawItems[i] = new JRadioButtonMenuItem(shapes[i]);
      drawItems[i].setMnemonic(shapes[i].charAt(0));
      drawItems[i].addActionListener(drawHandler);
      drawButtonGroup.add(drawItems[i]);
      dMenu.add(drawItems[i]);
    }

    // Set line as default drawing shape
    drawItems[0].setSelected(true);
    return dMenu;
  }

  /** Create color menu available for menubar or popup menu usage. */
  private JMenu createColorMenu() {
    JMenu cMenu = new JMenu("Color");
    cMenu.setMnemonic('C');

    // Array listing string color names (can't derive from Color constants)
    String colors[] = {"White", "Red", "Green", "Blue"};

    colorItems = new JRadioButtonMenuItem[colors.length];
    colorButtonGroup = new ButtonGroup();

    // Create radio button menu items for colors
    for (int i = 0; i < colors.length; i++) {
      colorItems[i] = new JRadioButtonMenuItem();
      ColorAction cAction = new ColorAction(colors[i], new Integer(colors[i].charAt(0)));
      colorItems[i].setAction(cAction);
      colorButtonGroup.add(colorItems[i]);
      cMenu.add(colorItems[i]);
    }

    // Set white as default drawing color
    colorItems[0].setSelected(true);
    return cMenu;
  }

  /** Create option menu available for menubar or popup menu usage. */
  private JMenu createOptionMenu() {
    JMenu oMenu= new JMenu("Option");
    oMenu.setMnemonic('O');

    // Arrays listing styles and widths for lines 
    String styles[] = {"Solid", "Dashed", "Dotted"};
    String widths[] = {"1", "2", "Select"};

    optionHandler = new OptionHandler();
    styleItems = new JRadioButtonMenuItem[styles.length];
    styleButtonGroup = new ButtonGroup();
    widthItems = new JRadioButtonMenuItem[widths.length];
    widthButtonGroup = new ButtonGroup();

    JMenu styleMenu = new JMenu("Line Style");
    styleMenu.setMnemonic('S');
    
    // Create radio button menu items for styles
    for (int i = 0; i < styles.length; i++) {
      styleItems[i] = new JRadioButtonMenuItem(styles[i]);
      styleItems[i].setMnemonic(styles[i].charAt(0));
      styleItems[i].addActionListener(optionHandler);
      styleButtonGroup.add(styleItems[i]);
      styleMenu.add(styleItems[i]);
    }
    // Exception for mnemonic for Dotted, which should use 't'
    styleItems[2].setMnemonic('t');
    // Set solid as default line style
    styleItems[0].setSelected(true);

    JMenu widthMenu = new JMenu("Line Width");
    widthMenu.setMnemonic('W');

    // Create radio button menu items for widths
    for (int i = 0; i < widths.length; i++) {
      widthItems[i] = new JRadioButtonMenuItem(widths[i]);
      widthItems[i].setMnemonic(widths[i].charAt(0));
      widthItems[i].addActionListener(optionHandler);
      widthButtonGroup.add(widthItems[i]);
      widthMenu.add(widthItems[i]);
    }
    // Set 1 as default line width
    widthItems[0].setSelected(true);

    fillItem = new JCheckBoxMenuItem("Fill");
    fillItem.setMnemonic('F');
    fillItem.addActionListener(optionHandler);

    autoSaveItem = new JCheckBoxMenuItem("Auto Save");
    autoSaveItem.setMnemonic('A');
    autoSaveItem.addActionListener(optionHandler);

    oMenu.add(styleMenu);
    oMenu.add(widthMenu);
    oMenu.add(fillItem);
    oMenu.add(autoSaveItem);

    return oMenu;
  }
  
  /** Create help menu available for menubar usage. */
  private JMenu createHelpMenu() {
    JMenu hMenu = new JMenu("Help");
    hMenu.setMnemonic('H');

    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.setMnemonic('A');
    hMenu.add(aboutItem);
    aboutItem.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          JOptionPane.showMessageDialog(RubberbandGraphics.this,
                                        APP_TITLE + "\n\nby Steven Handy\n443.756.1315\nsjhandy@loyola.edu",
                                        "About", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    );

    return hMenu;
  }

  /** Set the frame title using the application name and the current file. */
  protected void setFrameFileName(String title) {
    this.setTitle(APP_TITLE + ":  " + title);
  }

  /**
   * Create the GUI and show it.  For thread safety, this method
   * should be invoked from the event-dispatching thread.
   */
  private static void createAndShowGUI() {
    // Use Java's Metal look and feel
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);

    // Create, set up, and display the window
    RubberbandGraphics rbg = new RubberbandGraphics();
    rbg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    rbg.setSize(640, 640);
    rbg.setVisible(true);
  }

  /** Instantiates a new RubberbandGraphics application. */
  public static void main(String[] args) {
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }

  /** Handle file manipulation events from the main menu. */
  private class FileHandler implements ActionListener, MenuListener {
    public void menuCanceled(MenuEvent e) { }
    public void menuDeselected(MenuEvent e) { }

    /**
     * Checks the saved state of the drawing panel to determine whether the Save
     * item should be enabled or disabled.
     */
    public void menuSelected(MenuEvent e) {
      // Displaying the menu
      if (e.getSource() == fileMenu) {
        if (drawingPanel.isSaved()) fileItems[2].setEnabled(false);
        else fileItems[2].setEnabled(true);
      }
    }
    
    public void actionPerformed(ActionEvent e) {
      // New drawing
      if (e.getSource() == fileItems[0]) {
        drawingPanel.clearPanel();

      // Open a drawing
      } else if (e.getSource() == fileItems[1]) {
        drawingPanel.loadPanel();

      // Save the drawing
      } else if (e.getSource() == fileItems[2]) {
        drawingPanel.savePanel(false);

      // Save the drawing with a different file name
      } else if (e.getSource() == fileItems[3]) {
        drawingPanel.savePanel(true);

      // Print the drawing
      } else if (e.getSource() == fileItems[4]) {
        drawingPanel.printPanel();
        drawingPanel.repaint();

      // Exit the program
      } else if (e.getSource() == fileItems[5]) {
        drawingPanel.clearPanel();
        System.exit(0);
      }
    }
  }

  /** Handle draw type selection events from all menu types. */
  private class DrawHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      for (int i = 0; i < drawItems.length; i++) {
        if (e.getSource() == drawItems[i]) {
          drawingPanel.setCurrentShape(i);
          break;
        }
      }
    }
  }

  /** Handle color selection events from all menu types. */
  private class ColorAction extends AbstractAction {
    public ColorAction(String text, Integer mnemonic) {
      super(text);
      putValue(MNEMONIC_KEY, mnemonic);
    }

    public void actionPerformed(ActionEvent e) {
      for (int i = 0; i < colorItems.length; i++) {
        if (e.getSource() == colorItems[i]) {
          drawingPanel.setCurrentColor(colorValues[i]);
          break;
        }
      }
    }
  }

  /** Handle option selection events from all menu types. */
  private class OptionHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      for (int i = 0; i < styleItems.length; i++) {
        if (e.getSource() == styleItems[i]) {
          drawingPanel.setCurrentStyle(i);
          return;
        }
      }

      if (e.getSource() == widthItems[0]) {
        drawingPanel.setCurrentWidth(1);
        widthItems[2].setText("Select");
        return;
      } else if (e.getSource() == widthItems[1]) {
        drawingPanel.setCurrentWidth(2);
        widthItems[2].setText("Select");
        return;
      } else if (e.getSource() == widthItems[2]) {
        try {
          int newWidth = Integer.parseInt(JOptionPane.showInputDialog(RubberbandGraphics.this,
                                                                      "Please enter a line width:", "Select Width",
                                                                      JOptionPane.QUESTION_MESSAGE));
          if (newWidth < 1) throw new NumberFormatException("Value must be > 0!");
          drawingPanel.setCurrentWidth(newWidth);
          widthItems[2].setText("Select (" + newWidth + ")");
          return;
        } catch (NumberFormatException nfe) {
          JOptionPane.showMessageDialog(RubberbandGraphics.this, "The line width must be a positive integer!\n" +
                                        "Line width reset to 1 (default).",
                                        "Invalid Width", JOptionPane.ERROR_MESSAGE);
          widthItems[0].setSelected(true);
          drawingPanel.setCurrentWidth(1);
          widthItems[2].setText("Select");
          return;
        }
      }

      if (e.getSource() == fillItem) drawingPanel.setCurrentFill(fillItem.isSelected());
      if (e.getSource() == autoSaveItem) {
        if (autoSaveItem.isSelected()) drawingPanel.savePanel(false);

        drawingPanel.setAutoSave(autoSaveItem.isSelected());
      }
    }
  }
}
