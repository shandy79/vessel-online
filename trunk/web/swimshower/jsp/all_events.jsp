<%@ page language="java" contentType="text/html;charset=iso-8859-1" 
    import="com.swimshower.dao.EventDAO, com.swimshower.dao.EventHibernateDAO, com.swimshower.model.Event" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
  EventDAO eventDAO = new EventHibernateDAO();
  java.util.List<Event> allEvents = eventDAO.findAllSorted();
  pageContext.setAttribute("allEvents", allEvents);
%>

<h1>Events</h1>
<hr />
<table class="post" summary="all events">
<tr class="hdrw">
  <th scope="col">Date</th><th scope="col">Title</th><th scope="col">Host/Venue</th>
  <th scope="col">Category</th><th scope="col">Last Activity</th>
</tr>

<c:forEach items="${allEvents}" var="e">
<tr>
  <td style="font-size:8pt;text-align:center;"><fmt:formatDate value="${e.date}" pattern="M/d/yyyy h:mm a" /></td>
  <td><a href="nav?type=event&amp;id=<c:out value="${e.id}" />"><c:out value="${e.title}" escapeXml="false" /></a></td>
  <td><c:out value="${e.creator}" escapeXml="false" /></td>
  <td style="font-size:8pt;"><c:out value="${e.subject}" escapeXml="false" /></td>
  <td style="font-size:8pt;"><fmt:formatDate value="${e.latestComment.date}" pattern="M/d/yyyy h:mm a" /> by
    <c:out value="${e.latestComment.contributor.username}" /></td>
</tr>
</c:forEach>

</table>
