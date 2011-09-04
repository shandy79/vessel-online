package org.vesselonline.ai.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class SimpleNode<T> {
  protected String name;
  private T value;
  private T previousValue;
  private List<T> domain;
  private int x;
  private int y;
  private List<SimpleEdge<T>> edgeList;

  public SimpleNode(String name) {
    this.name = name;
    edgeList = new ArrayList<SimpleEdge<T>>();
  }

  public SimpleNode(String name, List<T> domain) {
    this(name);
    this.domain = domain;
  }

  public SimpleNode(String name, List<T> domain, int x, int y) {
    this(name, domain);
    this.x = x;
    this.y = y;
  }

  public String getName() { return name; }

  public T getValue() { return value; }
  public void setValue(T value) { this.value = value; }

  public T getPreviousValue() { return previousValue; }
  public void setPreviousValue(T previousValue) { this.previousValue = previousValue; }

  public List<T> getDomain() { return domain; }

  public boolean addToDomain(T value) {
    if (! getDomain().contains(value)) {
      return getDomain().add(value);
    }

    return true;
  }

  public boolean removeFromDomain(T value) {
    return getDomain().remove(value);
  }

  public int getX() { return x; }
  public int getY() { return y; }

  public List<SimpleEdge<T>> getEdgeList() { return edgeList; }

  public boolean addEdge(SimpleEdge<T> edge) {
    if (! getEdgeList().contains(edge)) {
      return getEdgeList().add(edge);
    }

    return true;
  }

  public boolean removeEdge(SimpleEdge<T> edge) {
    return getEdgeList().remove(edge);
  }

  public int getDegree() {
    return getEdgeList().size();
  }

  public double getDistanceToNode(SimpleNode<T> node) {
    return Point2D.distance(getX(), getY(), node.getX(), node.getY());
  }

  public boolean isConnectedToNode(SimpleNode<T> node) {
    if (node.getName().equals(getName())) { return true; }

    for (SimpleEdge<T> edge : getEdgeList()) {
      if (edge.getToNode().getName().equals(node.getName())) {
        return true;
      }
    }

    return false;
  }

  public void printNodeContents(int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
    System.out.println(getName() + ":" + getValue());

    for (SimpleEdge<T> edge : getEdgeList()) {
      if (edge.getLabel() != null) {
        System.out.print(edge.getLabel());
      }
      (edge.getToNode()).printNodeContents(level + 1);
    }
  }
}
