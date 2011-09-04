package org.vesselonline.semantic.ws.ols;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GOMashup {
  public static final String SALMON_URL = "http://genomicasalmones.dim.uchile.cl:9002/biomart/martservice";
  private static final String SALMON_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE Query>" +
  "<Query virtualSchemaName=\"default\" formatter=\"HTML\" header=\"0\" uniqueRows=\"1\" count=\"\" datasetConfigVersion=\"0.6\">" +
  "<Dataset name=\"gene\" interface=\"default\">" +
  "<Filter name=\"!_GO_TYPE_!\" value=\"!_GO_TERM_!\"/>" +  //<Filter name="gobpro_id_1010|gocc_id_1011|gomolf_id_1012" value=""/>
  "<Attribute name=\"locus_tag\"/><Attribute name=\"product\"/><Attribute name=\"interpro_desc_1013\"/><Attribute name=\"interpro_id_1013\"/>" +
  "</Dataset></Query>";

  public static final String WORM_URL = "http://caprica.caltech.edu:9002/biomart/martservice";
  private static final String WORM_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE Query>" +
  "<Query virtualSchemaName=\"WS220-bugFix\" formatter=\"HTML\" header=\"0\" uniqueRows=\"1\" count=\"\" datasetConfigVersion=\"0.6\">" +
  "<Dataset name=\"wormbase_gene\" interface=\"default\">" +
  "<Filter name=\"go_term\" value=\"!_GO_TERM_!\"/><Filter name=\"species_selection\" value=\"Caenorhabditis elegans\"/>" +
  "<Filter name=\"identity_status\" value=\"Live\"/>" +
  "<Attribute name=\"gene\"/><Attribute name=\"public_name\"/><Attribute name=\"concise_description\"/><Attribute name=\"protein_id\"/>" +
  "</Dataset></Query>";

  private static final String GO_TERM_KEY = "!_GO_TERM_!";
  private static final String GO_TYPE_KEY = "!_GO_TYPE_!";

  private static final String XML_FILE = "mashup.xml";
  private static final String XSL_FILE = "mashup.xsl";
  private static final String HTML_FILE = "mashup.html";

  public static final int GO_BIOLOGICAL_PROCESS_TYPE = 1;
  public static final int GO_CELLULAR_COMPONENT_TYPE = 2;
  public static final int GO_MOLECULAR_FUNCTION_TYPE = 3;

  private String goTerm;
  private int goType;
  private String olsResults;
  private String salmonXML;
  private String salmonResults;
  private String wormXML;
  private String wormResults;

  public GOMashup(String goTerm, int goType) {
    this.goTerm = goTerm;
    this.goType = goType;
  }

  public String getGOTerm() { return this.goTerm; }
  public void setGOTerm(String goTerm) { this.goTerm = goTerm; }

  public int getGOType() { return this.goType; }
  public void setGOType(int goType) { this.goType = goType; }

  public String getOLSResults() { return this.olsResults; }

  public String getSalmonXML() { return this.salmonXML; }

  public String getSalmonResults() { return this.salmonResults; }

  public String getWormXML() { return this.wormXML; }

  public String getWormResults() { return this.wormResults; }

  public void runQuery() {
    initQueryXML();
    runOLSClient();

    BioMartRESTClient salmonClient = new BioMartRESTClient(SALMON_URL);
    salmonResults = salmonClient.runQuery(getSalmonXML());

    BioMartRESTClient wormClient = new BioMartRESTClient(WORM_URL);
    wormResults = wormClient.runQuery(getWormXML());

    createXMLResults();

    RunSaxonTransform rst = new RunSaxonTransform(XML_FILE, XSL_FILE);
    rst.transformToFile(HTML_FILE);
  }

  private void initQueryXML() {
    salmonXML = SALMON_XML.replace(GO_TERM_KEY, getGOTerm());

    if (getGOType() == GO_CELLULAR_COMPONENT_TYPE) {
      salmonXML = salmonXML.replace(GO_TYPE_KEY, "gocc_id_1011");
    } else if (getGOType() == GO_MOLECULAR_FUNCTION_TYPE) {
      salmonXML = salmonXML.replace(GO_TYPE_KEY, "gomolf_id_1012");
    } else {  // getGOType() == GO_BIOLOGICAL_PROCESS_TYPE
      salmonXML = salmonXML.replace(GO_TYPE_KEY, "gobpro_id_1010");
    }

    wormXML = WORM_XML.replace(GO_TERM_KEY, getGOTerm());
  }

  private void createXMLResults() {
    StringBuilder xmlResults = new StringBuilder();
    xmlResults.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MashupResponse>\n");
    xmlResults.append(getOLSResults());
    xmlResults.append("<SalmonDB>\n" + getSalmonResults() + "\n</SalmonDB>\n");
    xmlResults.append("<WormBase>\n" + getWormResults() + "\n</WormBase>\n");
    xmlResults.append("</MashupResponse>\n");

    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(XML_FILE));
      out.write(xmlResults.toString());
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      try { if (out != null) out.close(); } catch (IOException ioe) { ; }
    }
  }

  // ProcessBuilder usage based on code from:
  // http://www.rgagnon.com/javadetails/java-0014.html
  private void runOLSClient() {
    String jarDir = "/home/shandy/workspace/jhu_wsOLS/build/lib/";
    InputStream inputStream = null;
    BufferedReader bufferedReader = null;
    StringBuilder results = new StringBuilder();

    try {
      List<String> command = new ArrayList<String>();
      command.add("/bin/sh");
      command.add("-c");
      command.add(System.getenv("AXIS2_HOME") +"/bin/axis2.sh -cp \"QueryService-test-client.jar:XBeans-packaged.jar\" " +
              "org.vesselonline.semantic.ws.ols.Client " + getGOTerm());

      ProcessBuilder builder = new ProcessBuilder(command);
      builder.directory(new File(jarDir));
      Process process = builder.start();
      inputStream = process.getInputStream();
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (! (line.startsWith(" Using") || line.startsWith("[INFO]"))) {
          results.append(line + "\n");
        }
      }

      olsResults = results.toString();      
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      try {
        if (bufferedReader != null) bufferedReader.close();
        if (inputStream != null) inputStream.close();
      } catch (IOException ioe) { ; }
    }
  }

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("GOMashup ARGS: <GO ID> <GO Type (1=Bio Process, 2=Cell Component, 3=Molecular Fn)>");
      return;
    }

    String goTerm = args[0];  //"GO:0006914";
    int goType = Integer.parseInt(args[1]);  //GO_BIOLOGICAL_PROCESS_TYPE;

    GOMashup goMashup = new GOMashup(goTerm, goType);
    goMashup.runQuery();
  }
}
