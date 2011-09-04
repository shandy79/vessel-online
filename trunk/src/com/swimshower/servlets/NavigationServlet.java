package com.swimshower.servlets;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.swimshower.beans.EventBean;
import com.swimshower.beans.PostBean;
import com.swimshower.beans.ProfileBean;
import com.swimshower.beans.SongBean;
import org.vesselonline.beans.AccountBean;

/** NavigationServlet
 *  Steven Handy
 */
public class NavigationServlet extends HttpServlet {
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
                   throws ServletException, IOException {
    String address = "/index.jsp";

    String type = request.getParameter("type");
    if (type == null) type = "unknown";

    String id = request.getParameter("id");
    try {
      Long.parseLong(id);
    } catch (Exception e) {
      type = "invalid_id";
    }

    String c = request.getParameter("c");
    long cEdit = -1;
    try {
      cEdit = Long.parseLong(c);
    } catch (Exception e) { }

    String f = request.getParameter("f");
    boolean isEdit = (f != null && f.equals("edit"));

    if (type.equals("account")) {
      AccountBean accountBean = new AccountBean();
      accountBean.setId(id);
      request.setAttribute("accountBean", accountBean);
      address = "/account.jsf";

    } else if (type.equals("profile")) {
      ProfileBean profileBean = new ProfileBean();
      profileBean.setId(id);
      request.setAttribute("profileBean", profileBean);
      address = "/profile.jsf";

    } else if (type.equals("event")) {
      EventBean eventBean = new EventBean();
      eventBean.setId(id);
      address = "/event.jsf";

      // Check for editing comment
      if (cEdit > 0) {
        eventBean.setCommentToEdit(Long.parseLong(c));
        address = "/edit_event_comment.jsf";
      }

      // Check for editing event, overrides edit of comment
      if (isEdit) {
        eventBean.setCommentToEdit(0);
        address = "/edit_event.jsf";
      }

      request.setAttribute("eventBean", eventBean);

    } else if (type.equals("post")) {
      PostBean postBean = new PostBean();
      postBean.setId(id);
      address = "/post.jsf";

      // Check for editing comment
      if (cEdit > 0) {
        postBean.setCommentToEdit(Long.parseLong(c));
        address = "/edit_post_comment.jsf";
      }

      // Check for editing post, overrides edit of comment
      if (isEdit) {
        postBean.setCommentToEdit(0);
        address = "/edit_post.jsf";
      }

      request.setAttribute("postBean", postBean);

    } else if (type.equals("song")) {
      SongBean songBean = new SongBean();
      songBean.setId(id);
      address = "/song.jsf";

      // Check for editing comment
      if (cEdit > 0) {
        songBean.setCommentToEdit(Long.parseLong(c));
        address = "/edit_song_comment.jsf";
      }

      // Check for editing song, overrides edit of comment
      if (isEdit) {
        songBean.setCommentToEdit(0);
        address = "/edit_song.jsf";
      }

      request.setAttribute("songBean", songBean);

    } else if (type.equals("invalid_id")) {
      // Forward to error handling page here w/message
    }

    RequestDispatcher dispatcher = request.getRequestDispatcher(address);
    dispatcher.forward(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
    doGet(request, response);
  }
}
