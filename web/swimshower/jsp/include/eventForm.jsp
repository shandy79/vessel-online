<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

    <tr><td class="lbl">* Title:</td><td>
      <h:inputText value="#{eventBean.title}" id="title" required="true" size="32" maxlength="64" styleClass="txt">
        <f:validateLength maximum="64" />
      </h:inputText> <h:message for="title" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Category:</td><td>
      <h:inputText value="#{eventBean.category}" id="category" size="16" maxlength="32" styleClass="txt">
        <f:validateLength maximum="32" />
      </h:inputText> <h:message for="category" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Description:</td><td>
      <h:inputTextarea value="#{eventBean.description}" id="description" required="true" rows="6" cols="60" />
      <h:message for="description" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Event URL:</td><td>
      <h:inputText value="#{eventBean.source}" id="source" size="48" maxlength="128" styleClass="txt">
        <f:validateLength maximum="128" />
        <t:validateRegExpr pattern="http://[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)+(/[A-Za-z0-9~_\.-]*)*((\?|#)[A-Za-z0-9_/~+\.;,&=-]*)?" />
      </h:inputText> <h:message for="source" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Host/Venue:</td><td>
      <h:inputText value="#{eventBean.host}" id="host" required="true" size="32" maxlength="64" styleClass="txt">
        <f:validateLength maximum="64" />
      </h:inputText> <h:message for="host" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Host/Venue URL:</td><td>
      <h:inputText value="#{eventBean.hostURL}" id="hostURL" size="48" maxlength="128" styleClass="txt">
        <f:validateLength maximum="128" />
        <t:validateRegExpr pattern="http://[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(:\d+)?(/[A-Za-z0-9~_\.-]*)*((\?|#)[A-Za-z0-9_/~+\.;,&=-]*)?" />
      </h:inputText> <h:message for="hostURL" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Start Date/Time:</td><td>
      <t:inputDate value="#{eventBean.startDate}" id="startDate" required="true" type="both" popupCalendar="true" ampm="true" styleClass="txt" />
      <h:message for="startDate" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* End Date/Time:</td><td>
      <t:inputDate value="#{eventBean.endDate}" id="endDate" required="true" type="both" popupCalendar="true" ampm="true" styleClass="txt" />
      <h:message for="endDate" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Address:</td><td>
      <h:inputText value="#{eventBean.address}" id="address" size="32" maxlength="64" styleClass="txt">
        <f:validateLength maximum="64" />
      </h:inputText> <h:message for="address" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* City:</td><td>
      <h:inputText value="#{eventBean.city}" id="city" required="true" size="16" maxlength="32" styleClass="txt">
        <f:validateLength maximum="32" />
      </h:inputText>
      &nbsp;&nbsp;<span class="lbl">* State:</span>
      <h:inputText value="#{eventBean.state}" id="state" required="true" size="16" maxlength="16" styleClass="txt">
        <f:validateLength maximum="16" />
      </h:inputText>
      <h:message for="city" styleClass="msg" /> <h:message for="state" styleClass="msg" />
    </td></tr>
