package org.vesselonline.beans;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import org.vesselonline.dao.HibernateUtil;
import org.vesselonline.dao.PersonDAO;
import org.vesselonline.dao.PersonHibernateDAO;
import org.vesselonline.model.Person;
import org.vesselonline.model.Role;

public class AccountBean implements VesselJSFBean {
  private Person person;
  private PersonDAO personDAO = new PersonHibernateDAO();
  private String currentPassword, newPassword1, newPassword2;

  public AccountBean() {
    person = new Person();
  }

  public String getCurrentPassword() { return null; }
  public void setCurrentPassword(String currentPassword) {
    this.currentPassword = currentPassword;
  }

  public String getNewPassword1() { return null; }
  public void setNewPassword1(String newPassword1) {
    this.newPassword1 = newPassword1;
  }

  public String getNewPassword2() { return null; }
  public void setNewPassword2(String newPassword2) {
    this.newPassword2 = newPassword2;
  }

  public String getId() { return Long.toString(person.getId()); }
  public void setId(String id) { person = personDAO.findById(new Long(id), false); }

  public String getLastname() { return person.getLastname(); }
  public void setLastname(String lastname) { person.setLastname(lastname.trim()); }

  public String getFirstname() { return person.getFirstname(); }
  public void setFirstname(String firstname) { person.setFirstname(firstname.trim()); }

  public String getEmail() { return person.getEmail(); }
  public void setEmail(String email) { person.setEmail(email.trim()); }

  public String getUsername() { return person.getUsername(); }
  public void setUsername(String username) { person.setUsername(username.trim()); }

  private boolean setPassword() {
    if (newPassword1 != null && newPassword2 != null && ! newPassword1.equals(newPassword2)) {
      return false;
    } else if (person.getPassword() != null && (currentPassword == null || currentPassword.equals(""))) {
      return true;
    }

    try {
      MessageDigest md = MessageDigest.getInstance("SHA");
      byte[] digest = null;
      BigInteger big = null;

      if (person.getPassword() != null && currentPassword != null && ! currentPassword.equals("")) {
        digest = md.digest(currentPassword.getBytes());
        big = new BigInteger(digest);
      
        if (! person.getPassword().equals(big.toString(16))) return false;
      } else {
        Role fan = new Role();
        HibernateUtil.getSessionFactory().getCurrentSession().load(fan, new Long(2));
        person.addRole(fan);

        person.setJoinDate(new Date());
      }

      digest = md.digest(newPassword1.getBytes());

      // Must ensure the leftmost bit is a 0 so number is positive
      byte[] temp = new byte[digest.length + 1];
      System.arraycopy(digest, 0, temp, 1, digest.length);

      big = new BigInteger(temp);
      person.setPassword(big.toString(16));

      return true;
    } catch (NoSuchAlgorithmException nsae) {
      System.out.println(nsae.getMessage());
      return false;
    }
  }

  public int getCount() { return 1; }

  // Method for the form action, returns a string used in navigation-rules
  public String navigate() {
    boolean result = false;

    if (person != null) {
      result = setPassword();
      if (result) personDAO.makePersistent(person);
    }

    return (result) ? "valid" : "invalid";
  }
}
