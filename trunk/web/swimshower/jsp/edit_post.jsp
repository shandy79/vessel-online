<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<f:view>

<%= com.swimshower.model.SwimShowerResource.createSimpleNavBanner("posts") %>

<c:if test="${postBean.id > 0}">
  <table class="post" summary="post item">

  <%@ include file="WEB-INF/include/postInfo.jsp" %>

    </td></tr>
  </table>
</c:if>

<h:form id="editPostForm" onsubmit="return fixAction(this, 'edit_post.jsf');">
  <h:inputHidden value="#{postBean.id}" id="id" />
  <h3>Edit This Post</h3>
  <table summary="edit post form">

  <%@ include file="WEB-INF/include/postForm.jsp" %>

  </table>
  <p><h:commandButton value="Submit" styleClass="btn" action="#{postBean.navigate}" />
    <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;
    <input type="button" value="Cancel" onclick="location.href='nav?type=post&amp;id=${postBean.id}'" class="btn" />
    &nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
</h:form>
</f:view>
