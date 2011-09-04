<%@ page language="java" contentType="text/html;charset=iso-8859-1" 
    import="com.swimshower.dao.SongDAO, com.swimshower.dao.SongHibernateDAO, com.swimshower.model.Song" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
  SongDAO songDAO = new SongHibernateDAO();
  java.util.List<Song> allSongs = songDAO.findAllSorted();
  pageContext.setAttribute("allSongs", allSongs);
%>

<h1>Songs</h1>
<hr />
<table class="post" summary="all songs">
<tr class="hdrw">
  <th scope="col">Title</th><th scope="col">Written By</th><th scope="col">Length</th>
  <th scope="col">File Size</th><th scope="col">Download</th>
</tr>

<c:forEach items="${allSongs}" var="s">
<tr>
  <td><a href="nav?type=song&amp;id=<c:out value="${s.id}" />"><c:out value="${s.title}" escapeXml="false" /></a></td>
  <td><c:out value="${s.creator}" escapeXml="false" /></td>
  <td style="text-align:center;"><c:out value="${s.subject}" /></td>
  <td style="text-align:center;"><c:out value="${s.sizeInMegabytes}" />MB</td>
  <td style="font-size:8pt;text-align:center;">

  <c:choose>
    <c:when test="${s.source ne null and s.source ne \"\"}">
      <a href="<c:out value="${s.source}" />">download</a>
    </c:when>
    <c:otherwise>
      n/a
    </c:otherwise>
  </c:choose>
  
  </td>
</tr>
</c:forEach>

</table>
