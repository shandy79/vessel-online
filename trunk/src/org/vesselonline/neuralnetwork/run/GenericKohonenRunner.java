package org.vesselonline.neuralnetwork.run;

import org.encog.util.logging.Logging;
import org.vesselonline.neuralnetwork.KohonenNeuralNetwork;
import org.vesselonline.neuralnetwork.VesselNeuralNetwork;
import org.vesselonline.neuralnetwork.training.KohonenTrainingConfiguration;
import org.vesselonline.neuralnetwork.training.TrainingConfiguration;
import org.vesselonline.neuralnetwork.util.WeightConfiguration;

public class GenericKohonenRunner {
  private static final int INPUT_NODES = 4;
  private static final int OUTPUT_NODES = 2;

  private static final boolean RANDOMIZE_WEIGHTS = true;
  private static final double WEIGHT_MIN = -0.5;
  private static final double WEIGHT_MAX = 0.5;
  // Weight Matrix dimensions are [# input nodes to synapse][# output nodes from synapse] for WeightedSynapse
  private static final double[][] WEIGHT_INPUTOUTPUT = { { 0.1, 0.2, 0.3, 0.2 }, { 0.1, 0.3, 0.2, 0.3 } };

  private static final int NEIGHBORHOOD_FUNCTION = TrainingConfiguration.SINGLE;
  private static final int NEIGHBORHOOD_RADIUS = 1;
  private static final double LEARNING_RATE = 0.7;
  private static final int MAX_ITERATIONS = 10;
  private static final double MIN_ACCEPTED_ERROR = 0.01;
  private static final double MIN_ACCEPTED_ERROR_CHANGE = 0.0;
  private static final int STATISTIC_SAMPLE_RATE = 0;
  private static final int STATUS_PRINT_RATE = 2;

  private static double INPUT_VALUES[][] = { { -1.0, -1.0, 1.0, 1.0 }, { 1.0, 1.0, -1.0, -1.0 } };
  
  public static void main(String[] args) {
    Logging.stopConsoleLogging();

    KohonenTrainingConfiguration trainConfig = new KohonenTrainingConfiguration(NEIGHBORHOOD_FUNCTION, NEIGHBORHOOD_RADIUS, LEARNING_RATE,
                                                                                MAX_ITERATIONS, MIN_ACCEPTED_ERROR, MIN_ACCEPTED_ERROR_CHANGE,
                                                                                STATISTIC_SAMPLE_RATE, STATUS_PRINT_RATE);

    KohonenNeuralNetwork k = new KohonenNeuralNetwork(INPUT_NODES, OUTPUT_NODES, createDefaultWeightConfiguration(WEIGHT_INPUTOUTPUT),
                                                      INPUT_VALUES, trainConfig);

    k.train();
    k.interrogate(k.getTrainingDataSet(), VesselNeuralNetwork.TRAINING);
    k.printNetworkStatus();
  }

  private static WeightConfiguration createDefaultWeightConfiguration(double[][] weights) {
    if (RANDOMIZE_WEIGHTS) {
      return new WeightConfiguration(WEIGHT_MIN, WEIGHT_MAX);
    } else {
      return new WeightConfiguration(weights);
    }
  }
}
