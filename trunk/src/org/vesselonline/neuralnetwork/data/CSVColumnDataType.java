package org.vesselonline.neuralnetwork.data;

public enum CSVColumnDataType {
  DOUBLE("Double"),
  BINARY("Binary"),
  BIPOLAR("Bipolar");

  private String name;

  CSVColumnDataType(String name) {
    this.name = name;
  }

  public String getName() { return name; }
}
