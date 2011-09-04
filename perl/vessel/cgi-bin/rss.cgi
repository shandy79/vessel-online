#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';
require 'site/pl/PostFile.pl';

my $cgi = new CGI;
my $this = $ENV{'SCRIPT_NAME'};
my $type = $cgi->param('type');
my ($utitle, $ltitle, @curr, $line, %item, $date);

print $cgi->header('text/xml');

if ($type eq 'ev') {
  $utitle = 'Events';
  $ltitle = 'event';
} else {  
  $type = 'hl';
  $utitle = 'Headlines';
  $ltitle = 'headline'
}

$date = &Vessel::GetDateString('rfc');

print <<RSS;
<?xml version="1.0" encoding="iso-8859-1"?>
<rss version="2.0">
  <channel>
    <title>Vessel Online: $utitle</title>
    <link>${Vessel::dynRoot}$Vessel::cgiRoot/$ltitle.cgi</link>
    <description>$utitle posted at Vessel Online.</description>
    <category>$title</category>
    <language>en-us</language>
    <pubDate>$date</pubDate>
    <docs>http://blogs.law.harvard.edu/tech/rss</docs>
    <generator>${Vessel::dynRoot}${Vessel::cgiRoot}$this?type=$type</generator>
    <managingEditor>$Vessel::siteEmail</managingEditor>
    <webMaster>$Vessel::siteEmail</webMaster>
    <copyright>All original content on this site is the property of Vessel.</copyright>
    <image>
      <url>$Vessel::htmlRoot/img/splash/vessel.gif</url>
      <title>Vessel Online</title>
      <link>$Vessel::htmlRoot/index.html</link>
    </image>
RSS

@curr = &PostFile::GetRecentPosts($type, $PostFile::rssItems);
foreach $line (@curr) {
  %item = &PostFile::GetItemHash($line);
  $date = &Vessel::GetDateFromFile($item{'pubDate'}, 'rfc');

  # Remove HTML tags from description since not compliant w/RSS schema
  $item{'description'} =~ s/<\/?.+?>//g;

  print <<RSS;
    <item>
      <title>$item{'title'}</title>
      <link>${Vessel::dynRoot}$Vessel::cgiRoot/$ltitle.cgi?id=$item{'id'}</link>
      <description>$item{'description'}</description>
      <author>$item{'author'}</author>
      <source url="$item{'source_url'}">$item{'source'}</source>
      <category>$item{'category'}</category>
      <pubDate>$date</pubDate>
    </item>
RSS
}

print "  </channel>\n</rss>\n";
exit;
