<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>

<%= com.swimshower.model.SwimShowerResource.createSimpleNavBanner("songs") %>

<c:if test="${songBean.id > 0}">
  <table class="post" summary="song item">

  <%@ include file="WEB-INF/include/songInfo.jsp" %>

    </td></tr>
  </table>
</c:if>

<h:form id="editSongForm" enctype="multipart/form-data" onsubmit="return fixAction(this, 'edit_song.jsf');">
  <h:inputHidden value="#{songBean.id}" id="id" />
  <h3>Edit This Song</h3>
  <table summary="edit song form">

  <%@ include file="WEB-INF/include/songForm.jsp" %>

  </table>
  <p><h:commandButton value="Submit" styleClass="btn" action="#{songBean.navigate}" />
    <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;
    <input type="button" value="Cancel" onclick="location.href='nav?type=song&amp;id=${songBean.id}'" class="btn" />
    &nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
</h:form>
</f:view>
