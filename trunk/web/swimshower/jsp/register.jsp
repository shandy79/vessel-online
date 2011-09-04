<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

<h1>Register</h1>
<p>If you would like to post comments or be notified of the latest happenings with Swim Shower,
you'll first need to create an account.  Just provide a few pieces of information, then specify
a username and password and you'll be ready to go!  Once your account is created, you can customize
the information about yourself.</p>

<f:view>
<h:form id="registerForm">
<table summary="create account form">
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
<tr><td class="lbl">* Username:</td><td>
  <h:inputText value="#{accountBean.username}" id="username" required="true" size="16" maxlength="16" styleClass="txt">
    <f:validateLength minimum="2" />
  </h:inputText> <h:message for="username" styleClass="msg" />
</td></tr>
<tr><td class="lbl">* Password:</td><td>
  <h:inputSecret value="#{accountBean.newPassword1}" id="newPassword1" required="true" size="16" maxlength="16" styleClass="txt">
    <f:validateLength minimum="6" maximum="16" />
  </h:inputSecret> <h:message for="newPassword1" styleClass="msg" />
</td></tr>
<tr><td class="lbl">* Re-type Password:</td><td>
  <h:inputSecret value="#{accountBean.newPassword2}" id="newPassword2" required="true" size="16" maxlength="16" styleClass="txt">
    <f:validateLength minimum="6" maximum="16" />
    <t:validateEqual for="newPassword1" />
  </h:inputSecret> <h:message for="newPassword2" styleClass="msg" />
</td></tr>
</table>
<p><h:commandButton value="Submit" styleClass="btn" action="#{accountBean.navigate}" />
  <input type="reset" value="Reset" class="btn" />&nbsp;&nbsp;&nbsp;&nbsp;* - required field</p>
</h:form>
</f:view>
