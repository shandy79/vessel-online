package org.vesselonline.ai.graph;

public class SimpleEdge<T> {
  private SimpleNode<T> toNode;
  private double value;
  private double weight;
  private String label;

  public SimpleEdge(SimpleNode<T> toNode) {
    this.toNode = toNode;
    value = 1;
    weight = 1;
  }

  public SimpleNode<T> getToNode() { return toNode; }

  public double getValue() { return value; }
  public void setValue(double value) { this.value = value; }

  public double getWeight() { return weight; }
  public void setWeight(double weight) { this.weight = weight; }

  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
}
