package org.vesselonline.neuralnetwork.run;

import org.encog.util.logging.Logging;
import org.vesselonline.neuralnetwork.HopfieldNeuralNetwork;
import org.vesselonline.neuralnetwork.VesselNeuralNetwork;

public class GenericHopfieldRunner {
  private static final int INPUT_NODES = 9;
  private static final int MAX_ITERATIONS = 100;
  private static final boolean PRINT_DOUBLE = false;

  private static final double[][] TRAINING_PATTERNS = {
    { 1.4, 2.0, -1.0, 3.0, -1.0, 1.0, -1.0, 3.0, 0.2 },
    { -1.0, 1.0, -1.0, 3.0, 1.0, 0.0, 3.0, -2.0, -0.2 },
    { 1.0, 0.0, 3.0, -2.0, 2.0, 1.0, 2.0, -1.0, 1.3 },
    { 2.0, 1.0, 2.0, -1.0, 1.4, 2.0, -1.0, 3.0, -2.3 }
  };
  private static final double[][] VALIDATION_PATTERNS = {
    { 1.0, -2.0, 0.0, 2.9, -1.0, 0.8, -0.9, 2.4, 0.6 },
    { -1.0, 0.8, -0.9, 2.4, 0.8, -0.1, 3.0, -2.0, 1.4 },
    { 0.8, 0.1, 3.0, -2.0, -2.0, 0.8, 2.0, -0.9, 3.1 },
    { 2.0, 0.8, 2.0, -0.9, 1.0, 2.0, 0.2, 2.9, 1.7 }
  };
  
  public static void main(String[] args) {
    Logging.stopConsoleLogging();

    HopfieldNeuralNetwork h = new HopfieldNeuralNetwork(INPUT_NODES, TRAINING_PATTERNS, VALIDATION_PATTERNS, MAX_ITERATIONS, PRINT_DOUBLE);
    h.printNetworkStatus();
    h.train();
    h.interrogate(h.getTrainingDataSet(), VesselNeuralNetwork.TRAINING);
    h.interrogate(h.getValidationDataSet(), VesselNeuralNetwork.VALIDATION);
  }
}
