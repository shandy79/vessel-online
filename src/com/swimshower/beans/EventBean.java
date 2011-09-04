package com.swimshower.beans;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.vesselonline.beans.CommentableBean;
import org.vesselonline.beans.VesselJSFBean;
import org.vesselonline.model.Comment;
import org.vesselonline.model.Person;
import com.swimshower.dao.EventDAO;
import com.swimshower.dao.EventHibernateDAO;
import com.swimshower.model.Event;
import com.swimshower.model.SwimShowerResource;

public class EventBean extends CommentableBean implements VesselJSFBean {
  private Event event;
  private EventDAO eventDAO = new EventHibernateDAO();

  public EventBean() {
    super();
    event = new Event();
  }

  public String getId() { return Long.toString(event.getId()); }
  public void setId(String id) { event = eventDAO.findById(new Long(id), false); }

  public String getTitle() { return event.getTitle(); }
  public void setTitle(String title) { event.setTitle(title.trim()); }

  public String getCategory() { return event.getSubject(); }
  public void setCategory(String category) {
    if (category.trim().equals("")) category = SwimShowerResource.DEFAULT_CATEGORY;
    event.setSubject(category.trim());
  }

  public String getDescription() { return event.getDescription(); }
  public void setDescription(String description) { event.setDescription(description.trim()); }

  public Date getStartDate() { return event.getDate(); }
  public void setStartDate(Date startDate) { event.setDate(startDate); }

  public String getStartDateString() {
    if (event.getDate() != null) {
      return SwimShowerResource.DATE_FORMAT.format(event.getDate());
    } else {
      return "";
    }
  }

  public Date getEndDate() { return event.getEndDate(); }
  public void setEndDate(Date endDate) { event.setEndDate(endDate); }

  public String getEndDateString() {
    if (event.getEndDate() != null) {
      return SwimShowerResource.DATE_FORMAT.format(event.getEndDate());
    } else {
      return "";
    }
  }

  public String getSource() {
    if (event.getSource() != null) {
      return event.getSource().toExternalForm();
    } else {
      return "";
    }
  }
  public void setSource(String source) {
    try {
      event.setSource(new URL(source.trim()));
    } catch (MalformedURLException murle) {
      event.setSource(null);
    }
  }

  public String getHost() { return event.getCreator(); }
  public void setHost(String host) { event.setCreator(host.trim()); }

  public String getHostURL() {
    if (event.getHostURL() != null) {
      return event.getHostURL().toExternalForm();
    } else {
      return "";
    }
  }
  public void setHostURL(String hostURL) {
    try {
      event.setHostURL(new URL(hostURL.trim()));
    } catch (MalformedURLException murle) {
      event.setHostURL(null);
    }
  }

  public String getAddress() { return event.getAddress(); }
  public void setAddress(String address) { event.setAddress(address.trim()); }

  public String getCity() { return event.getCity(); }
  public void setCity(String city) { event.setCity(city.trim()); }

  public String getState() { return event.getState(); }
  public void setState(String state) { event.setState(state.trim()); }

  public Person getContributor() {
    if (event.getContributor() != null) {
      return event.getContributor();
    } else {
      return new Person();
    }
  }

  public String getEditor() {
    if (event.getEditor() != null) {
      return event.getEditor().getUsername();
    } else {
      return "";
    }
  }

  public String getEdited() {
    if (event.getEdited() != null) {
      return SwimShowerResource.DATE_FORMAT.format(event.getEdited());
    } else {
      return "";
    }
  }

  private boolean storeEventContextItems() {
    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
    HttpSession session = (HttpSession) context.getSession(false);

    // For a new event
    if (event.getId() == 0)  {  
      if (session != null && session.getAttribute("user") != null) {
        event.setContributor((Person) session.getAttribute("user"));
        return true;
      } else {
        return false;
      }

    // For an edited event
    } else if (getComment() == null || getComment().equals("")) {
      if (session != null && session.getAttribute("user") != null) {
        event.setEditor((Person) session.getAttribute("user"));
        event.setEdited(new Date());
        return true;
      } else {
        return false;
      }

    } else {
      return true;
    }
  }

  public void setCommentToEdit(long commentToEdit) {
    this.commentToEdit = commentToEdit;

    if (commentToEdit > 0) {
      for (Comment c : event.getComments()) {
        if (commentToEdit == c.getId()) {
          comment = c;
          break;
        }
      }
    }
  }

  public List<Comment> getComments() { return event.getComments(); }

  protected boolean storeComment() {
    boolean result = storeCommentContextItems();

    if (result) {
      // For a new comment
      if (commentToEdit == 0) {
        comment.setDate(new Date());
        event.addComment(comment);

      // For an edited comment
      } else {
        for (Comment c : event.getComments()) {
          if (c.getId() == commentToEdit) {
            c.setDescription(getComment());
            c.setEdited(new Date());
            break;
          }
        }
      }

      eventDAO.makePersistent(event);
    }

    // Fixes a bug with doubly displaying the comment just submitted, not sure why
    event.getCommentCount();
    // Must clear to prevent display on next presentation of event.jsp
    comment = new Comment();
    commentToEdit = 0;
    return result;
  }

  public int getCount() { return eventDAO.getCount(); }
  public long getPreviousId() { return eventDAO.getPreviousId(event.getDate()); }
  public long getNextId() { return eventDAO.getNextId(event.getDate()); }

  // Method for the form action, returns a string used in navigation-rules
  public String navigate() {
    boolean result = false;

    // Always store the event
    result = storeEventContextItems();
    if (result) eventDAO.makePersistent(event);

    // For a new comment or an edited comment
    if (getComment() != null && ! getComment().equals("")) {
      result = storeComment();
    }

    return (result) ? "valid" : "invalid";
  }
}
