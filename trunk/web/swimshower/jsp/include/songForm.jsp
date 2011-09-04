<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t" %>

    <tr><td class="lbl">* Title:</td><td>
      <h:inputText value="#{songBean.title}" id="title" required="true" size="32" maxlength="64" styleClass="txt">
        <f:validateLength maximum="64" />
      </h:inputText> <h:message for="title" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Length:</td><td>
      <h:inputText value="#{songBean.length}" id="category" size="5" maxlength="5" styleClass="txt">
        <f:validateLength maximum="5" />
        <t:validateRegExpr pattern="\d?\d:\d\d" />
      </h:inputText> (e.g. 2:15)
      &nbsp;&nbsp;<span class="lbl">File Size:</span>
      <h:inputText value="#{songBean.sizeInMegabytes}" id="sizeInMegabytes" size="4" maxlength="4" styleClass="txt">
        <f:validateDoubleRange minimum="0.0" maximum="5.0" />
      </h:inputText> (in MB, e.g. 4.2)
      <h:message for="category" styleClass="msg" /> <h:message for="sizeInMegabytes" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Description:</td><td>
      <h:inputTextarea value="#{songBean.description}" id="description" required="true" rows="6" cols="60" />
      <h:message for="description" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">* Written By:</td><td>
      <h:inputText value="#{songBean.author}" id="author" required="true" size="32" maxlength="64" styleClass="txt">
        <f:validateLength maximum="64" />
      </h:inputText> <h:message for="author" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">Lyrics:</td><td>
      <h:inputTextarea value="#{songBean.lyrics}" id="lyrics" rows="12" cols="60" />
    </td></tr>
    <tr><td class="lbl">Song URL:</td><td>
      <h:inputText value="#{songBean.source}" id="source" size="64" maxlength="128" styleClass="txt">
        <f:validateLength maximum="128" />
        <t:validateRegExpr pattern="http://[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(:\d+)?(/[A-Za-z0-9~_\.-]*)*((\?|#)[A-Za-z0-9_/~+\.;,&=-]*)?" />
      </h:inputText> <h:message for="source" styleClass="msg" />
    </td></tr>
    <tr><td class="lbl">mp3 File:</td><td>
      <t:inputFileUpload value="#{songBean.songFile}" id="songFile" styleClass="txt" /><br />
      <span style="font-size:8pt;">(supports <b>mp3</b> format up to <b>5.0MB</b>; will override a user-supplied
      <b>Song URL</b> and <b>File Size</b>)</span>
    </td></tr>