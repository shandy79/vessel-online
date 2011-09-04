package org.vesselonline.jai.overlay;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Arrays;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;

public class JAISourceImage implements SourceImage {
  private PlanarImage sourceImage, workImage, pipelineImage;
  private String sourceImagePath, color;
  private int sourceWidth, sourceHeight, scaleWidth, scaleHeight;
  private byte[][] lookupTable;
  private int brightness, convolve, rotate;
  private float contrast, scale, lightness;
  private boolean inverted;
  private double[] thresholdLow, thresholdHigh, thresholdMap;

  private static final int CONVOLVE_MODE = 3;
  private static final int CONVOLVE_INIT = -1;

  private static final float CONTRAST_VALUE = 1.2F;
  private static final float CONTRAST_INIT = 1.0F;

  private static final String COLOR_INIT = "normal";

  private static final int ROTATE_INC = 1;
  private static final int ROTATE_INIT = 0;
  private static final int ROTATE_MIN = -180;
  private static final int ROTATE_MAX = 180;

  private static final int BRIGHTNESS_INC = 5;
  private static final int BRIGHTNESS_INIT = 0;
  private static final int BRIGHTNESS_MIN = -255;
  private static final int BRIGHTNESS_MAX = 255;

  private static final float SCALE_INIT = 1.0F;
  private static final float SCALE_MIN = 0.1F;
  private static final float SCALE_MAX = 5.0F;

  private static final float LIGHTNESS_INIT = 1.0F;
  private static final float LIGHTNESS_MIN = 0.0F;
  private static final float LIGHTNESS_MAX = 1.0F;

  private static final double THRESHOLD_INIT = 0;
  private static final double THRESHOLD_MIN = 0;
  private static final double THRESHOLD_MAX = 256;


  public JAISourceImage(String sourceImagePath) {
    this(JAI.create("fileload", sourceImagePath));
    this.sourceImagePath = sourceImagePath;
  }

  public JAISourceImage() {
    this(PlanarImage.wrapRenderedImage(ImageOverlayUtilities.createDefaultImage()));
    sourceImagePath = ImageOverlayUtilities.NO_IMG_PATH;
  }

  public JAISourceImage(PlanarImage sourceImage) {
    this.sourceImage = sourceImage;

    // For handling proper color display and operation with GIF images.  See
    // http://java.sun.com/products/java-media/jai/forDevelopers/jaifaq.html#gifoperations and
    // http://java.sun.com/products/java-media/jai/forDevelopers/jaifaq.html#palette
    // for more information.  Code for this operation was taken from
    // http://java.sun.com/products/java-media/jai/forDevelopers/samples/PaletteToRGB.java
    if (sourceImage.getColorModel() instanceof IndexColorModel) {
      IndexColorModel icm = (IndexColorModel) sourceImage.getColorModel();
      byte[][] data = new byte[3][icm.getMapSize()];
      icm.getReds(data[0]);
      icm.getGreens(data[1]);
      icm.getBlues(data[2]);
      LookupTableJAI lut = new LookupTableJAI(data);
      this.sourceImage = JAI.create("lookup", sourceImage, lut);
    }

    sourceWidth = sourceImage.getWidth();
    sourceHeight = sourceImage.getHeight();

    // Initialize image settings
    lookupTable = new byte[3][256];
    thresholdLow = new double[1];
    thresholdHigh = new double[1];
    thresholdMap = new double[1];
    thresholdLow[0] = 0.0F;
    thresholdMap[0] = 0.0F;

    resetImage();
  }


  public PlanarImage doPipeline(String initPhase) {
    PlanarImage pipeImg = workImage.createSnapshot();

    if ("long".equals(initPhase)) {
      if (! getColor().equals(COLOR_INIT)) {
        pipeImg = applyInvert(sourceImage);
      } else {
        pipeImg = getSourceImage().createSnapshot();
      }
      pipeImg = applyLookupTable(pipeImg);

      if (isConvolve()) pipeImg = applyConvolution(pipeImg);
      if (isContrast()) pipeImg = applyContrast(pipeImg);
      if (isInverted()) pipeImg = applyInvert(pipeImg);
      if (rotate != ROTATE_INIT) { pipeImg = applyRotate(pipeImg); }
    }

    workImage = pipeImg.createSnapshot();
    pipeImg = applyThreshold(pipeImg);
    pipeImg = applyLightness(pipeImg);
    pipeImg = applyScale(pipeImg);

    pipelineImage = pipeImg;
    return pipeImg;
  }


  public void resetImage() {
    workImage = getSourceImage().createSnapshot();
    pipelineImage = getSourceImage().createSnapshot();

    setConvolve(false);
    setContrast(false);
    setInverted(false);

    setColor(COLOR_INIT);

    brightness = BRIGHTNESS_INIT;
    rotate = ROTATE_INIT;

    setScale(SCALE_INIT);
    setLightness(LIGHTNESS_INIT);
    setThreshold(THRESHOLD_INIT);
  }


