package org.vesselonline.jai.overlay;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import net.miginfocom.swing.MigLayout;

public class CompositeImagePanel extends JPanel implements ActionListener, SelectableImageListener, SourceImagePanelListener {
  private RenderedImage cmpImg;
  private String cmpImagePath;
  private JLabel imgNameSizeLabel;
  private CompositeImageDisplayJAI imgDisplayJAI;
  private JScrollPane displayJAIScrollPane;

  private JButton saveButton, printButton;
  private JToggleButton addButton, subtractButton, andButton, orButton;
  private JPanel selectPanel;
  private JTextArea selectTxtArea;

  private List<SourceImagePanel> sipList;

  private static final String FILE_EXT = "jpg";


  public CompositeImagePanel() {
    super(new MigLayout("wrap 2", "[center][center]", "[][][][]" + ImageOverlayUtilities.LAYOUT_GAP + "[]"));
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    imgDisplayJAI = new CompositeImageDisplayJAI();
    imgDisplayJAI.addSelectableImageListener(this);
    cmpImagePath = "Untitled";
    displayJAIScrollPane = new JScrollPane(imgDisplayJAI, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    sipList = new ArrayList<SourceImagePanel>();

    imgNameSizeLabel = new JLabel(createImgNameSizeText());
    saveButton = ImageOverlayUtilities.createJButton("Save", "Save the composite image in JPEG format.", this);
    printButton = ImageOverlayUtilities.createJButton("Print", "Print the composite image.", this);

    addButton = ImageOverlayUtilities.createJToggleButton("Add", "Overlay the source images by addition.", this);
    addButton.setSelected(true);
    subtractButton = ImageOverlayUtilities.createJToggleButton("Subtract", "Overlay the source images by subtracting image 2 from 1.", this);
    andButton = ImageOverlayUtilities.createJToggleButton("And", "Overlay the source images using a logical AND operation.", this);
    orButton = ImageOverlayUtilities.createJToggleButton("Or", "Overlay the source images using a logical OR operation.", this);

    selectPanel = createSelectPanel();

    add(imgNameSizeLabel, "spanx 2");
    add(saveButton, "split 2, spanx 2");
    add(printButton);
    add(displayJAIScrollPane, "spanx 2, " + ImageOverlayUtilities.MIG_COMP_FORMAT);

    add(addButton, "split 2, align left, push");
    add(subtractButton);
    add(andButton, "split 2, align right, push");
    add(orButton);

    add(selectPanel, "spanx 2, width " + (ImageOverlayUtilities.DEFAULT_IMG_DIM + ImageOverlayUtilities.LAYOUT_GAP) + "!");
  }


  /** Updates the text for the select panel with the selection area data. */
  public void selectionUpdated() {
    Rectangle rect = imgDisplayJAI.getSelection();
    StringBuilder txt = new StringBuilder("Origin (x,y) = " + rect.x + ", " + rect.y + "\t" +
                                          "Size (w,h) = " + rect.width + ", " + rect.height + "\n\n");

    List<RenderedImage> imageCollection = createImageCollection();
    ROI roi = new ROIShape(rect);
    PlanarImage tempImg;

    // Variables for the Mean operation
    ParameterBlock meanPb = new ParameterBlock();
    meanPb.add(roi);  // ROI, null means whole image
    meanPb.add(1);  // horizontal sampling rate
    meanPb.add(1);  // vertical sampling rate

    List<Double> meanBandSumAry = new ArrayList<Double>();
    double[] meanBandAry;
    double meanBandSum;
    int meanBandCount;
/*
    // Variables for the Histogram operation
    ParameterBlock histPb = new ParameterBlock();
    histPb.add(roi);  // ROI, null means whole image
    histPb.add(1);  // horizontal sampling rate
    histPb.add(1);  // vertical sampling rate
    histPb.add(new int[]{256, 256, 256});  // number of bins, one entry per band
    histPb.add(new double[]{0.0D, 0.0D, 0.0D});  // low pixel value, one entry per band
    histPb.add(new double[]{256.0D, 256.0D, 256.0D});  // high pixel value, one entry per band

    Histogram histogram;
*/
    for (int i = 0; i < imageCollection.size(); i++) {
      meanPb.removeSources();
      meanPb.addSource(imageCollection.get(i));
      tempImg = JAI.create("mean", meanPb, null);
      meanBandAry = (double[]) tempImg.getProperty("mean");
      meanBandSum = 0;
      meanBandCount = 0;

      txt.append("IMAGE " + (i + 1) + ":  Band Averages (R,G,B) = (");

      for (int j = 0; j < meanBandAry.length; j++) {
        if (meanBandAry[j] > 0) {
          meanBandSum += meanBandAry[j];
          meanBandCount++;
        }
        txt.append(ImageOverlayUtilities.getShortDecimal(meanBandAry[j]) + ", ");
//        System.out.println("Image " + i + ", Band " + j + " mean = " + meanBandAry[j]);
      }

      if (txt.lastIndexOf(", ") == (txt.length() - 2)) {
        txt.setLength(txt.length() - 2);
      }

      txt.append(")\n         Pixel Average = " + ImageOverlayUtilities.getShortDecimal(meanBandSum / meanBandCount) +
                 " using " + meanBandCount + " band(s)\n\n");
      meanBandSumAry.add(meanBandSum);
/*
      histPb.removeSources();
      histPb.addSource(imageCollection.get(i));
      tempImg = JAI.create("histogram", histPb, null);
      histogram = (Histogram) tempImg.getProperty("histogram");

      for (int j = 0; j < histogram.getNumBands(); j++) {
        for (int k = 0; k < histogram.getBins(j).length; k++) {
//          System.out.println("Image " + (i + 1) + ", Band " + j + ", Bin " + k + " = " + histogram.getBins()[j][k]);
        }
      } */
    }

    txt.append("Percentage Similar = ");
    if (! meanBandSumAry.isEmpty()) {
      txt.append(ImageOverlayUtilities.getShortDecimal(Collections.min(meanBandSumAry) / Collections.max(meanBandSumAry) * 100.0F));
    }

    selectTxtArea.setText(txt.toString());
  }


  public void addSourceImagePanel(SourceImagePanel sip) { sipList.add(sip); }

  public void removeSourceImagePanel(SourceImagePanel sip) { sipList.remove(sip); }

  public void imageUpdated() {
    // Following section is set to run with SwingUtilities.invokeAndWait() due to
    // timing issues with SourceImagePanel.resetImagePanel() method.  See
    // Javadoc for SwingUtilities.invokeAndWait() for more information.
    final Runnable doUpdateDisplayJAI = new Runnable() {
      public void run() {
        List<RenderedImage> imageCollection = createImageCollection();

        if (imageCollection.size() > 1) {
          ParameterBlock pb = new ParameterBlock();

          if (addButton.isSelected()) {
            pb.addSource(imageCollection);
            cmpImg = JAI.create("addcollection", pb, null);

          } else if (subtractButton.isSelected()) {
            pb.addSource(imageCollection.get(0));
            pb.addSource(imageCollection.get(1));
            cmpImg = JAI.create("subtract", pb, null);
          } else if (andButton.isSelected()) {
            pb.addSource(imageCollection.get(0));
            pb.addSource(imageCollection.get(1));
            cmpImg = JAI.create("and", pb, null);
          } else if (orButton.isSelected()) {
            pb.addSource(imageCollection.get(0));
            pb.addSource(imageCollection.get(1));
            cmpImg = JAI.create("or", pb, null);
          }

        } else if (imageCollection.size() == 1) {
          cmpImg = imageCollection.get(0);
        } else {
          cmpImg = ImageOverlayUtilities.createDefaultImage();
        }

        imgNameSizeLabel.setText(createImgNameSizeText());
        imgDisplayJAI.set(cmpImg);
      }
    };

    Thread appThread = new Thread() {
      public void run() {
        try {
          SwingUtilities.invokeAndWait(doUpdateDisplayJAI);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    appThread.start();
  }


  public final void actionPerformed(ActionEvent ae) {
    AbstractButton evtButton = (AbstractButton) ae.getSource();

    if (evtButton == saveButton) {
      savePanel();

    } else if (evtButton == printButton) {
      printPanel();

    } else if (evtButton == addButton) {
      subtractButton.setSelected(false);
      andButton.setSelected(false);
      orButton.setSelected(false);
      imageUpdated();

    } else if (evtButton == subtractButton) {
      addButton.setSelected(false);
      andButton.setSelected(false);
      orButton.setSelected(false);
      imageUpdated();

    } else if (evtButton == andButton) {
      addButton.setSelected(false);
      subtractButton.setSelected(false);
      orButton.setSelected(false);
      imageUpdated();

    } else if (evtButton == orButton) {
      addButton.setSelected(false);
      subtractButton.setSelected(false);
      andButton.setSelected(false);
      imageUpdated();
    }
  }


  /** Save the composite image to a file. */
  public void savePanel() {
    File f = new File(cmpImagePath);
    
    JFileChooser saveDialog = new JFileChooser(".");
    saveDialog.setMultiSelectionEnabled(false);
    saveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
    saveDialog.setFileFilter(new JPGFileFilter());

    if (f.exists()) {
      saveDialog.setCurrentDirectory(f.getParentFile());
      saveDialog.setSelectedFile(f);
    }

    int saveResult = saveDialog.showSaveDialog(this);
    if (saveResult == JFileChooser.APPROVE_OPTION) {
      f = saveDialog.getSelectedFile();
      cmpImagePath = f.getAbsolutePath();

      if (! f.getName().toLowerCase().endsWith("." + FILE_EXT)) {
        cmpImagePath += "." + FILE_EXT;
        f = new File(cmpImagePath);
      }
    } else {
      return;
    }

    try {
      JAI.create("filestore", cmpImg, cmpImagePath, "JPEG", null);
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "An error occurred while attempting to save your image!\n" +
                                    "Please try saving this image again.",
                                    "Image Not Saved", JOptionPane.ERROR_MESSAGE);
      return;
    }
  }


  /** Adapted from http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html */
  public void printPanel() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(imgDisplayJAI);
    if (printJob.printDialog()) {
      try {
        printJob.print();
      } catch (PrinterException pe) {
        pe.printStackTrace();
        JOptionPane.showMessageDialog(this, "An error occurred while attempting to print your image!\n" +
                                      "Please try printing this image again.",
                                      "Image Not Printed", JOptionPane.ERROR_MESSAGE);
        return;
      }
    }
  }


  public void clearSelection() {
    imgDisplayJAI.setSelection(null);
    selectionUpdated();
  }


  private RenderedImage createViewportImage(SourceImagePanel sip) {
    PlanarImage srcImg = sip.getSourceImage().getPipelineImage();
    JViewport viewport = sip.getViewport();

    Point pos = viewport.getViewPosition();
    Dimension dim = viewport.getExtentSize();

    int width = (dim.width > srcImg.getWidth()) ? srcImg.getWidth() : dim.width;
    int height = (dim.height > srcImg.getHeight()) ? srcImg.getHeight() : dim.height;

    if (width <= 0 || height <= 0) {
      return srcImg;
    } else {
      return srcImg.getAsBufferedImage(new Rectangle(pos.x, pos.y, width, height), null);
    }
  }

  private List<RenderedImage> createImageCollection() {
    ArrayList<RenderedImage> imgList = new ArrayList<RenderedImage>(sipList.size());
    RenderedImage rImg;

    for (int i = 0; i < sipList.size(); i++) {
      rImg = createViewportImage(sipList.get(i));
      if (rImg != null) {
        imgList.add(rImg);
      }
    }

    return imgList;
  }


  private String createImgNameSizeText() {
    if (cmpImg != null) {
      return "Composite Image - " + cmpImg.getWidth() + "W x " + cmpImg.getHeight() + "H";
    } else {
      return "Composite Image - 0W x 0H";
    }
  }


  /**
   * Create and layout the Debug Info panel using the supplied
   * font for the border title and dimension for the preferred size.
   */
  private JPanel createSelectPanel() {
    JPanel pnl = new JPanel();
    pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
    pnl.setBorder(BorderFactory.createTitledBorder("Selection Area Info"));
    ((TitledBorder) pnl.getBorder()).setTitleFont(ImageOverlayUtilities.SMALL_FONT);

    selectTxtArea = new JTextArea();
    selectTxtArea.setEditable(false);
    selectTxtArea.setFont(ImageOverlayUtilities.SMALL_FONT);

    selectionUpdated();
    pnl.add(selectTxtArea);
    return pnl;
  }


  /** Create a file filter that allows one to select a supported graphics file. */
  private class JPGFileFilter extends FileFilter {
    public boolean accept(File pathname) {
      if (pathname.isDirectory() || pathname.getName().toLowerCase().endsWith("." + FILE_EXT)) {
        return true;
      } else {
        return false;
      }
    }

    public String getDescription() { return "JPG Files"; }
  }
}
