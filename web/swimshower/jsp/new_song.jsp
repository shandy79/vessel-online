<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>

<%= com.swimshower.model.SwimShowerResource.createSimpleNavBanner("songs") %>

<h:form id="newSongForm" enctype="multipart/form-data">
  <h3>New Song</h3>
  <table summary="create song form">

  <%@ include file="WEB-INF/include/songForm.jsp" %>

  </table>
  <p><h:commandButton value="Submit" styleClass="btn" action="#{songBean.navigate}" />
    <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
</h:form>
</f:view>
