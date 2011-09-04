<%@ page language="java" contentType="text/html;charset=iso-8859-1" pageEncoding="iso-8859-1" 
    import="com.swimshower.dao.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

  <br />&nbsp;
</div>
<div id="tools" class="column">

<!-- Dynamic content for most recent music, events, posts -->
<% 
  SongDAO recentSongDAO = new SongHibernateDAO();
  java.util.List recentSongs = recentSongDAO.findRecentSorted();

  EventDAO upcomingEventDAO = new EventHibernateDAO();
  java.util.List upcomingEvents = upcomingEventDAO.findUpcomingSorted();

  PostDAO recentPostDAO = new PostHibernateDAO();
  java.util.List recentPosts = recentPostDAO.findRecentSorted();

  pageContext.setAttribute("songs", recentSongs);
  pageContext.setAttribute("events", upcomingEvents);
  pageContext.setAttribute("posts", recentPosts);
%>

  <h2>Music</h2>
  <div id="playlist" class="box">
    <p style="text-align:center;">
      <img src="/img/clouds_or_mountains/cldsrmtns_sm.gif" alt="Clouds Or Mountains" /></p>
    <p>Introducing <a href="/album/clouds_or_mountains.jsp">Clouds Or
      Mountains</a>, the second full-length album from <b>Swim Shower</b>,
      featuring the single <a href="/nav?type=song&amp;id=7">Winter
      Worries</a>.</p>
<!--
    <ul>

    <c:forEach items="${songs}" var="s">
      <li><a href="/nav?type=song&amp;id=<c:out value="${s.id}" />" title="<fmt:formatDate value="${s.date}" pattern="MM/dd" />"><c:out value="${s.title}" /></a>&nbsp;&nbsp;<c:out value="${s.subject}" /></li>
    </c:forEach>

    </ul>&nbsp;&nbsp;
    <a href="/all_songs.jsp" title="view all songs"><img src="/img/viewall_sm.gif" alt="View All" /></a>
-->
  </div>
  <h2>Events</h2>
  <div id="events" class="box">
    <ul>

    <c:forEach items="${events}" var="e">
      <li><fmt:formatDate value="${e.date}" pattern="MM/dd" />&nbsp;&nbsp;<a href="/nav?type=event&amp;id=<c:out value="${e.id}" />" title="<c:out value="${e.subject}" escapeXml="false" />"><c:out value="${e.title}" /></a></li>
    </c:forEach>

    </ul>&nbsp;&nbsp;
    <a href="/all_events.jsp" title="view all events"><img src="/img/viewall_sm.gif" alt="View All" /></a>
    <!--a href="/feed?type=event" title="events XML feeds"><img src="/img/xml_sm.gif" alt="XML" /></a-->
  </div>
  <h2>Posts</h2>
  <div id="posts" class="box">
    <ul>

    <c:forEach items="${posts}" var="p">
      <li><c:out value="${p.contributor.username}" />&nbsp;&nbsp;<a href="/nav?type=post&amp;id=<c:out value="${p.id}" />" title="<fmt:formatDate value="${p.date}" pattern="MM/dd" />"><c:out value="${p.title}" /></a></li>
    </c:forEach>

    </ul>&nbsp;&nbsp;
    <a href="/all_posts.jsp" title="view all posts"><img src="/img/viewall_sm.gif" alt="View All" /></a>
    <!--a href="/feed?type=post" title="posts XML feeds"><img src="/img/xml_sm.gif" alt="XML" /></a-->
  </div>
</div>

<%@ include file="../include/siteFooter.jsp" %>
