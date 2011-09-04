package org.vesselonline.semantic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Person {
  public static final String MALE = "male";
  public static final String FEMALE = "female";

  private String name;
  private String gender;
  private Set<Relation> relations;

  public Person(String name, String gender) {
    this.name = name;
    this.gender = gender;

    relations = new HashSet<Relation>();
  }

  public String getName() { return name; }

  public String getGender() { return gender; }

  public Set<Relation> getRelations() { return relations; }

  public void addRelation(Relation relation) { relations.add(relation); }

  public List<Person> getSiblings() {
    List<Person> siblings = new ArrayList<Person>();
    for (Relation relation : getRelations()) {
      if (relation.getType().equals(Relation.SIBLING)) {
        siblings.add(relation.getPerson());
      }
    }
    return siblings;
  }

  public List<Person> getChildren() {
    List<Person> children = new ArrayList<Person>();
    for (Relation relation : getRelations()) {
      if (relation.getType().equals(Relation.CHILD)) {
        children.add(relation.getPerson());
      }
    }
    return children;
  }

  public List<Person> getParents() {
    List<Person> parents = new ArrayList<Person>();
    for (Relation relation : getRelations()) {
      if (relation.getType().equals(Relation.PARENT)) {
        parents.add(relation.getPerson());
      }
    }
    return parents;
  }

  public int hashCode() {
    return ((getName().hashCode() * 13) + (getGender().hashCode() * 11)) * 7;
  }

  public boolean equals(Object obj) {
    if (obj == null) return false;
    Person person = (Person) obj;

    if (person.getName().equals(getName()) && person.getGender().equals(getGender())) {
      return true;
    } else {
      return false;
    }
  }

  public String toString() {
    return getName() + " (" + getGender() + ")";
  }
}
