package org.vesselonline.ai.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vesselonline.ai.learning.data.Attribute;
import org.vesselonline.ai.learning.data.BasicCSVData;
import org.vesselonline.ai.learning.data.CSVData;
import org.vesselonline.ai.util.Instrumentation;

public class LearningStatistics {
  public static final List<Map<String, Map<String, Integer>>> kFoldDecisionTree(CSVData[] partitionedData, Instrumentation instrumentation) {
    List<Map<String, Map<String, Integer>>> confusionMatrixList = new ArrayList<Map<String, Map<String, Integer>>>(partitionedData.length);
    Map<String, Map<String, Integer>> confusionMatrix;
    CSVData trainingData, testData;
    DecisionTreeC4_5 decisionTree;
    String predictedClass, actualClass;

    for (int i = 0; i < partitionedData.length; i++) {
      // Create data structure to contain results for creating confusion matrix
      confusionMatrix = createEmptyConfusionMatrix(partitionedData[0].getClassification());

      trainingData = concatenateCSVData(partitionedData, i);
      testData = partitionedData[i];

      decisionTree = new DecisionTreeC4_5(trainingData, instrumentation);

      for (String[] ex : testData.getData()) {
        predictedClass = decisionTree.classify(ex);
        actualClass = ex[testData.getAttributeCount()];

        confusionMatrix.get(actualClass).put(predictedClass, confusionMatrix.get(actualClass).get(predictedClass).intValue() + 1);
      }

      confusionMatrixList.add(confusionMatrix);
    }

    return confusionMatrixList;
  }

  public static final List<Map<String, Map<String, Integer>>> kFoldNaiveBayes(CSVData[] partitionedData, Instrumentation instrumentation) {
    List<Map<String, Map<String, Integer>>> confusionMatrixList = new ArrayList<Map<String, Map<String, Integer>>>(partitionedData.length);
    Map<String, Map<String, Integer>> confusionMatrix;
    CSVData trainingData, testData;
    NaiveBayesClassifier naiveBayes;
    String predictedClass, actualClass;

    for (int i = 0; i < partitionedData.length; i++) {
      // Create data structure to contain results for creating confusion matrix
      confusionMatrix = createEmptyConfusionMatrix(partitionedData[0].getClassification());

      trainingData = concatenateCSVData(partitionedData, i);
      testData = partitionedData[i];

      naiveBayes = new NaiveBayesClassifier(trainingData, instrumentation);

      for (String[] ex : testData.getData()) {
        predictedClass = naiveBayes.classify(ex);
        actualClass = ex[testData.getAttributeCount()];

        confusionMatrix.get(actualClass).put(predictedClass, confusionMatrix.get(actualClass).get(predictedClass).intValue() + 1);
      }

      confusionMatrixList.add(confusionMatrix);
    }

    return confusionMatrixList;
  }

  private static final CSVData concatenateCSVData(CSVData[] partitionedData, int testIdx) {
    List<String[]> data = new ArrayList<String[]>();

    for (int i = 0; i < partitionedData.length; i++) {
      if (i == testIdx) {
        continue;
      }

      data.addAll(partitionedData[i].getData());
    }

    return new BasicCSVData(data, partitionedData[0].getAttributes(), partitionedData[0].getClassification());
  }

  private static final Map<String, Map<String, Integer>> createEmptyConfusionMatrix(Attribute classification) {
    Map<String, Map<String, Integer>> confusionMatrix = new HashMap<String, Map<String, Integer>>(classification.getDomain().length);
    for (String cA : classification.getDomain()) {
      confusionMatrix.put(cA, new HashMap<String, Integer>(classification.getDomain().length));

      for (String cP : classification.getDomain()) {
        confusionMatrix.get(cA).put(cP, 0);
      }
    }

    return confusionMatrix;
  }

  private static final Map<String, Map<String, Double>> createEmptyConfusionMatrixD(Attribute classification) {
    Map<String, Map<String, Double>> confusionMatrix = new HashMap<String, Map<String, Double>>(classification.getDomain().length);
    for (String cA : classification.getDomain()) {
      confusionMatrix.put(cA, new HashMap<String, Double>(classification.getDomain().length));

      for (String cP : classification.getDomain()) {
        confusionMatrix.get(cA).put(cP, 0.0);
      }
    }

    return confusionMatrix;
  }

