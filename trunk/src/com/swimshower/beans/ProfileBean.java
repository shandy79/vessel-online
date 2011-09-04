package com.swimshower.beans;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.vesselonline.beans.VesselJSFBean;
import org.vesselonline.model.Person;
import com.swimshower.dao.ProfileDAO;
import com.swimshower.dao.ProfileHibernateDAO;
import com.swimshower.model.SwimShowerResource;

public class ProfileBean implements VesselJSFBean {
  private static final String iconDir = SwimShowerResource.SERVER_DIR + SwimShowerResource.UPLOAD_DIR +
                                        File.separator + "icon";

  private Person person = new Person();
  private ProfileDAO profileDAO = new ProfileHibernateDAO();
  private UploadedFile iconFile;

  public ProfileBean() { }

  public Person getPerson() { return person; }

  public UploadedFile getIconFile() { return iconFile; }
  public void setIconFile(UploadedFile iconFile) { this.iconFile = iconFile; }

  public String getId() { return Long.toString(person.getId()); }
  public void setId(String id) { person = profileDAO.findById(new Long(id), false); }

  public String getLastname() { return person.getLastname(); }
  public void setLastname(String lastname) { person.setLastname(lastname.trim()); }

  public String getFirstname() { return person.getFirstname(); }
  public void setFirstname(String firstname) { person.setFirstname(firstname.trim()); }

  public String getEmail() { return person.getEmail(); }
  public void setEmail(String email) { person.setEmail(email.trim()); }

  public String getUsername() { return person.getUsername(); }

  public String getDescription() { return person.getDescription(); }
  public void setDescription(String description) { person.setDescription(description.trim()); }

  public String getJoinDate() {
    if (person.getJoinDate() != null) {
      return (new SimpleDateFormat("M/d/yyyy")).format(person.getJoinDate());
    } else {
      return "";
    }
  }

  public String getWebSite() {
    if (person.getWebSite() != null) {
      return person.getWebSite().toExternalForm();
    } else {
      return "";
    }
  }
  public void setWebSite(String webSite) {
    try {
      person.setWebSite(new URL(webSite.trim()));
    } catch (MalformedURLException murle) {
      person.setWebSite(null);
    }
  }

  public String getRole() { return person.getRole(); }

  public String getIcon() { return person.getIcon(); }

  private boolean storeIconFile() {
    if (iconFile == null) return true;

    String ext = iconFile.getName().substring(iconFile.getName().lastIndexOf('.') + 1);
    if (! ext.toLowerCase().equals("gif") && ! ext.toLowerCase().equals("jpg") &&
        ! ext.toLowerCase().equals("bmp")) return false; 

    File f = new File(iconDir + File.separator + getUsername() + "." + ext);
    byte[] b = new byte[1024];

    try {
      BufferedInputStream in = new BufferedInputStream(iconFile.getInputStream());
      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));

      int i = in.read(b);
      while (i != -1) {
        out.write(b);
        i = in.read(b);
      }
      out.flush();
      out.close();
      in.close();

      person.setIcon(ext);
      return true;
    } catch (IOException ioe) {
      System.out.println("Unable to save icon file: " + f);
      ioe.printStackTrace(System.out);
      return false;
    }
  }

  public int getCount() { return 1; }

  // Method for the form action, returns a string used in navigation-rules
  public String navigate() {
    boolean result = false;

    if (person != null) {
      result = storeIconFile();
      if (result) profileDAO.makePersistent(person);
    }

    return (result) ? "valid" : "invalid";
  }
}
