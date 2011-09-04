package org.vesselonline.draftroom.api;

public interface Player {
  long getID();

  String getName();
  void setName(String name);

  Team getProTeam();
  void setProTeam(Team team);

  Position getPosition();
  void setPosition(Position position);

  FantasyTeam getFantasyTeam();
  void setFantasyTeam(FantasyTeam fantasyTeam);

  int getPickNumber();
  void setPickNumber(int pickNumber);
}
