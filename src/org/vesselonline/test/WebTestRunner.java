package org.vesselonline.test;

import java.io.*;
import java.util.*;
import javax.xml.xpath.XPathExpressionException;
import junit.framework.AssertionFailedError;
import net.sourceforge.jwebunit.junit.WebTestCase;
import org.w3c.dom.*;

/**
 * Provides an adapter between the objects in this package and the
 * assertions provided by <code>jWebUnit</code>.  Has facilities for
 * testing at the resource, form, or test case level. 
 * @author Steven Handy
 * @version 1.0
 * @see WebSite
 * @see WebResource
 * @see WebForm
 * @see WebSession
 * @see XmlUtils
 */
public class WebTestRunner extends WebTestCase {
  private WebSite ws;
  private PrintWriter pw;
  private XmlUtils xu;

  /**
   * Constructor sets the test runner's web site and print writer
   * attributes.  Also establishes the <code>XmlUtils</code> object.
   * @param ws  Web site upon which tests shall be run.
   * @param pw  Output writer for the reporting mechanisms.
   * @see WebSite
   * @see XmlUtils
   */
  public WebTestRunner(WebSite ws, PrintWriter pw) {
    super(ws.getName());
    this.ws = ws;
    this.pw = pw;

    try {
      this.xu = new XmlUtils(ws.getTestSuite());
    } catch (Exception e) {
      handleException(e);
    }
    // Javascript support has not been added, so disable
    // its execution for this web client.
    setScriptingEnabled(false);
    getTestContext().setBaseUrl(ws.getBaseUrl());
  }

  /**
   * Prints information about the web site and parameters of the test execution.
   */
  public void printReportHeader() {
    pw.println("WEBSITE REPORT FOR " + ws.getXmlSiteFile());
    pw.println("Name:\t\t" + ws.getName());
    pw.println("Base URL:\t" + ws.getBaseUrl());
    pw.println("Test Suite:\t" + ws.getTestSuite());
    pw.println("User Name:\t" + ws.getSession().getUserName());
    pw.print("Frames:\t");
    for (Map.Entry<String, String> me : ws.getFrames()) {
      pw.print("\t" + me.getKey());
    }
    pw.println();
    pw.println("Test executed at " + new Date().toString() + ".");
    pw.println();
  }

  /**
   * Using the test suite assigned to the web site, runs the test fixture and its
   * test cases for the specified resource.
   * @param resName  Name identifying a <code>WebResource</code> upon which to
   *                 run its associated test cases.
   */
  public void testResource(String resName) {
    WebResource wr = ws.getResource(resName);
    pw.println("RESOURCE: " + wr.getName() + " (" + wr.getUrl() + ")");

    try {
      long start = new Date().getTime();
      beginAt(wr.getUrl());

      // Evaluate template for the frame for this resource
      pw.println("Testing template for frame '" + wr.getFrame() + "' . . .");
      // Get the test cases for this resource
      NodeList nodeList = xu.getNodeList("/jWebUnitTestSuite/testFixture[name='" +
                                         ws.getFrameFixture(wr.getFrame()) + "']/testCase[name='" +
                                         wr.getFrame() + "']/output/*", xu.getDoc());
      for (int i = 0; i < nodeList.getLength(); i++) {
        selectAssertions(nodeList.item(i));
      }

      // Evaluate constant elements of this resource
      pw.println("Testing constant elements . . .");
      // Assert H1 elements for the resource
      if (wr.getH1() != null && ! wr.getH1().equals("")) {
        selectAssertions("h1", wr.getH1(), true, null);
      }
      // Assert H2 elements for the resource
      for (String h2 : wr.getH2s()) {
        selectAssertions("h2", h2, true, null);
      }
      // Assert IMG elements for the resource
      for (String img : wr.getImgs()) {
        selectAssertions("img", img, true, null);
      }
      // Assert TEXT elements for the resource
      for (String txt : wr.getText()) {
        selectAssertions("text", txt, true, null);
      }
      // Assert LINK elements for the resource
      for (Map.Entry<String, String> lnk : wr.getLinks()) {
        selectAssertions("link", lnk.getKey(), true, lnk.getValue());
      }
      // Assert TABLE elements for the resource
      for (Map.Entry<String, String[]> tbl : wr.getTables()) {
        pw.println("  TABLE[" + tbl.getKey() + "]:" + Arrays.toString(tbl.getValue()));
        assertTable(tbl.getKey(), true, tbl.getValue());
        pw.println("    Present . . . passed!");
      }
      // Run tests on the FORMs for the resource
      for (Map.Entry<String, WebForm> frm : wr.getForms()) {
        testForm(frm.getValue(), wr.getUrl());
      }

      double dur = (new Date().getTime() - start) / 1000.0;
      pw.println("All tests passed in " + dur + "s.");
      pw.println();
    } catch (AssertionFailedError afe) {
      handleError(afe);
    } catch (XPathExpressionException xpee) {
      handleException(xpee);
    }
  }

