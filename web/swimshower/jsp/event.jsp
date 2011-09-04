<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
<table style="border:0;width:100%;" cellpadding="0" summary="layout"><tr>
  <td><h1>Events</h1></td>
  <td style="text-align:right;vertical-align:bottom;">

  <c:if test="${eventBean.previousId > 0}">
    <a href="nav?type=event&amp;id=${eventBean.previousId}"><img src="img/previous.gif" alt="previous event" /></a>
  </c:if>
  <c:if test="${eventBean.nextId > 0}">
    <a href="nav?type=event&amp;id=${eventBean.nextId}"><img src="img/next.gif" alt="next event" /></a>
  </c:if>

    &nbsp;&nbsp;<a href="all_events.jsp"><img src="img/viewall.gif" alt="view all events" /></a></td></tr>
</table>
<hr />

<c:if test="${eventBean.id > 0}">
  <table class="post" summary="event item">

  <%@ include file="WEB-INF/include/eventInfo.jsp" %>

  <c:if test="${user.role eq \"Admin\" or user.role eq \"Band Member\"}">
    <h:form id="editEventLinkForm" onsubmit="return fixAction(this, 'event.jsf');">
      <h:inputHidden value="#{eventBean.id}" id="id" />
      <h:commandButton action="edit" image="img/edit_sm.gif" alt="edit event" />
    </h:form>
  </c:if>

    </td></tr>
  </table>
</c:if>

<h3>Add A Comment</h3>

<c:choose>
<c:when test="${user.username ne null and user.username ne \"\"}">
  <h:form id="newEventCommentForm" onsubmit="return fixAction(this, 'event.jsf');">
    <h:inputHidden value="#{eventBean.id}" id="id" />
    <table summary="new event comment form">
    <tr><td class="lbl">User:</td><td><b>${user.username}</b><span style="font-size:8pt;font-style:italic;">
      (If you're not <b>${user.username}</b>, please <a href="logout.jsp">log out</a>.)</span></td></tr>
    <tr><td class="lbl">Location:</td><td>
      <h:inputText value="#{eventBean.commentLocation}" id="commentLocation" size="24" maxlength="32" styleClass="txt">
        <f:validateLength maximum="32" />
      </h:inputText> <h:message for="commentLocation" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Comment:</td><td>
      <h:inputTextarea value="#{eventBean.comment}" id="comment" required="true" rows="2" cols="56" />
      <h:message for="comment" styleClass="msg" />
    </td></tr>
    </table>
    <p><h:commandButton value="Submit" styleClass="btn" action="#{eventBean.navigate}" />
      <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
  </h:form>
</c:when>
<c:otherwise>
  You must be <a href="login.jsp">logged in</a> to submit comments.
</c:otherwise>
</c:choose>

<c:if test="${eventBean.id > 0}">
<h3>Comments</h3>
<table class="post" summary="event comments">

<c:forEach items="${eventBean.comments}" var="cmnt">

  <%@ include file="WEB-INF/include/commentBeforeEditLink.jsp" %>

  <c:if test="${user.role eq \"Admin\" or user.username eq cmnt.contributor.username}">
    &nbsp;&nbsp;<a href="nav?type=event&amp;id=${eventBean.id}&amp;c=${cmnt.id}"><img src="img/edit_sm.gif" alt="edit event comment" /></a>
  </c:if>

  <%@ include file="WEB-INF/include/commentAfterEditLink.jsp" %>

</c:forEach>

</table>
</c:if>
</f:view>
