package org.vesselonline.draftroom.api;

import java.util.Collection;

public interface FantasyTeam extends Team {
  Owner getOwner();
  void setOwner(Owner owner);

  int getDraftOrder();
  void setDraftOrder(int draftOrder);

  Collection<Player> getPlayers();
  boolean addPlayer(Player player);
  boolean removePlayer(Player player);
}
