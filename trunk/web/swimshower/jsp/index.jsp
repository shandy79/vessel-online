<%@ page language="java" contentType="text/html;charset=iso-8859-1"
    import="com.swimshower.dao.*, com.swimshower.model.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Dynamic content for next event, last post -->
<% 
  EventDAO nextEventDAO = new EventHibernateDAO();
  Event nextEvent = nextEventDAO.findNextUpcoming();

  PostDAO lastPostDAO = new PostHibernateDAO();
  Post lastPost = lastPostDAO.findMostRecent();

  pageContext.setAttribute("nextEvent", nextEvent);
  pageContext.setAttribute("lastPost", lastPost);
%>

<h1>Welcome Home</h1>

<c:if test="${nextEvent ne null}">

<h3>Next Event</h3>
<table class="post" summary="latest event">
<tr class="hdrw"><td><a href="nav?type=event&amp;id=${nextEvent.id}" class="hdln">${nextEvent.title}</a></td>
  <!--td style="text-align:right;">Posted by
    <a href="nav?type=profile&amp;id=${nextEvent.contributor.id}">${nextEvent.contributor.username}</a></td></tr>
<tr><td colspan="2"><span class="cat">[${nextEvent.subject}]</span>
  ${nextEvent.description}</td></tr>
<tr class="hdrw"><td>. . . plus <a href="nav?type=event&amp;id=${nextEvent.id}">${nextEvent.commentCount} comment(s)</a>.</td-->
  <td style="text-align:right;">At <a href="${nextEvent.hostURL}">${nextEvent.creator}</a>
  in ${nextEvent.city}, ${nextEvent.state}<br />on <fmt:formatDate value="${nextEvent.date}" pattern="M/d/yyyy h:mm a" /></td></tr>
</table>

</c:if>

<c:if test="${lastPost ne null}">

<h3>Latest Post</h3>
<table class="post" summary="latest post">
<tr class="hdrw"><td><a href="nav?type=post&amp;id=${lastPost.id}" class="hdln">${lastPost.title}</a></td>
  <td style="text-align:right;">Posted <fmt:formatDate value="${lastPost.date}" pattern="M/d/yyyy h:mm a" /> by
    <a href="nav?type=profile&amp;id=${lastPost.contributor.id}">${lastPost.contributor.username}</a></td></tr>
<tr><td colspan="2"><span class="cat">[${lastPost.subject}]</span>
  ${lastPost.description}</td></tr>
<tr class="hdrw"><td colspan="2">. . . plus <a href="nav?type=post&amp;id=${lastPost.id}">${lastPost.commentCount} comment(s)</a>.</td></tr>
</table>

</c:if>

<p><b>. . . and don't forget</b> to check out the goods from our previous shows:</p>
<ul>
  <!-- li><a href="concert/20070323-conduit.jsp">Conduit's Room 449 - 3/23/2007</a></li -->
  <li><a href="concert/20061202-barkingdog.jsp">Barking Dog - 12/2/2006</a></li>
  <li><a href="concert/20060602-prince.jsp">Prince Theatre - 6/2/2006</a></li>
  <li><a href="concert/20060217-currentspace.jsp">Current Space - 2/17/2006</a></li>
</ul>
<br />

<p style="text-align:center;">
  <img src="swimfile/photo/20060602-prince/full_band4.jpg" alt="band photo - 6/2" />
</p>

<p>In a tiny apartment without air conditioning, <b>Swim Shower</b> was born.
  Remembering swims at grandparents' before bedtime as a child, the shower
  would run cold to cool off the humidity on the skin. It was here a tiny
  record was made.  Since that quiet recording, growth into a five-piece and
  recording a new, fuller record, <b>Swim Shower</b> is now an indie-country
  outfit; complete with all the quiet hum and blasting noise of a favorite
  headphone record.  Combining guitars, organ, bass, banjo, mandolin, horns,
  and drums layered with a nostalgic lyric, <b>Swim Shower</b> now exists in
  your backyard to sing you to sleep.
  For more information about our music and upcoming shows and events, contact us at
  <a href="mailto:<%= SwimShowerResource.INFO_EMAIL %>"><%= SwimShowerResource.INFO_EMAIL %></a>.</p>

<p style="text-align:center;"><img src="img/logo.jpg" alt="logo" /></p>

<div id="splash" class="overlay">
  <br />&nbsp;<br />&nbsp;<br />
  <img src="img/clouds_or_mountains/cldsrmtns.gif" alt="Clouds Or Mountains" />
  <br />
  <a href="album/clouds_or_mountains.jsp"><img src="img/clouds_or_mountains/splash_link1.gif" alt="Clouds Or Mountains" /></a>
  <br />
  <a href="nav?type=event&id=22"><img src="img/clouds_or_mountains/splash_link2.gif" alt="Clouds Or Mountains" /></a>
  <br />
  <a href="javascript:void(0);" onclick="return yuiHide('splash');">CLOSE [X]</a>
</div>
