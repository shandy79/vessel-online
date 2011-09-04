#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';
require 'site/pl/PostFile.pl';

my $cgi = new CGI;
print $cgi->header();
&Vessel::PrintHeader('10', 'tools');

my (@curr, $line, %item, $date, $usr);

print <<HTML;
<script type="text/javascript" src="site/js/tools.js"></script>
<base target="Main" />
</head>
<body onload="initDHTMLAPI(); return preloadImg()">
<div id="contents" style="position:absolute; left:27px; top:37px">
  <img src="$Vessel::htmlRoot/img/headlines.gif" alt="headlines" />
  <div id="headlines" class="box">
    <ul>
HTML

@curr = &PostFile::GetRecentPosts('hl', $PostFile::summaryItems);
foreach $line (@curr) {
  %item = &PostFile::GetItemHash($line);

  $date = &Vessel::GetDateFromFile($item{'pubDate'}, 'short');
  ($usr) = &Vessel::GetAuthorFromFile($item{'author'});

  print qq{<li>$date&nbsp;&nbsp;<a href="headline.cgi?id=$item{'id'}" title="$usr">$item{'title'}</a></li>\n};
}

print <<HTML;
    </ul>
    [ <a href="headline.cgi">View All</a> ] [ <a href="headline.cgi?id=new">Add Headline</a> ]
    <a href="rss.cgi?type=hl" title="Headlines RSS Feed" target="aux">
      <img src="$Vessel::htmlRoot/img/xml.gif" alt="XML RSS" /></a>
  </div>
  <br />
  <img src="$Vessel::htmlRoot/img/events.gif" alt="events" />
  <div id="events" class="box">
    <ul>
HTML

@curr = &GetUpcomingEvents();
foreach $line (@curr) {
  %item = &PostFile::GetItemHash($line);

  $date = &Vessel::GetDateFromFile($item{'pubDate'}, 'short');
  ($usr) = &Vessel::GetAuthorFromFile($item{'author'});

  print qq{<li>$date&nbsp;&nbsp;<a href="event.cgi?id=$item{'id'}" title="$usr">$item{'title'}</a></li>\n};
}

print <<HTML;
    </ul>
    [ <a href="event.cgi">View All</a> ] [ <a href="event.cgi?id=new">Add Event</a> ]
    <a href="rss.cgi?type=ev" title="Events RSS Feed" target="aux">
      <img src="$Vessel::htmlRoot/img/xml.gif" alt="XML RSS" /></a>
  </div>
  <br />
  <img src="$Vessel::htmlRoot/img/playlist.gif" alt="playlist" />
  <div id="playlist" class="box">
    <ul>
HTML

@curr = &PostFile::GetRecentPosts('pl', $PostFile::summaryItems);
foreach $line (@curr) {
  %item = &PostFile::GetItemHash($line);
  ($usr) = &Vessel::GetAuthorFromFile($item{'author'});

  print qq{<li title="$usr">$item{'title'}, <span style="color:red;">$item{'description'}</span></li>\n};
}

print <<HTML;
    </ul>
    [ <a href="playlist.cgi">View All</a> ] [ <a href="playlist.cgi?id=new">Add Music</a> ]
  </div>
  <br />
  <img src="$Vessel::htmlRoot/img/tools.gif" alt="tools" />
  <div id="tools" class="box">
    <ul>
      <li><a href="home.cgi">Home Page</a></li>
      <li><a href="splash.cgi" target="_top">Splash Page</a></li>
      <li><a href="font.cgi">Vessel Font</a></li>
      <li><a href="latest_cmnt.cgi">Latest Comments</a></li>
      <li><a href="link.cgi">Link Archive</a></li>
      <li><a href="siteinfo.cgi">Site Info</a></li>
    </ul>
  </div>
</div>

<!-- Launch panel -->
<div id="launch" style="position:absolute; left:70px; top:-100px; width:200px; height:120px; z-index:1">
  <table class="imgcell" cellspacing="0" cellpadding="0"><tr>
    <td><a href="http://cityguide.aol.com/baltimore/" onmouseout="imgOff('launch',0)" onmouseover="imgOn('launch',0)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/city.gif" id="launch0" alt="city" /></a></td>
    <td><a href="http://dictionary.reference.com/" onmouseout="imgOff('launch',1)" onmouseover="imgOn('launch',1)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/dictionary.gif" id="launch1" alt="dictionary" /></a></td>
  </tr><tr>
    <td><a href="http://en.wikipedia.org/wiki/Main_Page" onmouseout="imgOff('launch',2)" onmouseover="imgOn('launch',2)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/encyclopedia.gif" id="launch2" alt="encyclopedia" /></a></td>
    <td><a href="http://www.mapquest.com/" onmouseout="imgOff('launch',3)" onmouseover="imgOn('launch',3)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/maps.gif" id="launch3" alt="maps" /></a></td>
  </tr><tr>
    <td><a href="http://www.cnn.com/" onmouseout="imgOff('launch',4)" onmouseover="imgOn('launch',4)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/news.gif" id="launch4" alt="news" /></a></td>
    <td><a href="http://www.google.com/" onmouseout="imgOff('launch',5)" onmouseover="imgOn('launch',5)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/search.gif" id="launch5" alt="search" /></a></td>
  </tr><tr>
    <td><a href="http://espn.go.com/" onmouseout="imgOff('launch',6)" onmouseover="imgOn('launch',6)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/sports.gif" id="launch6" alt="sports" /></a></td>
    <td><a href="http://www.weather.com/weather/local/21229" onmouseout="imgOff('launch',7)" onmouseover="imgOn('launch',7)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/weather.gif" id="launch7" alt="weather" /></a></td>
  </tr><tr>
    <td><a href="https://gmail.google.com/" onmouseout="imgOff('launch',8)" onmouseover="imgOn('launch',8)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/gmail.gif" id="launch8" alt="gmail" /></a></td>
    <td><a href="https://mail.yahoo.com/" onmouseout="imgOff('launch',9)" onmouseover="imgOn('launch',9)" target="aux">
      <img src="$Vessel::htmlRoot/img/launch/yahoo.gif" id="launch9" alt="yahoo! mail" /></a></td>
  </tr><tr>
    <td colspan="2">
    <a href="javascript:void(0);" onclick="return moveLaunch()" onmouseout="return statusMsg('')" onmouseover="return statusMsg('Show/hide Launch...')" target="_self">
    <img src="$Vessel::htmlRoot/img/launch/launch.gif" alt="launch" /></a></td>
  </tr></table>
</div>
</body></html>
HTML

exit;


#### GetUpcomingEvents:  this function will retrieve the next $PostFile::summaryItems events that
#      have yet to take place and sort them in chronological order
sub GetUpcomingEvents {
  my ($now, $then, $line, %dates, @evnt);
  $now = &Vessel::GetDateString('file');

  open(INFILE, "<$PostFile::files{'ev'}") or &Vessel::CgiError("Unable to open $PostFile::files{'ev'}: $!");
  while ($line = <INFILE>) {
    $line =~ /\|([0-9\/:]+)$/;
    $then = $1;
    $dates{$then} = $line if ($then gt $now);
  }
  close(INFILE);

  $line = 0;
  # Use $line as a counter to make sure only $summaryItems events are displayed
  foreach $then (sort keys %dates) {
    push (@evnt, $dates{$then});
    $line++;
    last if ($line == $PostFile::summaryItems);
  }

  return @evnt;
}
