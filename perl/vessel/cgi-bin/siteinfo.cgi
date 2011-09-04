#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';

my $cgi = new CGI;
my ($key, $val);

print $cgi->header();
&Vessel::PrintHeader('11', 'main');
print qq{<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/post.css" />\n};

my %links = ('Events'=>'event.cgi', 'Headlines'=>'headline.cgi', 'Playlist'=>'playlist.cgi');
&Vessel::PrintMainOpen('o', %links);

print <<HTML;
  <h1>Vessel Site Information</h1><hr />

  <h2>Web Site Environment</h2>
  <b>$Vessel::htmlRoot</b>
  <ul><li><i>Web Pages</i>: *.html</li>
    <li><i>Cascading Style Sheets</i>: site/css/*.css</li>
    <li><i>Javascripts</i>: site/js/*.js</li>
    <li><i>Java Applets</i>: site/*.class</li>
    <li><i>Images</i>: img/*.{gif|jpg}</li>
    <li><i>Audio</i>: aud/*.wav</li></ul>

  <h2>CGI Scripting Environment</h2>
  <b>$Vessel::dynRoot${Vessel::cgiRoot}</b>
  <ul><li><i>CGI Scripts</i>: *.cgi</li>
    <li><i>Perl Libraries</i>: site/pl/*.pl</li>
    <li><i>Data Files</i>: txt/*.txt</li>
    <li><i>Javascripts</i>: site/js/*.js (cannot be linked due to browser security)</li></ul>
  <br />
  <table class="post" summary="cgi scripting environment">
HTML

foreach $key (sort keys %ENV) {
  print qq{<tr><td class="ttl">$key</td><td>};

  # To allow line wrapping
  if ($key eq 'HTTP_ACCEPT') {
    $val = $ENV{$key};
    $val =~ s/(;|,)/$1 /g;
    print "$val</td></tr>\n";
  } else {
    print qq{$ENV{$key}</td></tr>\n};
  }
}

print <<HTML;
  </table>
  <h2>Web Standards Compliance</h2>
  Vessel Online is proud to publish standards-compliant web pages that retain a similar look and
  feel across different browsers and operating systems.  On every page in this frame, you will find
  buttons linking to <a href="http://www.w3.org/">W3C</a>
  <a href="http://www.w3.org/QA/Tools/#validators">validators</a> for
  <a href="http://www.w3.org/MarkUp/">XHTML</a> and <a href="http://www.w3.org/Style/CSS/">CSS</a>
  that both indicate our efforts to remain up to date with the latest web standards and allow the
  visitor to validate the pages himself.<br />
  <br />
  In addition, Vessel Online web pages meet a majority of the provisions set forth under the
  <a href="http://www.section508.gov/">Section 508</a> and <a href="http://www.w3.org/WAI/">WAI</a>
  guidelines for web accessibility.<br />
  <br />
  Finally, when you see the <img src="$Vessel::htmlRoot/img/xml.gif" alt="XML/RSS" /> icon, you can
  use the linked resource as a valid <a href="http://blogs.law.harvard.edu/tech/rss">RSS 2.0</a>
  (Really Simple Syndication) feed with your preferred newsreader.

  <h2>Note From The Webmaster</h2>
  Thanks for visiting our site.  If you have any questions or comments, please contact us at
  <a href="mailto:$Vessel::siteEmail">$Vessel::siteEmail</a>.  Please note that all email addresses
  on this site are displayed with the convention _DOT_ = . and _AT_ = @.  This is used to prevent
  the harvesting of site user email addresses by spambots.<br />
  <br />
  Finally, I have to give a fair amount of credit to <a href="http://www.dannyg.com/">Danny Goodman</a>
  and his excellent book, <i>Dynamic HTML, The Definitive Reference</i>, from
  <a href="http://www.oreilly.com/">O'Reilly.</a>  The cross-browser Javascript API and chapters
  on CSS Positioning were invaluable in constructing this site.
HTML

&Vessel::PrintMainClose();
exit;
