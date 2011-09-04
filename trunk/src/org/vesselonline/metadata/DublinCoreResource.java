package org.vesselonline.metadata;

import java.net.URL;
import java.util.Date;
import org.vesselonline.model.Person;

/**
 * <p>Superclass for all major resources within the web application.  This class
 * is based on the <a href="http://dublincore.org/">Dublin Core Metadata
 * Initiative</a>'s <a href="http://dublincore.org/documents/dces/">Element
 * Set</a>.  Refer also to the
 * <a href="http://dublincore.org/schemas/xmls/simpledc20021212.xsd">XML Schema</a>
 * and <a href="http://dublincore.org/documents/dcmes-xml/">RDF Guidelines</a>
 * for more information about Dublin Core and its relation to this class.</p>
 * <p>Site-level subclasses of <code>DublinCoreResource</code> are expected to
 * implement constructors that define site-specific values for the
 * <code>publisher</code> and <code>rights</code> attributes, while
 * resource-level subclass are expected to provide defaults for
 * <code>type</code> and optionally the <code>source</code> attribute.</p>
 * <p>Resource-level subclasses are expected to be compatible with the
 * <a href="http://hibernate.org/">Hibernate</a> O/R mapping tool for
 * database persistence and retrieval.  Each should also be paired with a
 * <code>*.hbm.xml</code> file outlining its relationship to the database.</p>
 * <p>Resource-level subclasses may optionally implement the <code>XMLMetadata</code>
 * interface, which provides object-to-XML conversions for RDF, RSS 1.0, RSS
 * 2.0, and Atom feeds.</p>
 * @author Steven Handy
 * @version 1.0
 * @see XMLMetadata
 * @see MetadataUtils
 */
public class DublinCoreResource {
  private long id;
  private String title;
  private Person contributor;
  private String subject;
  private String description;
  private Date date;
  private URL source;
  private String creator;
  private String type;
  private String publisher;
  private String rights;
  private String format = "text/html";
  private String language = "en-US";
  private String coverage = "www";
  // The Dublin Core Metadata Element Set also contains an element 'relation', which will
  // be implemented in specific subclasses requiring that functionality (e.g. comments).

  /**
   * No-argument constructor for JavaBean compatibility.
   */
  public DublinCoreResource() { this.setType(this.getClass().getSimpleName()); }

  /**
   * Value is a Hibernate-generated identifier, typically derived from an SQL
   * <code>sequence</code>.
   * @return An unambiguous reference to the resource within a given context.
   */
  public long getId() { return id; }
  /**
   * Assigns an identifier to the resource.  This method is only invoked
   * by the Hibernate internals when the object is initially persisted.
   * @param id
   */
  protected void setId(long id) { this.id = id; }

  /**
   * @return A name given to the resource.
   */
  public String getTitle() { return title; }
  /**
   * Assign a title to the resource.
   * @param title
   */
  public void setTitle(String title) { this.title = title; }

  /**
   * Value is a reference to a <code>Person</code> through its 
   * <code>id</code> attribute.
   * @return An entity responsible for making contributions to the content of
   *         the resource.
   * @see Person
   */
  public Person getContributor() { return contributor; }
  /**
   * Assigns a contributor to the resource.
   * @param contributor
   */
  public void setContributor(Person contributor) { this.contributor = contributor; }

  /**
   * In this context, value is used for categorization.
   * @return A topic of the content of the resource.
   */
  public String getSubject() { return subject; }
  /**
   * Assigns a subject to the resource.
   * @param subject
   */
  public void setSubject(String subject) { this.subject = subject; }

  /**
   * @return An account of the content of the resource.
   */
  public String getDescription() { return description; }
  /**
   * Assigns a description to the resource.
   * @param description
   */
  public void setDescription(String description) { this.description = description; }

  /**
   * Default value should be the date and time the resource was created.
   * @return A date of an event in the lifecycle of the resource.
   */
  public Date getDate() { return date; }
  /**
   * Assigns a date to the resource.
   * @param date
   */
  public void setDate(Date date) { this.date = date; }

  /**
   * Value is a URL linking to the original content if not from the site.  May
   * be used in conjunction with the creator to provide a name/URL pair.
   * @return A reference to a resource from which the present resource is derived.
   * @see #getCreator()
   */
  public URL getSource() { return source; }
  /**
   * Assigns a source to the resource.
   * @param source
   */
  public void setSource(URL source) { this.source = source; }

  /**
   * Value only set if the creator differs from the contributor.  May be used to
   * provide a label for the source for references to external content.
   * @return An entity primarily responsible for making the content of the resource.
   * @see #getContributor()
   * @see #getSource()
   */
  public String getCreator() { return creator; }
  /**
   * Assigns a creator to the resource.
   * @param creator
   */
  public void setCreator(String creator) { this.creator = creator; }

  /**
   * Value set by this class constructor to be the simple name of the subclass
   * that is currently extending this class.
   * @return The nature or genre of the content of the resource.
   */
  public String getType() { return type; }
  /**
   * Assigns a type to the resource.
   * @param type
   */
  protected void setType(String type) { this.type = type; }

  /**
   * Value typically set by the site-level subclass constructor to be the
   * name of the web site.
   * @return An entity responsible for making the resource available.
   */
  public String getPublisher() { return publisher; }
  /**
   * Assigns a publisher to the resource.  Typically not used.
   * @param publisher
   */
  protected void setPublisher(String publisher) { this.publisher = publisher; }

  /**
   * Value typically set by the site-level subclass constructor with the
   * format:  '&copy; {start yr}-{current yr} {site name}.  All rights reserved.'
   * @return Information about rights held in and over the resource.
   */
  public String getRights() { return rights; }
  /**
   * Assigns the rights to the resource.  Typically not used.
   * @param rights
   */
  protected void setRights(String rights) { this.rights = rights; }

  /**
   * Default value set by this class in the attribute declaration.
   * @return The physical or digital manifestation of the resource.
   */
  public String getFormat() { return format; }
  /**
   * Assigns a format to the resource.  Typically not used.
   * @param format
   */
  protected void setFormat(String format) { this.format = format; }

  /**
   * Default value set by this class in the attribute declaration.
   * @return A language of the intellectual content of the resource.
   */
  public String getLanguage() { return language; }
  /**
   * Assigns a language to the resource.  Typically not used.
   * @param language
   */
  protected void setLanguage(String language) { this.language = language; }

  /**
   * Default value set by this class in the attribute declaration.
   * @return The extent or scope of the content of the resource.
   */
  public String getCoverage() { return coverage; }
  /**
   * Assigns a coverage to the resource.  Typically not used.
   * @param coverage
   */
  protected void setCoverage(String coverage) { this.coverage = coverage; }
}
