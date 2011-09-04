<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  <tr class="hdrw"><td><span class="hdln">${eventBean.title}</span>

  <c:if test="${eventBean.source ne \"\"}">
    &nbsp;&nbsp;<a href="${eventBean.source}">[ Link ]</a>
  </c:if>

    </td><td style="text-align:right;">Posted by
      <a href="nav?type=profile&amp;id=${eventBean.contributor.id}">${eventBean.contributor.username}</a></td></tr>
  <tr><td colspan="2"><span class="cat">[${eventBean.category}]</span> ${eventBean.description}

  <c:if test="${eventBean.edited ne \"\"}">
    <br />&nbsp;<br /><span style="font-size:8pt;">[Updated ${eventBean.edited} by ${eventBean.editor}]</span>
  </c:if>

  </td></tr>
  <tr style="text-align:center;"><td><a href="${eventBean.hostURL}">${eventBean.host}</a>
      <br />${eventBean.address}<br />${eventBean.city}, ${eventBean.state}</td>
    <td><b>From</b> ${eventBean.startDateString}<br /><b>to</b> ${eventBean.endDateString}</td></tr>
  <tr class="hdrw"><td colspan="2">
    <a href="nav?type=event&amp;id=${eventBean.id}"><img src="img/bookmark_sm.gif" alt="bookmark" /></a>
    <a href="meta?type=event&amp;id=${eventBean.id}"><img src="img/metadata_sm.gif" alt="RDF metadata" /></a>

  <!-- Complete the </td></tr> here, with optional Edit link -->
