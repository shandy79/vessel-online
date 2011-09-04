package org.vesselonline.neuralnetwork.data;

import org.encog.neural.data.NeuralDataSet;

public interface CSVDataImporter {
  String getCSVFileName();
  int getInputColumns();
  CSVColumnConfiguration[] getInputColumnConfigurations();
  int getIdealColumns();
  CSVColumnConfiguration[] getIdealColumnConfigurations();
  boolean hasHeaders();
  boolean isRandomize();
  NeuralDataSet getDataSet();
  NeuralDataSet getValidationDataSet();
  double getPercentValidationData();
}
