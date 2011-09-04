package org.vesselonline.model;

import java.util.HashSet;
import java.util.Set;

public class Role {
  private long id;
  private String rolename;
  private long sortOrder;
  private String display;
  private Set<Person> persons = new HashSet<Person>();

  public Role() { }

  public long getId() { return id; }
  private void setId(long id) { this.id = id; }

  public String getRolename() { return rolename; }
  public void setRolename(String rolename) { this.rolename = rolename; }

  public long getSortOrder() { return sortOrder; }
  public void setSortOrder(long sortOrder) { this.sortOrder = sortOrder; }

  public String getDisplay() { return display; }
  public void setDisplay(String display) { this.display = display; }

  public Set<Person> getPersons() { return persons; }
  private void setPersons(Set<Person> persons) { this.persons = persons; }

  public void addPerson(Person p) {
    persons.add(p);
    p.getRoles().add(this);
  }
  public void removePerson(Person p) {
    persons.remove(p);
    p.getRoles().remove(this);
  }
}
