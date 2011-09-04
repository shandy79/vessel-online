package org.vesselonline.neuralnetwork.util;

import org.encog.matrix.Matrix;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.util.randomize.RangeRandomizer;

public class VesselNeuralNetworkUtils {
  public static String convertArrayToString(double[] ary) {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < ary.length; i++) {
      str.append(ary[i] + ",");
    }
    str.setLength(str.length() - 1);
    return str.toString();
  }

  // Note that conversion from double to bipolar is as follows:
  // if (double > 0) then true, else false  (true = 1, false = -1)
  public static BiPolarNeuralData convertPatternToBipolar(double[] data) {
    BiPolarNeuralData neuralData = new BiPolarNeuralData(data.length);
    neuralData.setData(data);
    return neuralData;
  }

  public static double convertValueToNewRange(double value, double oldMin, double oldMax, double newMin, double newMax) {
    return newMin + (((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin));
  }

  public static String createBipolarMappingString(BiPolarNeuralData inputPattern, BiPolarNeuralData outputPattern, boolean printDouble) {
    StringBuilder line = new StringBuilder();
    for (int i = 0; i < inputPattern.size(); i++) {
      if (printDouble) {
        line.append(inputPattern.getData(i) + " ");
      } else {
        if (inputPattern.getBoolean(i)) line.append('X');
        else line.append('_');
      }
    }
    if (printDouble) line.setLength(line.length() - 1);

    line.append("  ->  ");

    for (int i = 0; i < outputPattern.size(); i++) {
      if (printDouble) {
        line.append(outputPattern.getData(i) + " ");
      } else {
        if (outputPattern.getBoolean(i)) line.append('X');
        else line.append('_');
      }
    }
    if (printDouble) line.setLength(line.length() - 1);

    return line.toString();
  }

  public static Layer createLayer(ActivationFunction activationFn, int nodes, String name) {
    Layer layer = new BasicLayer(activationFn, false, nodes);
    layer.setName(name);
    return layer;
  }

  public static Layer createLayer(ActivationFunction activationFn, int nodes, String name, ThresholdConfiguration thresholdConfig) {
    if (thresholdConfig == null) {
      return createLayer(activationFn, nodes, name);
    }

    Layer layer = new BasicLayer(activationFn, true, nodes);
    layer.setName(name);

    if (thresholdConfig.isRandom()) {
      layer.setThreshold(createRandomDoubleArray(nodes, thresholdConfig.getMinThreshold(), thresholdConfig.getMaxThreshold()));
    } else {
      layer.setThreshold(thresholdConfig.getThresholds());
    }

    return layer;
  }

  public static Synapse createWeightedSynapse(Synapse synapse, WeightConfiguration weightConfig) {
    if (weightConfig == null) {
      weightConfig = new WeightConfiguration();
    }

    if (weightConfig.isRandom()) {
      synapse.setMatrix(new Matrix(createRandomDoubleArray2D(synapse.getFromNeuronCount(), synapse.getToNeuronCount(),
                                                             weightConfig.getMinWeight(), weightConfig.getMaxWeight())));
    } else {
      synapse.setMatrix(new Matrix(weightConfig.getWeights()));
    }

    return synapse;
  }

  public static void printThresholdStatus(Layer lyr) {
    if (! lyr.hasThreshold()) { return; }

    System.out.println("\n" + lyr.getName() + " Threshold Status");
    double[] thresholds = lyr.getThreshold();
    for (int i = 0; i < thresholds.length; i++) {
      System.out.println("t[" + i + "]: " + thresholds[i]);
    }
  }

  public static void printWeightStatus(Synapse syn) {
    System.out.println("\n" + syn.getFromLayer().getName() + "-" + syn.getToLayer().getName() + " Weight Status");
    double[][] weights = syn.getMatrix().getData();
    for (int i = 0; i < weights.length; i++) {
      for (int j = 0; j < weights[i].length; j++) {
        System.out.println("w[" + i + "][" + j + "]: " + weights[i][j]);
      }
    }
  }

  private static double[] createRandomDoubleArray(int length, double min, double max) {
    double[] values = new double[length];

    // If min > max, flip the values
    if (min > max) {
      double tmp = min;
      min = max;
      max = tmp;
    }

    // Set random values based on a scaling of the 0-1 result to fit within the specified range
    // (range * Math.random()) + min
    (new RangeRandomizer(min, max)).randomize(values);

    return values;
  }

  private static double[][] createRandomDoubleArray2D(int rows, int columns, double min, double max) {
    double[][] values = new double[rows][columns];

    // If min > max, flip the values
    if (min > max) {
      double tmp = min;
      min = max;
      max = tmp;
    }

    // Set random values based on a scaling of the 0-1 result to fit within the specified range
    // (range * Math.random()) + min
    (new RangeRandomizer(min, max)).randomize(values);

    return values;
  }
}
