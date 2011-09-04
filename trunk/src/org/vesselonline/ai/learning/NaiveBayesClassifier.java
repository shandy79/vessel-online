package org.vesselonline.ai.learning;

import java.util.HashMap;
import java.util.Map;
import org.vesselonline.ai.learning.data.Attribute;
import org.vesselonline.ai.learning.data.BasicCSVData;
import org.vesselonline.ai.learning.data.CSVData;
import org.vesselonline.ai.util.Instrumentable;
import org.vesselonline.ai.util.Instrumentation;

public class NaiveBayesClassifier implements Instrumentable {
  private CSVData csvData;
  private Map<String, Double> classificationPPT;
  private Map<Integer, Map<String, Map<String, Double>>> attrValueCPTs;
  private Instrumentation instrumentation;

  private static final double DIRICHLET_M = 1;
  private static final double DIRICHLET_P = 0.001;

  public NaiveBayesClassifier(CSVData csvData, Instrumentation instrumentation) {
    this.csvData = csvData;
    this.instrumentation = instrumentation;

    classificationPPT = new HashMap<String, Double>(getCSVData().getClassification().getDomain().length);
    attrValueCPTs = new HashMap<Integer, Map<String, Map<String, Double>>>(getCSVData().getAttributeCount());
    calculateProbabilities();
  }

  public CSVData getCSVData() { return csvData; }
  public Map<String, Double> getClassificationPPT() { return classificationPPT; }
  public Map<Integer, Map<String, Map<String, Double>>> getAttrValueCPTs() { return attrValueCPTs; }

  @Override
  public Instrumentation getInstrumentation() { return instrumentation; }
  @Override
  public void setInstrumentation(Instrumentation instrumentation) { this.instrumentation = instrumentation; }

  public String classify(String[] example) {
    StringBuilder output = new StringBuilder();
    double attrClassValue, maxClassValue = 0;
    String predictedClass = null;

    for (String classVal : getCSVData().getClassification().getDomain()) {
      attrClassValue = getClassificationPPT().get(classVal);
      
      for (int i = 0; i < getCSVData().getAttributeCount(); i++) {
        attrClassValue *= getAttrValueCPTs().get(i).get(example[i]).get(classVal);
      }

      output.append(classVal + ":" + attrClassValue + "  ");

      if (attrClassValue > maxClassValue) {
        predictedClass = classVal;
        maxClassValue = attrClassValue;
      }
    }

    output.append("\tPredicted: " + predictedClass + ", Actual: " + example[getCSVData().getAttributeCount()]);
    getInstrumentation().print(output.toString());
    return predictedClass;
  }

