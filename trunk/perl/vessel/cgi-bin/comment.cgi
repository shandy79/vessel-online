#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';
require 'site/pl/PostFile.pl';

my $cgi = new CGI;
my $type = $cgi->param('type');
my $id = $cgi->param('id');
my (%item, $date, $ltype);

# If the comment author is a logged in user, then HTML comments are
# allowed.  Allowing this globally is a cross-site scripting risk.
my ($usr) = &Vessel::GetSessionUser($cgi);

# Must 'untaint' the $type due to Tripod -T flag usage
$type =~ /^(.*)$/;
$type = $1;

if ($type eq 'ec') {
  $ltype = 'event';
} elsif ($type eq 'pc') {
  $ltype = 'playlist';
} else {  # $type eq 'hc'
  $ltype = 'headline';
}

print $cgi->header();
&Vessel::PrintHeader('11', 'main');

my %links = ('Headlines'=>'headline.cgi', 'Events'=>'event.cgi', 'Playlist'=>'playlist.cgi');
&Vessel::PrintMainOpen('i', %links);
print "<h1>Vessel Comments</h1><hr />\n";

$item{'comment'} = &PostFile::FileProtect($cgi->param('comment'), 4000, $usr);
$item{'author'} = &PostFile::FileProtect($cgi->param('author'), 36);
$item{'email'} = &PostFile::FileProtect($cgi->param('email'), 36);
$item{'location'} = &PostFile::FileProtect($cgi->param('location'), 36);

$date = &Vessel::GetDateString('file');

# Convert email @ and . characters for spam privacy
$item{'email'} =~ s/@/_AT_/g;
$item{'email'} =~ s/\./_DOT_/g;

# If no comment entered, then print error message and exit
if (! $item{'comment'}) {
  &Vessel::InputError("You must enter a comment.<br />\n");
  &Vessel::PrintMainClose();
  exit;
}

open(OUTFILE, ">>$PostFile::files{$type}") or &Vessel::CgiError("Unable to open $PostFile::files{$type}: $!");
flock(OUTFILE, 2);
print OUTFILE "$id|$item{'comment'}|$item{'author'}|$item{'email'}|$item{'location'}|$date\n";
close(OUTFILE);

print qq{<b>SUCCESS!</b>  Your comment has been added.<br />\n};
print qq{Here is a link to the <a href="$ltype.cgi?id=$id">$ltype</a> to which you posted a comment.\n};

&Vessel::PrintMainClose();
exit;
