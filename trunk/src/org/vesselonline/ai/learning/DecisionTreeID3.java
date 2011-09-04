package org.vesselonline.ai.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vesselonline.ai.graph.GraphUtilities;
import org.vesselonline.ai.graph.SimpleEdge;
import org.vesselonline.ai.graph.SimpleNode;
import org.vesselonline.ai.learning.data.Attribute;
import org.vesselonline.ai.learning.data.BasicCSVData;
import org.vesselonline.ai.learning.data.CSVData;
import org.vesselonline.ai.util.Instrumentable;
import org.vesselonline.ai.util.Instrumentation;

public class DecisionTreeID3 implements Instrumentable {
  private CSVData csvData;
  private SimpleNode<Integer> tree;
  private Instrumentation instrumentation;

  protected static final double LOG_2 = Math.log(2);

  public DecisionTreeID3(CSVData csvData, Instrumentation instrumentation) {
    this.csvData = csvData;
    this.instrumentation = instrumentation;

    tree = decisionTree(getCSVData().getData(), new ArrayList<Integer>(), new ArrayList<String[]>());
    if (getInstrumentation().isPrint()) {
      getInstrumentation().print("\nResultant Decision Tree\n-----------------------");
      getTree().printNodeContents(0);
    }
  }

  public CSVData getCSVData() { return csvData; }
  public SimpleNode<Integer> getTree() { return tree; }

  @Override
  public Instrumentation getInstrumentation() { return instrumentation; }
  @Override
  public void setInstrumentation(Instrumentation instrumentation) { this.instrumentation = instrumentation; }

  public String classify(String[] example) {
    SimpleNode<Integer> node = getTree();
    String attrValue;
    StringBuilder output = new StringBuilder();

    while (node.getValue() != getCSVData().getAttributeCount()) {
      attrValue = example[node.getValue()];

      for (SimpleEdge<Integer> edge : node.getEdgeList()) {
        if (edge.getLabel().equals(attrValue)) {
          output.append("->" + node.getName() + ":" + attrValue);
          node = edge.getToNode();
          break;
        }
      }
    }

    output.append(" = " + node.getName());
    getInstrumentation().print(output.toString());
    return node.getName();
  }

  /**
   * Implements the creation of a decision tree using the ID3 algorithm for measuring attribute
   * importance.  Decision Tree Learning is described in AIMA, Ch. 18, Fig. 18.5.
   *
   * @param examples
   * @param ignoreAttrs  List of attribute indices that have been removed from the examples.
   * @param parentExs
   * @return  The returned tree consists of nodes with the name of the represented attribute and a value
   *          of that attribute index.  Edges are labeled with the represented attribute value.
   */
  private SimpleNode<Integer> decisionTree(List<String[]> examples, List<Integer> ignoreAttrs, List<String[]> parentExs) {
    if (examples.size() == 0) {
      return pluralityValue(parentExs);
    } else if (allExamplesSameClass(examples)) {
      SimpleNode<Integer> classificationNode = new SimpleNode<Integer>(examples.get(0)[getCSVData().getAttributeCount()]);
      classificationNode.setValue(getCSVData().getAttributeCount());
      return classificationNode;
    // Modified from examples.get(0).length in original implementation
    } else if (ignoreAttrs.size() == examples.get(0).length - 1) {
      return pluralityValue(examples);
    }

    double entropy = calculateEntropy(examples);
    // Modified from maxGain = 0 in original implementation
    double tmpGain, maxGain = -1;
    int maxAttr = -1;

    for (int i = 0; i < getCSVData().getAttributeCount(); i++) {
      if (! ignoreAttrs.contains(i)) {
        tmpGain = calculateGain(examples, i, entropy);

        if (tmpGain > maxGain) {
          maxGain = tmpGain;
          maxAttr = i;
        }
      }
    }

    Map<String, List<String[]>> exsMap = createAttrValueExampleMap(examples, maxAttr);
    List<Integer> newIgnoreAttrs = GraphUtilities.copyListToNewList(ignoreAttrs);
    newIgnoreAttrs.add(maxAttr);
    SimpleNode<Integer> tree = new SimpleNode<Integer>(getCSVData().getAttributes()[maxAttr].getName());
    tree.setValue(maxAttr);
    SimpleNode<Integer> subTree;
    SimpleEdge<Integer> edge;

    for (String v : exsMap.keySet()) {
      subTree = decisionTree(exsMap.get(v), newIgnoreAttrs, examples);
      edge = new SimpleEdge<Integer>(subTree);
      edge.setLabel(v);
      tree.addEdge(edge);
    }

    return tree;
  }

