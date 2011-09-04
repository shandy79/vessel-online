package org.vesselonline.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Represents the information needed to describe an HTTP session as
 * described in the <code>jWebUnitSiteDescription</code> XML file.
 * @author Steven Handy
 * @version 1.0
 * @see WebSite
 */
public class WebSession {
  private String cookieName;
  private String cookieValue;
  private String userName;
  private String passwdElem;
  private String password;
  private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

  /**
   * Constructor for full session attributes.  Prompts user in the console
   * for the value of the login password. 
   * @param cookieName  Name of the cookie that identifies the HTTP session.
   * @param cookieValue  Expected value of a valid cookie for an HTTP session.
   * @param userName  User account to utilize for sessions for this web site.
   * @param passwdElem  Name of the form text input field that will accept the password.
   * @throws IOException
   */
  public WebSession(String cookieName, String cookieValue, String userName, String passwdElem)
                   throws IOException {
    this.cookieName = cookieName;
    this.cookieValue = cookieValue;
    this.userName = userName;
    this.passwdElem = passwdElem;

    System.out.print("Please enter the password for user '" + userName + "':\t");
    password = stdin.readLine();
  }

  /**
   * Gets the name of the cookie identifying the site's HTTP session.
   * @return Name of the cookie for the site's session.
   */
  public String getCookieName() { return cookieName; }

  /**
   * Gets the expected value for a valid HTTP session for this site.  Will be set
   * if the username and password match to provide a successful login.
   * @return Value of a cookie representing a valid session for this site.
   */
  public String getCookieValue() { return cookieValue; }

  /**
   * Gets the username being utilized for testing for this site.  Must match the
   * password and cookie value to create a valid session.
   * @return Name of a user to use when testing this site.
   */
  public String getUserName() { return userName; }

  /**
   * Gets the name of the password input field that will contain the password.
   * This field should always be present when testing with <code>jWebUnit</code>,
   * but will not show up after a user is authenticated during normal usage.
   * @return Name of the password input field for this user's session.
   */
  public String getPasswdElem() { return passwdElem; }

  /**
   * Gets the password for testing as entered by the test administrator.  This value
   * is protected so as to limit exposure to external programs, however, a test account
   * should always be used, and then discarded prior to deployment.
   * @return Password for the user for testing this site. 
   */
  protected String getPassword() { return password; }
}
