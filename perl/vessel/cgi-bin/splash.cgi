#!/usr/bin/perl

use CGI;

my $cgi = new CGI;
print $cgi->header();

print <<HTML;
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en"><head>
<title>Vessel Online Boarding Terminal</title>
<meta http-equiv="Content-Type" content="text/html;charset=iso-8859-1" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<link rel="stylesheet" type="text/css" href="site/css/splash.css" />
<script type="text/javascript" src="site/js/dhtmlApi.js"></script>
<script type="text/javascript" src="site/js/splash.js"></script>
<script type="text/javascript">
<!--
  MM_reloadPage(true);
// -->
</script>
</head>
<body onload="initDHTMLAPI(); return loadDance()">
<div id="bkNorm"><img src="img/splash/bkNorm.gif" alt="background" /></div>

<!-- Vessel graphic -->
<div id="vessel" style="position:absolute; left:25px; top:15px; width:172px; height:172px; z-index:3">
  <a href="javascript:void(0);" onmouseover="return rollVesselOver()" onmouseout="return rollVesselOut()">
  <img src="img/splash/vessel.gif" id="vesselPic" alt="vessel" /></a></div>

<!-- Style, Innovation, Dedication, and Intel graphics -->
<div id="style" style="position:absolute; left:331px; top:16px; width:100px; height:50px; z-index:1">
  <a href="javascript:void(0);" onmouseover="return rollWordOver('style',3)" onmouseout="return rollWordOut('style',3)">
  <img src="img/splash/style.gif" id="style3" alt="style" /></a></div>

<div id="innov" style="position:absolute; left:313px; top:110px; width:177px; height:40px; z-index:1">
  <a href="javascript:void(0);" onmouseover="return rollWordOver('innov',0)" onmouseout="return rollWordOut('innov',0)">
  <img src="img/splash/innov.gif" id="innov0" alt="innovation" /></a></div>

<div id="dedic" style="position:absolute; left:256px; top:201px; width:177px; height:40px; z-index:1">
  <a href="javascript:void(0);" onmouseover="return rollWordOver('dedic',1)" onmouseout="return rollWordOut('dedic',1)">
  <img src="img/splash/dedic.gif" id="dedic1" alt="dedication" /></a></div>

<div id="intel" style="position:absolute; left:97px; top:275px; width:200px; height:50px; z-index:1">
  <a href="javascript:void(0);" onmouseover="return rollWordOver('intel',2)" onmouseout="return rollWordOut('intel',2)">
  <img src="img/splash/intel.gif" id="intel2" alt="intelligence" /></a></div>

<!-- Enter graphic -->
<div id="enter" style="position:absolute; left:623px; top:386px; width:136px; height:32px; z-index:1">
  <a href="index.cgi" onmouseover="return rollEnterOver()" onmouseout="return rollEnterOut()">
  <img src="img/splash/enter.gif" id="enter4" alt="Click to enter Vessel..." /></a></div>

<!-- Background rays -->
<div id="rays" style="position:absolute; left:0px; top:0px; width:760px; height:420px; z-index:2; visibility:hidden">
  <img src="img/splash/bkRays.gif" alt="rays" /></div>

<!-- Definition img -->
<div id="styleDef" style="position:absolute; left:4px; top:360px; width:220px; height:59px; z-index:1; visibility:hidden">
  <img src="img/splash/styleDef.gif" alt="style definition" /></div>
<div id="innovDef" style="position:absolute; left:4px; top:360px; width:220px; height:59px; z-index:1; visibility:hidden">
  <img src="img/splash/innovDef.gif" alt="innov definition" /></div>
<div id="dedicDef" style="position:absolute; left:4px; top:360px; width:220px; height:59px; z-index:1; visibility:hidden">
  <img src="img/splash/dedicDef.gif" alt="dedic definition" /></div>
<div id="intelDef" style="position:absolute; left:4px; top:360px; width:220px; height:59px; z-index:1; visibility:hidden">
  <img src="img/splash/intelDef.gif" alt="intel definition" /></div>

</body></html>
HTML

exit;
