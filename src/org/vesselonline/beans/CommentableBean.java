package org.vesselonline.beans;

import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.vesselonline.model.Comment;
import org.vesselonline.model.Person;
import com.swimshower.model.SwimShowerResource;

public abstract class CommentableBean {
  protected Comment comment;
  protected long commentToEdit;

  protected CommentableBean() { comment = new Comment(); }

  public long getCommentToEdit() { return commentToEdit; }
  public abstract void setCommentToEdit(long commentToEdit);
  
  public abstract List<Comment> getComments();

  public String getComment() { return comment.getDescription(); }
  public void setComment(String comment) { this.comment.setDescription(comment.trim()); }

  public String getCommentLocation() { return comment.getSubject(); }
  public void setCommentLocation(String location) {
    if (location.trim().equals("")) location = SwimShowerResource.DEFAULT_LOCATION;
    comment.setSubject(location.trim());
  }

  public String getCommentDate() {
    if (comment.getDate() != null) {
      return SwimShowerResource.DATE_FORMAT.format(comment.getDate());
    } else {
      return "";
    }
  }

  public long getCommenterId() {
    if (comment.getContributor() != null) {
      return comment.getContributor().getId();
    } else {
      return 0;
    }
  }

  public String getCommenterName() {
    if (comment.getContributor() != null) {
      return comment.getContributor().getUsername();
    } else {
      return "";
    }
  }

  protected boolean storeCommentContextItems() {
    // Only necessary for new comments, need to initialize required fields
    if (commentToEdit == 0)  {
      ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
      HttpSession session = (HttpSession) context.getSession(false);
      HttpServletRequest request = (HttpServletRequest) context.getRequest();

      // Retrieve the contributor from the session and IP from the request
      if (session != null && session.getAttribute("user") != null) {
        comment.setContributor((Person) session.getAttribute("user"));
        comment.setIpAddress(request.getRemoteAddr());
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  protected abstract boolean storeComment();
}
