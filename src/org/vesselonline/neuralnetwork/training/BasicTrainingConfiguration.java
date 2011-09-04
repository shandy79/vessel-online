package org.vesselonline.neuralnetwork.training;

public abstract class BasicTrainingConfiguration implements TrainingConfiguration {
  private double learningRate, minAcceptedError, minAcceptedErrorChange;
  private int maxIterations, statisticSampleRate, statusPrintRate;

  public BasicTrainingConfiguration(double learningRate, int maxIterations, double minAcceptedError,
                                    double minAcceptedErrorChange, int statisticSampleRate, int statusPrintRate) {
    this.learningRate = learningRate;
    this.maxIterations = maxIterations;
    this.minAcceptedError = minAcceptedError;
    this.minAcceptedErrorChange = minAcceptedErrorChange;
    this.statisticSampleRate = statisticSampleRate;
    this.statusPrintRate = statusPrintRate;
  }

  public double getLearningRate() { return learningRate; }
  public int getMaxIterations() { return maxIterations; }
  public double getMinAcceptedError() { return minAcceptedError; }
  public double getMinAcceptedErrorChange() { return minAcceptedErrorChange; }
  public int getStatisticSampleRate() { return statisticSampleRate; }
  public int getStatusPrintRate() { return statusPrintRate; }
}
