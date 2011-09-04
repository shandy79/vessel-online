package com.swimshower.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.vesselonline.dao.PersonDAO;
import org.vesselonline.dao.PersonHibernateDAO;
import org.vesselonline.model.Person;
import com.swimshower.beans.ProfileBean;
import com.swimshower.model.SwimShowerResource;

/** LoginContentServlet
 *  Steven Handy
 */
public class LoginContentServlet extends HttpServlet {
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
                   throws ServletException, IOException {

    PrintWriter out = response.getWriter();
    HttpSession session = request.getSession();

    // If the last request updated the profile of the logged in user,
    // put new Person object into the session for the user.
    ProfileBean pBean = (ProfileBean) request.getAttribute("profileBean");
    if (pBean != null && pBean.getPerson() != null && pBean.getUsername() != null &&
        pBean.getUsername().equals(request.getRemoteUser())) {
      session.setAttribute("user", pBean.getPerson());
    }

    // Only display login profile if there is a user and an established
    // session, and the request is not for logout.
    if (request.getRemoteUser() != null && ! session.isNew() && ! request.getRequestURI().endsWith("logout.jsp")) {
      Person user = (Person) session.getAttribute("user");

      // If user is not yet present in the session, retrieve it from db and store in session
      if (user == null) {
        PersonDAO personDAO = new PersonHibernateDAO();
        user = personDAO.findByUsername(request.getRemoteUser());

        session.setAttribute("user", user);
      }

      out.println("<a href=\"/nav?type=profile&amp;id=" + user.getId() + "\" class=\"hdln\">" + user.getUsername() + "</a><br />");

      if (user.getIcon() != null && ! user.getIcon().equals("")) {
        out.println("<img src=\"" + SwimShowerResource.SERVER_PREFIX + SwimShowerResource.UPLOAD_DIR + "/icon/" + user.getUsername() + "." + user.getIcon() + "\" alt=\"icon\" style=\"padding-left:3px;\" /><br />");
      }

      out.println("<span style=\"white-space:nowrap;\">" + user.getRole() + "<br />Since " + SwimShowerResource.DATE_FORMAT.format(user.getJoinDate()) + "</span>");
      out.println("<ul>\n  <!--li><a href=\"/summary.cgi?id=steve\">View Your Items</a></li-->");
      out.println("  <li><a href=\"/nav?type=profile&amp;id=" + user.getId() + "\">Edit Profile</a></li>");
      out.println("  <li><a href=\"/nav?type=account&amp;id=" + user.getId() + "\">Edit Account</a></li>");

      if (user.getRole() != null && (user.getRole().equals("Admin") || user.getRole().equals("Band Member"))) {
        out.println("<li>New: <a href=\"/new_song.jsf\">Song</a> | <a href=\"/new_event.jsf\">Event</a> | <a href=\"/new_post.jsf\">Post</a></li>"); 
      }

      out.println("  <li><a href=\"/logout.jsp\">Log Out</a></li>\n</ul>");

    } else {
      out.println("<ul>\n  <li><a href=\"/login.jsp\">Log in</a> to Swim Shower</li>");
      out.println("<li>New user?  <a href=\"/register.jsf\">Register here!</a></li>\n</ul>");
    }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
    doGet(request, response);
  }
}
