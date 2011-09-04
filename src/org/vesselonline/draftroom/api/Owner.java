package org.vesselonline.draftroom.api;

public interface Owner {
  long getID();

  String getLastName();
  void setLastName(String lastName);

  String getFirstName();
  void setFirstName(String firstName);

  String getEmail();
  void setEmail(String email);
}
