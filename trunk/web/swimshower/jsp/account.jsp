<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<h1>Accounts</h1>

<f:view>
<c:if test="${user.role eq \"Admin\" or user.username eq accountBean.username or user.username eq helper.username}">
  <h3>${accountBean.username}</h3>

<h:form id="accountForm" onsubmit="return fixAction(this, 'account.jsf');">
<h:inputHidden value="#{accountBean.id}" id="id" />
<table summary="edit account form">
<tr><td class="lbl">* First Name:</td><td>
  <h:inputText value="#{accountBean.firstname}" id="firstname" required="true" size="16" maxlength="24" styleClass="txt">
    <f:validateLength maximum="24" />
  </h:inputText> <h:message for="firstname" styleClass="msg" />
</td></tr>
<tr><td class="lbl">* Last Name:</td><td>
  <h:inputText value="#{accountBean.lastname}" id="lastname" required="true" size="16" maxlength="24" styleClass="txt">
    <f:validateLength maximum="24" />
  </h:inputText> <h:message for="lastname" styleClass="msg" />
</td></tr>
<tr><td class="lbl">Email:</td><td>
  <h:inputText value="#{accountBean.email}" id="email" size="24" maxlength="64" styleClass="txt">
    <f:validateLength maximum="64" />
    <t:validateEmail />
  </h:inputText> <h:message for="email" styleClass="msg" />
</td></tr>
<tr><td colspan="2">&nbsp;</td></tr>
<tr><td class="lbl">Username:</td><td><b>${accountBean.username}</b></td></tr>
<tr><td class="lbl">Current Password:</td><td>
  <h:inputSecret value="#{accountBean.currentPassword}" id="currentPassword" size="16" maxlength="16" styleClass="txt">
    <f:validateLength minimum="6" maximum="16" />
  </h:inputSecret> <h:message for="currentPassword" styleClass="msg" />
</td></tr>
<tr><td class="lbl">New Password:</td><td>
  <h:inputSecret value="#{accountBean.newPassword1}" id="newPassword1" size="16" maxlength="16" styleClass="txt">
    <f:validateLength minimum="6" maximum="16" />
  </h:inputSecret> <h:message for="newPassword1" styleClass="msg" />
</td></tr>
<tr><td class="lbl">Re-type New Password:</td><td>
  <h:inputSecret value="#{accountBean.newPassword2}" id="newPassword2" size="16" maxlength="16" styleClass="txt">
    <f:validateLength minimum="6" maximum="16" />
    <t:validateEqual for="newPassword1" />
  </h:inputSecret> <h:message for="newPassword2" styleClass="msg" />
</td></tr>
</table>
<p><h:commandButton value="Submit" styleClass="btn" action="#{accountBean.navigate}" />
  <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
</h:form>

  <jsp:useBean id="helper" scope="session" class="org.vesselonline.beans.FormHelperBean" />
    <jsp:setProperty name="helper" property="username" value="${accountBean.username}" />
    <jsp:setProperty name="helper" property="id" value="${accountBean.id}" />
</c:if>
</f:view>
