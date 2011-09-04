<%@ page language="java" contentType="text/html;charset=iso-8859-1" %>

<h1>Login Successful</h1>
<p>Welcome, <b><%= request.getRemoteUser() %></b>, you have successfully logged in!
  If you are not <b><%= request.getRemoteUser() %></b>, please <a href="logout.jsp">log out</a>.</p>
