package org.vesselonline.model;

import java.util.Date;
import org.vesselonline.metadata.DublinCoreResource;
import org.vesselonline.metadata.XMLMetadata;

public class Comment extends DublinCoreResource implements XMLMetadata {
  private DublinCoreResource relation;
  private String ipAddress;
  private Date edited;

  public Comment() { super(); }

  public DublinCoreResource getRelation() { return relation; }
  public void setRelation(DublinCoreResource relation) { this.relation = relation; }

  public String getIpAddress() { return ipAddress; }
  public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

  public Date getEdited() { return edited; }
  public void setEdited(Date edited) { this.edited = edited; }

  public String toDublinCoreRDF() {
    // TODO Auto-generated method stub
    return null;
  }
}
