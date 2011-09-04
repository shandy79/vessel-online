package org.vesselonline.jai.overlay;

import javax.media.jai.PlanarImage;

public interface SourceImage {
  PlanarImage getSourceImage();
  String getSourceImagePath();
  PlanarImage getPipelineImage();

  PlanarImage doPipeline(String initPhase);
  void resetImage();

  int getWidth();
  int getHeight();

  boolean isConvolve();
  void setConvolve(boolean convolve);

  boolean isContrast();
  void setContrast(boolean contrast);

  boolean isInverted();
  void setInverted(boolean inverted);

  String getColor();
  void setColor(String color);

  int getRotate();
  void setRotate(int rotate);
  void decRotate();
  void incRotate();

  int getBrightness();
  void setBrightness(int brightness);
  void decBrightness();
  void incBrightness();

  float getScale();
  void setScale(float scale);

  float getLightness();
  void setLightness(float lightness);

  double getThreshold();
  void setThreshold(double threshold);
}
