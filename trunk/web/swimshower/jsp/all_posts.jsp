<%@ page language="java" contentType="text/html;charset=iso-8859-1" 
    import="com.swimshower.dao.PostDAO, com.swimshower.dao.PostHibernateDAO, com.swimshower.model.Post" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
  PostDAO postDAO = new PostHibernateDAO();
  java.util.List<Post> allPosts = postDAO.findAllSorted();
  pageContext.setAttribute("allPosts", allPosts);
%>

<h1>Posts</h1>
<hr />
<table class="post" summary="all posts">
<tr class="hdrw">
  <th scope="col">Title</th><th scope="col">Submitted</th><th scope="col">By</th>
  <th scope="col">Comments</th><th scope="col">Last Activity</th>
</tr>

<c:forEach items="${allPosts}" var="p">
<tr>
  <td><a href="nav?type=post&amp;id=<c:out value="${p.id}" />"><c:out value="${p.title}" escapeXml="false" /></a></td>
  <td style="font-size:8pt;text-align:center;"><fmt:formatDate value="${p.date}" pattern="M/d/yyyy h:mm a" /></td>
  <td style="text-align:center;">
    <a href="nav?type=profile&amp;id=<c:out value="${p.contributor.id}" />"><c:out value="${p.contributor.username}" /></a></td>
  <td style="text-align:center;"><c:out value="${p.commentCount}" /></td>
  <td style="font-size:8pt;"><fmt:formatDate value="${p.latestComment.date}" pattern="M/d/yyyy h:mm a" /> by
    <c:out value="${p.latestComment.contributor.username}" /></td>
</tr>
</c:forEach>

</table>
