package org.vesselonline.jai.overlay;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;
import com.sun.media.jai.widget.DisplayJAI;

public class JAISourceImagePanel extends JPanel implements ActionListener, ChangeListener, SourceImagePanel {
  private SourceImage srcImg;
  private String srcImgFileName;
  private JLabel imgNameSizeLabel;
  private DisplayJAI imgDisplayJAI;
  private JScrollPane displayJAIScrollPane;

  private JToggleButton sharpenButton, contrastButton, invertButton;
  private JToggleButton activeButton, redButton, greenButton, blueButton;
  private JButton resetButton, rotateMinusButton, rotatePlusButton;
  private JLabel rotateLabel, scaleLabel, lightLabel, thresholdLabel;
  private JSlider scaleSlider, lightSlider, thresholdSlider;

  private List<SourceImagePanelListener> listeners;

  // JSlider constraints
  private static final int SCALE_MIN = 25;
  private static final int SCALE_INIT = 100;
  private static final int SCALE_MAX = 150;
  private static final int SCALE_MAJOR = (SCALE_MAX - SCALE_MIN) / 5;
  private static final int SCALE_MINOR = SCALE_MAJOR / 5;
  private static final float SCALE_CONV = 100.0F;

  private static final int LIGHT_MIN = 0;
  private static final int LIGHT_INIT = 100;
  private static final int LIGHT_MAX = 100;
  private static final int LIGHT_MAJOR = LIGHT_MAX / 5;
  private static final int LIGHT_MINOR = LIGHT_MAJOR / 4;
  private static final float LIGHT_CONV = 100.0F;

  private static final int THRESHOLD_MIN = 0;
  private static final int THRESHOLD_INIT = 0;
  private static final int THRESHOLD_MAX = 256;
  private static final int THRESHOLD_MAJOR = THRESHOLD_MAX / 4;
  private static final int THRESHOLD_MINOR = THRESHOLD_MAJOR / 4;

