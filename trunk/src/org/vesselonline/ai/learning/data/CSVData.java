package org.vesselonline.ai.learning.data;

import java.util.List;

public interface CSVData {
  List<String[]> getData();
  Attribute[] getAttributes();
  Attribute getClassification();
  int getAttributeCount();
}
