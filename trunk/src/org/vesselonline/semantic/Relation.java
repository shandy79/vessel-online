package org.vesselonline.semantic;

public class Relation {
  public static final String SIBLING = "sibling";
  public static final String CHILD = "child";
  public static final String PARENT = "parent";

  private Person person;
  private String type;

  public Relation(Person person, String type) {
    this.person = person;
    this.type = type;
  }

  public Person getPerson() { return person; }

  public String getType() { return type; }

  public String toString() {
    return getPerson().toString() + " is a " + getType() + " of";
  }
}
