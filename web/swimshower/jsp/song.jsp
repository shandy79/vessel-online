<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
<table style="border:0;width:100%;" cellpadding="0" summary="layout"><tr>
  <td><h1>Songs</h1></td>
  <td style="text-align:right;vertical-align:bottom;">

  <c:if test="${songBean.previousId > 0}">
    <a href="nav?type=song&amp;id=${songBean.previousId}"><img src="img/previous.gif" alt="previous song" /></a>
  </c:if>
  <c:if test="${songBean.nextId > 0}">
    <a href="nav?type=song&amp;id=${songBean.nextId}"><img src="img/next.gif" alt="next song" /></a>
  </c:if>

    &nbsp;&nbsp;<a href="all_songs.jsp"><img src="img/viewall.gif" alt="view all songs" /></a></td></tr>
</table>
<hr />

<c:if test="${songBean.id > 0}">
  <table class="post" summary="song item">

  <%@ include file="WEB-INF/include/songInfo.jsp" %>

  <c:if test="${user.role eq \"Admin\" or user.role eq \"Band Member\"}">
    &nbsp;&nbsp;<a href="nav?type=song&amp;id=${songBean.id}&amp;f=edit"><img src="img/edit_sm.gif" alt="edit song" /></a>
  </c:if>

    </td></tr>
  </table>

  <c:if test="${songBean.source ne \"\"}">
    <h3>Listen/Download</h3>
    <embed src="${songBean.source}" height="48" width="480" autoplay="false"></embed>
    <!--object data="${songBean.source}" type="audio/mpeg" height="48" width="480"></object-->
    <br />If the audio player controls do not appear above, click here to listen to
    <a href="${songBean.source}">${songBean.title}</a>.
  </c:if>
</c:if>

<h3>Add A Comment</h3>

<c:choose>
<c:when test="${user.username ne null and user.username ne \"\"}">
  <h:form id="newSongCommentForm" onsubmit="return fixAction(this, 'song.jsf');">
    <h:inputHidden value="#{songBean.id}" id="id" />
    <table summary="new song comment form">
    <tr><td class="lbl">User:</td><td><b>${user.username}</b><span style="font-size:8pt;font-style:italic;">
      (If you're not <b>${user.username}</b>, please <a href="logout.jsp">log out</a>.)</span></td></tr>
    <tr><td class="lbl">Location:</td><td>
      <h:inputText value="#{songBean.commentLocation}" id="commentLocation" size="24" maxlength="32" styleClass="txt">
        <f:validateLength maximum="32" />
      </h:inputText> <h:message for="commentLocation" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Comment:</td><td>
      <h:inputTextarea value="#{songBean.comment}" id="comment" required="true" rows="2" cols="56" />
      <h:message for="comment" styleClass="msg" />
    </td></tr>
    </table>
    <p><h:commandButton value="Submit" styleClass="btn" action="#{songBean.navigate}" />
      <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
  </h:form>
</c:when>
<c:otherwise>
  You must be <a href="login.jsp">logged in</a> to submit comments.
</c:otherwise>
</c:choose>

<c:if test="${songBean.id > 0}">
<h3>Comments</h3>
<table class="post" summary="song comments">

<c:forEach items="${songBean.comments}" var="cmnt">

  <%@ include file="WEB-INF/include/commentBeforeEditLink.jsp" %>

  <c:if test="${user.role eq \"Admin\" or user.username eq cmnt.contributor.username}">
    &nbsp;&nbsp;<a href="nav?type=song&amp;id=${songBean.id}&amp;c=${cmnt.id}"><img src="img/edit_sm.gif" alt="edit song comment" /></a>
  </c:if>

  <%@ include file="WEB-INF/include/commentAfterEditLink.jsp" %>

</c:forEach>

</table>
</c:if>
</f:view>
