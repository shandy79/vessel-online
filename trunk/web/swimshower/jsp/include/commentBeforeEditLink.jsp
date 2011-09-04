<%@ page language="java" contentType="text/html;charset=iso-8859-1"
    import="com.swimshower.model.SwimShowerResource" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

  <tr class="hdrw"><td rowspan="2">
    <a href="nav?type=profile&amp;id=<c:out value="${cmnt.contributor.id}" />" class="hdln"><c:out value="${cmnt.contributor.username}" /></a>
    <br />
  <c:if test="${cmnt.contributor.icon ne null and cmnt.contributor.icon ne \"\"}">
    <img src="<%= SwimShowerResource.SERVER_PREFIX %><%= SwimShowerResource.UPLOAD_DIR %>/icon/<c:out value="${cmnt.contributor.username}" />.<c:out value="${cmnt.contributor.icon}" />" alt="icon" style="padding-left:3px;" /><br />
  </c:if>
    <span style="font-size:8pt;white-space:nowrap;"><c:out value="${cmnt.contributor.role}" /></span>
    </td><td style="text-align:right;">
      Posted <fmt:formatDate value="${cmnt.date}" pattern="M/d/yyyy h:mm a" /> from <c:out value="${cmnt.subject}" />
