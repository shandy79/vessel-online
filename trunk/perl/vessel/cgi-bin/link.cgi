#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';
require 'site/pl/PostFile.pl';

my $cgi = new CGI;
my (%urls, $i, $j, $script);

print $cgi->header();
&Vessel::PrintHeader('11', 'main');

print <<CSS;
  <style type="text/css">
  <!--
    .type { color: red;
            font-weight: bold;
            text-decoration: none;
            text-transform: uppercase; }
  -->
  </style>
CSS

my %links = ('Events'=>'event.cgi', 'Headlines'=>'headline.cgi', 'Playlist'=>'playlist.cgi');
&Vessel::PrintMainOpen('o', %links);
print <<HTML;
  <h1>Vessel Link Archive</h1><hr />
  <b>KEY TO LINK SOURCE TYPES</b><br />
  <span style="font-size:8pt;">[<span class="type">HL</span>]:headline&nbsp;&nbsp;[<span class="type">HC</span>]:headline comment&nbsp;&nbsp;[<span class="type">EV</span>]:event&nbsp;&nbsp;[<span class="type">EC</span>]:event comment&nbsp;&nbsp;[<span class="type">PC</span>]:playlist comment</span>
  <br />&nbsp;<br />
  <ul>
HTML

# Search the headlines, headline comments, events, event comments, and playlist
# comments files for URLs
foreach $i ('hl', 'hc', 'ev', 'ec', 'pc') {
  &CollectLinks($i);
}

foreach $i (sort keys %urls) {
  print qq{<li><a href="$i">$i</a>};

  for ($j = 0; $j <= $#{$urls{$i}}; $j+=2) {
    if (${$urls{$i}}[$j] eq 'hl' || ${$urls{$i}}[$j] eq 'hc') {
      $script = 'headline';
    } elsif (${$urls{$i}}[$j] eq 'ev' || ${$urls{$i}}[$j] eq 'ec') {
      $script = 'event';
    } else {  # ${$urls{$i}}[$j] eq 'pc'
      $script = 'playlist';
    }

    print qq{&nbsp;[<a href="$script.cgi?id=${$urls{$i}}[$j+1]" class="type">${$urls{$i}}[$j]</a>]};
  }

  print "</li>\n";
}

print "</ul>\n";
&Vessel::PrintMainClose();
exit;


#### CollectLinks: use regular expression on each line in the supplied file to match links
#      for URL and link text
sub CollectLinks {
  my ($type) = @_;
  my ($line, $id);

  open(INFILE, "<$PostFile::files{$type}") or &Vessel::CgiError("Unable to open $PostFile::files{$type}: $!");
  while ($line = <INFILE>) {
    $line =~ /^(\d+)\|/;
    $id = $1;

    # Each line may have several URLs, match from href=", src=", or | (for
    # <a>, <img> tag or field) up to a | or " (for field or <a>, <img> tag)
    while ($line =~ /(href="|src="|\|)(http:\/\/.*?)["|]/g) {
      push(@{$urls{$2}}, ($type, $id));
    }
  }
  close(INFILE);

  return;
}
