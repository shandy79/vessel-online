package org.vesselonline.ai.learning.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

public class AfCSDataTransformer {
  private String fileName;
  private SortedMap<Integer, AfCSColumn> columnMap;
  private int binCount;

  private Attribute[] attributes;
  private Attribute classification;

  public AfCSDataTransformer(String fileName, SortedMap<Integer, AfCSColumn> columnMap, int binCount) throws IOException {
    this.fileName = fileName;
    this.columnMap = columnMap;
    this.binCount = binCount;

    importAfCSFile();
  }

  public Attribute[] getAttributes() { return attributes; }

  // Assumes final column is classification, builds domain similar to text type columns
  public Attribute getClassification() { return classification; }

  private void importAfCSFile() throws IOException {
    List<String[]> tsvData = new ArrayList<String[]>();

    BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
    String line = null;
    String[] valueAry;

    // Read in data, establish domain or range for values
    while((line = bufferedReader.readLine()) != null) {
      valueAry = line.split("\t");

      // If GeneName is for a control, ignore the line
      if (valueAry[16].startsWith("\"Fiducial") || valueAry[16].startsWith("\"Agilent")) {
        continue;
      }

      for (Integer col : columnMap.keySet()) {
        valueAry[col] = valueAry[col].replaceAll("\"", "");
        columnMap.get(col).updateRange(valueAry[col]);
      }

      tsvData.add(valueAry);
    }

    bufferedReader.close();

    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName.substring(0, fileName.lastIndexOf('.')) + ".csv"));
    int binNumber;
    String boolValue;

    for (int i = 0; i < tsvData.size(); i++) {
      for (Integer col : columnMap.keySet()) {
        if (col == columnMap.lastKey()) {
          bufferedWriter.write(tsvData.get(i)[col]);
          columnMap.get(col).addToDomain(tsvData.get(i)[col]);
        } else if (columnMap.get(col).getType() == AfCSColumn.TEXT_TYPE) {
          bufferedWriter.write(tsvData.get(i)[col] + ",");
          columnMap.get(col).addToDomain(tsvData.get(i)[col]);
        // For BOOLEAN columns, use values true and false, rather than 0 and 1
        } else if (columnMap.get(col).getType() == AfCSColumn.BOOLEAN_TYPE) {
          boolValue = (tsvData.get(i)[col].equals("0")) ? "false" : "true";
          bufferedWriter.write(boolValue + ",");
          columnMap.get(col).addToDomain(boolValue);
        } else {
          binNumber = getUniformDistributionIndex(tsvData.get(i)[col], columnMap.get(col).getMinValue(),
                  columnMap.get(col).getMaxValue());
          bufferedWriter.write(binNumber + ",");
          columnMap.get(col).addToDomain("" + binNumber);
        }
      }

      bufferedWriter.write("\n");
    }

    bufferedWriter.close();

    classification = new Attribute(columnMap.get(columnMap.lastKey()).getName(),
            columnMap.get(columnMap.lastKey()).getDomain().toArray(new String[0]));

    attributes = new Attribute[columnMap.size() - 1];
    int i = 0;
    for (Integer col : columnMap.keySet()) {
      if (col == columnMap.lastKey()) {
        break;
      } else {
        attributes[i] = new Attribute(columnMap.get(col).getName(), columnMap.get(col).getDomain().toArray(new String[0]));
      }

      i++;
    }
  }

  // Use bin values of 1 to binCount, rather than 0 to binCount-1
  private int getUniformDistributionIndex(String value, double minValue, double maxValue) {
    double dValue = Double.parseDouble(value);
    double range = maxValue - minValue;
    double increment = range / binCount;
    double total = minValue;
    
    for (int i = 0; i < binCount; i++) {
      total += increment;
      if (dValue < total) {
        return i + 1;
      }
    }

    return binCount;
  }
}