  public JAISourceImagePanel(String sourceImageFilePath) {
    super(new MigLayout("wrap 2", "[center][center]", "[][][][][]" + ImageOverlayUtilities.LAYOUT_GAP + "[][][][]"));
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    imgDisplayJAI = new DisplayJAI();
    displayJAIScrollPane = new JScrollPane(imgDisplayJAI, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    displayJAIScrollPane.getViewport().addChangeListener(this);

    if (sourceImageFilePath != null && new File(sourceImageFilePath).exists()) {
      srcImg = new JAISourceImage(sourceImageFilePath);
      srcImgFileName = new File(sourceImageFilePath).getName();
    } else {
      srcImg = new JAISourceImage();
      srcImgFileName = ImageOverlayUtilities.NO_IMG_PATH;
    }

    setDefaultScale();
    listeners = new ArrayList<SourceImagePanelListener>();

    imgNameSizeLabel = new JLabel(createImgNameSizeText());
    activeButton = ImageOverlayUtilities.createJToggleButton("Active", "Contribute image content to composite.", this);
    activeButton.setSelected(true);
    resetButton = ImageOverlayUtilities.createJButton("Reset", "Reset the image to its original contents.", this);

    sharpenButton = ImageOverlayUtilities.createJToggleButton("Sharpen", "Sharpen edges in the image.", this);
    contrastButton = ImageOverlayUtilities.createJToggleButton("Contrast", "Enhance contrast in the image.", this);
    invertButton = ImageOverlayUtilities.createJToggleButton("Invert", "Invert each pixel in the original image.", this);

    redButton = ImageOverlayUtilities.createJToggleButton("Red", "Convert the original image to redscale.", this);
    greenButton = ImageOverlayUtilities.createJToggleButton("Green", "Convert the original image to greenscale.", this);
    blueButton = ImageOverlayUtilities.createJToggleButton("Blue", "Convert the original image to bluescale.", this);

    rotateLabel = ImageOverlayUtilities.createJLabel(createRotateText());
    rotateMinusButton = ImageOverlayUtilities.createJButton("-", "Rotate the image one degree counterclockwise.", this);
    rotatePlusButton = ImageOverlayUtilities.createJButton("+", "Rotate the image one degree clockwise.", this);
//    brightLabel = ImageOverlayUtilities.createJLabel(createBrightnessText());
//    brightMinusButton = ImageOverlayUtilities.createJButton("-", "Decrease the image brightness one level.", this);
//    brightPlusButton = ImageOverlayUtilities.createJButton("+", "Increase the image brightness one level.", this);

    scaleLabel = ImageOverlayUtilities.createJLabel(createScaleText());
    scaleSlider = ImageOverlayUtilities.createJSlider("scale", SCALE_MIN, SCALE_MAX, SCALE_INIT, SCALE_MAJOR, SCALE_MINOR, this);
    lightLabel = ImageOverlayUtilities.createJLabel(createLightnessText());
    lightSlider = ImageOverlayUtilities.createJSlider("lightness", LIGHT_MIN, LIGHT_MAX, LIGHT_INIT, LIGHT_MAJOR, LIGHT_MINOR, this);
    thresholdLabel = ImageOverlayUtilities.createJLabel(createThresholdText());
    thresholdSlider = ImageOverlayUtilities.createJSlider("threshold", THRESHOLD_MIN, THRESHOLD_MAX, THRESHOLD_INIT, THRESHOLD_MAJOR, THRESHOLD_MINOR, this);

    add(imgNameSizeLabel, "spanx 2");
    add(activeButton, "split 2, spanx 2");
    add(resetButton);
    add(displayJAIScrollPane, "spanx 2, " + ImageOverlayUtilities.MIG_COMP_FORMAT);

    add(sharpenButton, "split 3, spanx 2");
    add(contrastButton);
    add(invertButton);
    add(redButton, "split 3, spanx 2");
    add(greenButton);
    add(blueButton);

    add(rotateLabel);
    add(scaleLabel);

    add(rotateMinusButton, "split 2, aligny top");
    add(rotatePlusButton, "aligny top");
    add(scaleSlider);

    add(lightLabel);
    add(thresholdLabel);

    add(lightSlider);
    add(thresholdSlider);

    resetImagePanel();
  }


  public SourceImage getSourceImage() { return srcImg; }

  public void setSourceImage(SourceImage sourceImage) {
    if (sourceImage != null) {
      srcImg = sourceImage;
      srcImgFileName = new File(sourceImage.getSourceImagePath()).getName();
      setDefaultScale();
      resetImagePanel();
    }
  }

  public void setSourceImage(String sourceImageFilePath) {
    if (sourceImageFilePath != null && new File(sourceImageFilePath).exists()) {
      setSourceImage(new JAISourceImage(sourceImageFilePath));
    }
  }

  public JViewport getViewport() { return displayJAIScrollPane.getViewport(); }

  public void resetImagePanel() {
    sharpenButton.setSelected(srcImg.isConvolve());
    contrastButton.setSelected(srcImg.isContrast());
    invertButton.setSelected(srcImg.isInverted());

    redButton.setSelected(false);
    greenButton.setSelected(false);
    blueButton.setSelected(false);
    if ("red".equals(srcImg.getColor())) {
      redButton.setSelected(true);
    } else if ("green".equals(srcImg.getColor())) {
      greenButton.setSelected(true);
    } else if ("blue".equals(srcImg.getColor())) {
      blueButton.setSelected(true);
    }

//    brightLabel.setText(createBrightnessText());
    rotateLabel.setText(createRotateText());

    scaleSlider.setValue((int) (srcImg.getScale() * SCALE_CONV));
    scaleLabel.setText(createScaleText());

    lightSlider.setValue((int) (srcImg.getLightness() * LIGHT_CONV));
    lightLabel.setText(createLightnessText());

    thresholdSlider.setValue((int) srcImg.getThreshold());
    thresholdLabel.setText(createThresholdText());

    imgNameSizeLabel.setText(createImgNameSizeText());

    updateDisplayJAI("long");
  }

  public void importSettings(SourceImage sourceImage, final JViewport viewport) {
    srcImg.resetImage();
    srcImg.setBrightness(sourceImage.getBrightness());
    srcImg.setColor(sourceImage.getColor());
    srcImg.setContrast(sourceImage.isContrast());
    srcImg.setConvolve(sourceImage.isConvolve());
    srcImg.setInverted(sourceImage.isInverted());
    srcImg.setLightness(sourceImage.getLightness());
    srcImg.setRotate(sourceImage.getRotate());
    srcImg.setScale(sourceImage.getScale());
    srcImg.setThreshold(sourceImage.getThreshold());

    resetImagePanel();

    // Following section is set to run with SwingUtilities.invokeAndWait() due to
    // timing issues with scrolling in the SourceImagePanel.importSettings() method.
    // See Javadoc for SwingUtilities.invokeAndWait() for more information.
    final Runnable doScrollRect = new Runnable() {
      public void run() {
        imgDisplayJAI.scrollRectToVisible(new Rectangle(viewport.getViewPosition(), viewport.getExtentSize()));
      }
    };

    Thread appThread = new Thread() {
      public void run() {
        try {
          SwingUtilities.invokeAndWait(doScrollRect);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    appThread.start();
  }


  public final void addSourceImagePanelListener(SourceImagePanelListener sipl) {
    listeners.add(sipl);
    sipl.addSourceImagePanel(this);
  }

  public final void removeSourceImagePanelListener(SourceImagePanelListener sipl) {
    listeners.remove(sipl);
    sipl.removeSourceImagePanel(this);
  }

  public void notifyImageUpdated() {
    for (SourceImagePanelListener sipl : listeners) {
      sipl.imageUpdated();
    }
  }


  public final void actionPerformed(ActionEvent ae) {
    AbstractButton evtButton = (AbstractButton) ae.getSource();

    if (evtButton == sharpenButton) {
      if (sharpenButton.isSelected()) {
        srcImg.setConvolve(true);
      } else {
        srcImg.setConvolve(false);
      }
      
    } else if (evtButton == contrastButton) {
      if (contrastButton.isSelected()) {
        srcImg.setContrast(true);
      } else {
        srcImg.setContrast(false);
      }

    } else if (evtButton == invertButton) {
      if (invertButton.isSelected()) {
        srcImg.setInverted(true);
      } else {
        srcImg.setInverted(false);
      }

    } else if (evtButton == redButton) {
      if (redButton.isSelected()) {
        srcImg.setColor("red");
        greenButton.setSelected(false);
        blueButton.setSelected(false);
      } else {
        srcImg.setColor("normal");
      }

    } else if (evtButton == greenButton) {
      if (greenButton.isSelected()) {
        srcImg.setColor("green");
        redButton.setSelected(false);
        blueButton.setSelected(false);
      } else {
        srcImg.setColor("normal");
      }

    } else if (evtButton == blueButton) {
      if (blueButton.isSelected()) {
        srcImg.setColor("blue");
        redButton.setSelected(false);
        greenButton.setSelected(false);
      } else {
        srcImg.setColor("normal");
      }

    } else if (evtButton == rotateMinusButton) {
      srcImg.decRotate();
      rotateLabel.setText(createRotateText());        

    } else if (evtButton == rotatePlusButton) {
      srcImg.incRotate();
      rotateLabel.setText(createRotateText());
/*
    } else if (evtButton == brightMinusButton) {
      srcImg.decBrightness();
      brightLabel.setText(createBrightnessText());      

    } else if (evtButton == brightPlusButton) {
      srcImg.incBrightness();
      brightLabel.setText(createBrightnessText());      
*/
    } else if (evtButton == activeButton) {
      if (activeButton.isSelected()) {
        for (SourceImagePanelListener sipl : listeners) {
          sipl.addSourceImagePanel(this);
        }
      } else {
        for (SourceImagePanelListener sipl : listeners) {
          sipl.removeSourceImagePanel(this);
        }
      }
      notifyImageUpdated();
      return;

    } else if (evtButton == resetButton) {
      srcImg.resetImage();
      setDefaultScale();
      resetImagePanel();
      return;
    }

    updateDisplayJAI("long");
  }


  public final void stateChanged(ChangeEvent ce) {
    JComponent evtComp = (JComponent) ce.getSource();

    if (evtComp instanceof JSlider) {
      int value = ((JSlider) evtComp).getValue();

      if (evtComp == scaleSlider) {
        srcImg.setScale(value / SCALE_CONV);
        scaleLabel.setText(createScaleText());
        imgNameSizeLabel.setText(createImgNameSizeText());

      } else if (evtComp == lightSlider) {
        srcImg.setLightness(value / LIGHT_CONV);
        lightLabel.setText(createLightnessText());

      } else if (evtComp == thresholdSlider) {
        srcImg.setThreshold(value);
        thresholdLabel.setText(createThresholdText());
      }

      updateDisplayJAI("short");

    } else {  // else evtComp is from the JViewport, in which case we just notifyImageUpdated()
      notifyImageUpdated();
    }
  }


  private void updateDisplayJAI(final String pipelineInitPhase) {
    // DisplayJAI.set(RenderedImage) calls setPreferredSize() and revalidate()
    // at the end of its execution, so those methods should be unnecessary.
    imgDisplayJAI.set(srcImg.doPipeline(pipelineInitPhase));
    notifyImageUpdated();
  }


  private void setDefaultScale() {
    float fValue = ImageOverlayUtilities.DEFAULT_IMG_DIM / srcImg.getWidth();
    if (fValue < 1.0) {
      srcImg.setScale(fValue);
    }
  }


  private String createImgNameSizeText() { return srcImgFileName + " - " + srcImg.getWidth() + "W x " + srcImg.getHeight() + "H"; }

  private String createRotateText() { return "Rotate (" + srcImg.getRotate() + '\u00b0' + ")"; }

//  private String createBrightnessText() { return "Brightness (" + srcImg.getBrightness() + ")"; }

  private String createScaleText() { return "Scale (" + ImageOverlayUtilities.getShortDecimal(srcImg.getScale() * SCALE_CONV) + "%)"; }

  private String createLightnessText() { return "Lightness (" + ImageOverlayUtilities.getShortDecimal(srcImg.getLightness() * LIGHT_CONV) + "%)"; }

  private String createThresholdText() { return "Threshold (" + ImageOverlayUtilities.getShortDecimal(srcImg.getThreshold()) + ")"; }
}
