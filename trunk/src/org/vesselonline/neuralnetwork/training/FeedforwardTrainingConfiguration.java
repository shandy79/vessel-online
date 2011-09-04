package org.vesselonline.neuralnetwork.training;

public class FeedforwardTrainingConfiguration extends BasicTrainingConfiguration {
  private int trainingMethod;
  private double momentum;
  
  public FeedforwardTrainingConfiguration(int trainingMethod, double momentum, double learningRate, int maxIterations,
                                          double minAcceptedError, double minAcceptedErrorChange, int statisticSampleRate, int statusPrintRate) {
    super(learningRate, maxIterations, minAcceptedError, minAcceptedErrorChange, statisticSampleRate, statusPrintRate);

    this.trainingMethod = trainingMethod;
    this.momentum = momentum;
  }

  public int getTrainingMethod() { return trainingMethod; }
  public double getMomentum() { return momentum; }
}
