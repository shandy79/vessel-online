package org.vesselonline.ai.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class AIUtilities {
  private static final AIUtilities AI_UTILITIES = new AIUtilities();
  private SecureRandom secureRNG;
  private List<Double> randomDoubleList;
  private int randomDoubleListIndex;

  private AIUtilities() {
    initRandomDoubleList();
    randomDoubleListIndex = -1;

    try {
      secureRNG = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException nsae) {
      nsae.printStackTrace();
    }
  }

  public static AIUtilities getInstance() { return AI_UTILITIES; }

  public int getUniformDistributionIndex(int size) {
    double random = nextRandomDoubleFromList();
    double increment = 1.0 / size;
    double total = 0;

    for (int i = 0; i < size; i++) {
      total += increment;
      if (random < total) {
        return i;
      }
    }

    return size - 1;
  }

  /** @param distribution Array containing incremental probabilities from 0-1 */
  public int getSpecificDistributionIndex(double[] distribution) {
    double random = nextRandomDoubleFromList();

    for (int i = 0; i < distribution.length; i++) {
      if (random < distribution[i]) {
        return i;
      }
    }

    return distribution.length - 1;
  }

  /** @return Next random int from 0 (inclusive) to max (exclusive) */
  public int nextRandomInt(int max) { return secureRNG.nextInt(max); }
  /** @return Next random double from 0.0d (inclusive) to 1.0d (exclusive) */
  public double nextRandomDouble() { return secureRNG.nextDouble(); }

  public double nextRandomDoubleFromList() {
    randomDoubleListIndex++;
    try {
      return randomDoubleList.get(randomDoubleListIndex);
    } catch (IndexOutOfBoundsException ioobe) {
      randomDoubleListIndex = 0;
      return randomDoubleList.get(randomDoubleListIndex);
    }
  }

  private void initRandomDoubleList() {
    randomDoubleList = new ArrayList<Double>();

    randomDoubleList.add(0.0003154268);
    randomDoubleList.add(0.2659188076);
    randomDoubleList.add(0.2863795152);
    randomDoubleList.add(0.7631404540);
    randomDoubleList.add(0.6562722689);
    randomDoubleList.add(0.0809623762);
    randomDoubleList.add(0.4063307727);
    randomDoubleList.add(0.7320145965);
    randomDoubleList.add(0.7164227543);
    randomDoubleList.add(0.5530682500);
    randomDoubleList.add(0.4361599619);
    randomDoubleList.add(0.0298291887);
    randomDoubleList.add(0.2978145923);
    randomDoubleList.add(0.9741973644);
    randomDoubleList.add(0.8859894155);
    randomDoubleList.add(0.9006782891);
    randomDoubleList.add(0.5753098927);
    randomDoubleList.add(0.6746316035);
    randomDoubleList.add(0.6743161762);
    randomDoubleList.add(0.4083973686);
    randomDoubleList.add(0.2354092784);
    randomDoubleList.add(0.2909661843);
    randomDoubleList.add(0.1733946074);
    randomDoubleList.add(0.7333112652);
    randomDoubleList.add(0.3236172278);
    randomDoubleList.add(0.0882079490);
    randomDoubleList.add(0.9853111263);
    randomDoubleList.add(0.9003628618);
    randomDoubleList.add(0.1993004158);
    randomDoubleList.add(0.1175715770);
    randomDoubleList.add(0.4354966725);
    randomDoubleList.add(0.4612993081);
    randomDoubleList.add(0.5171223381);
    randomDoubleList.add(0.5327264774);
    randomDoubleList.add(0.1068088119);
    randomDoubleList.add(0.1258486682);
    randomDoubleList.add(0.5345052117);
    randomDoubleList.add(0.9276948348);
    randomDoubleList.add(0.8951480598);
    randomDoubleList.add(0.3156356812);
    randomDoubleList.add(0.7807694189);
    randomDoubleList.add(0.4231621567);
    randomDoubleList.add(0.7262664465);
    randomDoubleList.add(0.6594850561);
    randomDoubleList.add(0.9483409966);
    randomDoubleList.add(0.2612223737);
    randomDoubleList.add(0.5422371402);
    randomDoubleList.add(0.4172979458);
    randomDoubleList.add(0.3397246394);
  }
}
