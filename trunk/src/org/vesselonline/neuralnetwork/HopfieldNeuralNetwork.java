package org.vesselonline.neuralnetwork;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.bipolar.BiPolarNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.Network;
import org.encog.neural.networks.logic.HopfieldLogic;
import org.encog.neural.pattern.HopfieldPattern;
import org.vesselonline.neuralnetwork.training.TrainingStatistics;
import org.vesselonline.neuralnetwork.util.VesselNeuralNetworkUtils;

public class HopfieldNeuralNetwork implements VesselNeuralNetwork {
  private BasicNetwork network;
  private HopfieldLogic hopfieldLogic;
  private NeuralDataSet trainingDataSet, validationDataSet;
  private int maxIterations;
  private boolean printDouble;

  private static final String NETWORK_NAME = "Hopfield Network";

  public HopfieldNeuralNetwork(int inputNodes, double[][] trainingPatterns, double[][] validationPatterns,
                               int maxIterations, boolean printDouble) {
    HopfieldPattern hopfieldPattern = new HopfieldPattern();
    hopfieldPattern.setInputNeurons(inputNodes);
    network = hopfieldPattern.generate();
    network.setName(NETWORK_NAME);
    hopfieldLogic = (HopfieldLogic) network.getLogic();

    trainingDataSet = createNeuralDataSet(trainingPatterns);
    validationDataSet = createNeuralDataSet(validationPatterns);

    this.maxIterations = maxIterations;
    this.printDouble = printDouble;
  }

  public Network getNetwork() { return network; }
  public String getNetworkName() { return getNetwork().getName(); }
  public int getInputNodes() { return hopfieldLogic.getNeuronCount(); }
  public int getHiddenNodes() { return 0; }
  public int getOutputNodes() { return getInputNodes(); }
  public NeuralDataSet getTrainingDataSet() { return trainingDataSet; }
  public NeuralDataSet getValidationDataSet() { return validationDataSet; }
  public List<TrainingStatistics> getTrainingStatistics() { return null; }

  public String getNetworkDescription() {
    String logicStr = network.getLogic().getClass().toString();
    logicStr = logicStr.substring(logicStr.lastIndexOf('.') + 1);

    return logicStr + " Network (" + getInputNodes() + " nodes)";
  }

  public void train() {
    Iterator<NeuralDataPair> iter = getTrainingDataSet().iterator();
    while (iter.hasNext()) {
      hopfieldLogic.addPattern(iter.next().getInput());
    }
  }

  public void interrogate(NeuralDataSet dataSet, String dataType) {
    Iterator<NeuralDataPair> iter = dataSet.iterator();
    BiPolarNeuralData inputPattern, outputPattern;
    int iterations, i = 1;

    while (iter.hasNext()) {
      inputPattern = (BiPolarNeuralData) iter.next().getInput();
      hopfieldLogic.setCurrentState(inputPattern);
      iterations = hopfieldLogic.runUntilStable(getMaxIterations());
      outputPattern = (BiPolarNeuralData) hopfieldLogic.getCurrentState();

      System.out.println("\n" + dataType + " Pattern #" + i + " (after " + iterations + " of " + getMaxIterations() +
                         " possible iterations)\n----------------------------------------------------------------");
      System.out.println(VesselNeuralNetworkUtils.createBipolarMappingString(inputPattern, outputPattern, printDouble));
      i++;
    }
  }

  // DO NOT USE!
  public void interrogate(NeuralDataSet dataSet, String filePath, String csvColHeaders) {
    PrintWriter outputStream = null;
//    NeuralData output;

    try {
      outputStream = new PrintWriter(new FileWriter(filePath), true);
      outputStream.println("Neural Network Results");
      outputStream.println(csvColHeaders);

//      for (NeuralDataPair pair : dataSet) {
//        output = network.compute(pair.getInput());
//        outputStream.println(createCSVResultString(pair, output));
//      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }

  public void printNetworkStatus() {
    System.out.println("\n" + getNetworkDescription());
  }

  public int getMaxIterations() { return maxIterations; }

  private NeuralDataSet createNeuralDataSet(double[][] patterns) {
    NeuralDataSet dataSet = new BasicNeuralDataSet();
    for (int i = 0; i < patterns.length; i++) {
      dataSet.add(VesselNeuralNetworkUtils.convertPatternToBipolar(patterns[i]));
    }
    return dataSet;
  }
}
