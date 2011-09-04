package org.vesselonline.draftroom.beans;

import org.vesselonline.draftroom.api.Position;

public class PositionBean implements Position {
  private long id;
  private String name;
  private String abbreviation;
  private int rosterOrder;
  private int rosterSlots;

  /**
   * No-argument constructor for JavaBean compatibility.
   */
  public PositionBean() { }

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

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getAbbreviation() { return abbreviation; }
  public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }

  /**
   * Value is the order in which this position should appear on a fantasy
   * team's roster in this league.
   */
  public int getRosterOrder() { return rosterOrder; }
  public void setRosterOrder(int rosterOrder) { this.rosterOrder = rosterOrder; }

  /**
   * Value is the number of starting slots for this position on a fantasy
   * team's roster in this league.
   */
  public int getRosterSlots() { return rosterSlots; }
  public void setRosterSlots(int rosterSlots) { this.rosterSlots = rosterSlots; }
}
