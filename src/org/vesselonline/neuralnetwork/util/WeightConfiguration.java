package org.vesselonline.neuralnetwork.util;

public class WeightConfiguration {
  // Weight Matrix dimensions are [# input nodes to synapse][# output nodes from synapse] for WeightedSynapse
  private double[][] weights;
  private double minWeight, maxWeight;
  private boolean random;

  private static final double DEFAULT_MIN = 0.0;
  private static final double DEFAULT_MAX = 1.0;

  // Randomize w/default 0-1 range
  public WeightConfiguration() {
    this(DEFAULT_MIN, DEFAULT_MAX);
  }

  // Randomize w/user-specified min and max range
  public WeightConfiguration(double minWeight, double maxWeight) {
    this.minWeight = minWeight;
    this.maxWeight = maxWeight;
    this.random = true;
  }

  // Assign weight values directly
  public WeightConfiguration(double[][] weights) {
    this.minWeight = Double.MIN_VALUE;
    this.maxWeight = Double.MAX_VALUE;
    this.random = false;
    this.weights = weights;
  }

  public double[][] getWeights() { return weights; }
  public double getMinWeight() { return minWeight; }
  public double getMaxWeight() { return maxWeight; }
  public boolean isRandom() { return random; }
}