  public static final Map<String, Map<String, Double>> avgConfusionMatrix(List<Map<String, Map<String, Integer>>> confusionMatrixList, Attribute classification) {
    if (confusionMatrixList == null || confusionMatrixList.size() <= 0) {
      return null;
    }
    Map<String, Map<String, Double>> avgMatrix = createEmptyConfusionMatrixD(classification);
    double avgValue;

    for (Map<String, Map<String, Integer>> confusionMatrix : confusionMatrixList) {
      for (String actualClass : confusionMatrix.keySet()) {
        for (String predictedClass : confusionMatrix.get(actualClass).keySet()) {
          avgMatrix.get(actualClass).put(predictedClass, avgMatrix.get(actualClass).get(predictedClass) +
                                         confusionMatrix.get(actualClass).get(predictedClass));
        }
      }
    }

    for (String actualClass : avgMatrix.keySet()) {
      for (String predictedClass : avgMatrix.get(actualClass).keySet()) {
        avgValue = avgMatrix.get(actualClass).get(predictedClass).doubleValue() / confusionMatrixList.size();
        avgMatrix.get(actualClass).put(predictedClass, avgValue);
      }
    }

    return avgMatrix;
  }

  private static final double calculateAccuracy(Map<String, Map<String, Integer>> confusionMatrix) {
    double correctSum = 0, matrixSum = 0;

    for (String actualClass : confusionMatrix.keySet()) {
      for (String predictedClass : confusionMatrix.get(actualClass).keySet()) {
        if (actualClass.equals(predictedClass)) {
          correctSum += confusionMatrix.get(actualClass).get(predictedClass);
        }
        matrixSum += confusionMatrix.get(actualClass).get(predictedClass);
      }
    }

    return (double) correctSum / matrixSum;
  }

  public static final double calculateAccuracyD(Map<String, Map<String, Double>> avgConfusionMatrix) {
    double correctSum = 0, matrixSum = 0;

    for (String actualClass : avgConfusionMatrix.keySet()) {
      for (String predictedClass : avgConfusionMatrix.get(actualClass).keySet()) {
        if (actualClass.equals(predictedClass)) {
          correctSum += avgConfusionMatrix.get(actualClass).get(predictedClass);
        }
        matrixSum += avgConfusionMatrix.get(actualClass).get(predictedClass);
      }
    }

    return (double) correctSum / matrixSum;
  }

  public static final double calculateVariance(List<Map<String, Map<String, Integer>>> confusionMatrixList, double avgAccuracy) {
    double variance = 0;

    for (Map<String, Map<String, Integer>> confusionMatrix : confusionMatrixList) {
      variance += Math.pow(calculateAccuracy(confusionMatrix) - avgAccuracy, 2);
    }

    return variance / confusionMatrixList.size();
  }

  public static final double calculateStandardError(double variance, int fold) {
    return Math.sqrt(variance) / Math.sqrt(fold);
  }

  public static final double calculateTStat(int fold1, double acc1, double var1, int fold2, double acc2, double var2) {
    double varPooled = ((fold1 - 1) * var1 + (fold2 - 1) * var2) / (fold1 + fold2 - 2);
    System.out.println("\ns2_pooled = ( (" + fold1 + " - 1) * " + var1 + " + (" + fold2 + " - 1) * " + var2 + " ) / (" + fold1 + " + " +
                       fold2 + " - 2) = " + varPooled);

    double stdErrD = Math.sqrt(varPooled / fold1 + varPooled / fold2);
    System.out.println("se_d = (" + varPooled + " / " + fold1 + " + " + varPooled + " / " + fold2 + ") ^ 1/2 = " + stdErrD);

    double tStat = Math.abs((acc1 - acc2) / stdErrD);
    System.out.println("t-stat = abs((" + acc1 + " - " + acc2 + ") / " + stdErrD + ") = " + tStat);

    return tStat;
  }

  public static final void printConfusionMatrix(Map<String, Map<String, Double>> confusionMatrix) {
    ArrayList<String> sortedKeyList = new ArrayList<String>(confusionMatrix.keySet());
    Collections.sort(sortedKeyList);

    System.out.print("\n\t|");
    for (String actualClass : sortedKeyList) {
      System.out.print(" " + actualClass + "\t|");
    }
    System.out.println("\n--------------------------------------------------------------------------------");

    for (String actualClass : sortedKeyList) {
      System.out.print(" " + actualClass + "\t|");

      for (String predictedClass : sortedKeyList) {
        System.out.print(" " + Math.round(confusionMatrix.get(actualClass).get(predictedClass)) + "\t|");
      }

      System.out.println("\n--------------------------------------------------------------------------------");
    }
  }
}
