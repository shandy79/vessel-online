package org.vesselonline.test;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Provides XML-related services to the <code>org.vesselonline.test</code> package.
 * Deals primarily with XPath expressions, utilizing the JAXP 1.3 specification.
 * @author Steven Handy
 * @version 1.0
 * @see WebTestRunner
 * @see WebSite
 */
public class XmlUtils {
  private Document doc;
  private XPath xpath;

  /**
   * Class constuctor, requires a well-formed XML file name on which to base the XML
   * operations provided by this instance.  Establishes the DOM Node for the document
   * root and the XPath object needed to provide XPath services to this instance.
   * @param xmlFile  The name of a well-formed XML file that will serve as the basis
   *                 for this instance's operations. 
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   */
  public XmlUtils(String xmlFile) throws ParserConfigurationException, SAXException, IOException  {
    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    doc = parser.parse(new File(xmlFile));
    xpath = XPathFactory.newInstance().newXPath();
  }

  /**
   * Returns the DOM Node object representing this instance's XML file. 
   * @return DOM Node as a <code>Document</code> object.
   */
  public Document getDoc() { return doc; }

  /**
   * Returns the XPath object that processes XPath expressions for this instance.
   * @return XPath object.
   */
  public XPath getXPath() { return xpath; }

  /**
   * Evaluates an XPath expression against a DOM Node.  Used when needing
   * the text content of a specific element or attribute node.
   * @param expr  An XPath expression returning an element or attribute node.
   * @param context  The context node upon which to apply the XPath expression.
   * @return The text content of the selected node.
   * @throws XPathExpressionException
   */
  public String getXPathResult(String expr, Object context) throws XPathExpressionException {
    Node node = (Node)xpath.evaluate(expr, context, XPathConstants.NODE);
    if (node == null) return null;
    else return node.getTextContent();
  }

  /**
   * Evaluates an XPath expression against a DOM Node to create a NodeSet.  Used
   * when needing a list of child nodes of the context node.
   * @param expr  An XPath expression returning zero or more element nodes.
   * @param context  The context node upon which to apply the XPath expression.
   * @return The elements selected by the XPath expression.
   * @throws XPathExpressionException
   */
  public NodeList getNodeList(String expr, Object context) throws XPathExpressionException {
    return (NodeList)xpath.evaluate(expr, context, XPathConstants.NODESET);
  }
}
