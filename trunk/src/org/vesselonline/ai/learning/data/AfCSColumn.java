package org.vesselonline.ai.learning.data;

import java.util.HashSet;
import java.util.Set;

public class AfCSColumn {
  private String name;
  private int index;
  private int type;

  private Set<String> domain;

  private double minValue;
  private double maxValue;

  public static final int TEXT_TYPE = 1;
  public static final int FLOAT_TYPE = 2;
  public static final int BOOLEAN_TYPE = 3;

  public AfCSColumn(String name, int index, int type) {
    this.name = name;
    this.index = index;
    this.type = type;

    if (type == BOOLEAN_TYPE) {
      minValue = 0;
      maxValue = 1;
    } else if (type == FLOAT_TYPE) {
      minValue = Double.MAX_VALUE;
      maxValue = Double.MIN_VALUE;
    }

    domain = new HashSet<String>();
  }

  public String getName() { return name; }
  public int getIndex() { return index; }
  public int getType() { return type; }

  public Double getMinValue() { return minValue; }

  public Double getMaxValue() { return maxValue; }

  public Set<String> getDomain() { return domain; }

  public void updateRange(String value) {
    if (type == FLOAT_TYPE) {
      double doubleValue = Double.parseDouble(value);
    
      if (doubleValue < minValue) {
        minValue = doubleValue;
      } else if (doubleValue > maxValue) {
        maxValue = doubleValue;
      }
    }
  }

  public void addToDomain(String value) {
    domain.add(value);
  }
}
