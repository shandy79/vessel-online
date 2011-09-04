package org.vesselonline.draftroom.api;

public interface Position {
  long getID();

  String getName();
  void setName(String name);

  String getAbbreviation();
  void setAbbreviation(String abbreviation);

  int getRosterOrder();
  void setRosterOrder(int rosterOrder);

  int getRosterSlots();
  void setRosterSlots(int rosterSlots);
}
