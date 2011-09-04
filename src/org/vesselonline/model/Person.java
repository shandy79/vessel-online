package org.vesselonline.model;

import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Person {
  private long id;
  private String lastname;
  private String firstname;
  private String email;
  private String username;
  private String password;
  private String description;
  private Date joinDate;
  private URL webSite;
  private String icon;
  private Set<Role> roles = new HashSet<Role>();
  
  public Person() { }

  public long getId() { return id; }
  private void setId(long id) { this.id = id; }

  public String getLastname() { return lastname; }
  public void setLastname(String lastname) { this.lastname = lastname; }

  public String getFirstname() { return firstname; }
  public void setFirstname(String firstname) { this.firstname = firstname; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public Date getJoinDate() { return joinDate; }
  public void setJoinDate(Date joinDate) { this.joinDate = joinDate; }

  public URL getWebSite() { return webSite; }
  public void setWebSite(URL webSite) { this.webSite = webSite; }

  public String getIcon() { return icon; }
  public void setIcon(String icon) { this.icon = icon; }

  public Set<Role> getRoles() { return roles; }
  private void setRoles(Set<Role> roles) { this.roles = roles; }

  public void addRole(Role r) {
    roles.add(r);
    r.getPersons().add(this);
  }
  public void removeRole(Role r) {
    roles.remove(r);
    r.getPersons().remove(this);
  }

  /** Returns a string identifying the highest role the person occupies. */
  public String getRole() {
    String role = null;
    
    if (roles.size() == 1) {
      role = roles.iterator().next().getDisplay();
    } else {
      Role r = null;
      long maxSort = 0;
      Iterator i = roles.iterator();

      while (i.hasNext()) {
        r = (Role) i.next();
        if (r.getSortOrder() > maxSort && r.getSortOrder() < 50) {
          maxSort = r.getSortOrder();
          role = r.getDisplay();
        }
      }
    }

    return role;
  }
}
