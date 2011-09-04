package org.vesselonline.neuralnetwork.training;

import java.util.List;

public class TrainingStatistics {
  private int iteration;
  private double error;
  private double errorChange;
  private long elapsedTime;

  public TrainingStatistics(int iteration, double error, double errorChange, long elapsedTime) {
    this.iteration = iteration;
    this.error = error;
    this.errorChange = errorChange;
    this.elapsedTime = elapsedTime;
  }

  public int getIteration() { return iteration; }
  public double getError() { return error; }
  public double getErrorChange() { return errorChange; }
  public long getElapsedTime() { return elapsedTime; }

  public String toString() { return iteration + "," + error + "," + elapsedTime; }

  // Use last sample from each run to compute average stats for a collection of runs
  public static TrainingStatistics getMeanTrainingStatistics(List<List<TrainingStatistics>> trainingStatisticsLists) {
    TrainingStatistics statsAggregator = new TrainingStatistics(0, 0, 0, 0);
    TrainingStatistics stats;

    for (List<TrainingStatistics> statsList : trainingStatisticsLists) {
      stats = statsList.get(statsList.size() - 1);
      statsAggregator.iteration += stats.getIteration();
      statsAggregator.error += stats.getError();
      statsAggregator.elapsedTime += stats.getElapsedTime();
    }

    statsAggregator.iteration /= trainingStatisticsLists.size();
    statsAggregator.error /= trainingStatisticsLists.size();
    statsAggregator.elapsedTime /= trainingStatisticsLists.size();

    return statsAggregator;
  }
}
