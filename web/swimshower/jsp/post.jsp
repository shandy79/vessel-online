<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>
<table style="border:0;width:100%;" cellpadding="0" summary="layout"><tr>
  <td><h1>Posts</h1></td>
  <td style="text-align:right;vertical-align:bottom;">

  <c:if test="${postBean.previousId > 0}">
    <a href="nav?type=post&amp;id=${postBean.previousId}"><img src="img/previous.gif" alt="previous post" /></a>
  </c:if>
  <c:if test="${postBean.nextId > 0}">
    <a href="nav?type=post&amp;id=${postBean.nextId}"><img src="img/next.gif" alt="next post" /></a>
  </c:if>

    &nbsp;&nbsp;<a href="all_posts.jsp"><img src="img/viewall.gif" alt="view all posts" /></a></td></tr>
</table>
<hr />

<c:if test="${postBean.id > 0}">
  <table class="post" summary="post item">

  <%@ include file="WEB-INF/include/postInfo.jsp" %>

  <c:if test="${user.role eq \"Admin\" or user.role eq \"Band Member\"}">
    &nbsp;&nbsp;<a href="nav?type=post&amp;id=${postBean.id}&amp;f=edit"><img src="img/edit_sm.gif" alt="edit post" /></a>
  </c:if>

    </td></tr>
  </table>
</c:if>

<h3>Add A Comment</h3>

<c:choose>
<c:when test="${user.username ne null and user.username ne \"\"}">
  <h:form id="newPostCommentForm" onsubmit="return fixAction(this, 'post.jsf');">
    <h:inputHidden value="#{postBean.id}" id="id" />
    <table summary="new post comment form">
    <tr><td class="lbl">User:</td><td><b>${user.username}</b><span style="font-size:8pt;font-style:italic;">
      (If you're not <b>${user.username}</b>, please <a href="logout.jsp">log out</a>.)</span></td></tr>
    <tr><td class="lbl">Location:</td><td>
      <h:inputText value="#{postBean.commentLocation}" id="commentLocation" size="24" maxlength="32" styleClass="txt">
        <f:validateLength maximum="32" />
      </h:inputText> <h:message for="commentLocation" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Comment:</td><td>
      <h:inputTextarea value="#{postBean.comment}" id="comment" required="true" rows="2" cols="56" />
      <h:message for="comment" styleClass="msg" />
    </td></tr>
    </table>
    <p><h:commandButton value="Submit" styleClass="btn" action="#{postBean.navigate}" />
      <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
  </h:form>
</c:when>
<c:otherwise>
  You must be <a href="login.jsp">logged in</a> to submit comments.
</c:otherwise>
</c:choose>

<c:if test="${postBean.id > 0}">
<h3>Comments</h3>
<table class="post" summary="post comments">

<c:forEach items="${postBean.comments}" var="cmnt">

  <%@ include file="WEB-INF/include/commentBeforeEditLink.jsp" %>

  <c:if test="${user.role eq \"Admin\" or user.username eq cmnt.contributor.username}">
    &nbsp;&nbsp;<a href="nav?type=post&amp;id=${postBean.id}&amp;c=${cmnt.id}"><img src="img/edit_sm.gif" alt="edit post comment" /></a>
  </c:if>

  <%@ include file="WEB-INF/include/commentAfterEditLink.jsp" %>

</c:forEach>

</table>
</c:if>
</f:view>
