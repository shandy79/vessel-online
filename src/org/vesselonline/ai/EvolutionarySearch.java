package org.vesselonline.ai;

import org.vesselonline.ai.util.AIUtilities;
import org.vesselonline.ai.util.Instrumentable;
import org.vesselonline.ai.util.Instrumentation;

public class EvolutionarySearch implements Instrumentable {
  private String targetStr;
  private Instrumentation instrumentation;

  private int[] targetStrScoreAry;
  private int adjustmentFactor;

  private PopulationMember[] population;
  private PopulationMember[] children;
  private double crossoverRate;
  private double mutationRate;

  private static final String[] GUESS_STR_ARY = { "YHGFREK", "UNHCXSS", "RGVBGWQ", "POKYHND", "YBGRFDW",
                                                  "LMBGTRD", "KULFDEW", "KUJQKDS", "EJUHNSH", "UHYHFWG" };
  private static final double DEFAULT_CROSSOVER = 0.75;
  private static final double DEFAULT_MUTATION = 0.25;

  private EvolutionarySearch(String targetStr, double crossoverRate, double mutationRate, Instrumentation instrumentation) {
    this.targetStr = targetStr;
    this.crossoverRate = crossoverRate;
    this.mutationRate = mutationRate;
    this.instrumentation = instrumentation;

    // Maximum error given the target string
    adjustmentFactor = (27 - 1) * targetStr.length();

    targetStrScoreAry = new int[targetStr.length()];
    for (int i = 0; i < targetStr.length(); i++) {
      targetStrScoreAry[i] = getCharScore(targetStr.charAt(i));
      getInstrumentation().print(targetStr.charAt(i) + " " + targetStrScoreAry[i]);
    }
    getInstrumentation().print("");
  }

  public EvolutionarySearch(String targetStr, String[] initialPopulation,  double crossoverRate, double mutationRate, Instrumentation instrumentation) {
    this(targetStr, crossoverRate, mutationRate, instrumentation);

    population = new PopulationMember[initialPopulation.length];
    children = new PopulationMember[initialPopulation.length];

    for (int i = 0; i < initialPopulation.length; i++) {
      population[i] = new PopulationMember(initialPopulation[i]);
    }
  }

  public String getTargetStr() { return targetStr; }
  public PopulationMember[] getPopulation() { return population; }
  public double getCrossoverRate() { return crossoverRate; }
  public double getMutationRate() { return mutationRate; }

  @Override
  public Instrumentation getInstrumentation() { return instrumentation; }
  @Override
  public void setInstrumentation(Instrumentation instrumentation) { this.instrumentation = instrumentation; }

