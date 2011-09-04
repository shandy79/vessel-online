package org.vesselonline.ai.graph;

public enum ThreeColor {
  RED("Red"),
  GREEN("Green"),
  BLUE("Blue");

  private String color;

  ThreeColor(String color) {
    this.color = color;
  }

  public String getColor() { return color; }
}
