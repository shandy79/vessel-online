package org.vesselonline.ai.graph;

public class ComparableNode<T extends Comparable<T>> extends SimpleNode<T> implements Comparable<SimpleNode<T>> {
  public ComparableNode(String name) {
    super(name);
  }

  public void setName(String name) { this.name = name; }

  @Override
  public int compareTo(SimpleNode<T> o) {
    return getValue().compareTo(o.getValue());
  }
}