  private PlanarImage applyLookupTable(PlanarImage inputImg) {
    int red, green, blue;
    byte[][] modLookupTbl = new byte[3][256];

    for (int i = 0; i < 256; i++) {
      red = (int) lookupTable[0][i] & 0xFF;
      green = (int) lookupTable[1][i] & 0xFF;
      blue = (int) lookupTable[2][i] & 0xFF;
      modLookupTbl[0][i] = clamp(red + brightness);
      modLookupTbl[1][i] = clamp(green + brightness);
      modLookupTbl[2][i] = clamp(blue + brightness);
    }

    LookupTableJAI lookup = new LookupTableJAI(modLookupTbl);
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(inputImg);
    pb.add(lookup);

    return JAI.create("lookup", pb, null);
  }


  private PlanarImage applyConvolution(PlanarImage inputImg) {
    KernelJAI kernel = loadKernel(convolve);
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(inputImg);
    pb.add(kernel);
    return JAI.create("convolve", pb, null);
  }


  private PlanarImage applyContrast(PlanarImage inputImg) {
    int average = 127;
    float modContrast = (contrast + 10) / 10.0F;
    byte[][] contrastLookupTbl = new byte[3][256];

    for (int i = 0; i < 256; i++) {
      contrastLookupTbl[0][i] = clamp((average + (int) ((i - average) * modContrast)) + i);
      contrastLookupTbl[1][i] = clamp((average + (int) ((i - average) * modContrast)) + i);
      contrastLookupTbl[2][i] = clamp((average + (int) ((i - average) * modContrast)) + i);
    }

    LookupTableJAI lookup = new LookupTableJAI(contrastLookupTbl);
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(inputImg);
    pb.add(lookup);

    return JAI.create("lookup", pb, null);
  }


  private PlanarImage applyInvert(PlanarImage inputImg) {
    byte[][] invertLookupTbl = new byte[3][256];

    for (int i = 0; i < 256; i++) {
      invertLookupTbl[0][i] = (byte) (255 - i);
      invertLookupTbl[1][i] = (byte) (255 - i);
      invertLookupTbl[2][i] = (byte) (255 - i);
    }

    LookupTableJAI lookup = new LookupTableJAI(invertLookupTbl);
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(inputImg);
    pb.add(lookup);

    return JAI.create("lookup", pb, null);
  }


  private PlanarImage applyRotate(PlanarImage inputImg) {
    float angle = (float) (getRotate() * Math.PI / 180.0F);

    ParameterBlock pb = new ParameterBlock();
    pb.addSource(inputImg);
    pb.add(getWidth() / 2.0F);  // X origin
    pb.add(getHeight() / 2.0F);  // Y origin
    pb.add(angle);  // rotation angle (in radians)
    pb.add(new InterpolationBilinear());  // interpolation
 
    return JAI.create("Rotate", pb, null);
  }


  private PlanarImage applyScale(PlanarImage inputImg) {
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(inputImg);
    pb.add(getScale());  // X scale
    pb.add(getScale());  // Y scale
    pb.add(0.0F);  // X translation
    pb.add(0.0F);  // Y translation
    pb.add(new InterpolationBilinear());  // interpolation

    return JAI.create("scale", pb, null);
  }


  private PlanarImage applyLightness(PlanarImage inputImg) {
    double[] constants = new double[3];
    Arrays.fill(constants, getLightness());

    ColorModel colorModel = inputImg.getColorModel();
    WritableRaster raster = inputImg.getAsBufferedImage().getRaster();
    BufferedImage bi = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);

    ParameterBlock pb = new ParameterBlock();
    pb.addSource(bi);
    pb.add(constants);

