package org.vesselonline.jai.overlay;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeListener;

public class ImageOverlayUtilities {
  // Setting the font to a Plain, 11pt (vs. Bold, 12pt) font keeps the size
  // small enough for use on Mac and Linux platforms
  private static final Font SMALL_BUTTON_FONT = new Font(new JButton().getFont().getFamily(), Font.PLAIN, 11);
  private static final Insets SMALL_BUTTON_INSETS = new Insets(0, 2, 0, 2);
  private static final int SCROLL_PAD = 20;
  private static final DecimalFormat SHORT_FORMAT = new DecimalFormat("##0.##");

  public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 11);
  public static final int LAYOUT_GAP = 20;
  public static final float DEFAULT_IMG_DIM = 320.0F;
  public static final String MIG_COMP_FORMAT = "width " + (DEFAULT_IMG_DIM + SCROLL_PAD) + "!, " +
                                               "height " + (DEFAULT_IMG_DIM * 0.75 + SCROLL_PAD) + "!";
  public static final String NO_IMG_PATH = "No Source Image";

  public static final BufferedImage createDefaultImage() {
    return new BufferedImage((int) DEFAULT_IMG_DIM, (int) DEFAULT_IMG_DIM, BufferedImage.TYPE_INT_RGB);
  }

  public static final JLabel createJLabel(String labelStr) {
    JLabel label = new JLabel(labelStr);
    label.setFont(SMALL_FONT);
    return label;
  }

  public static final JButton createJButton(String btnLabelStr, String btnHelpStr, ActionListener actListener) {
    JButton btn = new JButton(btnLabelStr);
    btn.setToolTipText(btnHelpStr);
    btn.addActionListener(actListener);
    btn.setFont(SMALL_BUTTON_FONT);
    btn.setMargin(SMALL_BUTTON_INSETS);
    return btn;
  }

  public static final JToggleButton createJToggleButton(String btnLabelStr, String btnHelpStr, ActionListener actListener) {
    JToggleButton btn = new JToggleButton(btnLabelStr);
    btn.setToolTipText(btnHelpStr);
    btn.addActionListener(actListener);
    btn.setFont(SMALL_BUTTON_FONT);
    btn.setMargin(SMALL_BUTTON_INSETS);
    return btn;
  }

  public static final JSlider createJSlider(String name, int min, int max, int init, int major, int minor, ChangeListener chgListener) {
    JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, init);
    slider.setToolTipText("Change the " + name + " within a range from " + min + " to " + max + ".");
    slider.setFont(SMALL_FONT);
    slider.setMajorTickSpacing(major);
    slider.setMinorTickSpacing(minor);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.addChangeListener(chgListener);
    return slider;
  }

  public static final String getShortDecimal(double number) {
    String shortDecimal;

    if (new Double(number).isNaN()) {
      shortDecimal = "NaN";
    } else {
      shortDecimal = SHORT_FORMAT.format(number);
    }

    return shortDecimal;
  }
}
