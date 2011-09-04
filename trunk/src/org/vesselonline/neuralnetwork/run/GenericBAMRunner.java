package org.vesselonline.neuralnetwork.run;

import org.encog.util.logging.Logging;
import org.vesselonline.neuralnetwork.BAMNeuralNetwork;
import org.vesselonline.neuralnetwork.VesselNeuralNetwork;

public class GenericBAMRunner {
  private static final int INPUT_NODES = 9;
  private static final int OUTPUT_NODES = 5;
  private static final boolean PRINT_DOUBLE = false;

  private static final double[][] TRAINING_INPUTS = {
    { 1.4, 2.0, -1.0, 3.0, -1.0, 1.0, -1.0, 3.0, 0.2 },
    { -1.0, 1.0, -1.0, 3.0, 1.0, 0.0, 3.0, -2.0, -0.2 },
    { 1.0, 0.0, 3.0, -2.0, 2.0, 1.0, 2.0, -1.0, 1.3 },
    { 2.0, 1.0, 2.0, -1.0, 1.4, 2.0, -1.0, 3.0, -2.3 }
  };
  private static final double[][] DESIRED_OUTPUTS = {
    { 3.7, 4.1, -1.4, -3.7, -1.0 },
    { -1.6, -1.0, 1.0, 3.0, -1.0 },
    { -1.0, 10.0, -3.0, 2.0, -2.0 },
    { 2.0, 1.0, -2.0, 1.0, 1.4 }
  };
  private static final double[][] VALIDATION_INPUTS = {
    { 1.0, -2.0, 0.0, 2.9, -1.0, 0.8, -0.9, 2.4, 0.6 },
    { -1.0, 0.8, -0.9, 2.4, 0.8, -0.1, 3.0, -2.0, 1.4 },
    { 0.8, 0.1, 3.0, -2.0, -2.0, 0.8, 2.0, -0.9, 3.1 },
    { 2.0, 0.8, 2.0, -0.9, 1.0, 2.0, 0.2, 2.9, 1.7 }
  };
  
  public static void main(String[] args) {
    Logging.stopConsoleLogging();

    BAMNeuralNetwork bam = new BAMNeuralNetwork(INPUT_NODES, OUTPUT_NODES, TRAINING_INPUTS, DESIRED_OUTPUTS, VALIDATION_INPUTS, PRINT_DOUBLE);
    bam.printNetworkStatus();
    bam.train();
    bam.interrogate(bam.getTrainingDataSet(), VesselNeuralNetwork.TRAINING);
    bam.interrogate(bam.getDesiredData(), VesselNeuralNetwork.DESIRED, bam.getInputNodes());
    bam.interrogate(bam.getValidationDataSet(), VesselNeuralNetwork.VALIDATION);
  }
}
