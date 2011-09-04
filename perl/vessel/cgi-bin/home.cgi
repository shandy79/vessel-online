#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';
require 'site/pl/PostFile.pl';

my $cgi = new CGI;
my ($id, %item, $usr, $eml, $date);

print $cgi->header();
&Vessel::PrintHeader('11', 'main');
print qq{<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/post.css" />\n};

my %links = ('Events'=>'event.cgi', 'Headlines'=>'headline.cgi', 'Playlist'=>'playlist.cgi');
&Vessel::PrintMainOpen('o', %links);

print <<HTML;
  <h1>Vessel Home</h1><hr />
  Welcome to Vessel Online, my entry into the decidedly low-stakes world of personal Internet
  publishing.  I had originally put together this basic site design a couple years back but never
  got around to creating content for it.  Then, I decided to revamp it as my class project for a Web
  Programming course at <a href="http://www.loyola.edu/">Loyola College</a> that I took for my MS in
  Software Engineering.  Now, you all are stuck with the consequences.  Feel free to check out all
  the links available, read some headlines and events, and make some comments.  If you'd like to
  join in the fun at Vessel and post headlines, events, and music, let me know and I can create an
  account for you.  Enjoy!

  <h2>Latest Headline</h2>
HTML

$id = &PostFile::GetMaxItems('hl');
%item = &PostFile::GetPost('hl', $id);

($usr, $eml) = &Vessel::GetAuthorFromFile($item{'author'});
$date = &Vessel::GetDateFromFile($item{'pubDate'}, 'long');

print <<HTML;
<table class="post" summary="latest headline">
<tr><td><a href="$item{'link'}" class="ttl">$item{'title'}</a></td>
  <td align="right">By: <a href="mailto:$eml">$usr</a></td></tr>
<tr><td colspan="2"><span class="cat">[$item{'category'}]</span>
  $item{'description'}</td></tr>
<tr><td>Posted: <i>$date</i></td><td align="right">Source: <a href="$item{'source_url'}">$item{'source'}</a></td></tr>
</table>
HTML

print "  <h2>Latest Event</h2>\n";

$id = &PostFile::GetMaxItems('ev');
%item = &PostFile::GetPost('ev', $id);

($usr, $eml) = &Vessel::GetAuthorFromFile($item{'author'});
$date = &Vessel::GetDateFromFile($item{'pubDate'}, 'long');

print <<HTML;
<table class="post" summary="latest event">
<tr><td><a href="$item{'link'}" class="ttl">$item{'title'}</a></td>
  <td align="right">By: <a href="mailto:$eml">$usr</a></td></tr>
<tr><td colspan="2"><span class="cat">[$item{'category'}]</span>
  $item{'description'}</td></tr>
<tr><td>Date &amp; Time: <i>$date</i></td><td align="right">Event Host: <a href="$item{'source_url'}">$item{'source'}</a></td></tr>
</table>
HTML

print <<HTML;
  <h2>Planned Improvements</h2>
  Although the <a href="http://www.tripod.lycos.com/">Tripod</a> CGI hosting is pretty bare-bones, it
  does encourage some creativity in implementing new features.  Here is a list of some things I plan
  on providing on the site.  With any luck, I will find a good web host that will provide a better
  platform in the future (i.e. database, Java/Tomcat, more Perl modules, etc.)
  <ul>
    <li><i>Comprehensive search</i> of all site data, including headlines, events, playlist, and comments</li>
    <li>Use of <i>iframes</i> instead of regular frames to improve layout</li>
    <li>Rewrite of <a href="font.cgi">Vessel Font</a> as an <i>AJAX</i> application</li>
    <li>More <i>RSS feeds</i> generated from an improved framework</li>
    <li><i>Dynamic calendar</i> generation with links to headlines and events for each day</li>
    <li>More static content about myself and the other members of Vessel</li>
  </ul>
  Of course, I am very busy at the moment with two grad courses, two volleyball leagues, soccer, and
  involvement on the boards of two volunteer organizations, so we'll see when I actually get any of
  these things done!
HTML

&Vessel::PrintMainClose();
exit;
