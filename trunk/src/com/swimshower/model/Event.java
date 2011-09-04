package com.swimshower.model;

import java.net.URL;
import java.util.Date;
import org.vesselonline.metadata.XMLMetadata;

public class Event extends SwimShowerResource implements XMLMetadata {
  private URL hostURL;
  private String address;
  private String city;
  private String state;
  private Date endDate;

  public Event() { super(); }

  public URL getHostURL() { return hostURL; }
  public void setHostURL(URL hostURL) { this.hostURL = hostURL; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }

  public String getState() { return state; }
  public void setState(String state) { this.state = state; }

  public Date getEndDate() { return endDate; }
  public void setEndDate(Date endDate) { this.endDate = endDate; }

  public String toDublinCoreRDF() {
    // TODO Auto-generated method stub
    return null;
  }
}
