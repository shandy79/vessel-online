<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  <tr class="hdrw"><td><span class="hdln">${songBean.title}</span></td>
    <td style="text-align:right;">Posted ${songBean.date}</td></tr>
  <tr><td colspan="2"><span class="cat">[Length:</span> ${songBean.length}<span class="cat">]&nbsp;&nbsp;
    [File Size:</span> ${songBean.sizeInMegabytes}MB<span class="cat">]</span></td></tr>
  <tr><td colspan="2">${songBean.description}

  <c:if test="${songBean.edited ne \"\"}">
    <br />&nbsp;<br /><span style="font-size:8pt;">[Updated ${songBean.edited} by ${songBean.editor}]</span>
  </c:if>

  </td></tr>

  <c:if test="${songBean.lyrics ne \"\"}">
    <tr><td colspan="2"><span class="cat">[Lyrics]</span><pre>${songBean.lyrics}</pre></td></tr>
  </c:if>

  <tr class="hdrw"><td colspan="2">
    <a href="nav?type=song&amp;id=${songBean.id}"><img src="img/bookmark_sm.gif" alt="bookmark" /></a>
    <a href="meta?type=song&amp;id=${songBean.id}"><img src="img/metadata_sm.gif" alt="RDF metadata" /></a>
    &nbsp;&nbsp;Written by <b>${songBean.author}</b>

  <!-- Complete the </td></tr> here, with optional Edit link -->
