package org.vesselonline.draftroom.beans;

import org.vesselonline.draftroom.api.Owner;

public class OwnerBean implements Owner {
  private long id;
  private String lastName;
  private String firstName;
  private String email;

  /**
   * No-argument constructor for JavaBean compatibility.
   */
  public OwnerBean() { }

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

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}
