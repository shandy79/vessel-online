package org.vesselonline.neuralnetwork.training;

public interface TrainingConfiguration {
  int BACKPROPAGATION = 0;
  int MANHATTAN = 1;
  int RESILIENT = 2;

  int SINGLE = 0;
  int BUBBLE = 1;

  double getLearningRate();
  int getMaxIterations();
  double getMinAcceptedError();
  double getMinAcceptedErrorChange();
  int getStatisticSampleRate();
  int getStatusPrintRate();
}
