<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>

<% if (! session.isNew()) session.invalidate(); %>

<h1>Logout Successful</h1>
<p>You have successfully logged out!  If you have done this by mistake,
  <a href="login.jsp">log in</a> again here.</p>
