package org.vesselonline.ai.learning.data;

public class Attribute {
  private String name;
  private String[] domain;

  public Attribute(String name, String[] domain) {
    this.name = name;
    this.domain = domain;
  }

  public String getName() { return name; }
  public String[] getDomain() { return domain; }
}
