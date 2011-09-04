package org.vesselonline.ai.learning;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.vesselonline.ai.learning.data.AfCSColumn;
import org.vesselonline.ai.learning.data.AfCSDataTransformer;
import org.vesselonline.ai.learning.data.Attribute;
import org.vesselonline.ai.learning.data.BasicCSVData;
import org.vesselonline.ai.learning.data.CSVData;
import org.vesselonline.ai.util.Instrumentation;

public class AfCSData {
  public static void main(String[] args) {
    String fileName = "all_afcs_sm.tsv"; 

    SortedMap<Integer, AfCSColumn> columnMap = new TreeMap<Integer, AfCSColumn>();
    columnMap.put(21, new AfCSColumn("LogRatio", 21, AfCSColumn.FLOAT_TYPE));
    columnMap.put(22, new AfCSColumn("LogRatioError", 22, AfCSColumn.FLOAT_TYPE));
    columnMap.put(23, new AfCSColumn("PValueLogRatio", 23, AfCSColumn.FLOAT_TYPE));
    columnMap.put(26, new AfCSColumn("gIsFound", 26, AfCSColumn.BOOLEAN_TYPE));
    columnMap.put(27, new AfCSColumn("rIsFound", 27, AfCSColumn.BOOLEAN_TYPE));
    columnMap.put(28, new AfCSColumn("gProcessedSignal", 28, AfCSColumn.FLOAT_TYPE));
    columnMap.put(29, new AfCSColumn("rProcessedSignal", 29, AfCSColumn.FLOAT_TYPE));
    columnMap.put(30, new AfCSColumn("gProcessedSigError", 30, AfCSColumn.FLOAT_TYPE));
    columnMap.put(31, new AfCSColumn("rProcessedSigError", 31, AfCSColumn.FLOAT_TYPE));
    columnMap.put(56, new AfCSColumn("PixCorrelation", 56, AfCSColumn.FLOAT_TYPE));
    columnMap.put(57, new AfCSColumn("BGPixCorrelation", 57, AfCSColumn.FLOAT_TYPE));
    columnMap.put(72, new AfCSColumn("gIsPosAndSignif", 72, AfCSColumn.BOOLEAN_TYPE));
    columnMap.put(73, new AfCSColumn("rIsPosAndSignif", 73, AfCSColumn.BOOLEAN_TYPE));
    columnMap.put(81, new AfCSColumn("gBGUsed", 81, AfCSColumn.FLOAT_TYPE));
    columnMap.put(82, new AfCSColumn("rBGUsed", 82, AfCSColumn.FLOAT_TYPE));
    columnMap.put(92, new AfCSColumn("ligandClass", 92, AfCSColumn.TEXT_TYPE));

    int binCount = 30;

    AfCSDataTransformer afcsDataTransformer = null;
    try {
      afcsDataTransformer = new AfCSDataTransformer(fileName, columnMap, binCount);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    Instrumentation instrumentation = new Instrumentation(false, System.out);

    Attribute[] afcsAttrAry = afcsDataTransformer.getAttributes();
    for (Attribute attr : afcsAttrAry) {
      instrumentation.print(attr.getName());
      for (String v : attr.getDomain()) {
        instrumentation.print("  " + v);
      }
      instrumentation.print("");
    }
    instrumentation.print("");

    Attribute ligandClass = afcsDataTransformer.getClassification();
    instrumentation.print(ligandClass.getName());
    for (String v : ligandClass.getDomain()) {
      instrumentation.print("  " + v);
    }
    instrumentation.print("\n");

    CSVData[] partitionedData = null;
    int folds = 10;

    partitionedData = BasicCSVData.createPartitionedCSVData("all_afcs_sm.csv", afcsAttrAry, ligandClass, folds, true);

    List<Map<String, Map<String, Integer>>> confusionMatrixList = LearningStatistics.kFoldDecisionTree(partitionedData, instrumentation);
    Map<String, Map<String, Double>> avgMatrix = LearningStatistics.avgConfusionMatrix(confusionMatrixList, partitionedData[0].getClassification());

    double avgAccuracy = LearningStatistics.calculateAccuracyD(avgMatrix);
    double variance = LearningStatistics.calculateVariance(confusionMatrixList, avgAccuracy);
    double stdErr = LearningStatistics.calculateStandardError(variance, folds);
    System.out.println("Accuracy = " + avgAccuracy + ", Variance = " + variance + ", Standard Error = " + stdErr);
    LearningStatistics.printConfusionMatrix(avgMatrix);
  }
}
