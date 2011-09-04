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
import org.encog.neural.networks.NeuralDataMapping;
import org.encog.neural.networks.logic.BAMLogic;
import org.encog.neural.pattern.BAMPattern;
import org.vesselonline.neuralnetwork.training.TrainingStatistics;
import org.vesselonline.neuralnetwork.util.VesselNeuralNetworkUtils;

public class BAMNeuralNetwork implements VesselNeuralNetwork {
  private BasicNetwork network;
  private BAMLogic bamLogic;
  private NeuralDataSet trainingDataSet, validationDataSet, desiredDataSet;
  private boolean printDouble;

  private static final String NETWORK_NAME = "Bidirectional Associative Memory Network";

  public BAMNeuralNetwork(int inputNodes, int outputNodes, double[][] trainingInputs, double[][] desiredOutputs,
                          double[][] validationInputs, boolean printDouble) {
    BAMPattern bamPattern = new BAMPattern();
    bamPattern.setF1Neurons(inputNodes);
    bamPattern.setF2Neurons(outputNodes);
    network = bamPattern.generate();
    network.setName(NETWORK_NAME);
    bamLogic = (BAMLogic) network.getLogic();

    trainingDataSet = createNeuralDataSet(trainingInputs, desiredOutputs);
    desiredDataSet = createNeuralDataSet(desiredOutputs, null);
    validationDataSet = createNeuralDataSet(validationInputs, null);

    this.printDouble = printDouble;
  }

  public Network getNetwork() { return network; }
  public String getNetworkName() { return getNetwork().getName(); }
  public int getInputNodes() { return bamLogic.getF1Neurons(); }
  public int getHiddenNodes() { return 0; }
  public int getOutputNodes() { return bamLogic.getF2Neurons(); }
  public NeuralDataSet getTrainingDataSet() { return trainingDataSet; }
  public NeuralDataSet getValidationDataSet() { return validationDataSet; }
  public List<TrainingStatistics> getTrainingStatistics() { return null; }

  public String getNetworkDescription() {
    String logicStr = network.getLogic().getClass().toString();
    logicStr = logicStr.substring(logicStr.lastIndexOf('.') + 1);

    return logicStr + " Network (" + getInputNodes() + "-" + getOutputNodes() + " structure)";
  }

  public void train() {
    Iterator<NeuralDataPair> trainingIter = getTrainingDataSet().iterator();
    NeuralDataPair trainingDataPair = null;
    while (trainingIter.hasNext()) {
      trainingDataPair = trainingIter.next();
      bamLogic.addPattern(trainingDataPair.getInput(), trainingDataPair.getIdeal());
    }
  }

  public void interrogate(NeuralDataSet dataSet, String dataType, int outputNodeCount) {
    Iterator<NeuralDataPair> iter = dataSet.iterator();
    NeuralDataMapping dataMapping;
    int i = 1;

    while (iter.hasNext()) {
      dataMapping = new NeuralDataMapping((BiPolarNeuralData) iter.next().getInput(), createRandomBipolarNeuralData(outputNodeCount));

      System.out.println("\n" + dataType + " Pattern #" + i + "\n------------------------------------------------");
      System.out.print(VesselNeuralNetworkUtils.createBipolarMappingString((BiPolarNeuralData) dataMapping.getFrom(),
                                                                           (BiPolarNeuralData) dataMapping.getTo(), printDouble));
      bamLogic.compute(dataMapping);
      System.out.println("  |  " + VesselNeuralNetworkUtils.createBipolarMappingString((BiPolarNeuralData) dataMapping.getFrom(),
                                                                                       (BiPolarNeuralData) dataMapping.getTo(), printDouble));
      i++;
    }
  }

  public void interrogate(NeuralDataSet dataSet, String dataType) {
    interrogate(dataSet, dataType, getOutputNodes());
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

  public NeuralDataSet getDesiredData() { return desiredDataSet; }

  private NeuralDataSet createNeuralDataSet(double[][] inputPatterns, double[][] outputPatterns) {
    NeuralDataSet dataSet = new BasicNeuralDataSet();
    for (int i = 0; i < inputPatterns.length; i++) {
      if (outputPatterns != null && outputPatterns.length > i) {
        dataSet.add(VesselNeuralNetworkUtils.convertPatternToBipolar(inputPatterns[i]), VesselNeuralNetworkUtils.convertPatternToBipolar(outputPatterns[i]));
      } else {
        dataSet.add(VesselNeuralNetworkUtils.convertPatternToBipolar(inputPatterns[i]));
      }
    }
    return dataSet;
  }

  private BiPolarNeuralData createRandomBipolarNeuralData(int size) {
    BiPolarNeuralData result = new BiPolarNeuralData(size);
    for (int i = 0; i < size; i++) {
      if (Math.random() > 0.5) result.setData(i, -1);
      else result.setData(i, 1);
    }
    return result;
  }
}
