package org.vesselonline.neuralnetwork;

import java.util.List;
import org.encog.neural.activation.ActivationLinear;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.FeedforwardLogic;
import org.encog.neural.networks.structure.NeuralStructure;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.manhattan.ManhattanPropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.vesselonline.neuralnetwork.training.FeedforwardTrainingConfiguration;
import org.vesselonline.neuralnetwork.training.TrainingConfiguration;
import org.vesselonline.neuralnetwork.util.ThresholdConfiguration;
import org.vesselonline.neuralnetwork.util.VesselNeuralNetworkUtils;
import org.vesselonline.neuralnetwork.util.WeightConfiguration;

public class FeedforwardNeuralNetwork extends AbstractNeuralNetwork {
  private Layer hiddenLayer;
  private Synapse synapseInputToHidden, synapseHiddenToOutput;

  private static final String NETWORK_NAME = "Feedforward Network";

  public FeedforwardNeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes, ThresholdConfiguration hiddenThresholdConfig,
          ThresholdConfiguration outputThresholdConfig, WeightConfiguration inputHiddenWeightConfig,
          WeightConfiguration hiddenOutputWeightConfig, double[][] inputValues, double[][] desiredOutputs,
          FeedforwardTrainingConfiguration trainingConfiguration) {
    this(inputNodes, hiddenNodes, outputNodes, hiddenThresholdConfig, outputThresholdConfig, inputHiddenWeightConfig,
         hiddenOutputWeightConfig, new BasicNeuralDataSet(inputValues, desiredOutputs), trainingConfiguration);
  }

  public FeedforwardNeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes, ThresholdConfiguration hiddenThresholdConfig,
                                ThresholdConfiguration outputThresholdConfig, WeightConfiguration inputHiddenWeightConfig,
                                WeightConfiguration hiddenOutputWeightConfig, NeuralDataSet trainingDataSet,
                                FeedforwardTrainingConfiguration trainingConfiguration) {
    network = new BasicNetwork();
    network.setName(NETWORK_NAME);

    inputLayer = VesselNeuralNetworkUtils.createLayer(new ActivationLinear(), inputNodes, VesselNeuralNetwork.INPUT_NAME);
    hiddenLayer = VesselNeuralNetworkUtils.createLayer(new ActivationSigmoid(), hiddenNodes, VesselNeuralNetwork.HIDDEN_NAME, hiddenThresholdConfig);
    outputLayer = VesselNeuralNetworkUtils.createLayer(new ActivationSigmoid(), outputNodes, VesselNeuralNetwork.OUTPUT_NAME, outputThresholdConfig);

    network.addLayer(inputLayer);
    network.addLayer(hiddenLayer);
    network.addLayer(outputLayer);
    network.tagLayer(BasicNetwork.TAG_INPUT, inputLayer);
    network.tagLayer(BasicNetwork.TAG_OUTPUT, outputLayer);

    network.setLogic(new FeedforwardLogic());
    network.getStructure().finalizeStructure();

    synapseInputToHidden = VesselNeuralNetworkUtils.createWeightedSynapse(network.getStructure().findSynapse(inputLayer, hiddenLayer, true), inputHiddenWeightConfig);
    synapseHiddenToOutput = VesselNeuralNetworkUtils.createWeightedSynapse(network.getStructure().findSynapse(hiddenLayer, outputLayer, true), hiddenOutputWeightConfig);

    initTraining(trainingDataSet, trainingConfiguration);
//    printNetworkStatus();
  }

  public FeedforwardNeuralNetwork(BasicNetwork network, NeuralDataSet trainingSet, FeedforwardTrainingConfiguration trainingConfiguration) {
    this.network = network;
    network.setName(NETWORK_NAME);

    inputLayer = network.getLayer(BasicNetwork.TAG_INPUT);
    inputLayer.setName(VesselNeuralNetwork.INPUT_NAME);
    outputLayer = network.getLayer(BasicNetwork.TAG_OUTPUT);
    outputLayer.setName(VesselNeuralNetwork.OUTPUT_NAME);

    NeuralStructure structure = network.getStructure();
    List<Layer> layers = structure.getLayers();

    for (Layer layer : layers) {
      if (layer != inputLayer && layer != outputLayer) {
        hiddenLayer = layer;
        break;
      }
    }
    hiddenLayer.setName(VesselNeuralNetwork.HIDDEN_NAME);

    synapseInputToHidden = structure.findSynapse(inputLayer, hiddenLayer, true);
    synapseHiddenToOutput = structure.findSynapse(hiddenLayer, outputLayer, true);

    initTraining(trainingSet, trainingConfiguration);
    printNetworkStatus();
  }

  public int getHiddenNodes() { return hiddenLayer.getNeuronCount(); }

  public String getNetworkDescription() {
    String logicStr = network.getLogic().getClass().toString();
    logicStr = logicStr.substring(logicStr.lastIndexOf('.') + 1);
    
    String trainStr = train.getClass().toString();
    trainStr = trainStr.substring(trainStr.lastIndexOf('.') + 1);

    return logicStr + "-" + trainStr + " Network (" + getInputNodes() + "-" + getHiddenNodes() + "-" + getOutputNodes() + " structure, " +
           getTrainingConfiguration().getLearningRate() + " learning rate, " +
           ((FeedforwardTrainingConfiguration) getTrainingConfiguration()).getMomentum() + " momentum)";
  }

  public void printNetworkStatus() {
    System.out.println("\n" + getNetworkDescription());

    VesselNeuralNetworkUtils.printThresholdStatus(inputLayer);
    VesselNeuralNetworkUtils.printThresholdStatus(hiddenLayer);
    VesselNeuralNetworkUtils.printThresholdStatus(outputLayer);

    VesselNeuralNetworkUtils.printWeightStatus(synapseInputToHidden);
    VesselNeuralNetworkUtils.printWeightStatus(synapseHiddenToOutput);
  }

  protected void initTraining(NeuralDataSet trainingDataSet, TrainingConfiguration trainingConfiguration) {
    // Train the neural network using propagation
    this.trainingDataSet = trainingDataSet;
    this.trainingConfiguration = trainingConfiguration;

    switch (((FeedforwardTrainingConfiguration) getTrainingConfiguration()).getTrainingMethod()) {
      case TrainingConfiguration.RESILIENT:
        train = new ResilientPropagation(network, getTrainingDataSet());
        break;
      case TrainingConfiguration.MANHATTAN:
        train = new ManhattanPropagation(network, getTrainingDataSet(), getTrainingConfiguration().getLearningRate());
        break;
      case TrainingConfiguration.BACKPROPAGATION:
      default:
        train = new Backpropagation(network, getTrainingDataSet(), getTrainingConfiguration().getLearningRate(),
                                    ((FeedforwardTrainingConfiguration) getTrainingConfiguration()).getMomentum());
    }
  }
}
