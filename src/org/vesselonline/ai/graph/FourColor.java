package org.vesselonline.ai.graph;

public enum FourColor {
  RED("Red"),
  GREEN("Green"),
  BLUE("Blue"),
  YELLOW("Yellow");

  private String color;

  FourColor(String color) {
    this.color = color;
  }

  public String getColor() { return color; }
}
