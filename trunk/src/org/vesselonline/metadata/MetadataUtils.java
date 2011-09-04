package org.vesselonline.metadata;

/**
 * @author Steven Handy
 * @version 1.0
 * @see DublinCoreResource
 * @see XMLMetadata
 */
public class MetadataUtils {
  /**
   * The specification for RSS 1.0 channels can be found at the
   * <a href="http://purl.org/rss/1.0/spec">RSS-DEV Working Group</a>.
   * @return An XML fragment describing the resource as an RSS 1.0 item.
   */
  public static String fromRDFToRSS1(XMLMetadata rdf) { return null; }

  /**
   * The specification for RSS 2.0 channels can be found at the
   * <a href="http://blogs.law.harvard.edu/tech/rss">Berkman Center for Internet
   * and Society at Harvard Law School</a>.
   * @return An XML fragment describing the resource as an RSS 2.0 item.
   */
  public static String fromRDFToRSS2Feed(XMLMetadata rdf) { return null; }

  /**
   * The specification for Atom feeds can be found in the
   * <a href="http://www.ietf.org/rfc/rfc4287.txt">IETF RFC 4287</a>.
   * @return An XML fragment describing the resource as an Atom entry.
   */
  public static String fromRDFToAtomFeed(XMLMetadata rdf) { return null; }
}
