package org.vesselonline.neuralnetwork.data;

public enum CSVColumnChangeType {
  UNCHANGED("Unchanged"),
  TO_NEW_RANGE("To New Range"),
  TO_BINARY("To Binary"),
  TO_BIPOLAR("To Bipolar"),
  INCREMENT("Increment");

  private String name;

  CSVColumnChangeType(String name) {
    this.name = name;
  }

  public String getName() { return name; }
}
