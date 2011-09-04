package org.vesselonline.gene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AfCS2RelNetTransformer {
  public static void transformAfCSFile(String fileName, int[] columnAry) {
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter = null;
    try {
      bufferedReader = new BufferedReader(new FileReader(fileName));
      bufferedWriter = new BufferedWriter(new FileWriter("rn_" + fileName));
      String line = null;
      String[] valueAry;

      while((line = bufferedReader.readLine()) != null) {
        valueAry = line.split("\t");

        // If GeneName is for a control or Unigene code is not set, ignore the line
        if (valueAry[16].startsWith("Fiducial") || valueAry[16].startsWith("Agilent") ||
                ! (valueAry[8].startsWith("Mm.") || valueAry[8].equals("unigene_code"))) {
          continue;
        }

        for (int col : columnAry) {
          // RelNet appears to determine the data type from the first data row's value
          valueAry[col] = valueAry[col].replaceAll("^0$", "0.0");
          bufferedWriter.write(valueAry[col] + "\t");
        }

        bufferedWriter.write("\n");
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      try {
        bufferedReader.close();
        bufferedWriter.close();
      } catch (IOException ioe) { ; }
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // Sample Name -> ProbeName, Gene Name -> unigene_code, Amount -> LogRatio
    int[] columnAry = {8, 21};  // Removed 15 from front for Sample Name

    // Files to be transformed
    String[] fileNameAry = {"MAE020701N11_sm.txt", "MAE020701N12_sm.txt", "MAE020701N13_sm.txt", "MAE020701N14_sm.txt"};

    for (String fileName : fileNameAry) {
      transformAfCSFile(fileName, columnAry);
    }
  }
}
