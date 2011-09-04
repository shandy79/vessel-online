package org.vesselonline.neuralnetwork;

import java.util.List;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.Network;
import org.vesselonline.neuralnetwork.training.TrainingStatistics;

public interface VesselNeuralNetwork {
  String TRAINING = "Training";
  String VALIDATION = "Validation";
  String DESIRED = "Desired";

  String INPUT_NAME = "Input Layer";
  String HIDDEN_NAME = "Hidden Layer";
  String OUTPUT_NAME = "Output Layer";

  Network getNetwork();
  String getNetworkName();
  String getNetworkDescription();

  int getInputNodes();
  int getHiddenNodes();
  int getOutputNodes();

  NeuralDataSet getTrainingDataSet();
  void train();
  List<TrainingStatistics> getTrainingStatistics();

  void interrogate(NeuralDataSet dataSet, String dataType);
  void interrogate(NeuralDataSet dataSet, String filePath, String csvColHeaders);
  void printNetworkStatus();
}
