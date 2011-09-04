#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';
require 'site/pl/PostFile.pl';

my $cgi = new CGI;
my $this = $ENV{'SCRIPT_NAME'};
my $id = $cgi->param('id');
my ($userKey);

# Attempt to set browser cookie if user supplied a password
if ($cgi->param('passwd')) {
  $userKey = &Vessel::SetSessionUser($cgi->param('passwd'));
}

print $cgi->header();
&Vessel::PrintHeader('11', 'main');
print qq{<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/post.css" />\n};
my (%links);

if ($id eq 'new') {
  &PrintForm();
} elsif ($id eq 'add') {
  &ProcessForm();
} else {
  &PrintAllItems();
}

&Vessel::PrintMainClose();
exit;


#### PrintForm:
sub PrintForm {
  my ($usr, $eml) = &Vessel::GetSessionUser($cgi);

  print qq{<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/form.css" />\n};
  $links{'Archive'} = "$Vessel::cgiRoot${this}";
  &Vessel::PrintMainOpen('i', %links);

  print <<HTML;
<h1>Vessel Playlist</h1><hr />

<form id="staffItem" method="post" action="$Vessel::cgiRoot${this}">
<table summary="new item form">
<tr><td class="lbl">Artist 1:</td><td><input type="text" name="artist1" size="36" maxlength="36" class="txt" /></td></tr>
<tr><td class="lbl">Album/Track 1:</td><td><input type="text" name="album1" size="48" maxlength="48" class="txt" /></td></tr>
<tr><td class="lbl">Artist 2:</td><td><input type="text" name="artist2" size="36" maxlength="36" class="txt" /></td></tr>
<tr><td class="lbl">Album/Track 2:</td><td><input type="text" name="album2" size="48" maxlength="48" class="txt" /></td></tr>
<tr><td class="lbl">Artist 3:</td><td><input type="text" name="artist3" size="36" maxlength="36" class="txt" /></td></tr>
<tr><td class="lbl">Album/Track 3:</td><td><input type="text" name="album3" size="48" maxlength="48" class="txt" /></td></tr>
<tr><td class="lbl">Artist 4:</td><td><input type="text" name="artist4" size="36" maxlength="36" class="txt" /></td></tr>
<tr><td class="lbl">Album/Track 4:</td><td><input type="text" name="album4" size="48" maxlength="48" class="txt" /></td></tr>
<tr><td class="lbl">Artist 5:</td><td><input type="text" name="artist5" size="36" maxlength="36" class="txt" /></td></tr>
<tr><td class="lbl">Album/Track 5:</td><td><input type="text" name="album5" size="48" maxlength="48" class="txt" /></td></tr>
</table><p><input type="hidden" name="id" value="add" />
<input type="submit" value="Submit" class="btn" /> <input type="reset" value="Clear" class="btn" />&nbsp;&nbsp;&nbsp;&nbsp;
HTML

  # If the user is logged in, display their name, else print a text box for the password
  if ($usr) {
    print qq{User: <a href="mailto:$eml">$usr</a>\n};
  } else {
    print qq{Enter Password: <input type="password" name="passwd" size="12" maxlength="24" class="txt" />\n};
  }

  print "</p></form>\n";
  return;
}

#### ProcessForm:
sub ProcessForm {
  my ($date, $i);
  my ($usr, $eml) = &Vessel::GetSessionUser($cgi);

  # If the user supplied a valid password, retrieve credentials
  if (! $usr && $userKey) {
    $usr = $Vessel::staff{$userKey}{'name'};
    $eml = $Vessel::staff{$userKey}{'email'};
  }

  $links{'Archive'} = "$Vessel::cgiRoot${this}";
  $links{'New Music'} = "$Vessel::cgiRoot${this}?id=new";
  &Vessel::PrintMainOpen('i', %links);
  print "<h1>Vessel Playlist</h1><hr />\n";

  # If no valid user, then print error message and end function
  if (! $usr) {
    &Vessel::InputError("You are not logged in or have submitted an invalid password.<br />\n");
    return;
  } elsif (! $cgi->param('artist1') || ! $cgi->param('album1')) {
    &Vessel::InputError("You must enter at least one artist and album or track.<br />\n");
    return;
  }

  $date = &Vessel::GetDateString('file');

  open(OUTFILE, ">>$PostFile::files{'pl'}") or &Vessel::CgiError("Unable to open $PostFile::files{'pl'}: $!");
  flock(OUTFILE, 2);

  for ($i = 1; $i < 6; $i++) {
    # AddPlaylist performs file protection prior to file append
    &AddPlaylist($cgi->param("artist$i"), $cgi->param("album$i"), "$eml ($usr)", $date);
  }

  close(OUTFILE);

  print <<HTML;
<b>SUCCESS!</b>  <a href="mailto:$eml">$usr</a>, your music has been added.<br />
Here is a link to the <a href="$Vessel::cgiRoot${this}">playlist archive</a>.
<script type="text/javascript">
<!--
  parent.Tools.location.reload(true);
// -->
</script>
HTML

  return;
}

#### PrintAllItems: derived from &PostFile::PrintAllPosts()
sub PrintAllItems {
  my ($uArr, $dArr, $line, @elems, $usr, $eml, %play, $key, $seq);

  my $sort = $cgi->param('sort');
  my $dir = $cgi->param('dir');
  $sort = 'd' if ($sort ne 'a');
  $dir = 'a' if ($dir ne 'd');

  # On IE, the double arrow chars do not render, but in Firefox
  # and Netscape, the single arrow chars are too small
  if ($ENV{'HTTP_USER_AGENT'} =~ /MSIE/) {
    $uArr = '&uarr;';
    $dArr = '&darr;';
  } else {
    $uArr = '&uArr;';
    $dArr = '&dArr;';
  }

  print qq{<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/form.css" />\n};
  $links{'New Music'} = "$Vessel::cgiRoot${this}?id=new";
  &Vessel::PrintMainOpen('i', %links);
  print qq{<h1>Vessel Playlist Archive</h1><hr />\n<table summary="playlist archive" class="post">\n};

  # Print correct clickable sort arrows based on current sort order
  print '<tr><th>Artist ';

  if ($sort eq 'a') {
    if ($dir eq 'd') {
      print qq{<a href="$Vessel::cgiRoot${this}?sort=a&amp;dir=a">$uArr</a></th><th>Album/Track</th>};
    } else {  # $dir eq 'a'
      print qq{$uArr</th><th>Album/Track</th>};
    }

    print qq{<th>Date&nbsp;<a href="$Vessel::cgiRoot${this}?sort=d&amp;dir=a">$uArr</a>};
    print qq{<a href="$Vessel::cgiRoot${this}?sort=d&amp;dir=d">$dArr</a>};
  } else {  # $sort eq 'd'
    print qq{<a href="$Vessel::cgiRoot${this}?sort=a&amp;dir=a">$uArr</a></th><th>Album/Track</th>};

    if ($dir eq 'd') {
      print qq{<th>Date&nbsp;<a href="$Vessel::cgiRoot${this}?sort=d&amp;dir=a">$uArr</a>$dArr};
    } else {  # $dir eq 'a'
      print qq{<th>Date&nbsp;$uArr<a href="$Vessel::cgiRoot${this}?sort=d&amp;dir=d">$dArr</a>};
    }
  }

  print "</th><th>By</th></tr>\n";

  $seq = 1;
  open(INFILE, "<$PostFile::files{'pl'}") or &Vessel::CgiError("Unable to open $PostFile::files{'pl'}: $!");
  while ($line = <INFILE>) {
    chomp($line);
    @elems = split(/\|/, $line);
    ($usr, $eml) = &Vessel::GetAuthorFromFile($elems[4]);

    # Use different strings for sort keys whether sort by artist or date
    if ($sort eq 'a') {
      $key = "$elems[1]/$elems[8]/$seq";
    } else {  # $sort eq 'd'
      $key = "$elems[8]/$seq";
    }

    $play{$key}{'artist'} = $elems[1];
    $play{$key}{'album'} = $elems[3];
    $play{$key}{'date'} = &Vessel::GetDateFromFile($elems[8], 'short');
    $play{$key}{'user'} = qq{<a href="mailto:$eml">$usr</a>};

    $seq++;
  }
  close(INFILE);

  if ($dir eq 'd') {
    foreach $key (reverse sort keys %play) {
      print "<tr><td>$play{$key}{'artist'}</td><td>$play{$key}{'album'}</td>";
      print "<td>$play{$key}{'date'}</td><td>$play{$key}{'user'}</td></tr>\n";
    }
  } else {  # $dir eq 'a'
    foreach $key (sort keys %play) {
      print "<tr><td>$play{$key}{'artist'}</td><td>$play{$key}{'album'}</td>";
      print "<td>$play{$key}{'date'}</td><td>$play{$key}{'user'}</td></tr>\n";
    }
  }

  print "</table><br />\n";

  ($usr, $eml) = &Vessel::GetSessionUser($cgi);
  &PostFile::PrintAllComments('pc', 1);
  &PostFile::PrintCommentForm('pc', 1, $usr, $eml);

  return;
}

#### AddPlaylist: derived from &PostFile::AddPost()
sub AddPlaylist {
  my ($artist, $album, $aut, $dat) = @_;

  return if (! $artist || ! $album);

  $artist = &PostFile::FileProtect($artist, 36);
  $album = &PostFile::FileProtect($album, 48);  
  print OUTFILE "1|$artist||$album|$aut||||$dat\n";

  return;
}