  private void testForm(WebForm frm, String url) throws AssertionFailedError, XPathExpressionException {
    selectAssertions("form", frm.getId(), true, null);
    for (Map.Entry<String, String> txt : frm.getText()) {
      selectAssertions("input", txt.getValue(), true, txt.getKey());
    }

    // Run test cases from the form's test fixture
    pw.println("Running test cases for form '" + frm.getId() + "' from test fixture '" +
               frm.getTestFixture() + "' . . .");

    NodeList nodeList = xu.getNodeList("/jWebUnitTestSuite/testFixture[name='" +
                                       frm.getTestFixture() + "']/testCase", xu.getDoc());
    for (int i = 0; i < nodeList.getLength(); i++) {
      testFormTestCase(frm, nodeList.item(i));

      // Reset page and form for next test case
      beginAt(url);
      setWorkingForm(frm.getId());
    }
  }

  /**
   * Provides simplified interface to execute tests against a specific form
   * found in the current web site.  All test cases for the form will be executed.
   * Invokes the private method <code>testForm(WebForm, url)</code> to run the test cases.
   * @param res  Name of the resource in which the form is located.
   * @param frm  Name of the form on which to run test cases.
   */
  public void testForm(String res, String frm) {
    WebResource wr = ws.getResource(res);
    pw.println("RESOURCE: " + wr.getName() + " (" + wr.getUrl() + ")");
    pw.println("Form test only for '" + frm + "' . . .");

    try {
      long start = new Date().getTime();
      beginAt(wr.getUrl());
      testForm(wr.getForm(frm), wr.getUrl());
      double dur = (new Date().getTime() - start) / 1000.0;
      pw.println("All tests passed in " + dur + "s.");
      pw.println();
    } catch (AssertionFailedError afe) {
      handleError(afe);
    } catch (XPathExpressionException xpee) {
      handleException(xpee);
    }    
  }

  private void testFormTestCase(WebForm frm, Node testCase) throws AssertionFailedError, XPathExpressionException {
    Node node;
    WebSession session = ws.getSession();

    pw.println("TEST CASE:" + xu.getXPathResult("name", testCase));

    // Retrieve the input elements and set them in the form
    NodeList nodeList = xu.getNodeList("input/*", testCase);
    for (int j = 0; j < nodeList.getLength(); j++) {
      node = nodeList.item(j);
      pw.println("  SET INPUT[" + xu.getXPathResult("./@name", node) + "]:" +
                 node.getTextContent());
      setFormElement(xu.getXPathResult("./@name", node), node.getTextContent());
    }

    // Must submit password for each test case, since cookie send broken
    if (frm.isCheckCookie()) {
      selectAssertions("input", "", true, session.getPasswdElem());
      pw.println("  SET INPUT[" + session.getPasswdElem() + "]:" + session.getPassword());
      setFormElement(session.getPasswdElem(), session.getPassword());
    }

    // Submit the form
    submit();

    // Assert the output from the form execution
    pw.println("Assert expected output . . .");
    nodeList = xu.getNodeList("output/*", testCase);
    for (int j = 0; j < nodeList.getLength(); j++) {
      selectAssertions(nodeList.item(j));
    }
    // Verify cookie received from server
    if (frm.isCheckCookie()) {
      selectAssertions("cookie", session.getCookieValue(), true, session.getCookieName());
    }
  }

