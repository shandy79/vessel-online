package org.vesselonline.test;

import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * Represents a web site, or a set of connected HTML resources.  Contains
 * the frames, HTTP session, and resources that make up the site, along
 * with supporting information about the associated test suite.
 * @author Steven Handy
 * @version 1.0
 * @see WebSession
 * @see WebResource
 * @see XmlUtils
 */
public class WebSite {
  private String xmlSiteFile;
  private String name;
  private String baseUrl;
  private String testSuite;
  private WebSession session;
  private HashMap<String, String> frames = new HashMap<String, String>(2);
  private HashMap<String, WebResource> resources = new HashMap<String, WebResource>(4);
  
  private XmlUtils xu;

  /**
   * Constructor builds the web site and associated <code>WebSession</code> and
   * <code>WebResource</code> objects that comprise the site.  A well-formed
   * <code>jWebUnitSiteDescription</code> XML file is required.
   * <br />&nbsp;<br />
   * This constructor makes heavy use of the XPath expression processing provided
   * by the <code>XmlUtils</code> object created for this site instance.
   * @param xmlSiteFile
   * @throws ParserConfigurationException
   * @throws XPathExpressionException
   * @throws SAXException
   * @throws IOException
   * @see WebResource
   * @see WebSession
   * @see XmlUtils
   */
  public WebSite(String xmlSiteFile) throws ParserConfigurationException, XPathExpressionException,
                                            SAXException, IOException {
    this.xmlSiteFile = xmlSiteFile;
    this.xu = new XmlUtils(xmlSiteFile);

    Node node, child, child2, child3;
    NodeList nlist, nlist2, nlist3;
    WebResource res;
    WebForm form;
    
    name = xu.getXPathResult("/jWebUnitSiteDescription/name", xu.getDoc());
    baseUrl = xu.getXPathResult("/jWebUnitSiteDescription/baseUrl", xu.getDoc());
    testSuite = xu.getXPathResult("/jWebUnitSiteDescription/testSuite", xu.getDoc());
    // Create WebSession object
    node = (Node)xu.getXPath().evaluate("/jWebUnitSiteDescription/session", xu.getDoc(), XPathConstants.NODE);
    if (node != null) {
      session = new WebSession(xu.getXPathResult("cookieName", node), xu.getXPathResult("cookieValue", node),
                               xu.getXPathResult("userName", node), xu.getXPathResult("passwdElem", node));
    }
    // Create frames HashMap
    nlist = xu.getNodeList("/jWebUnitSiteDescription/frames/frame", xu.getDoc());
    for (int i = 0; i < nlist.getLength(); i++) {
      child = nlist.item(i);
      frames.put(child.getTextContent(), xu.getXPathResult("./@testFixture", child));
    }
    // Create WebResources
    nlist = xu.getNodeList("/jWebUnitSiteDescription/resources/resource", xu.getDoc());
    for (int i = 0; i < nlist.getLength(); i++) {
      child = nlist.item(i);
      res = new WebResource(xu.getXPathResult("name", child), xu.getXPathResult("url", child),
                            xu.getXPathResult("frame", child), xu.getXPathResult("h1", child));
      // Add H2 elements for the resource
      nlist2 = xu.getNodeList("h2", child);
      for (int j = 0; j < nlist2.getLength(); j++) {
        res.addH2(nlist2.item(j).getTextContent());
      }
      // Add IMG elements for the resource
      nlist2 = xu.getNodeList("img", child);
      for (int j = 0; j < nlist2.getLength(); j++) {
        res.addImg(nlist2.item(j).getTextContent());
      }
      // Add TEXT elements for the resource
      nlist2 = xu.getNodeList("text", child);
      for (int j = 0; j < nlist2.getLength(); j++) {
        res.addText(nlist2.item(j).getTextContent());
      }
      // Add LINK elements for the resource
      nlist2 = xu.getNodeList("link", child);
      for (int j = 0; j < nlist2.getLength(); j++) {
        child2 = nlist2.item(j);
        res.addLink(child2.getTextContent(), xu.getXPathResult("./@type", child2));
      }
      // Add TABLE elements for the resource
      nlist2 = xu.getNodeList("table", child);
      for (int j = 0; j < nlist2.getLength(); j++) {
        child2 = nlist2.item(j);
        // Add TEXT elements for the table
        nlist3 = xu.getNodeList("text", child2);
        String[] ary = new String[nlist3.getLength()];
        for (int k = 0; k < nlist3.getLength(); k++) {
          ary[k] = nlist3.item(k).getTextContent();
        }
        
        res.addTable(xu.getXPathResult("summary", child2), ary);
      }
      // Add FORM elements for the resource
      nlist2 = xu.getNodeList("forms/form", child);
      for (int j = 0; j < nlist2.getLength(); j++) {
        child2 = nlist2.item(j);
        form = new WebForm(xu.getXPathResult("id", child2), xu.getXPathResult("testFixture", child2),
                           Boolean.parseBoolean(xu.getXPathResult("./@checkCookie", child2)));
        // Add INPUT elements for the form
        nlist3 = xu.getNodeList("input/text", child2);
        for (int k = 0; k < nlist3.getLength(); k++) {
          child3 = nlist3.item(k);
          form.addText(xu.getXPathResult("./@name", child3), child3.getTextContent());
        }
        
        res.addForm(form);
      }
      
      resources.put(res.getName(), res);
    }
  }

