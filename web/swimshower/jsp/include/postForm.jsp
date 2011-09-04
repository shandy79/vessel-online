<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

    <tr><td class="lbl">* Title:</td><td>
      <h:inputText value="#{postBean.title}" id="title" required="true" size="32" maxlength="64" styleClass="txt">
        <f:validateLength maximum="64" />
      </h:inputText> <h:message for="title" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Category:</td><td>
      <h:inputText value="#{postBean.category}" id="category" size="16" maxlength="32" styleClass="txt">
        <f:validateLength maximum="32" />
      </h:inputText> <h:message for="category" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Content:</td><td>
      <h:inputTextarea value="#{postBean.description}" id="description" required="true" rows="6" cols="60" />
      <h:message for="description" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Web Site Name:</td><td>
      <h:inputText value="#{postBean.author}" id="author" size="32" maxlength="64" styleClass="txt">
        <f:validateLength maximum="64" />
      </h:inputText> <h:message for="author" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Web Site URL:</td><td>
      <h:inputText value="#{postBean.source}" id="source" size="64" maxlength="128" styleClass="txt">
        <f:validateLength maximum="128" />
        <t:validateRegExpr pattern="http://[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(:\d+)?(/[A-Za-z0-9~_\.-]*)*((\?|#)[A-Za-z0-9_/~+\.;,&=-]*)?" />
      </h:inputText> <h:message for="source" styleClass="msg" />
    </td></tr>
