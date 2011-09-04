package org.vesselonline.semantic;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Based on an XSLT example included w/Saxon.
 */
public class RunSaxonTransform {
  private String xmlFile;
  private Transformer transformer;

  /**
   * Use the static TransformerFactory.newInstance() method to instantiate 
   * a TransformerFactory. The javax.xml.transform.TransformerFactory 
   * system property setting determines the actual class to instantiate. 
   */
  private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

  public RunSaxonTransform(String xmlFile, String xslFile, String... params) {
    this.xmlFile = xmlFile;

    // Use the TransformerFactory to instantiate a Transformer that will work with  
    // the stylesheet you specify. This method call also processes the stylesheet
    // into a compiled Templates object.
    try {
      transformer = TRANSFORMER_FACTORY.newTransformer(new StreamSource(xslFile));

      for (int i = 0; i < params.length; i += 2) {
        transformer.setParameter(params[i], params[i + 1]);
      }
    } catch(TransformerConfigurationException tce) {
      tce.printStackTrace();
    }
  }

  public String transformToString() {
    StringWriter writer = new StringWriter();

    try {
      transformer.transform(new StreamSource(xmlFile), new StreamResult(writer));
      return writer.toString();
    } catch(TransformerException te) {
      te.printStackTrace();
    }

    return null;
  }

  public void transformToFile(String outFile) {
    try {
      transformer.transform(new StreamSource(xmlFile),
                            new StreamResult(new FileOutputStream(outFile, false)));
    } catch(TransformerException te) {
      te.printStackTrace();
    } catch(FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    }
  }  

  public static void main(String[] args) {
    if (args.length < 3) {
      System.out.println("RunSaxonTransform ARGS: <xml file> <xsl file> <output file> [params]");
      return;
    }

    String[] prms = new String[0];
    if ((args.length - 3) > 0) {
      prms = new String[args.length - 3];
      System.arraycopy(args, 3, prms, 0, prms.length);
    }

    RunSaxonTransform rst = new RunSaxonTransform(args[0], args[1], prms);
    rst.transformToFile(args[2]);
  }
}
