<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  <tr class="hdrw"><td><span class="hdln">${postBean.title}</span></td>
    <td style="text-align:right;">Posted ${postBean.date} by
      <a href="nav?type=profile&amp;id=${postBean.contributor.id}">${postBean.contributor.username}</a></td></tr>
  <tr><td colspan="2"><span class="cat">[${postBean.category}]</span> ${postBean.description}

  <c:if test="${postBean.edited ne \"\"}">
    <br />&nbsp;<br /><span style="font-size:8pt;">[Updated ${postBean.edited} by ${postBean.editor}]</span>
  </c:if>

  </td></tr>
  <tr class="hdrw"><td colspan="2">
    <a href="nav?type=post&amp;id=${postBean.id}"><img src="img/bookmark_sm.gif" alt="bookmark" /></a>
    <a href="meta?type=post&amp;id=${postBean.id}"><img src="img/metadata_sm.gif" alt="RDF metadata" /></a>

  <c:choose>
    <c:when test="${postBean.author ne \"\" and postBean.source ne \"\"}">
      &nbsp;&nbsp;From <a href="${postBean.source}">${postBean.author}</a>
    </c:when>
    <c:when test="${postBean.author ne \"\"}">
      &nbsp;&nbsp;From ${postBean.author}
    </c:when>
    <c:when test="${postBean.source ne \"\"}">
      &nbsp;&nbsp;From <a href="${postBean.source}">${postBean.source}</a>
    </c:when>
  </c:choose>

  <!-- Complete the </td></tr> here, with optional Edit link -->
