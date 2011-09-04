<%@ page language="java" contentType="text/html;charset=iso-8859-1"
    import="com.swimshower.model.SwimShowerResource" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<f:view>
<table style="border:0;width:100%;" cellpadding="0" summary="layout"><tr>
  <td><h1>Profiles</h1></td>
  <td style="text-align:right;vertical-align:bottom;">
    <a href="all_profiles.jsp"><img src="img/viewall.gif" alt="view all profiles" /></a></td></tr>
</table>
<hr />

<c:if test="${profileBean.id > 0}">
  <h3>${profileBean.username}</h3>
  <table class="post" summary="current profile">
  <tr class="hdrw"><td rowspan="5"><a href="nav?type=profile&amp;id=${profileBean.id}" class="hdln">${profileBean.username}</a><br />

  <c:if test="${profileBean.icon ne null and profileBean.icon ne \"\"}">
    <img src="<%= SwimShowerResource.SERVER_PREFIX %><%= SwimShowerResource.UPLOAD_DIR %>/icon/${profileBean.username}.${profileBean.icon}" alt="icon" style="padding-left:3px;" /><br />
  </c:if>

    <span style="font-size:8pt;white-space:nowrap;">${profileBean.role}<br />
      Since ${profileBean.joinDate}</span><br />
      &nbsp;<br />
      <a href="nav?type=profile&amp;id=${profileBean.id}"><img src="img/bookmark_sm.gif" alt="bookmark" /></a></td>
    <td colspan="2" style="text-align:right;"><!--Last posted <a href="">an event</a> at 03/09/2006 17:45:11 from the loft-->&nbsp;</td></tr>
  <tr><td class="lbl">Name:</td><td>${profileBean.firstname} ${profileBean.lastname}</td></tr>
  <tr><td class="lbl">Email:</td><td><a href="mailto:${profileBean.email}">${profileBean.email}</a></td></tr>
  <tr><td class="lbl">Web Site:</td><td><a href="${profileBean.webSite}">${profileBean.webSite}</a></td></tr>
  <tr><td class="lbl">About Me:</td><td>${profileBean.description}</td></tr>
  </table>

  <jsp:useBean id="helper" scope="session" class="org.vesselonline.beans.FormHelperBean" />
    <jsp:setProperty name="helper" property="username" value="${profileBean.username}" />
    <jsp:setProperty name="helper" property="id" value="${profileBean.id}" />
</c:if>

<c:if test="${user.role eq \"Admin\" or user.username eq profileBean.username or user.username eq helper.username}">
  <h:form id="profileForm" enctype="multipart/form-data" onsubmit="return fixAction(this, 'profile.jsf');">
    <h:inputHidden value="#{profileBean.id}" id="id" />
    <h3>Edit Your Profile</h3>
    <table summary="edit profile form">
      <tr><td class="lbl">* First Name:</td><td>
        <h:inputText value="#{profileBean.firstname}" id="firstname" required="true" size="16" maxlength="24" styleClass="txt">
          <f:validateLength maximum="24" />
        </h:inputText> <h:message for="firstname" styleClass="msg" />
      </td></tr>
      <tr><td class="lbl">* Last Name:</td><td>
        <h:inputText value="#{profileBean.lastname}" id="lastname" required="true" size="16" maxlength="24" styleClass="txt">
          <f:validateLength maximum="24" />
        </h:inputText> <h:message for="lastname" styleClass="msg" />
      </td></tr>
      <tr><td class="lbl">Email:</td><td>
        <h:inputText value="#{profileBean.email}" id="email" size="24" maxlength="64" styleClass="txt">
          <f:validateLength maximum="64" />
          <t:validateEmail />
        </h:inputText> <h:message for="email" styleClass="msg" />
      </td></tr>
      <tr><td class="lbl">Web Site:</td><td>
        <h:inputText value="#{profileBean.webSite}" id="webSite" size="36" maxlength="64" styleClass="txt">
          <f:validateLength maximum="64" />
          <t:validateRegExpr pattern="http://[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(:\d+)?(/[A-Za-z0-9~_\.-]*)*((\?|#)[A-Za-z0-9_/~+\.;,&=-]*)?" />
        </h:inputText> <h:message for="webSite" styleClass="msg" />
      </td></tr>
      <tr><td class="lbl">About Me:</td><td>
        <h:inputTextarea value="#{profileBean.description}" id="description" rows="6" cols="60" />
      </td></tr>
      <tr><td class="lbl">Icon:</td><td>
        <t:inputFileUpload value="#{profileBean.iconFile}" id="iconFile" styleClass="txt" /><br />
      <span style="font-size:8pt;">(supports <b>bmp, gif, &amp; jpg</b> formats)</span>
      </td></tr>
    </table>
    <p><h:commandButton value="Submit" styleClass="btn" action="#{profileBean.navigate}" />
      <input type="reset" value="Reset" class="btn" />
      &nbsp;&nbsp;&nbsp;&nbsp;* - required field
      &nbsp;&nbsp;&nbsp;&nbsp;<a href="nav?type=account&amp;id=${profileBean.id}">[ Edit Account ]</a></p>
  </h:form>
</c:if>
</f:view>
