package org.vesselonline.neuralnetwork.run;

import org.encog.util.logging.Logging;
import org.vesselonline.neuralnetwork.FeedforwardNeuralNetwork;
import org.vesselonline.neuralnetwork.VesselNeuralNetwork;
import org.vesselonline.neuralnetwork.training.FeedforwardTrainingConfiguration;
import org.vesselonline.neuralnetwork.training.TrainingConfiguration;
import org.vesselonline.neuralnetwork.util.ThresholdConfiguration;
import org.vesselonline.neuralnetwork.util.WeightConfiguration;

public class GenericFeedforwardRunner {
  private static final int INPUT_NODES = 2;
  private static final int HIDDEN_NODES = 2;
  private static final int OUTPUT_NODES = 1;

  private static final boolean HAS_THRESHOLD = true;
  private static final boolean RANDOMIZE_THRESHOLDS = false;
  private static final double THRESHOLD_MIN = -1.0;
  private static final double THRESHOLD_MAX = 2.0;
  // Threshold dimension is [# nodes in layer] for BasicLayer
  private static final double[] THRESHOLD_HIDDEN = { 0.3, 0.1 };
  private static final double[] THRESHOLD_OUTPUT = { 0.9 };

  private static final boolean RANDOMIZE_WEIGHTS = false;
  private static final double WEIGHT_MIN = 0.0;
  private static final double WEIGHT_MAX = 1.0;
  // Weight Matrix dimensions are [# input nodes to synapse][# output nodes from synapse] for WeightedSynapse
  private static final double[][] WEIGHT_INPUTHIDDEN = { { 0.5, 0.4 }, { 0.8, 0.2 } };
  private static final double[][] WEIGHT_HIDDENOUTPUT = { { 0.4 }, { 0.2 } };

  private static final double[][] INPUT_VALUES = { { 0.34, 0.78 } };
  private static final double[][] DESIRED_OUTPUTS = { { 0.89 } };

  private static final int TRAINING_METHOD = TrainingConfiguration.BACKPROPAGATION;
  private static final double MOMENTUM = 1.0;
  private static final double LEARNING_RATE = 0.25;
  private static final int MAX_ITERATIONS = 1;
  private static final double MIN_ACCEPTED_ERROR = 0.01;
  private static final double MIN_ACCEPTED_ERROR_CHANGE = 0.0;
  private static final int STATISTIC_SAMPLE_RATE = 1;
  private static final int STATUS_PRINT_RATE = 10;
  
  public static void main(String[] args) {
    Logging.stopConsoleLogging();

    FeedforwardTrainingConfiguration trainConfig = new FeedforwardTrainingConfiguration(TRAINING_METHOD, MOMENTUM, LEARNING_RATE, MAX_ITERATIONS,
                                                                                        MIN_ACCEPTED_ERROR, MIN_ACCEPTED_ERROR_CHANGE,
                                                                                        STATISTIC_SAMPLE_RATE, STATUS_PRINT_RATE);

    FeedforwardNeuralNetwork ff = new FeedforwardNeuralNetwork(INPUT_NODES, HIDDEN_NODES, OUTPUT_NODES,
        createDefaultThresholdConfiguration(THRESHOLD_HIDDEN), createDefaultThresholdConfiguration(THRESHOLD_OUTPUT),
        createDefaultWeightConfiguration(WEIGHT_INPUTHIDDEN), createDefaultWeightConfiguration(WEIGHT_HIDDENOUTPUT),
        INPUT_VALUES, DESIRED_OUTPUTS, trainConfig);

    ff.train();
    ff.interrogate(ff.getTrainingDataSet(), VesselNeuralNetwork.TRAINING);
    ff.printNetworkStatus();
  }

  private static ThresholdConfiguration createDefaultThresholdConfiguration(double[] thresholds) {
    if (! HAS_THRESHOLD) {
      return null;
    } else if (RANDOMIZE_THRESHOLDS) {
      return new ThresholdConfiguration(THRESHOLD_MIN, THRESHOLD_MAX);
    } else {
      return new ThresholdConfiguration(thresholds);
    }
  }

  private static WeightConfiguration createDefaultWeightConfiguration(double[][] weights) {
    if (RANDOMIZE_WEIGHTS) {
      return new WeightConfiguration(WEIGHT_MIN, WEIGHT_MAX);
    } else {
      return new WeightConfiguration(weights);
    }
  }
}
