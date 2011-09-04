package org.vesselonline.jai.overlay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class ImageOverlayApplication extends JFrame {
  private CompositeImagePanel cmpImgPanel;
  private SourceImagePanel srcImgPanel1, srcImgPanel2;
  private String lastFilePath;

  private JMenu fileMenu, editMenu;
  private FileHandler fileHandler;
  private EditHandler editHandler;
  private JMenuItem[] fileItems, editItems;

  private static final String APP_TITLE = "Image Overlay";

  public ImageOverlayApplication() {
    super(APP_TITLE);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    cmpImgPanel = new CompositeImagePanel();
    srcImgPanel1 = new JAISourceImagePanel(null);
    srcImgPanel2 = new JAISourceImagePanel(null);

    srcImgPanel1.addSourceImagePanelListener(cmpImgPanel);
    srcImgPanel2.addSourceImagePanelListener(cmpImgPanel);

    lastFilePath = ImageOverlayUtilities.NO_IMG_PATH;

    fileMenu = createFileMenu();
    editMenu = createEditMenu();
    JMenu helpMenu = createHelpMenu();

    JMenuBar bar = new JMenuBar();
    this.setJMenuBar(bar);
    bar.add(fileMenu);
    bar.add(editMenu);
    bar.add(helpMenu);

    this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.LINE_AXIS));
    this.getContentPane().add((JAISourceImagePanel) srcImgPanel1);
    this.getContentPane().add((JAISourceImagePanel) srcImgPanel2);
    this.getContentPane().add(cmpImgPanel);
  }


  private String selectSourceImageFile(SourceImagePanel sip) {
    String currPath = sip.getSourceImage().getSourceImagePath();
    if (currPath == null || currPath.equals(ImageOverlayUtilities.NO_IMG_PATH)) {
      if (lastFilePath != null && ! lastFilePath.equals(ImageOverlayUtilities.NO_IMG_PATH)) {
        currPath = lastFilePath;
      }
    }

    JFileChooser openDialog = new JFileChooser(System.getProperty("user.home"));
    openDialog.setMultiSelectionEnabled(false);
    openDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
    openDialog.setFileFilter(new GraphicsFileFilter());

    if (currPath != null && ! currPath.equals(ImageOverlayUtilities.NO_IMG_PATH)) {
      openDialog.setCurrentDirectory(new File(currPath).getParentFile());
    }

    int openResult = openDialog.showOpenDialog(this);
    if (openResult == JFileChooser.APPROVE_OPTION) {
      return openDialog.getSelectedFile().getAbsolutePath();
    } else {
      return null;
    }
  }


  /** Create file menu available for menubar usage. */
  private JMenu createFileMenu() {
    JMenu fMenu = new JMenu("File");
    fMenu.setMnemonic('F');

    // Array listing file operations
    String files[] = {"Open Image 1...", "Open Image 2...", "Save Composite...", "Print Composite...", "Exit"};

    fileHandler = new FileHandler();
    fileItems = new JMenuItem[files.length];

    // Create menu items for file operations
    for (int i = 0; i < files.length; i++) {
      fileItems[i] = new JMenuItem(files[i]);
      fileItems[i].setMnemonic(files[i].charAt(0));
      fileItems[i].addActionListener(fileHandler);
      fMenu.add(fileItems[i]);
    }

    fileItems[4].setMnemonic('x');
    return fMenu;
  }

  /** Create edit menu available for menubar usage. */
  private JMenu createEditMenu() {
    JMenu eMenu = new JMenu("Edit");
    eMenu.setMnemonic('E');

    // Array listing edit operations
    String edits[] = {"Copy Settings from Image 1 to 2", "Copy Settings from Image 2 to 1", "Clear Composite Selection"};

    editHandler = new EditHandler();
    editItems = new JMenuItem[edits.length];

    // Create menu items for edit operations
    for (int i = 0; i < edits.length; i++) {
      editItems[i] = new JMenuItem(edits[i]);
      editItems[i].setMnemonic(edits[i].charAt(14));
      editItems[i].addActionListener(editHandler);
      eMenu.add(editItems[i]);
    }

    editItems[2].setMnemonic('C');
    return eMenu;
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
          JOptionPane.showMessageDialog(ImageOverlayApplication.this,
                                        APP_TITLE + "\n\nby Steven Handy\n443.756.1315\nsteve.handy@gmail.com",
                                        "About", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    );

    return hMenu;
  }


  /**
   * Create the GUI and show it.  For thread safety,
   * this method should be invoked from the
   * event-dispatching thread.
   */
  private static void createAndShowGUI() {
    //Create and set up the window.
    ImageOverlayApplication frame = new ImageOverlayApplication();

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * In order to avoid error messages on startup when not using the platform
   * native enhancements, use the following flag:
   * 
   * -Dcom.sun.media.jai.disableMediaLib=true
   * 
   * @param args
   */
  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          createAndShowGUI();
        }
    });
  }


  /** Handle file manipulation events from the main menu. */
  private class FileHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      // Open a file in SourceImagePanel 1
      if (e.getSource() == fileItems[0]) {
        srcImgPanel1.setSourceImage(lastFilePath = selectSourceImageFile(srcImgPanel1));

      // Open a file in SourceImagePanel 2
      } else if (e.getSource() == fileItems[1]) {
        srcImgPanel2.setSourceImage(lastFilePath = selectSourceImageFile(srcImgPanel2));
      
      // Save the composite image
      } else if (e.getSource() == fileItems[2]) {
        cmpImgPanel.savePanel();

      // Print the composite image
      } else if (e.getSource() == fileItems[3]) {
        cmpImgPanel.printPanel();
        cmpImgPanel.repaint();

      // Exit the program
      } else if (e.getSource() == fileItems[4]) {
        System.exit(0);
      }
    }
  }


  /** Handle image editing events from the main menu. */
  private class EditHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      // Copy current image settings from SourceImagePanel 1 to SourceImagePanel 2
      if (e.getSource() == editItems[0]) {
        srcImgPanel2.importSettings(srcImgPanel1.getSourceImage(), srcImgPanel1.getViewport());

      // Copy current image settings from SourceImagePanel 2 to SourceImagePanel 1
      } else if (e.getSource() == editItems[1]) {
        srcImgPanel1.importSettings(srcImgPanel2.getSourceImage(), srcImgPanel2.getViewport());

      // Clear the rectangle selection in the CompositeImagePanel
      } else if (e.getSource() == editItems[2]) {
        cmpImgPanel.clearSelection();
      }
    }
  }  


  /** Create a file filter that allows one to select a supported graphics file. */
  private class GraphicsFileFilter extends FileFilter {
    private static final String FILE_EXT_PATTERN = ".*\\.(bmp|gif|jpeg|jpg|png|tif|tiff)$";
    public boolean accept(File pathname) {
      if (pathname.isDirectory() || pathname.getName().toLowerCase().matches(FILE_EXT_PATTERN)) {
        return true;
      } else {
        return false;
      }
    }

    public String getDescription() { return "Supported Graphics Files"; }
  }
}
