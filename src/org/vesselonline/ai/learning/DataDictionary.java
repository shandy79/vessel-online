package org.vesselonline.ai.learning;

import java.util.List;
import java.util.Map;
import org.vesselonline.ai.learning.data.Attribute;
import org.vesselonline.ai.learning.data.BasicCSVData;
import org.vesselonline.ai.learning.data.CSVData;
import org.vesselonline.ai.util.Instrumentation;

public class DataDictionary {
  public static void main(String[] args) {
    Instrumentation instrumentation = new Instrumentation(false, System.out);
    CSVData[] partitionedData = null;

    String dataSet = "nursery";
    int folds = 10;

    if (dataSet.equals("nursery")) {
      // Nursery Data
      Attribute parents = new Attribute("parents", new String[] {"usual", "pretentious", "great_pret"});
      Attribute has_nurs = new Attribute("has_nurs", new String[] {"proper", "less_proper", "improper", "critical", "very_crit"});
      Attribute form = new Attribute("form", new String[] {"complete", "completed", "incomplete", "foster"});
      Attribute children = new Attribute("children", new String[] {"1", "2", "3", "more"});
      Attribute housing = new Attribute("housing", new String[] {"convenient", "less_conv", "critical"});
      Attribute finance = new Attribute("finance", new String[] {"convenient", "inconv"});
      Attribute social = new Attribute("social", new String[] {"nonprob", "slightly_prob", "problematic"});
      Attribute health = new Attribute("health", new String[] {"recommended", "priority", "not_recom"});

      Attribute nurseryClass = new Attribute("class", new String[] {"not_recom", "recommend", "very_recom", "priority", "spec_prior"});
      partitionedData = BasicCSVData.createPartitionedCSVData("AI_Data/nursery.data",
          new Attribute[] {parents, has_nurs, form, children, housing, finance, social, health}, nurseryClass, folds, true);

    } else if (dataSet.equals("mushroom")) {
      // Mushroom Data
      Attribute capShape = new Attribute("cap-shape", new String[] {"b", "c", "x", "f", "k", "s"});
      Attribute capSurface = new Attribute("cap-surface", new String[] {"f", "g", "y", "s"});
      Attribute capColor = new Attribute("cap-color", new String[] {"n", "b", "c", "g", "r", "p", "u", "e", "w", "y"});
      Attribute bruises = new Attribute("bruises?", new String[] {"t", "f"}); 
      Attribute odor = new Attribute("odor", new String[] {"a", "l", "c", "y", "f", "m", "n", "p", "s"}); 
      Attribute gillAttachment = new Attribute("gill-attachment", new String[] {"a", "d", "f", "n"}); 
      Attribute gillSpacing = new Attribute("gill-spacing", new String[] {"c", "w", "d"});
      Attribute gillSize = new Attribute ("gill-size", new String[] {"b", "n"}); 
      Attribute gillColor = new Attribute("gill-color", new String[] {"k", "n", "b", "h", "g", "r", "o", "p", "u", "e", "w", "y"}); 
      Attribute stalkShape = new Attribute("stalk-shape", new String[] {"e" , "t"}); 
      Attribute stalkRoot = new Attribute("stalk-root", new String[] {"b", "c", "u", "e", "z", "r", "?"}); 
      Attribute stalkSurfaceAboveRing = new Attribute("stalk-surface-above-ring", new String[] {"f", "y", "k", "s"}); 
      Attribute stalkSurfaceBelowRing = new Attribute("stalk-surface-below-ring", new String[] {"f", "y", "k", "s"}); 
      Attribute stalkColorAboveRing = new Attribute("stalk-color-above-ring", new String[] {"n", "b", "c", "g", "o", "p", "e", "w", "y"}); 
      Attribute stalkColorBelowRing = new Attribute("stalk-color-below-ring", new String[] {"n", "b", "c", "g", "o", "p", "e", "w", "y"}); 
      Attribute veilType = new Attribute("veil-type", new String[] {"p", "u"}); 
      Attribute veilColor = new Attribute("veil-color", new String[] {"n", "o", "w", "y"}); 
      Attribute ringNumber = new Attribute("ring-number", new String[] {"n", "o", "t"}); 
      Attribute ringType = new Attribute("ring-type", new String[] {"c", "e", "f", "l", "n", "p", "s", "z"}); 
      Attribute sporePrintColor = new Attribute("spore-print-color", new String[] {"k", "n", "b", "h", "r", "o", "u", "w", "y"}); 
      Attribute population = new Attribute("population", new String[] {"a", "c", "n", "s", "v", "y"}); 
      Attribute habitat = new Attribute("habitat", new String[] {"g", "l", "m", "p", "u", "w", "d"}); 

      Attribute mushroomClass = new Attribute("label", new String[] {"p", "e"});
      partitionedData = BasicCSVData.createPartitionedCSVData("AI_Data/mushroom.data",
          new Attribute[] {capShape, capSurface, capColor, bruises, odor, gillAttachment, gillSpacing, gillSize, gillColor, stalkShape, stalkRoot,
                           stalkSurfaceAboveRing, stalkSurfaceBelowRing, stalkColorAboveRing, stalkColorBelowRing, veilType, veilColor, ringNumber,
                           ringType, sporePrintColor, population, habitat}, mushroomClass, folds, true);

    } else if (dataSet.equals("car")) {
      // Car Data  
      Attribute price = new Attribute("price", new String[] {"vhigh", "high", "med", "low"}); 
      Attribute maint = new Attribute("maint", new String[] {"vhigh", "high", "med", "low"}); 
      Attribute doors = new Attribute("doors", new String[] {"2", "3", "4", "5more"});
      Attribute persons = new Attribute("persons", new String[] {"2", "4", "more"}); 
      Attribute lugBoot = new Attribute("lug_boot", new String[] {"small", "med", "big"}); 
      Attribute safety = new Attribute("safety", new String[] {"low", "med", "high"}); 

      Attribute carClass = new Attribute("label", new String[] {"unacc", "acc", "good", "vgood"});
      partitionedData = BasicCSVData.createPartitionedCSVData("AI_Data/car.data",
          new Attribute[] {price, maint, doors, persons, lugBoot, safety}, carClass, folds, true);
    }

    List<Map<String, Map<String, Integer>>> confusionMatrixList = LearningStatistics.kFoldDecisionTree(partitionedData, instrumentation);
    Map<String, Map<String, Double>> avgMatrix = LearningStatistics.avgConfusionMatrix(confusionMatrixList, partitionedData[0].getClassification());

    double avgAccuracy1 = LearningStatistics.calculateAccuracyD(avgMatrix);
    double variance1 = LearningStatistics.calculateVariance(confusionMatrixList, avgAccuracy1);
    double stdErr = LearningStatistics.calculateStandardError(variance1, folds);
    System.out.println("Decision Tree Accuracy = " + avgAccuracy1 + ", Variance = " + variance1 + ", Standard Error = " + stdErr);
    LearningStatistics.printConfusionMatrix(avgMatrix);

    confusionMatrixList = LearningStatistics.kFoldNaiveBayes(partitionedData, instrumentation);
    avgMatrix = LearningStatistics.avgConfusionMatrix(confusionMatrixList, partitionedData[0].getClassification());

    double avgAccuracy2 = LearningStatistics.calculateAccuracyD(avgMatrix);
    double variance2 = LearningStatistics.calculateVariance(confusionMatrixList, avgAccuracy2);
    stdErr = LearningStatistics.calculateStandardError(variance2, folds);
    System.out.println("\nNaive Bayes Accuracy = " + avgAccuracy2 + ", Variance = " + variance2 + ", Standard Error = " + stdErr);
    LearningStatistics.printConfusionMatrix(avgMatrix);

    LearningStatistics.calculateTStat(folds, avgAccuracy1, variance1, folds, avgAccuracy2, variance2);
  }
}
