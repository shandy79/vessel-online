package com.swimshower.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.vesselonline.metadata.DublinCoreResource;
import org.vesselonline.model.Comment;
import org.vesselonline.model.Commentable;
import org.vesselonline.model.Editable;
import org.vesselonline.model.Person;

public class SwimShowerResource extends DublinCoreResource implements Commentable, Editable {
  private Date edited;
  private Person editor;
  private List<Comment> comments = new ArrayList<Comment>();
  private static final String PROPS_FILE = "/swimshower.properties";

  // Constants available to the application
  public static String SERVER_PREFIX;
  public static String SERVER_DIR;
  public static String UPLOAD_DIR;
  public static String SITE_NAME;
  public static String INFO_EMAIL;
  public static String ADMIN_EMAIL;
  public static String COPYRIGHT;
  public static SimpleDateFormat DATE_FORMAT;
  public static String DEFAULT_CATEGORY;
  public static String DEFAULT_LOCATION;
  public static int RECENT_ITEM_COUNT;

  // Static initialization of fields from properties file
  {
    Properties props = new Properties();
    try {
      props.load(this.getClass().getResourceAsStream(PROPS_FILE));

      SERVER_PREFIX = props.getProperty("SERVER_PREFIX");
      SERVER_DIR = props.getProperty("SERVER_DIR");
      UPLOAD_DIR = props.getProperty("UPLOAD_DIR");
      SITE_NAME = props.getProperty("SITE_NAME");
      INFO_EMAIL = props.getProperty("INFO_EMAIL");
      ADMIN_EMAIL = props.getProperty("ADMIN_EMAIL");
      COPYRIGHT = props.getProperty("COPYRIGHT");
      DATE_FORMAT = new SimpleDateFormat(props.getProperty("DATE_FORMAT"));
      DEFAULT_CATEGORY = props.getProperty("DEFAULT_CATEGORY");
      DEFAULT_LOCATION = props.getProperty("DEFAULT_LOCATION");
      RECENT_ITEM_COUNT = Integer.parseInt(props.getProperty("RECENT_ITEM_COUNT"));

    } catch (IOException ioe) {
      SERVER_PREFIX = "http://www.swimshower.com/";
      SERVER_DIR = "/home/shandy79/public_html";
      UPLOAD_DIR = "swimfile";
      SITE_NAME = "Swim Shower";
      INFO_EMAIL = "theband@swimshower.com";
      ADMIN_EMAIL = "admin@swimshower.com";
      DATE_FORMAT = new SimpleDateFormat("M/d/yyyy h:mm a");
      RECENT_ITEM_COUNT = 5;
      
      ioe.printStackTrace();
    } finally {
      props = null;
    }
  }

  public static final String createSimpleNavBanner(String type) {
    return "<table style=\"border:0;width:100%;\" cellpadding=\"0\" summary=\"layout\"><tr>\n" +
           "  <td><h1>" + type + "</h1></td>\n  <td style=\"text-align:right;vertical-align:bottom;\">\n" +
           "    <a href=\"all_" + type + ".jsp\"><img src=\"img/viewall.gif\" alt=\"view all " +
           type + "\" /></a></td></tr>\n</table>\n<hr />";
  }

  public SwimShowerResource() {
    super();
    this.setPublisher(SITE_NAME);
    this.setRights(COPYRIGHT);
  }

  public Date getEdited() { return edited; }
  public void setEdited(Date edited) { this.edited = edited; }

  public Person getEditor() { return editor; }
  public void setEditor(Person editor) { this.editor = editor; }

  public List<Comment> getComments() { return comments; }
  private void setComments(List<Comment> comments) { this.comments = comments; }

  public void addComment(Comment c) {
    c.setRelation(this);
    comments.add(c);
  }
  public void removeComment(Comment c) { comments.remove(c); }

  public Comment getLatestComment() {
    if (comments.size() <= 0) {
      return new Comment();
    } else { 
      return comments.get(comments.size() - 1);
    }
  }

  public int getCommentCount() { return comments.size(); }
}
