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

<h:form id="editEventForm" onsubmit="return fixAction(this, 'edit_event.jsf');">
  <h:inputHidden value="#{eventBean.id}" id="id" />
  <h3>Edit This Event</h3>
  <table summary="edit event form">

  <%@ include file="WEB-INF/include/eventForm.jsp" %>

  </table>
  <p><h:commandButton value="Submit" styleClass="btn" action="#{eventBean.navigate}" />
    <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;
    <input type="button" value="Cancel" onclick="location.href='nav?type=event&amp;id=${eventBean.id}'" class="btn" />
    &nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
</h:form>
</f:view>
