package org.vesselonline.test;

import java.util.*;

/**
 * Represents a single web resource, typically defined by the HTML you would
 * receive by requesting the resource's URL with a web client.  Contains
 * representations for various HTML elements, including tables and forms,
 * that might make up such a page.
 * @author Steven Handy
 * @version 1.0
 * @see WebSite
 * @see WebForm
 */
public class WebResource {
  private String name;
  private String frame;
  private String url;
  private String h1;
  private ArrayList<String> h2s = new ArrayList<String>(2);
  private ArrayList<String> imgs = new ArrayList<String>(2);
  private ArrayList<String> text = new ArrayList<String>(2);
  private HashMap<String, String> links = new HashMap<String, String>(4);
  private HashMap<String, String[]> tables = new HashMap<String, String[]>(2);
  private HashMap<String, WebForm> forms = new HashMap<String, WebForm>(2); 

  /**
   * Constructor creates a new <code>WebResource</code> with the basic information
   * provided.  Descriptions for a new resource are defined in the
   * <code>jWebUnitSiteDescription</code> XML file.
   * @param name  Name of the web resource.
   * @param url  URL defining the location of the web resource, relative to the base
   *             URL of the web site this resource is a part of.  May contain a query
   *             string in addition to the resource path and file name. 
   * @param frame  Name of the frame this resource appears in on the web site.
   * @param h1  Top-level heading for the resource, if one exists.
   */
  public WebResource(String name, String url, String frame, String h1) {
    this.name = name;
    this.url = url;
    this.frame = frame;
    this.h1 = h1;
  }

  /**
   * Gets the name assigned to this resource.  This is an arbitrary
   * designation, but must be unique among the resources for the
   * web site.
   * @return Name of the resource.
   */
  public String getName() { return name; }

  /**
   * Gets the name of the frame in which this resource appears in
   * the web site.  The frame is used to derive the "template" set
   * of tests for all resources that appear in that frame.
   * @return Name of the frame in which this resource appears.
   */
  public String getFrame() { return frame; }

  /**
   * Gets the URL for the resource.  The URL is relative to the
   * base URL for the web site and may include a query string.
   * @return URL as a string for the resource.
   */
  public String getUrl() { return url; }

  /**
   * Gets the top-level heading for the resource.  This field may
   * not be present.
   * @return Text of the top-level heading for the resource.
   */
  public String getH1() { return h1; }

  /**
   * Gets a collection of all <code>h2</code> elements defined for the resource.
   * @return <code>ArrayList</code> of strings, each item containing
   *         the text of an <code>h2</code> element. 
   */
  public ArrayList<String> getH2s() { return h2s; }

  /**
   * Gets a collection of all <code>img</code> elements defined for the resource.
   * @return <code>ArrayList</code> of strings, each item containing
   *         the URL of an <code>img</code> element.
   */
  public ArrayList<String> getImgs() { return imgs; }

  /**
   * Gets a collection of all the text strings that should be verified to be
   * present in this resource.  The strings may contain HTML markup.
   * @return <code>ArrayList</code> of strings, each item containing
   *         text to be verified in the HTML of the resource.
   */
  public ArrayList<String> getText() { return text; }

  /**
   * Gets the set of HTML links that are defined for this resource. Valid
   * types of links include "txt" and "img".
   * @return <code>Set</code> of <code>Map.Entry</code> objects containing
   *         information about links defined for this resource.  The format of
   *         the entry is key=link text|image URL, value=type.
   */
  public Set<Map.Entry<String, String>> getLinks() { return links.entrySet(); }

  /**
   * Gets the set of HTML tables that are defined for this resource.  Tables
   * are identified by their <code>id</code> or <code>summary</code> attribute
   * text.  Their content is defined by an array of strings that should be
   * present in the table.
   * @return <code>Set</code> of <code>Map.Entry</code> objects containing
   *         information about tables defined for this resource.  The format of
   *         the entry is key=summary, value=array of strings in the table.
   */
  public Set<Map.Entry<String, String[]>> getTables() { return tables.entrySet(); }

  /**
   * Gets the set a HTML forms that are defined for this resource.  Forms are
   * identified by their <code>id</code> attribute text.
   * @return <code>Set</code> of <code>Map.Entry</code> objects containing
   *         information about forms defined for this resource.  The format of
   *         the entry is key=id, value=<code>WebForm</code> for that form.
   * @see WebForm
   */
  public Set<Map.Entry<String, WebForm>> getForms() { return forms.entrySet(); }

  /**
   * Gets a specific HTML form for this resource, based on the <code>id</code>.  
   * @param fName  Name of the form to be returned.
   * @return <code>WebForm</code> instance for the form specified.
   * @see WebForm
   */
  public WebForm getForm(String fName) { return forms.get(fName); }

  /**
   * Adds a new <code>h2</code> element to the array list for this resource.
   * @param h  Text of the <code>h2</code> element to be added.
   */
  public void addH2(String h) { h2s.add(h); }

  /**
   * Adds a new <code>img</code> element to the array list for this resource.
   * @param i  Text of the <code>img</code> element to be added.
   */
  public void addImg(String i) { imgs.add(i); }

  /**
   * Adds a new text string to the array list for this resource.
   * @param t  Text string to be added.
   */
  public void addText(String t) { text.add(t); }

  /**
   * Adds a new link to this resource.
   * @param txt  Link text or image URL, depending on the link type.
   * @param type  Type of URL.  Valid types of links include "txt" and "img".
   */
  public void addLink(String txt, String type) { links.put(txt, type); }

  /**
   * Adds a new <code>table</code> to this resource.
   * @param summary  Text for either the <code>id</code> or the <code>summary</code>
   *                 attribute for the table.  Used to identify the table.
   * @param txt  Array of strings that must be present in the table.
   */
  public void addTable(String summary, String[] txt) { tables.put(summary, txt); }

  /**
   * Adds a new <code>WebForm</code> to the resource, using the
   * <code>id</code> attribute as the hash key.
   * @param f  <code>WebForm</code> to be added.
   * @see WebForm
   */
  public void addForm(WebForm f) { forms.put(f.getId(), f); } 
}
