package org.vesselonline.neuralnetwork.training;

public class KohonenTrainingConfiguration extends BasicTrainingConfiguration {
  private int neighborhoodFunction, neighborhoodRadius;
  
  public KohonenTrainingConfiguration(int neighborhoodFunction, int neighborhoodRadius, double learningRate, int maxIterations,
                                      double minAcceptedError, double minAcceptedErrorChange, int statisticSampleRate, int statusPrintRate) {
    super(learningRate, maxIterations, minAcceptedError, minAcceptedErrorChange, statisticSampleRate, statusPrintRate);

    this.neighborhoodFunction = neighborhoodFunction;
    this.neighborhoodRadius = neighborhoodRadius;
  }

  public int getNeighborhoodFunction() { return neighborhoodFunction; }
  public int getNeighborhoodRadius() { return neighborhoodRadius; }
}
