<%@ page language="java" contentType="text/html;charset=iso-8859-1"
    import="com.swimshower.model.SwimShowerResource" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Swim Shower</title>
<meta http-equiv="Content-Type" content="text/html;charset=iso-8859-1" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<link rel="stylesheet" type="text/css" href="/css/site.css" />
<script type="text/javascript" src="/js/swimshower.js"></script>

<!-- Any Javascript, additional style sheets, <base>, etc. is to be output
     by this JSP, including the </head><body> section with any onload
     actions for the <body> -->
<!-- <%= request.getRequestURI() %> -->

<% if (request.getRequestURI().endsWith("/index.jsp") || request.getRequestURI().equals("/")) { %>
  <script type="text/javascript" src="/js/yui/yahoo.js"></script>
  <script type="text/javascript" src="/js/yui/dom.js"></script>
  <script type="text/javascript" src="/js/yuiLib.js"></script>
  </head>
  <body onload="return setOverlay('splash');">
<% } else { %>
  </head>
  <body>
<% } %>

<map name="banner_map" id="banner_map_id">
  <area shape="rect" coords="343,90,449,126" alt="Swim Shower home" href="/index.jsp" />
  <area shape="rect" coords="476,96,578,125" alt="Swim Shower music" href="/all_songs.jsp" />
  <area shape="rect" coords="600,98,710,132" alt="Swim Shower events" href="/all_events.jsp" />
  <area shape="rect" coords="726,104,827,137" alt="Swim Shower posts" href="/all_posts.jsp" />
  <area shape="rect" coords="17,5,418,63" alt="Swim Shower home" href="/index.jsp" />
</map>
<div id="container">
<div id="banner">
  <h1><img src="/img/tree_bg.jpg" alt="Swim Shower" width="960" height="140" usemap="#banner_map" /></h1>
</div>
<div id="navigator" class="column">
  <h2>Navigate</h2>
  <div id="nav_links" class="box">
    <ul>
      <li><a href="/index.jsp">Home</a></li>
      <li><a href="/all_songs.jsp">Music</a> | <a href="/all_events.jsp">Events</a> | <a href="/all_posts.jsp">Posts</a></li>
      <!--li><a href="latest_cmnt.cgi">Latest Comments</a></li>
      <li><a href="search.cgi">Search</a></li-->
      <li><a href="mailto:<%= SwimShowerResource.INFO_EMAIL %>">Contact Us</a></li>
      <!--li><a href="sitemap.cgi">Site Map</a></li-->
      <li><a href="http://www.myspace.com/swimshower">MySpace Music</a></li>
    </ul>
  </div>

  <h2>The Band</h2>
  <div id="band_links" class="box">
    <ul>
      <li><a href="/index.jsp">Swim Shower</a></li>
      <li><a href="/nav?type=profile&amp;id=15">Brad</a> (lead vocals, guitar)</li>
      <li><a href="/nav?type=profile&amp;id=14">Jeff B.</a> (lead guitar)</li>
      <li><a href="/nav?type=profile&amp;id=31">Jeff D.</a> (bass)</li>
      <li><a href="/nav?type=profile&amp;id=1">Steve</a> (drums)</li>
      <li><a href="/nav?type=profile&amp;id=29">Ben</a> (keys, vocals)</li>
    </ul>
  </div>