    return JAI.create("multiplyconst", pb, null);
  }


  private PlanarImage applyThreshold(PlanarImage inputImg) {
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(inputImg);
    pb.add(thresholdLow);
    pb.add(thresholdHigh);
    pb.add(thresholdMap);

    return JAI.create("threshold", pb, null);
  }


  public PlanarImage getSourceImage() { return sourceImage; }

  public String getSourceImagePath() { return sourceImagePath; }

  public PlanarImage getPipelineImage() { return pipelineImage; }

  public int getWidth() { return scaleWidth; }

  public int getHeight() { return scaleHeight; }


  public boolean isConvolve() { return (convolve == CONVOLVE_INIT) ? false : true; }

  public void setConvolve(boolean convolve) {
    if (convolve) {
      this.convolve = CONVOLVE_MODE;
    } else {
      this.convolve = CONVOLVE_INIT;
    }
  }


  public boolean isContrast() { return (contrast == CONTRAST_INIT) ? false : true; }

  public void setContrast(boolean contrast) {
    if (contrast) {
      this.contrast = CONTRAST_VALUE;
    } else {
      this.contrast = CONTRAST_INIT;
    }
  }


  public boolean isInverted() { return inverted; }

  public void setInverted(boolean inverted) { this.inverted = inverted; }


  public String getColor() { return color; }

  public void setColor(String color) {
    this.color = color;

    if (color.equals("red")) {
      for (int i = 0; i < 256; i++) {
        lookupTable[0][i] = (byte) i;
        lookupTable[1][i] = (byte) 0;
        lookupTable[2][i] = (byte) 0;
      }

    } else if (color.equals("green")) {
      for (int i = 0; i < 256; i++) { 
        lookupTable[0][i] = (byte) 0;
        lookupTable[1][i] = (byte) i;
        lookupTable[2][i] = (byte) 0;
      }

    } else if (color.equals("blue")) {
      for (int i = 0; i < 256; i++) {
        lookupTable[0][i] = (byte) 0;
        lookupTable[1][i] = (byte) 0;
        lookupTable[2][i] = (byte) i;
      }

    } else {
      this.color = COLOR_INIT;

      for (int i = 0; i < 256; i++) {
        lookupTable[0][i] = (byte) i;
        lookupTable[1][i] = (byte) i;
        lookupTable[2][i] = (byte) i;
      }
    }
  }


  public int getRotate() { return rotate; }

  public void setRotate(int rotate) {
    this.rotate = rotate;
    if (this.rotate < ROTATE_MIN) this.rotate = ROTATE_MIN;
    if (this.rotate > ROTATE_MAX) this.rotate = ROTATE_MAX;
  }

  public void decRotate() { setRotate(rotate - ROTATE_INC); }

  public void incRotate() { setRotate(rotate + ROTATE_INC); }


  public int getBrightness() { return brightness; }

  public void setBrightness(int brightness) {
    this.brightness = brightness;
    if (this.brightness < BRIGHTNESS_MIN) this.brightness = BRIGHTNESS_MIN;
    if (this.brightness > BRIGHTNESS_MAX) this.brightness = BRIGHTNESS_MAX;
  }

  public void decBrightness() { setBrightness(brightness - BRIGHTNESS_INC); }

  public void incBrightness() { setBrightness(brightness + BRIGHTNESS_INC); }


  public float getScale() { return scale; }

  public void setScale(float scale) {
    if (scale < SCALE_MIN) {
      this.scale = SCALE_MIN;
    } else if (scale > SCALE_MAX) {
      this.scale = SCALE_MAX;
    } else {
      this.scale = scale;
    }

    scaleWidth = (int) (sourceWidth * this.scale);
    scaleHeight = (int) (sourceHeight * this.scale);
  }


  public float getLightness() { return lightness; }

  public void setLightness(float lightness) {
    if (lightness < LIGHTNESS_MIN) {
      this.lightness = LIGHTNESS_MIN;
    } else if (lightness > LIGHTNESS_MAX) {
      this.lightness = LIGHTNESS_MAX;
    } else {
      this.lightness = lightness;
    }
  }


  public double getThreshold() { return thresholdHigh[0]; }

  public void setThreshold(double threshold) {
    if (threshold < THRESHOLD_MIN) {
      thresholdHigh[0] = THRESHOLD_MIN;
    } else if (threshold > THRESHOLD_MAX) {
      thresholdHigh[0] = THRESHOLD_MAX;
    } else {
      thresholdHigh[0] = threshold;
    }
  }


  private byte clamp(int v) {
    if (v > 255) {
      return (byte) 255;
    } else if (v < 0) {
      return (byte) 0;
    } else {
      return (byte) v;
    }
  }


  private KernelJAI loadKernel(int choice) {
    float[] data = new float[9];

    switch (choice) {
      case 0:
        data[0] =  0.0F; data[1] = -1.0F; data[2] =  0.0F;
        data[3] = -1.0F; data[4] =  5.0F; data[5] = -1.0F;
        data[6] =  0.0F; data[7] = -1.0F; data[8] =  0.0F;
        break;

      case 1:
        data[0] = -1.0F; data[1] = -1.0F; data[2] = -1.0F;
        data[3] = -1.0F; data[4] =  9.0F; data[5] = -1.0F;
        data[6] = -1.0F; data[7] = -1.0F; data[8] = -1.0F;
        break;

      case 2:
        data[0] =  1.0F; data[1] = -2.0F; data[2] =  1.0F;
        data[3] = -2.0F; data[4] =  5.0F; data[5] = -2.0F;
        data[6] =  1.0F; data[7] = -2.0F; data[8] =  1.0F;
        break;

      case 3:
        data[0] = -1.0F; data[1] = 1.0F; data[2] = -1.0F;
        data[3] =  1.0F; data[4] = 1.0F; data[5] =  1.0F;
        data[6] = -1.0F; data[7] = 1.0F; data[8] = -1.0F;
        break;

      case -1:
        data[0] = 0.0F; data[1] = 0.0F; data[2] = 0.0F;
        data[3] = 0.0F; data[4] = 1.0F; data[5] = 0.0F;
        data[6] = 0.0F; data[7] = 0.0F; data[8] = 0.0F;
        break;
    }

    return new KernelJAI(3, 3, data);
  }
}
