#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';
require 'site/pl/PostFile.pl';

my $cgi = new CGI;
my (%cmnts, $i, $j, @elems, $script, $item);

print $cgi->header();
&Vessel::PrintHeader('11', 'main');
print qq{<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/post.css" />\n};

my %links = ('Events'=>'event.cgi', 'Headlines'=>'headline.cgi', 'Playlist'=>'playlist.cgi');
&Vessel::PrintMainOpen('o', %links);
print "<h1>Vessel Latest Comments</h1><hr />\n";

# Collect all headline comments, event comments, and playlist comments
foreach $i ('hc', 'ec', 'pc') {
  &CollectComments($i);
}

$j = 0;
foreach $i (reverse sort keys %cmnts) {
  @elems = split(/\|/, $cmnts{$i}{'line'});

  if ($cmnts{$i}{'type'} eq 'hc') {
    $script = 'headline';
    $item = "Headline #$elems[0]";
  } elsif ($cmnts{$i}{'type'} eq 'ec') {
    $script = 'event';
    $item = "Event #$elems[0]";
  } else {  # $cmnts{$i}{'type'} eq 'pc'
    $script = 'playlist';
    $item = 'Playlist Archive';
  }

  print qq{<a href="$script.cgi?id=$elems[0]" class="ttl">$item</a>};
  &PostFile::PrintComment($elems[1], $elems[5], $elems[3], $elems[2], $elems[4]);

  $j++;
  last if ($j == $PostFile::rssItems);
}

&Vessel::PrintMainClose();
exit;


#### CollectComments: use regular expression on each line in the supplied file to match dates
#      for sorting
sub CollectComments {
  my ($type) = @_;
  my ($line);

  open(INFILE, "<$PostFile::files{$type}") or &Vessel::CgiError("Unable to open $PostFile::files{$type}: $!");
  while ($line = <INFILE>) {
    $line =~ /\|(\d{4}\/\d{2}\/\d{2}\/\d{2}:\d{2}:\d{2})\/\d$/;
    $cmnts{$1}{'line'} = $line;
    $cmnts{$1}{'type'} = $type;
  }
  close(INFILE);

  return;
}