  public void runGeneration() {
    // EVALUATION
    PopulationMember mem;
    int totalRawFitness = 0, totalAdjustedFitness = 0;
    double cumulativeProportionalFitness = 0;
    double[] distribution = new double[getPopulation().length];

    for (PopulationMember m : getPopulation()) {
      calculateFitness(m);
      totalRawFitness += m.getRawFitness();
      totalAdjustedFitness += m.getAdjustedFitness();
      getInstrumentation().print(m.getValue() + " | " + m.getRawFitness() + " | " + m.getAdjustedFitness());
    }

    getInstrumentation().print("Total Raw: " + totalRawFitness + ", Total Adjusted: " + totalAdjustedFitness);

    for (int i = 0; i < getPopulation().length; i++) {
      mem = getPopulation()[i];
      mem.setProportionalFitness(totalAdjustedFitness);
      cumulativeProportionalFitness += mem.getProportionalFitness();
      distribution[i] = cumulativeProportionalFitness;
      getInstrumentation().print(mem.getValue() + " | " + mem.getProportionalFitness() + " | " + cumulativeProportionalFitness);
    }

    // SELECTION & CROSSOVER
    boolean isCrossover;
    int parentOneIndex, parentTwoIndex, crossoverIndex = 0;

    for (int i = 0; i < getPopulation().length; i += 2) {
      parentOneIndex = AIUtilities.getInstance().getSpecificDistributionIndex(distribution);
      parentTwoIndex = AIUtilities.getInstance().getSpecificDistributionIndex(distribution);
      isCrossover = (AIUtilities.getInstance().nextRandomDoubleFromList() < getCrossoverRate()) ? true : false;

      if (isCrossover) {
        // Subtract 1 since using boundaries rather than characters.  Need to add 1 to get correct character index for substring().
        crossoverIndex = AIUtilities.getInstance().getUniformDistributionIndex(targetStr.length() - 1) + 1;

        children[i] = new PopulationMember(getPopulation()[parentOneIndex].getValue().substring(0, crossoverIndex) +
                                           getPopulation()[parentTwoIndex].getValue().substring(crossoverIndex));
        children[i + 1] = new PopulationMember(getPopulation()[parentTwoIndex].getValue().substring(0, crossoverIndex) +
                                               getPopulation()[parentOneIndex].getValue().substring(crossoverIndex));

      } else {
        children[i] = getPopulation()[parentOneIndex];
        children[i + 1] = getPopulation()[parentTwoIndex];
      }

      getInstrumentation().print(getPopulation()[parentOneIndex].getValue() + " & " + getPopulation()[parentTwoIndex].getValue() +
                                 ": " + isCrossover + ", " + (crossoverIndex - 1));
      getInstrumentation().print("  Children: " + children[i].getValue() + " & " + children[i + 1].getValue());
      crossoverIndex = 0;
    }

    // MUTATION
    boolean isMutation;
    int mutationIndex = -1;
    char mutationChar = ' ';
    char[] mutatedValue;

    for (int i = 0; i < children.length; i++) {
      isMutation = (AIUtilities.getInstance().nextRandomDoubleFromList() < getMutationRate()) ? true : false;

      if (isMutation) {
        mutationIndex = AIUtilities.getInstance().getUniformDistributionIndex(targetStr.length());
        mutationChar = (char) (AIUtilities.getInstance().getUniformDistributionIndex(26) + 65);
        mutatedValue = children[i].getValue().toCharArray();
        mutatedValue[mutationIndex] = mutationChar;
        children[i] = new PopulationMember(new String(mutatedValue));
      }

      getInstrumentation().print(children[i].getValue() + ": " +  isMutation + ", " + mutationIndex + ", " + mutationChar);
      mutationIndex = -1;
      mutationChar = ' ';
    }
  }

  private void calculateFitness(PopulationMember popMember) {
    int rawFitness = 0;
    char c;

    for (int i = 0; i < targetStrScoreAry.length; i++) {
      c = popMember.getValue().charAt(i);
      rawFitness += Math.abs(targetStrScoreAry[i] - getCharScore(c));
//      getInstrumentation().print(targetStrScoreAry[i] + " - " + getCharScore(c) + " (" + c + ")");
    }

    popMember.setFitness(rawFitness, adjustmentFactor);
  }

  private int getCharScore(char c) {
    if (Character.isSpaceChar(c)) {
      return 27;
    } else {
      int i = c;
      return i - 64;  // A = 65 in Unicode/ASCII
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Instrumentation instr = new Instrumentation(true, System.out);
    EvolutionarySearch es = new EvolutionarySearch("LOLCATS", GUESS_STR_ARY, DEFAULT_CROSSOVER, DEFAULT_MUTATION, instr);
    es.runGeneration();
  }
}


class PopulationMember {
  private String value;
  private int rawFitness;
  private int adjustedFitness;
  private double proportionalFitness;

  PopulationMember(String value) {
    this.value = value;
    proportionalFitness = -1;
  }

  String getValue() { return value; }

  int getRawFitness() { return rawFitness; }
  int getAdjustedFitness() { return adjustedFitness; }

  void setFitness(int rawFitness, int adjustmentFactor) {
    this.rawFitness = rawFitness;
    adjustedFitness = adjustmentFactor - rawFitness;
  }

  double getProportionalFitness() { return proportionalFitness; }
  void setProportionalFitness(double totalAdjustedFitness) {
    proportionalFitness = getAdjustedFitness() / totalAdjustedFitness;
  }
}
