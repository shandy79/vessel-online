package org.vesselonline.draftroom.api;

import java.net.URL;

public interface Team {
  long getID();

  String getCity();
  void setCity(String city);

  String getName();
  void setName(String name);

  String getAbbreviation();
  void setAbbreviation(String abbreviation);

  String getDivision();
  void setDivision(String division);

  URL getLogoURL();
  void setLogoURL(URL logoURL);

  int getByeWeek();
  void setByeWeek(int byeWeek);
}