  /**
   * @param examples
   * @param attrIdx
   * @return  For the specified attribute index, this method returns a map containing each value
   *          of that attribute contained in the examples as a key.  For each value, a list of
   *          all of the example rows that contain that value of the attribute is constructed.
   */
  private Map<String, List<String[]>> createAttrValueExampleMap(List<String[]> examples, int attrIdx) {
    Map<String, List<String[]>> valueMap = new HashMap<String, List<String[]>>();
    String value;

    for (String s : getCSVData().getAttributes()[attrIdx].getDomain()) {
      valueMap.put(s, new ArrayList<String[]>());
    }

    for (int i = 0; i < examples.size(); i++) {
      value = examples.get(i)[attrIdx];
      valueMap.get(value).add(examples.get(i));
    }

    return valueMap;
  }

  private boolean allExamplesSameClass(List<String[]> examples) {
    String exClass = examples.get(0)[getCSVData().getAttributeCount()];
    
    for (int i = 1; i < examples.size(); i++) {
      if (! exClass.equals(examples.get(i)[getCSVData().getAttributeCount()])) {
        return false;
      }
    }

    return true;
  }

  // Selects the most common output value among a set of examples, breaking ties randomly
  private SimpleNode<Integer> pluralityValue(List<String[]> examples) {
    Map<String, Integer> valueMap = new HashMap<String, Integer>();
    String value;
    int maxCount = -1;

    // To help break random ties
    Collections.shuffle(examples);

    for (int i = 0; i < examples.size(); i++) {
      value = examples.get(i)[getCSVData().getAttributeCount()];

      if (valueMap.get(value) == null) {
        valueMap.put(value, 0);
      }

      valueMap.put(value, valueMap.get(value).intValue() + 1);
    }

    value = null;
    for (String s : valueMap.keySet()) {
      if (valueMap.get(s) > maxCount) {
        value = s;
        maxCount = valueMap.get(s);
      }
    }

    SimpleNode<Integer> classificationNode = new SimpleNode<Integer>(value);
    classificationNode.setValue(getCSVData().getAttributeCount());
    return classificationNode;
  }

  private double calculateEntropy(List<String[]> examples) {
    Map<String, Integer> valueMap = new HashMap<String, Integer>();
    String value, pStr;
    double p, e = 0;

    for (int i = 0; i < examples.size(); i++) {
      value = examples.get(i)[getCSVData().getAttributeCount()];

      if (valueMap.get(value) == null) {
        valueMap.put(value, 0);
      }

      valueMap.put(value, valueMap.get(value).intValue() + 1);
    }

    StringBuilder output = new StringBuilder("\nEntropy(S) = -(");
    for (String v : valueMap.keySet()) {
      p = (double) valueMap.get(v) / examples.size();
      pStr = valueMap.get(v) + "/" + examples.size();

      e += p * Math.log(p) / LOG_2;

      output.append(" + (" + pStr + " * log2 " + pStr + ")");
    }

    output.append(" ) = " + (e * -1));
    getInstrumentation().print(output.toString());
    return e * -1;
  }