  /**
   * Provides simplified interface to test a single test case for a form found
   * in the current web site.  Invokes the private method
   * <code>testFormTestCase(WebForm, Node)</code> to run the test case.
   * @param res  Name of the resource in which the form is located.
   * @param frm  Name of the form on which to run the test case.
   * @param testCase  Name of the test case to be run.
   */
  public void testFormTestCase(String res, String frm, String testCase) {
    WebResource wr = ws.getResource(res);
    WebForm wf = wr.getForm(frm);
    pw.println("RESOURCE: " + wr.getName() + " (" + wr.getUrl() + ")");
    pw.println("Form test only for '" + frm + "', test case '" + testCase + "' . . .");

    try {
      long start = new Date().getTime();
      beginAt(wr.getUrl());
      Node node = xu.getNodeList("/jWebUnitTestSuite/testFixture[name='" + wf.getTestFixture() +
                                 "']/testCase[name='" + testCase + "']", xu.getDoc()).item(0);
      testFormTestCase(wf, node);
      double dur = (new Date().getTime() - start) / 1000.0;
      pw.println("All tests passed in " + dur + "s.");
      pw.println();
    } catch (AssertionFailedError afe) {
      handleError(afe);
    } catch (XPathExpressionException xpee) {
      handleException(xpee);
    }
  }

  private void selectAssertions(String name, String content, boolean present, String aux) {
    pw.print("  " + name.toUpperCase());
    if (aux != null && ! aux.equals("")) pw.print("[" + aux + "]");
    pw.println(":" + content);

    if (name.equals("title")) {
      assertTitleEquals(content);
    } else if (name.equals("h1")) {
      assertText("<h1>" + content + "</h1>", present);
    } else if (name.equals("h2")) {
      assertText("<h2>" + content + "</h2>", present);
    } else if (name.equals("img")) {
      assertText("src=\"" + content + "\"", present);
    } else if (name.equals("text")) {
      assertText(content, present);
    } else if (name.equals("link")) {
      assertLink(content, present, aux);
    } else if (name.equals("form")) {
      assertForm(content, present);
    } else if (name.equals("input")) {
      assertFormElement(aux, present, content);
    } else if (name.equals("cookie")) {
      assertCookie(aux, content);
    }

    if (present) {
      pw.println("    Present . . . passed!");
    } else {
      pw.println("    Not present . . . passed!");
    }
  }

  private void selectAssertions(Node n) {
    boolean present = true;
    String name = n.getNodeName();
    String content = n.getTextContent();
    String aux = null;

    NamedNodeMap attr = n.getAttributes();
    if (attr != null) {
      if (attr.getNamedItem("present") != null &&
          attr.getNamedItem("present").getTextContent().equals("false")) {
        present = false;
      } else if (name.equals("link") && attr.getNamedItem("type") != null) {
        aux = attr.getNamedItem("type").getTextContent();
      }
    }

    selectAssertions(name, content, present, aux);
  }

  private void assertCookie(String name, String value) {
    assertCookiePresent(name);
    assertCookieValueEquals(name, value);
  }

  private void assertForm(String id, boolean present) {
    if (present) {
      assertFormPresent(id);
      setWorkingForm(id);
    } else assertFormNotPresent(id);
  }

  private void assertFormElement(String name, boolean present, String value) {
    if (present) {
      assertFormElementPresent(name);
      if (value == null || value.equals("")) assertFormElementEmpty(name);
      else assertFormElementEquals(name, value);
    } else assertFormElementNotPresent(name);
  }

  private void assertLink(String txtOrImg, boolean present, String type) {
    if (type != null && type.equals("img")) {
      if (present) assertLinkPresentWithImage(txtOrImg);
      else assertLinkNotPresentWithImage(txtOrImg);
    } else {
      if (present) assertLinkPresentWithText(txtOrImg);
      else assertLinkNotPresentWithText(txtOrImg);
    }
  }

  private void assertTable(String summary, boolean present, String[] txt) {
    if (present) {
      assertTablePresent(summary);
      if (txt != null && txt.length != 0) assertTextInTable(summary, txt);
    } else assertTableNotPresent(summary);
  }

  private void assertText(String txt, boolean present) {
    if (present) assertTextPresent(txt);
    else assertTextNotPresent(txt);
  }

  private void handleError(AssertionFailedError afe) {
    pw.println("ASSERTION FAILED: " + afe.getClass().getName());
    pw.println("MESSAGE: " + afe.getMessage());
    pw.println();
    try {
      dumpHtml(new PrintStream("error" + new Date().getTime() + ".txt"));
    } catch (FileNotFoundException fnfe) {}
  }

  private void handleException(Exception e) {
    pw.println("EXCEPTION: " + e.getClass().getName());
    pw.println();
    e.printStackTrace(pw);
    pw.println();
    try {
      dumpHtml(new PrintStream("exception" + new Date().getTime() + ".txt"));
    } catch (FileNotFoundException fnfe) {}
  }
}
