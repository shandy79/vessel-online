package org.vesselonline.metadata;

/**
 * @author Steven Handy
 * @version 1.0
 * @see DublinCoreResource
 */
public interface XMLMetadata {
  /**
   * The guidelines for Dublin Core RDF can be found in the document
   * <a href="http://dublincore.org/documents/dcmes-xml/">DCMES-XML</a>.
   * @return An XML document describing the resource in Dublin Core RDF.
   */
  String toDublinCoreRDF();
}