  /**
   * Method works with multi-valued classifiers.
   * @param examples
   * @param attrIdx
   * @param entropy
   * @return
   */
  protected double calculateGain(List<String[]> examples, int attrIdx, double entropy) {
    return calculateGainID3(examples, attrIdx, entropy);
  }

  protected double calculateGainID3(List<String[]> examples, int attrIdx, double entropy) {
    // valueMap has each attribute value as a key pointing to the total example count for that attribute value
    Map<String, Integer> valueMap = new HashMap<String, Integer>();
    // classMap has each attribute value as a key pointing to another map containing each classification
    // value as a key pointing to the example count for that attribute value-classification value combo
    Map<String, Map<String, Integer>> classMap = new HashMap<String, Map<String, Integer>>();
    String value, classification, pStr;
    double magnitude, p, totalP, remainder = 0;

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

    StringBuilder output = new StringBuilder("Gain(S, " + getCSVData().getAttributes()[attrIdx].getName() + ") = Entropy(S) - (");
    for (String v : valueMap.keySet()) {
      magnitude = (double) valueMap.get(v) / examples.size();
      output.append(" + " + valueMap.get(v) + "/" + examples.size() + " * -(");
      totalP = 0;

      for (String c : classMap.get(v).keySet()) {
        p = (double) classMap.get(v).get(c) / valueMap.get(v);
        p *= Math.log(p) / LOG_2;
        pStr = classMap.get(v).get(c) + "/" + valueMap.get(v);

        totalP += p;
        output.append(" + (" + pStr + " * log2 " + pStr + ")");
      }

      remainder += magnitude * -1 * totalP;
      output.append(")");
    }

    output.append(" ) = " + (entropy - remainder));
    getInstrumentation().print(output.toString());
    return entropy - remainder;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    try {
      Instrumentation instrumentation = new Instrumentation(true, System.out);

      Attribute gpa = new Attribute("GPA", new String[] {"3.5", "3.7", "4.0"});
      Attribute univ = new Attribute("University", new String[] {"10", "20", "30"});
      Attribute pub = new Attribute("Published", new String[] {"Yes", "No"});
      Attribute rec = new Attribute("Recommendations", new String[] {"Good", "Normal"});
      Attribute status = new Attribute("Status", new String[] {"Accept", "Reject"});

//      CSVData univData = new BasicCSVData("university.data", new Attribute[] {gpa, univ, pub, rec}, status);
//      DecisionTreeID3 dtID3 = new DecisionTreeID3(univData, instrumentation);
//      DecisionTreeC4_5 dtC4_5 = new DecisionTreeC4_5(univData, instrumentation);

      System.out.println("\nClassification of the Training Items from the University Data\n" +
                         "-------------------------------------------------------------");
//      for (String[] ex : univData.getData()) {
//        dtC4_5.classify(ex);
//      }

      int folds = 2;
      CSVData[] univPartData = BasicCSVData.createPartitionedCSVData("AI_Data/university.data", new Attribute[] {gpa, univ, pub, rec}, status, folds, false);
      List<Map<String, Map<String, Integer>>> confusionMatrixList = LearningStatistics.kFoldDecisionTree(univPartData, instrumentation);
      Map<String, Map<String, Double>> avgMatrix = LearningStatistics.avgConfusionMatrix(confusionMatrixList, univPartData[0].getClassification());
      double avgAccuracy = LearningStatistics.calculateAccuracyD(avgMatrix);
      double variance = LearningStatistics.calculateVariance(confusionMatrixList, avgAccuracy);
      System.out.println("Accuracy = " + avgAccuracy + ", Variance = " + variance);
      System.out.println("Standard Error = " + LearningStatistics.calculateStandardError(variance, folds));

      LearningStatistics.calculateTStat(folds, avgAccuracy, variance, folds, 0.82342131, 0.00714832);
      LearningStatistics.printConfusionMatrix(avgMatrix);

    } catch (Exception ioe) {
      ioe.printStackTrace();
    }
  }
}
