package org.vesselonline.neuralnetwork.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.encog.matrix.BiPolarUtil;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.csv.CSVNeuralDataSet;
import org.vesselonline.neuralnetwork.util.VesselNeuralNetworkUtils;

public class BasicCSVDataImporter implements CSVDataImporter {
  private String csvFileName;
  private int inputColumns, idealColumns;
  private CSVColumnConfiguration[] inputColumnConfigurations, idealColumnConfigurations;
  private boolean headers, randomize;
  private NeuralDataSet dataSet, validationDataSet;
  private double percentValidationData;

  public BasicCSVDataImporter(String csvFileName, int inputColumns, CSVColumnConfiguration[] inputColumnConfigurations, int idealColumns,
                              CSVColumnConfiguration[] idealColumnConfigurations, boolean headers, boolean randomize, double percentValidationData) {
    CSVNeuralDataSet csvDataSet = new CSVNeuralDataSet(csvFileName, inputColumns, idealColumns, headers);

    Iterator<NeuralDataPair> iter = csvDataSet.iterator();
    List<NeuralDataPair> dataList = new ArrayList<NeuralDataPair>();
    NeuralDataPair dataPair;

    while (iter.hasNext()) {
      dataPair = iter.next();

      dataPair.getInput().setData(normalizeData(dataPair.getInput().getData(), inputColumnConfigurations));
      dataPair.getIdeal().setData(normalizeData(dataPair.getIdeal().getData(), idealColumnConfigurations));

      dataList.add(dataPair);
    }

    if (randomize) {
      Collections.shuffle(dataList);
    }

    if (percentValidationData > 0) {
      int validationRecordCount = (int) (dataList.size() * percentValidationData);
      int validationDeleteIndex = dataList.size() - validationRecordCount;

      List<NeuralDataPair> validationDataList = new ArrayList<NeuralDataPair>();
      for (int i = 0; i < validationRecordCount; i++) {
        validationDataList.add(dataList.remove(validationDeleteIndex));
      }

      validationDataSet = new BasicNeuralDataSet(validationDataList);
      validationDataSet.close();
    }

    dataSet = new BasicNeuralDataSet(dataList);

    csvDataSet.close();
    dataSet.close();

    this.csvFileName = csvFileName;
    this.inputColumns = inputColumns;
    this.inputColumnConfigurations = inputColumnConfigurations;
    this.idealColumns = idealColumns;
    this.idealColumnConfigurations = idealColumnConfigurations;
    this.headers = headers;
    this.randomize = randomize;
    this.percentValidationData = percentValidationData;
  }

  public String getCSVFileName() { return csvFileName; }
  public int getInputColumns() { return inputColumns; }
  public CSVColumnConfiguration[] getInputColumnConfigurations() { return inputColumnConfigurations; }
  public int getIdealColumns() { return idealColumns; }
  public CSVColumnConfiguration[] getIdealColumnConfigurations() { return idealColumnConfigurations; }
  public boolean hasHeaders() { return headers; }
  public boolean isRandomize() { return randomize; }
  public NeuralDataSet getDataSet() { return dataSet; }
  public NeuralDataSet getValidationDataSet() { return validationDataSet; }
  public double getPercentValidationData() { return percentValidationData; }

  private double[] normalizeData(double[] data, CSVColumnConfiguration[] colConfigs) {
    CSVColumnConfiguration colConfig;

    for (int i = 0; i < data.length; i++) {
      colConfig = (colConfigs[i] != null) ? colConfigs[i] : new CSVColumnConfiguration();

      switch (colConfig.getColumnChangeType()) {
        case TO_NEW_RANGE:
          if (colConfig.getColumnDataType() == CSVColumnDataType.DOUBLE) {
            data[i] = VesselNeuralNetworkUtils.convertValueToNewRange(data[i], colConfig.getCurrentRangeMin(), colConfig.getCurrentRangeMax(),
                                                                      colConfig.getNewRangeMin(), colConfig.getNewRangeMax());
          }
          break;
        case TO_BINARY:
          if (colConfig.getColumnDataType() == CSVColumnDataType.DOUBLE) {
            data[i] = BiPolarUtil.normalizeBinary(data[i]);
          } else if (colConfig.getColumnDataType() == CSVColumnDataType.BIPOLAR) {
            data[i] = BiPolarUtil.toBinary(data[i]);
          }
          break;
        case TO_BIPOLAR:
          if (colConfig.getColumnDataType() != CSVColumnDataType.BIPOLAR) {
            data[i] = BiPolarUtil.toBiPolar(data[i]);
          }
          break;
        case INCREMENT:
          if (colConfig.getColumnDataType() == CSVColumnDataType.DOUBLE) {
            data[i] += colConfig.getIncrement();
          }
          break;
        case UNCHANGED:
        default:
          break;
      }
    }

    return data;
  }
}
