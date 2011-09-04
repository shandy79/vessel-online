package org.vesselonline.draftroom.beans;

import java.util.Collection;
import org.vesselonline.draftroom.api.FantasyTeam;
import org.vesselonline.draftroom.api.Owner;
import org.vesselonline.draftroom.api.Player;

public class FantasyTeamBean extends TeamBean implements FantasyTeam {
  private Owner owner;
  private int draftOrder;
  private Collection<Player> players;

  /**
   * No-argument constructor for JavaBean compatibility.
   */
  public FantasyTeamBean() { }

  public Owner getOwner() { return owner; }
  public void setOwner(Owner owner) { this.owner = owner; }

  public int getDraftOrder() { return draftOrder; }
  public void setDraftOrder(int draftOrder) { this.draftOrder = draftOrder; }

  public Collection<Player> getPlayers() { return players; }
  public boolean addPlayer(Player player) { return players.add(player); }
  public boolean removePlayer(Player player) { return players.remove(player); }
}
