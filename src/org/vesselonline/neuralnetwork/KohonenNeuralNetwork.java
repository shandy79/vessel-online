package org.vesselonline.neuralnetwork;

import org.encog.neural.activation.ActivationCompetitive;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.logic.SOMLogic;
import org.encog.neural.networks.structure.NeuralStructure;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.competitive.CompetitiveTraining;
import org.encog.neural.networks.training.competitive.neighborhood.NeighborhoodBubble;
import org.encog.neural.networks.training.competitive.neighborhood.NeighborhoodSingle;
import org.vesselonline.neuralnetwork.training.KohonenTrainingConfiguration;
import org.vesselonline.neuralnetwork.training.TrainingConfiguration;
import org.vesselonline.neuralnetwork.util.VesselNeuralNetworkUtils;
import org.vesselonline.neuralnetwork.util.WeightConfiguration;

public class KohonenNeuralNetwork extends AbstractNeuralNetwork {
  private Synapse synapseInputToOutput;

  private static final String NETWORK_NAME = "Kohonen Network";

  public KohonenNeuralNetwork(int inputNodes, int outputNodes, WeightConfiguration inputOutputWeightConfig, double[][] inputValues,
      KohonenTrainingConfiguration trainingConfiguration) {
    this(inputNodes, outputNodes, inputOutputWeightConfig, new BasicNeuralDataSet(inputValues, null), trainingConfiguration);
  }

  public KohonenNeuralNetwork(int inputNodes, int outputNodes, WeightConfiguration inputOutputWeightConfig, NeuralDataSet trainingDataSet,
                              KohonenTrainingConfiguration trainingConfiguration) {
    network = new BasicNetwork();
    network.setName(NETWORK_NAME);

    inputLayer = VesselNeuralNetworkUtils.createLayer(new ActivationLinear(), inputNodes, VesselNeuralNetwork.INPUT_NAME);
    outputLayer = VesselNeuralNetworkUtils.createLayer(new ActivationCompetitive(), outputNodes, VesselNeuralNetwork.OUTPUT_NAME);

    network.addLayer(inputLayer);
    network.addLayer(outputLayer);
    network.tagLayer(BasicNetwork.TAG_INPUT, inputLayer);
    network.tagLayer(BasicNetwork.TAG_OUTPUT, outputLayer);

    network.setLogic(new SOMLogic());
    network.getStructure().finalizeStructure();

    synapseInputToOutput = VesselNeuralNetworkUtils.createWeightedSynapse(network.getStructure().findSynapse(inputLayer, outputLayer, true), inputOutputWeightConfig);

    initTraining(trainingDataSet, trainingConfiguration);
    printNetworkStatus();
  }

  public KohonenNeuralNetwork(BasicNetwork network, NeuralDataSet trainingSet, KohonenTrainingConfiguration trainingConfiguration) {
    this.network = network;
    network.setName(NETWORK_NAME);

    inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
    inputLayer.setName(VesselNeuralNetwork.INPUT_NAME);
    outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
    outputLayer.setName(VesselNeuralNetwork.OUTPUT_NAME);

    NeuralStructure structure = network.getStructure();
    synapseInputToOutput = structure.findSynapse(inputLayer, outputLayer, true);

    initTraining(trainingSet, trainingConfiguration);
    printNetworkStatus();
  }

  public int getHiddenNodes() { return 0; }

  public String getNetworkDescription() {
    String logicStr = network.getLogic().getClass().toString();
    logicStr = logicStr.substring(logicStr.lastIndexOf('.') + 1);

    return logicStr + " Network (" + getInputNodes() + "-" + getOutputNodes() + " structure)";
  }

  public void printNetworkStatus() {
    System.out.println("\n" + getNetworkDescription());

    VesselNeuralNetworkUtils.printWeightStatus(synapseInputToOutput);
  }

  protected void initTraining(NeuralDataSet trainingSet, TrainingConfiguration trainingConfiguration) {
    // Train the neural network using competitive learning
    this.trainingDataSet = trainingSet;
    this.trainingConfiguration = trainingConfiguration;

    switch (((KohonenTrainingConfiguration) getTrainingConfiguration()).getNeighborhoodFunction()) {
      case TrainingConfiguration.BUBBLE:
        train = new CompetitiveTraining(network, getTrainingConfiguration().getLearningRate(), getTrainingDataSet(),
                                        new NeighborhoodBubble(((KohonenTrainingConfiguration) getTrainingConfiguration()).getNeighborhoodRadius()));
        break;
      case TrainingConfiguration.SINGLE:
      default:
        train = new CompetitiveTraining(network, getTrainingConfiguration().getLearningRate(), getTrainingDataSet(), new NeighborhoodSingle());
    }
  }
}
