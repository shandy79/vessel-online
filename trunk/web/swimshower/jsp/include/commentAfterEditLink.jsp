<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

    </td></tr>
  <tr><td style="vertical-align:top;">

  <c:choose>
  <c:when test="${cmnt.contributor.role eq \"Admin\" or cmnt.contributor.role eq \"Band Member\"}">
    <c:out value="${cmnt.description}" escapeXml="false" />
  </c:when>
  <c:otherwise>
    <c:out value="${cmnt.description}" />
  </c:otherwise>
  </c:choose>

  <c:if test="${cmnt.edited ne null}">
    <br />&nbsp;<br /><span style="font-size:8pt;">
      [Updated <fmt:formatDate value="${cmnt.edited}" pattern="M/d/yyyy h:mm a" />]</span>
  </c:if>

  </td></tr>
