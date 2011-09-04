package org.vesselonline.neuralnetwork;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Network;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.training.Train;
import org.vesselonline.neuralnetwork.training.TrainingConfiguration;
import org.vesselonline.neuralnetwork.training.TrainingStatistics;
import org.vesselonline.neuralnetwork.util.VesselNeuralNetworkUtils;

public abstract class AbstractNeuralNetwork implements VesselNeuralNetwork {
  protected BasicNetwork network;
  protected Layer inputLayer, outputLayer;
  protected NeuralDataSet trainingDataSet;
  protected TrainingConfiguration trainingConfiguration;
  protected Train train;

  private List<TrainingStatistics> trainingStatistics = new ArrayList<TrainingStatistics>();

  public Network getNetwork() { return network; }
  public String getNetworkName() { return getNetwork().getName(); }
  public int getInputNodes() { return inputLayer.getNeuronCount(); }
  public int getOutputNodes() { return outputLayer.getNeuronCount(); }
  public NeuralDataSet getTrainingDataSet() { return trainingDataSet; }
  public List<TrainingStatistics> getTrainingStatistics() { return trainingStatistics; }

  public void train() {
    int iteration = 0;
    double errorChange = 0, lastError;
    long startTime = System.currentTimeMillis();

    train.setError(getTrainingConfiguration().getMinAcceptedError() + 1);

    while (iteration < getTrainingConfiguration().getMaxIterations() && train.getError() > getTrainingConfiguration().getMinAcceptedError() &&
           (errorChange > getTrainingConfiguration().getMinAcceptedErrorChange() || iteration < 5)) {
      iteration++;
      lastError = train.getError();
      train.iteration();
      errorChange = Math.abs(lastError - train.getError());

      if (iteration % getTrainingConfiguration().getStatusPrintRate() == 0) {
        System.out.println("Iteration #" + iteration + ";  % Error: " + train.getError() + ";  Error Change: " + errorChange);
      }

      if (iteration % getTrainingConfiguration().getStatisticSampleRate() == 0 || iteration == 1) {
        getTrainingStatistics().add(new TrainingStatistics(iteration, train.getError(), errorChange, System.currentTimeMillis() - startTime));
      }
    }

    train.finishTraining();
    getTrainingStatistics().add(new TrainingStatistics(iteration, train.getError(), errorChange, System.currentTimeMillis() - startTime));
    System.out.println("\n\nFinal Result:  Iteration #" + iteration + ";  % Error: " + train.getError() + ";  Error Change: " + errorChange);
  }

  public void interrogate(NeuralDataSet dataSet, String dataType) {
    NeuralData output;
    System.out.println("\nNeural Network Results (" + dataType + " with " + ((BasicNeuralDataSet) dataSet).getRecordCount() +" records)");
    for (NeuralDataPair pair : dataSet) {
      output = getNetwork().compute(pair.getInput());
      System.out.println(createResultString(pair, output));
    }
  }

  public void interrogate(NeuralDataSet dataSet, String filePath, String csvColHeaders) {
    PrintWriter outputStream = null;
    NeuralData output;

    try {
      outputStream = new PrintWriter(new FileWriter(filePath), true);
      outputStream.println(csvColHeaders);

      for (NeuralDataPair pair : dataSet) {
        output = getNetwork().compute(pair.getInput());
        outputStream.println(createCSVResultString(pair, output));
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }

  public TrainingConfiguration getTrainingConfiguration() { return trainingConfiguration; }

  public void writeTrainingStatistics(String filePath, String csvColHeaders) {
    PrintWriter outputStream = null;

    try {
      outputStream = new PrintWriter(new FileWriter(filePath), true);
      outputStream.println(csvColHeaders);

      for (TrainingStatistics stats : getTrainingStatistics()) {
        outputStream.println(stats.toString());
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }

  protected abstract void initTraining(NeuralDataSet trainingDataSet, TrainingConfiguration trainingConfiguration);

  protected String createResultString(NeuralDataPair pair, NeuralData output) {
    StringBuilder str = new StringBuilder("Input: " + VesselNeuralNetworkUtils.convertArrayToString(pair.getInput().getData()));
    str.append(";  Ideal: " + VesselNeuralNetworkUtils.convertArrayToString(pair.getIdeal().getData()));
    str.append(";  Actual: " + VesselNeuralNetworkUtils.convertArrayToString(output.getData()) + ";  Diff/Outlier: ");
    str.append(createDiffOutlierString(pair.getIdeal(), output));
    return str.toString();
  }

  protected String createCSVResultString(NeuralDataPair pair, NeuralData output) {
    StringBuilder str = new StringBuilder(VesselNeuralNetworkUtils.convertArrayToString(pair.getInput().getData()) + ",");
    str.append(VesselNeuralNetworkUtils.convertArrayToString(pair.getIdeal().getData()) + ",");
    str.append(VesselNeuralNetworkUtils.convertArrayToString(output.getData()) + ",");
    str.append(createDiffOutlierString(pair.getIdeal(), output));
    return str.toString();
  }

  private String createDiffOutlierString(NeuralData ideal, NeuralData output) {
    double diff = Math.abs(ideal.getData(ideal.getData().length - 1) - output.getData(output.getData().length - 1));
    String outlier = (diff > 0.5) ? "\"true\"" : "\"false\"";
    return diff + "," + outlier;
  }
}
