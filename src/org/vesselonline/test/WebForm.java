package org.vesselonline.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents an HTML form as described in the <code>jWebUnitSiteDescription</code>
 * XML file.
 * @author Steven Handy
 * @version 1.0
 * @see WebResource
 */
public class WebForm {
  private String id;
  private String testFixture;
  private HashMap<String, String> text = new HashMap<String, String>(4);
  private boolean checkCookie;

  /**
   * Constructor for primary form attributes.
   * @param id  The value of the HTML id attribute for this form.
   * @param testFixture  The test fixture containing the test cases for this form.
   *                     From the <code>jWebUnitTestSuite</code> XML file.
   * @param checkCookie  Indicates if submissions to this form require authentication.
   */
  public WebForm(String id, String testFixture, boolean checkCookie) {
    this.id = id;
    this.testFixture = testFixture;
    this.checkCookie = checkCookie;
  }

  /**
   * Gets the form's HTML id.
   * @return Form id.
   */
  public String getId() { return id; }

  /**
   * Gets the form's associated test fixture.
   * @return Test fixture name.
   */
  public String getTestFixture() { return testFixture; }

  /**
   * Whether the form requires authentication for submissions.
   * @return Should cookies be checked for this form.
   */
  public boolean isCheckCookie() { return checkCookie; }

  /**
   * Gets the set of input fields of type text that constitute this form.
   * @return <code>Set</code> of <code>Map.Entry</code> objects containing the text
   *         input fields.  The format of the entry is key=name, value=default text.
   */
  public Set<Map.Entry<String, String>> getText() { return text.entrySet(); }

  /**
   * Adds a new text input field to this form.
   * @param name  Name of the text input field.
   * @param txt  Default text, if any, for the input field.
   */
  public void addText(String name, String txt) { text.put(name, txt); }
}
