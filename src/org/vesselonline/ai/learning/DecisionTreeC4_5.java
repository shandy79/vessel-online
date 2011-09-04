package org.vesselonline.ai.learning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vesselonline.ai.learning.data.CSVData;
import org.vesselonline.ai.util.Instrumentation;

public class DecisionTreeC4_5 extends DecisionTreeID3 {
  public DecisionTreeC4_5(CSVData csvData, Instrumentation instrumentation) {
    super(csvData, instrumentation);
  }

  /**
   * Method works with multi-valued classifiers.
   * @param examples
   * @param attrIdx
   * @param entropy
   * @return
   */
  protected double calculateGain(List<String[]> examples, int attrIdx, double entropy) {
    double gainRatio = calculateGainID3(examples, attrIdx, entropy) / calculateSplitC4_5(examples, attrIdx);

    if (Double.isInfinite(gainRatio) || Double.isNaN(gainRatio)) {
      gainRatio = 0;
      getInstrumentation().print("Gain Ratio(S, " + getCSVData().getAttributes()[attrIdx].getName() + ") = 0  (Split causes divide by zero)");
    } else {
      getInstrumentation().print("Gain Ratio(S, " + getCSVData().getAttributes()[attrIdx].getName() + ") = " + gainRatio);
    }

    return gainRatio;
  }

  private double calculateSplitC4_5(List<String[]> examples, int attrIdx) {
    // valueMap has each attribute value as a key pointing to the total example count for that attribute value
    Map<String, Integer> valueMap = new HashMap<String, Integer>();
    // classMap has each attribute value as a key pointing to another map containing each classification
    // value as a key pointing to the example count for that attribute value-classification value combo
    Map<String, Map<String, Integer>> classMap = new HashMap<String, Map<String, Integer>>();
    String value, classification, mStr;
    double magnitude, split = 0;

    for (int i = 0; i < examples.size(); i++) {
      value = examples.get(i)[attrIdx];
      classification = examples.get(i)[getCSVData().getAttributeCount()];

      if (valueMap.get(value) == null) {
        valueMap.put(value, 0);
        classMap.put(value, new HashMap<String, Integer>());
      }

      if (classMap.get(value).get(classification) == null) {
        classMap.get(value).put(classification, 0);
      }

      valueMap.put(value, valueMap.get(value).intValue() + 1);
      classMap.get(value).put(classification, classMap.get(value).get(classification).intValue() + 1);
    }

    StringBuilder output = new StringBuilder("Split(S, " + getCSVData().getAttributes()[attrIdx].getName() + ") = - (");
    for (String v : valueMap.keySet()) {
      magnitude = (double) valueMap.get(v) / examples.size();
      mStr = valueMap.get(v) + "/" + examples.size();
      output.append(" + " + mStr + " * log2 " + mStr);

      split += magnitude * Math.log(magnitude) / LOG_2;
    }

    output.append(" ) = " + (-1 * split));
    getInstrumentation().print(output.toString());
    return -1 * split;
  }
}
