package org.vesselonline.draftroom.beans;

import java.net.URL;
import org.vesselonline.draftroom.api.Team;

public class TeamBean implements Team {
  private long id;
  private String city;
  private String name;
  private String abbreviation;
  private String division;
  private URL logoURL;
  private int byeWeek;

  /**
   * No-argument constructor for JavaBean compatibility.
   */
  public TeamBean() { }

  /**
   * Value is a Hibernate-generated identifier, typically derived from an SQL
   * <code>sequence</code>.
   * @return An unambiguous reference to the resource within a given context.
   */
  public long getID() { return id; }
  /**
   * Assigns an identifier to the resource.  This method is only invoked
   * by the Hibernate internals when the object is initially persisted.
   * @param id
   */
  protected void setID(long id) { this.id = id; }

  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getAbbreviation() { return abbreviation; }
  public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }

  public String getDivision() { return division; }
  public void setDivision(String division) { this.division = division; }

  public URL getLogoURL() { return logoURL; }
  public void setLogoURL(URL logoURL) { this.logoURL = logoURL; }

  public int getByeWeek() { return byeWeek; }
  public void setByeWeek(int byeWeek) { this.byeWeek = byeWeek; }
}