  private void calculateProbabilities() {
    Map<String, Integer> classPPTCount = new HashMap<String, Integer>(getCSVData().getClassification().getDomain().length);
    Map<Integer, Map<String, Map<String, Integer>>> attrValCPTCount = new HashMap<Integer,
                                                                              Map<String, Map<String, Integer>>>(getCSVData().getAttributeCount());
    Attribute attr;

    // Initialize class PPT and attribute CPTs
    for (String classVal : getCSVData().getClassification().getDomain()) {
      classPPTCount.put(classVal, 0);
      getClassificationPPT().put(classVal, 0.0);
    }

    for (int i = 0; i < getCSVData().getAttributeCount(); i++) {
      attr = getCSVData().getAttributes()[i];
      attrValCPTCount.put(i, new HashMap<String, Map<String, Integer>>(attr.getDomain().length));
      getAttrValueCPTs().put(i, new HashMap<String, Map<String, Double>>(attr.getDomain().length));

      for (String attrVal : attr.getDomain()) {
        attrValCPTCount.get(i).put(attrVal, new HashMap<String, Integer>(getCSVData().getClassification().getDomain().length));
        getAttrValueCPTs().get(i).put(attrVal, new HashMap<String, Double>(getCSVData().getClassification().getDomain().length));

        for (String classVal : getCSVData().getClassification().getDomain()) {
          attrValCPTCount.get(i).get(attrVal).put(classVal, 0);
          getAttrValueCPTs().get(i).get(attrVal).put(classVal, 0.0);
        }
      }
    }

    // Count everything in the data set
    for (String[] ex : getCSVData().getData()) {
      for (int i = 0; i < ex.length; i++) {
        if (i == getCSVData().getAttributeCount()) {
          classPPTCount.put(ex[i], classPPTCount.get(ex[i]).intValue() + 1);
        } else {
          attrValCPTCount.get(i).get(ex[i]).put(ex[getCSVData().getAttributeCount()],
                                                attrValCPTCount.get(i).get(ex[i]).get(ex[getCSVData().getAttributeCount()]).intValue() + 1);
        }
      }
    }

    // Calculate the PPT for the classification values based on counts
    for (String classVal : classPPTCount.keySet()) {
      getClassificationPPT().put(classVal, (classPPTCount.get(classVal).doubleValue() + DIRICHLET_M * DIRICHLET_P) / 
                                           (getCSVData().getData().size() + DIRICHLET_M));
    }

    // Calculate the CPTs for the attribute values based on counts
    for (Integer attrIdx : attrValCPTCount.keySet()) {
      for (String attrVal : attrValCPTCount.get(attrIdx).keySet()) {
        for (String classVal : attrValCPTCount.get(attrIdx).get(attrVal).keySet()) {
          getAttrValueCPTs().get(attrIdx).get(attrVal).put(classVal, (attrValCPTCount.get(attrIdx).get(attrVal).get(classVal).doubleValue() +
                                                                     DIRICHLET_M * DIRICHLET_P) / (classPPTCount.get(classVal).doubleValue() +
                                                                     DIRICHLET_M));
        }
      }
    }

    getInstrumentation().print(pptCPTToString(classPPTCount, attrValCPTCount));
  }

  private String pptCPTToString(Map<String, Integer> classPPTCount, Map<Integer, Map<String, Map<String, Integer>>> attrValCPTCount) {
    StringBuilder output = new StringBuilder("Classification PPT (counts w/Dirichlet Prior probability)\n" + 
                                             "---------------------------------------------------------\n\t\t" +
                                             getCSVData().getClassification().getName() + "\n");

    for (String classVal : classPPTCount.keySet()) {
      output.append(classVal + "\t(" + classPPTCount.get(classVal) +  "/" + getCSVData().getData().size() + ") " +
                    getClassificationPPT().get(classVal) + "\n");
    }

    output.append("\nAttribute Value CPTs (counts w/Dirichlet Prior probability)\n-----------------------------------------------------------");
    for (Integer attrIdx : attrValCPTCount.keySet()) {
      output.append("\n\nP(" + getCSVData().getAttributes()[attrIdx].getName() + "|" + getCSVData().getClassification().getName() + ")");

      for (String attrVal : attrValCPTCount.get(attrIdx).keySet()) {
        output.append("\n" + attrVal + "\t");

        for (String classVal : attrValCPTCount.get(attrIdx).get(attrVal).keySet()) {
          output.append("    " + classVal + " - (" + attrValCPTCount.get(attrIdx).get(attrVal).get(classVal) + "/" + classPPTCount.get(classVal) + ") " +
                        getAttrValueCPTs().get(attrIdx).get(attrVal).get(classVal));
        }
      }
    }

    return output.toString();
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
      Instrumentation instrumentation = new Instrumentation(true, System.out);

      Attribute outlook = new Attribute("Outlook", new String[] {"sunny", "overcast", "rain"});
      Attribute temp = new Attribute("Temp.", new String[] {"hot", "mild", "cool"});
      Attribute humid = new Attribute("Humidity", new String[] {"normal", "high"});
      Attribute windy = new Attribute("Windy", new String[] {"yes", "no"});
      Attribute enjoy = new Attribute("Enjoy?", new String[] {"yes", "no"});

      CSVData weatherData = new BasicCSVData("weather.data", new Attribute[] {outlook, temp, humid, windy}, enjoy);
      NaiveBayesClassifier nbc = new NaiveBayesClassifier(weatherData, instrumentation);

      System.out.println("\nClassification of the Training Items from the Weather Data\n" +
                         "----------------------------------------------------------");
      for (String[] ex : weatherData.getData()) {
        nbc.classify(ex);
      }
    } catch (Exception ioe) {
      ioe.printStackTrace();
    }
  }
}
