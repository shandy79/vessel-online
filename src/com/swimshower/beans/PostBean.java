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
import com.swimshower.dao.PostDAO;
import com.swimshower.dao.PostHibernateDAO;
import com.swimshower.model.Post;
import com.swimshower.model.SwimShowerResource;

public class PostBean extends CommentableBean implements VesselJSFBean {
  private Post post;
  private PostDAO postDAO = new PostHibernateDAO();

  public PostBean() {
    super();
    post = new Post();
  }

  public String getId() { return Long.toString(post.getId()); }
  public void setId(String id) { post = postDAO.findById(new Long(id), false); }

  public String getTitle() { return post.getTitle(); }
  public void setTitle(String title) { post.setTitle(title.trim()); }

  public String getCategory() { return post.getSubject(); }
  public void setCategory(String category) {
    if (category.trim().equals("")) category = SwimShowerResource.DEFAULT_CATEGORY;
    post.setSubject(category.trim());
  }

  public String getDescription() { return post.getDescription(); }
  public void setDescription(String description) { post.setDescription(description.trim()); }

  public String getDate() {
    if (post.getDate() != null) {
      return SwimShowerResource.DATE_FORMAT.format(post.getDate());
    } else {
      return "";
    }
  }

  public String getSource() {
    if (post.getSource() != null) {
      return post.getSource().toExternalForm();
    } else {
      return "";
    }
  }
  public void setSource(String source) {
    try {
      post.setSource(new URL(source.trim()));
    } catch (MalformedURLException murle) {
      post.setSource(null);
    }
  }

  public String getAuthor() { return post.getCreator(); }
  public void setAuthor(String author) { post.setCreator(author.trim()); }

  public Person getContributor() {
    if (post.getContributor() != null) {
      return post.getContributor();
    } else {
      return new Person();
    }
  }

  public String getEditor() {
    if (post.getEditor() != null) {
      return post.getEditor().getUsername();
    } else {
      return "";
    }
  }

  public String getEdited() {
    if (post.getEdited() != null) {
      return SwimShowerResource.DATE_FORMAT.format(post.getEdited());
    } else {
      return "";
    }
  }

  private boolean storePostContextItems() {
    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
    HttpSession session = (HttpSession) context.getSession(false);

    // For a new post
    if (post.getId() == 0)  {  
      if (session != null && session.getAttribute("user") != null) {
        post.setContributor((Person) session.getAttribute("user"));
        return true;
      } else {
        return false;
      }

    // For an edited post
    } else if (getComment() == null || getComment().equals("")) {
      if (session != null && session.getAttribute("user") != null) {
        post.setEditor((Person) session.getAttribute("user"));
        post.setEdited(new Date());
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
      for (Comment c : post.getComments()) {
        if (commentToEdit == c.getId()) {
          comment = c;
          break;
        }
      }
    }
  }

  public List<Comment> getComments() { return post.getComments(); }

  protected boolean storeComment() {
    boolean result = storeCommentContextItems();

    if (result) {
      // For a new comment
      if (commentToEdit == 0) {
        comment.setDate(new Date());
        post.addComment(comment);

      // For an edited comment
      } else {
        for (Comment c : post.getComments()) {
          if (c.getId() == commentToEdit) {
            c.setDescription(getComment());
            c.setEdited(new Date());
            break;
          }
        }
      }

      postDAO.makePersistent(post);
    }

    // Fixes a bug with doubly displaying the comment just submitted, not sure why
    post.getCommentCount();
    // Must clear to prevent display on next presentation of post.jsp
    comment = new Comment();
    commentToEdit = 0;
    return result;
  }

  public int getCount() { return postDAO.getCount(); }
  public long getPreviousId() { return postDAO.getPreviousId(post.getId()); }
  public long getNextId() { return postDAO.getNextId(post.getId()); }

  // Method for the form action, returns a string used in navigation-rules
  public String navigate() {
    boolean result = false;

    // Always store the post
    result = storePostContextItems();
    if (post.getDate() == null) post.setDate(new Date());
    if (result) postDAO.makePersistent(post);

    // For a new comment or an edited comment
    if (getComment() != null && ! getComment().equals("")) {
      result = storeComment();
    }

    return (result) ? "valid" : "invalid";
  }
}
