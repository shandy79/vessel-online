<%@ page language="java" contentType="text/html;charset=iso-8859-1" 
    import="org.vesselonline.dao.PersonDAO, org.vesselonline.dao.PersonHibernateDAO, org.vesselonline.model.Person" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
  PersonDAO personDAO = new PersonHibernateDAO();
  java.util.List<Person> allProfiles = personDAO.findAllSorted();
  pageContext.setAttribute("allProfiles", allProfiles);
%>

<h1>Profiles</h1>
<hr />
<table class="post" summary="all profiles">
<tr class="hdrw">
  <th scope="col">Name</th><th scope="col">Username</th><th scope="col">Type</th><th scope="col">Joined</th>
</tr>

<c:forEach items="${allProfiles}" var="p">
<tr>
  <td><c:out value="${p.lastname}" />, <c:out value="${p.firstname}" /></td>
  <td><a href="nav?type=profile&amp;id=<c:out value="${p.id}" />"><c:out value="${p.username}" /></a></td>
  <td style="text-align:center;"><c:out value="${p.role}" /></td> 
  <td style="text-align:center;"><fmt:formatDate value="${p.joinDate}" pattern="M/d/yyyy h:mm a" /></td>
</tr>
</c:forEach>

</table>
