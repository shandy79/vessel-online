package org.vesselonline.ai.learning;

import java.util.List;
import java.util.Map;
import org.vesselonline.ai.learning.data.Attribute;
import org.vesselonline.ai.learning.data.BasicCSVData;
import org.vesselonline.ai.learning.data.CSVData;
import org.vesselonline.ai.util.Instrumentation;

public class SpliceData {
  public static void main(String[] args) {
    Instrumentation instrumentation = new Instrumentation(false, System.out);
    CSVData[] partitionedData = null;
    int folds = 10;

    String[] ntDomain = new String[] { "A", "G", "T", "C", "D", "N", "S", "R" };
    String[] classDomain = new String[] { "EI", "IE", "N" };

    Attribute[] ntAttrAry = new Attribute[60];
    for (int i = 0; i < ntAttrAry.length; i++) {
      ntAttrAry[i] = new Attribute("nt" + i, ntDomain);
    }

    Attribute spliceClass = new Attribute("class", classDomain);

    partitionedData = BasicCSVData.createPartitionedCSVData("splice_mod.data", ntAttrAry, spliceClass, folds, true);

    List<Map<String, Map<String, Integer>>> confusionMatrixList = LearningStatistics.kFoldDecisionTree(partitionedData, instrumentation);
    Map<String, Map<String, Double>> avgMatrix = LearningStatistics.avgConfusionMatrix(confusionMatrixList, partitionedData[0].getClassification());

    double avgAccuracy = LearningStatistics.calculateAccuracyD(avgMatrix);
    double variance = LearningStatistics.calculateVariance(confusionMatrixList, avgAccuracy);
    double stdErr = LearningStatistics.calculateStandardError(variance, folds);
    System.out.println("Decision Tree Accuracy = " + avgAccuracy + ", Variance = " + variance + ", Standard Error = " + stdErr);
    LearningStatistics.printConfusionMatrix(avgMatrix);
  }
}
