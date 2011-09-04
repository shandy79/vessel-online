package org.vesselonline.neuralnetwork.util;

public class ThresholdConfiguration {
  // Threshold dimension is [# nodes in layer] for BasicLayer
  private double[] thresholds;
  private double minThreshold, maxThreshold;
  private boolean random;

  private static final double DEFAULT_MIN = 0.0;
  private static final double DEFAULT_MAX = 1.0;

  // Randomize w/default 0-1 range
  public ThresholdConfiguration() {
    this(DEFAULT_MIN, DEFAULT_MAX);
  }

  // Randomize w/user-specified min and max range
  public ThresholdConfiguration(double minThreshold, double maxThreshold) {
    this.minThreshold = minThreshold;
    this.maxThreshold = maxThreshold;
    this.random = true;
  }

  // Assign threshold values directly
  public ThresholdConfiguration(double[] thresholds) {
    this.minThreshold = Double.MIN_VALUE;
    this.maxThreshold = Double.MAX_VALUE;
    this.random = false;
    this.thresholds = thresholds;
  }

  public double[] getThresholds() { return thresholds; }
  public double getMinThreshold() { return minThreshold; }
  public double getMaxThreshold() { return maxThreshold; }
  public boolean isRandom() { return random; }
}
