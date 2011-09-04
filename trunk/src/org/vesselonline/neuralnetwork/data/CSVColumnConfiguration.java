package org.vesselonline.neuralnetwork.data;

public class CSVColumnConfiguration {
  private CSVColumnDataType columnDataType;
  private CSVColumnChangeType columnChangeType;
  private double currentRangeMin, currentRangeMax, newRangeMin, newRangeMax, increment;

  public CSVColumnConfiguration(CSVColumnDataType columnDataType, CSVColumnChangeType columnChangeType) {
    this.columnDataType = columnDataType;
    this.columnChangeType = columnChangeType;
  }

  public CSVColumnConfiguration(double currentRangeMin, double currentRangeMax, double newRangeMin, double newRangeMax) {
    this(CSVColumnDataType.DOUBLE, CSVColumnChangeType.TO_NEW_RANGE);

    this.currentRangeMin = currentRangeMin;
    this.currentRangeMax = currentRangeMax;
    this.newRangeMin = newRangeMin;
    this.newRangeMax = newRangeMax;
  }

  public CSVColumnConfiguration(double increment) {
    this(CSVColumnDataType.DOUBLE, CSVColumnChangeType.INCREMENT);

    this.increment = increment;
  }

  public CSVColumnConfiguration() {
    this(CSVColumnDataType.DOUBLE, CSVColumnChangeType.UNCHANGED);
  }

  public CSVColumnDataType getColumnDataType() { return columnDataType; }
  public CSVColumnChangeType getColumnChangeType() { return columnChangeType; }
  public double getCurrentRangeMin() { return currentRangeMin; }
  public double getCurrentRangeMax() { return currentRangeMax; }
  public double getNewRangeMin() { return newRangeMin; }
  public double getNewRangeMax() { return newRangeMax; }
  public double getIncrement() { return increment; }
}