  /**
   * Gets the name of the web site described in the <code>jWebUnitSiteDescription</code>
   * XML file.
   * @return Name of the web site described by this instance.
   */
  public String getName() { return name; }

  /**
   * Gets the base URL of the web site described in the
   * <code>jWebUnitSiteDescription</code> XML file.
   * @return Base URL of the web site described by this instance.
   */
  public String getBaseUrl() { return baseUrl; }

  /**
   * Gets the name of the <code>jWebUnitSiteDescription</code> XML file that
   * contains the metadata used to construct this instance of a web site.
   * @return Name of the file that serves as the basis for this instance.
   */
  public String getXmlSiteFile() { return xmlSiteFile; }

  /**
   * Gets the name of the <code>jWebUnitTestSuite</code> XML file that contains
   * the metadata used to run tests on this web site.
   * @return Name of the file that provides test inputs and outputs for this instance. 
   */
  public String getTestSuite() { return testSuite; }

  /**
   * Gets a <code>WebSession</code> object representing the HTTP session attributes
   * for a valid user of this web site.
   * @return <code>WebSession</code> instance for the web site.
   * @see WebSession
   */
  public WebSession getSession() { return session; }

  /**
   * Gets the set of frames that may exist in the HTML structure for this web site.
   * @return <code>Set</code> of <code>Map.Entry</code> objects containing
   *         information about frames identified for this web site.  The format of
   *         the entry is key=name, value=test fixture for the frame.
   */
  public Set<Map.Entry<String, String>> getFrames() { return frames.entrySet(); }

  /**
   * Gets the test fixture that is defined for the frame.  The test case within that
   * fixture will have the same name as the frame.  That test case is used to define
   * elements that should always be present for that frame, regardless of the resource
   * being displayed.
   * @param frName  Name of the frame for which to obtain the test fixture.
   * @return Name of the test fixture for the specified frame.
   */
  public String getFrameFixture(String frName) { return frames.get(frName); }

  /**
   * Gets the set of <code>WebResource</code>s that make up this web site.
   * @return <code>Set</code> of <code>Map.Entry</code> objects containing the resources
   *         that comprise this web site.  The format of the entry is key=name,
   *         value=<code>WebResource</code> identified by that name
   * @see WebResource
   */
  public Set<Map.Entry<String, WebResource>> getResources() { return resources.entrySet(); }

  /**
   * Gets an individual <code>WebResource</code> from the site based on its name.
   * @param resName  Name of a <code>WebResource</code> to retrieve.
   * @return <code>WebResource</code> identified by the specified name.
   * @see WebResource
   */
  public WebResource getResource(String resName) { return resources.get(resName); }
}
