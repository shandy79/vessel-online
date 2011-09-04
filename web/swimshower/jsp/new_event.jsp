<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>

<%= com.swimshower.model.SwimShowerResource.createSimpleNavBanner("events") %>

<h:form id="newEventForm">
  <h3>New Event</h3>
  <table summary="create event form">

  <%@ include file="WEB-INF/include/eventForm.jsp" %>

  </table>
  <p><h:commandButton value="Submit" styleClass="btn" action="#{eventBean.navigate}" />
    <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
</h:form>
</f:view>
