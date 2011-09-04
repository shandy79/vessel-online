<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>

<%= com.swimshower.model.SwimShowerResource.createSimpleNavBanner("events") %>

<c:if test="${eventBean.id > 0}">
  <table class="post" summary="event item">

  <%@ include file="WEB-INF/include/eventInfo.jsp" %>

    </td></tr>
  </table>
</c:if>

<c:if test="${user.role eq \"Admin\" or user.username eq eventBean.commenterName or user.username eq helper.username}">
<h:form id="editEventCommentForm" onsubmit="return fixAction(this, 'edit_event_comment.jsf');">
  <h:inputHidden value="#{eventBean.id}" id="id" />
  <h:inputHidden value="#{eventBean.commentToEdit}" id="commentToEdit" />

  <h3>Edit Your Comment</h3>
  <table class="post" summary="event comments">

  <c:if test="${eventBean.id > 0}">
    <jsp:useBean id="helper" scope="session" class="org.vesselonline.beans.FormHelperBean" />
      <jsp:setProperty name="helper" property="username" value="${eventBean.commenterName}" />
      <jsp:setProperty name="helper" property="id" value="${eventBean.id}" />

    <tr class="hdrw"><td>
      <a href="nav?type=profile&amp;id=${eventBean.commenterId}" class="hdln">${eventBean.commenterName}</a>&nbsp;
    </td><td style="text-align:right;">
      Posted ${eventBean.commentDate} from ${eventBean.commentLocation}
    </td></tr>
  </c:if>

  <tr><td colspan="2">
    <h:inputTextarea value="#{eventBean.comment}" id="description" required="true" rows="4" cols="56" />
    <br /><h:commandButton value="Submit" styleClass="btn" action="#{eventBean.navigate}" />
    <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;
    <input type="button" value="Cancel" onclick="location.href='nav?type=event&amp;id=${helper.id}'" class="btn" />
    &nbsp;&nbsp;&nbsp;&nbsp;<h:message for="description" styleClass="msg" />
  </td></tr>
  </table>
</h:form>
</c:if>
</f:view>
