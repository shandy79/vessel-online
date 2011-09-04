package org.vesselonline.ai.learning.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicCSVData implements CSVData {
  private List<String[]> data;
  private Attribute[] attributes;
  private Attribute classification;

  private BasicCSVData(Attribute[] attributes, Attribute classification) {
    this.attributes = attributes;
    this.classification = classification;
  }

  public BasicCSVData(String fileName, Attribute[] attributes, Attribute classification) throws IOException {
    this(attributes, classification);
    this.data = importCSVFile(fileName);
  }

  public BasicCSVData(List<String[]> data, Attribute[] attributes, Attribute classification) {
    this(attributes, classification);
    this.data = data;
  }

  @Override
  public List<String[]> getData() { return data; }

  @Override
  public Attribute[] getAttributes() { return attributes; }

  @Override
  public Attribute getClassification() { return classification; }

  @Override
  public int getAttributeCount() { return attributes.length; }

  private static final List<String[]> importCSVFile(String fileName) throws IOException {
    List<String[]> csvData = new ArrayList<String[]>();

    BufferedReader bufferedReader  = new BufferedReader(new FileReader(fileName));
    String line = null;

    while((line = bufferedReader.readLine()) != null) {
      csvData.add(line.split(","));
    }

    bufferedReader.close();
    return csvData;
  }

  public static final CSVData[] createPartitionedCSVData(String fileName, Attribute[] attributes, Attribute classification,
                                                         int count, boolean randomize) {
    CSVData[] dataObjects = new BasicCSVData[count];
    List<List<String[]>> dataPartitions = new ArrayList<List<String[]>>(count);
    for (int i = 0; i < count; i++) {
      dataPartitions.add(new ArrayList<String[]>());
    }
    int pIdx = 0;

    try {
      List<String[]> allData = importCSVFile(fileName);

      if (randomize) {
        Collections.shuffle(allData, SecureRandom.getInstance("SHA1PRNG"));
      }

      for (int i = 0; i < allData.size(); i++) {
        if (pIdx >= count) {
          pIdx = 0;
        }

        dataPartitions.get(pIdx).add(allData.get(i));
        pIdx++;
      }

      for (int i = 0; i < count; i++) {
        dataObjects[i] = new BasicCSVData(dataPartitions.get(i), attributes, classification);
      }

    } catch (NoSuchAlgorithmException nsae) {
      nsae.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    return dataObjects;
  }
}
