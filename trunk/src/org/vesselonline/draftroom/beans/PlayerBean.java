package org.vesselonline.draftroom.beans;

import org.vesselonline.draftroom.api.FantasyTeam;
import org.vesselonline.draftroom.api.Player;
import org.vesselonline.draftroom.api.Position;
import org.vesselonline.draftroom.api.Team;

public class PlayerBean implements Player {
  private long id;
  private String name;
  private Team proTeam;
  private Position position;
  private FantasyTeam fantasyTeam;
  private int pickNumber;

  /**
   * No-argument constructor for JavaBean compatibility.
   */
  public PlayerBean() { }

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

  public Team getProTeam() { return proTeam; }
  public void setProTeam(Team proTeam) { this.proTeam = proTeam; }

  public Position getPosition() { return position; }
  public void setPosition(Position position) { this.position = position; }

  public FantasyTeam getFantasyTeam() { return fantasyTeam; }
  public void setFantasyTeam(FantasyTeam fantasyTeam) { this.fantasyTeam = fantasyTeam; }

  /**
   * Value is the overall draft pick with which this player was selected.
   */
  public int getPickNumber() { return pickNumber; }
  public void setPickNumber(int pickNumber) { this.pickNumber = pickNumber; }
}
