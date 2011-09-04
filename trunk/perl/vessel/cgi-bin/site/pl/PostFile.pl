#!/usr/bin/perl

# PostFile.pl
# Parameters and utility function library for reading and writing posts
#   on the Vessel website.
# Created by: Steve Handy (last mod: 08/05/2005)

#### Package PostFile: contains variables to be used as parameters for
#      web applications that read and write files
package PostFile;

  #### Dynamic files, parameters
  $files{'hl'}  = 'txt/headlines.txt';
  $files{'hc'} = 'txt/hdln_comments.txt';
  $files{'ev'}  = 'txt/events.txt';
  $files{'ec'} = 'txt/evnt_comments.txt';
  $files{'pl'} = 'txt/playlist.txt';
  $files{'pc'} = 'txt/play_comments.txt';

  $maxItemFile = 'txt/max_items.txt';
  $summaryItems = 5;
  $rssItems = 10;


#### FileProtect: escapes string for HTML/XML entities and separator character,
#      truncates string to length specified
sub FileProtect {
  my ($str, $len, $html) = @_;
  return '' if (! $str);
  $str =~ s/^\s+|\s+$//g;  # remove heading and trailing spaces
  $str =~ s/\n|\r/ /g;  # remove newline chars, since line-based file format
  $str = substr($str, 0, $len) if (length($str) > $len);

  $str =~ s/&/&amp;/g;
  $str =~ s/\|/ /g;

  if (! $html) {
    $str =~ s/</&lt;/g;
    $str =~ s/>/&gt;/g;
    # Removed due to issues w/Internet Explorer
    # $str =~ s/'/&apos;/g;
    # $str =~ s/"/&quot;/g;
  }

  # Profanity filter
  $str =~ s/fuck/f*%k/ig;
  $str =~ s/shit/s*%t/ig;
  $str =~ s/cunt/c*%t/ig;

  return $str;
}

#### GetMaxItems: opens the maximum item number file and retrieves the highest
#      recorded record number from the specified file
sub GetMaxItems {
  my ($type) = @_;
  my ($line);

  open(INFILE, "<$maxItemFile") or &Vessel::CgiError("Unable to open $maxItemFile: $!");
  $line = <INFILE>;
  close(INFILE);

  $line =~ /$type=(\d+)\|/;
  return $1;
}

#### SetMaxItems: opens the maximum item number file and updates the specified
#      type with the new value
sub SetMaxItems {
  my ($type, $value) = @_;
  my ($line);

  open(INFILE, "<$maxItemFile") or &Vessel::CgiError("Unable to open $maxItemFile: $!");
  $line = <INFILE>;
  close(INFILE);

  $line =~ s/$type=\d+\|/$type=$value\|/;

  open(OUTFILE, ">$maxItemFile") or &Vessel::CgiError("Unable to open $maxItemFile: $!");
  flock(OUTFILE, 2);
  print OUTFILE $line;
  close(OUTFILE);

  return;
}

#### GetItemHash: translate a line from a file into a hash data structure
sub GetItemHash {
  my ($line) = @_;
  my (@elems, %item);

  $line =~ s/\s+$//;
  @elems = split(/\|/, $line);

  $item{'id'} = $elems[0];
  $item{'title'} = $elems[1];
  $item{'link'} = $elems[2];
  $item{'description'} = $elems[3];
  $item{'author'} = $elems[4];
  $item{'source_url'} = $elems[5];
  $item{'source'} = $elems[6];
  $item{'category'} = $elems[7];
  $item{'pubDate'} = $elems[8];

  return %item;
}

#### GetPost: read a single post from the specified file based on ID
sub GetPost {
  my ($type, $id) = @_;
  my ($line, %item);

  open(INFILE, "<$files{$type}") or &Vessel::CgiError("Unable to open $files{$type}: $!");
  while ($line = <INFILE>) {
    if ($line =~ /^$id\|/) {
      %item = &GetItemHash($line);
      last;
    }
  }
  close(INFILE);

  return %item;
}

#### GetRecentPosts: read the last $count posts from the specified file
sub GetRecentPosts {
  my ($type, $count) = @_;
  my ($line, @curr);

  open(INFILE, "<$files{$type}") or &Vessel::CgiError("Unable to open $files{$type}: $!");
  while ($line = <INFILE>) {
    unshift(@curr, $line);
    pop(@curr) if ($#curr > $count - 1);
  }
  close(INFILE);

  return @curr;
}

#### PrintPostSummary: print post summary with link for View All feature
sub PrintPostSummary {
  my ($this, $id, $ttl, $date, $auth) = @_;
  my ($usr, $eml);

  ($usr, $eml) = &Vessel::GetAuthorFromFile($auth);
  print qq{<li><a href="$Vessel::cgiRoot${this}?id=$id" class="ttl">$ttl</a> };
  print &Vessel::GetDateFromFile($date, 'long');
  print qq{ <a href="mailto:$eml">$usr</a></li>\n};

  return;
}

#### PrintAllPosts: read all posts from the specified file and print in
#      condensed format
sub PrintAllPosts {
  my ($type, $this) = @_;
  my ($line, @elems);

  print '<ul>';

  open(INFILE, "<$files{$type}") or &Vessel::CgiError("Unable to open $files{$type}: $!");
  while ($line = <INFILE>) {
    @elems = split(/\|/, $line);
    &PrintPostSummary($this, $elems[0], $elems[1], $elems[8], $elems[4]);
  }
  close(INFILE);

  print '</ul>';
  return;
}

#### PrintDateSortedPosts: read all posts from the specified file and print
#      in chronological order in condensed format
sub PrintDateSortedPosts {
  my ($type, $this) = @_;
  my ($line, @elems, %posts);

  open(INFILE, "<$files{$type}") or &Vessel::CgiError("Unable to open $files{$type}: $!");
  while ($line = <INFILE>) {
    @elems = split(/\|/, $line);
    $posts{$elems[8]} = $line;
  }
  close(INFILE);

  print '<ul>';

  foreach $line (sort keys %posts) {
    @elems = split(/\|/, $posts{$line});    
    &PrintPostSummary($this, $elems[0], $elems[1], $elems[8], $elems[4]);
  }

  print '</ul>';
  return;
}

#### AddPost: write a single post to the specified file using the
#      supplied form parameters
sub AddPost {
  my ($type, $author, $cgi) = @_;
  my ($id, $date, $hr, $mi, $am);

  $item{'title'} = &FileProtect($cgi->param('title'), 36);
  $item{'link'} = &FileProtect($cgi->param('link'), 256);
  $item{'description'} = &FileProtect($cgi->param('description'), 4000, 1);
  $item{'source_url'} = &FileProtect($cgi->param('source_url'), 256);
  $item{'source'} = &FileProtect($cgi->param('source'), 24);
  $item{'category'} = &FileProtect($cgi->param('category'), 24);

  if ($date = $cgi->param('pubDate')) {
    $date =~ /^(\d{2})\/(\d{2})\/(\d{4}) (\d{2}):(\d{2}) ([ap]m)$/;
    $date = "$3/$1/$2/";
    $hr = $4;
    $mi = $5;
    $am = $6;
    $hr += 12 if ($am =~ /pm/i && $hr < 12);
    $hr -= 12 if ($am =~ /am/i && $hr == 12);
    $date .= "$hr:$mi:00/" . $cgi->param('day');
  } else {
    $date = &Vessel::GetDateString('file');
  }

  $item{'link'} = "http://$item{'link'}" if ($item{'link'} !~ /^http:\/\//);
  $item{'source_url'} = "http://$item{'source_url'}" if ($item{'source_url'} !~ /^http:\/\//);

  $id = &GetMaxItems($type) + 1;

  open(OUTFILE, ">>$files{$type}") or &Vessel::CgiError("Unable to open $files{$type}: $!");
  flock(OUTFILE, 2);
  print OUTFILE "$id|$item{'title'}|$item{'link'}|$item{'description'}|$author|$item{'source_url'}|$item{'source'}|$item{'category'}|$date\n";
  close(OUTFILE);

  &SetMaxItems($type, $id);
  return $id;
}

#### PrintCommentForm: use type and item ID to generate a comment form
sub PrintCommentForm {
  my ($type, $id, $usr, $eml) = @_;

  print <<HTML;
<h2>Post Your Comment</h2>
<form id="postComment" method="post" action="comment.cgi">
<table summary="new comment form">
<tr><td class="lbl">Name:</td><td><input type="text" name="author" size="24" maxlength="36" class="txt" value="$usr" /></td></tr>
<tr><td class="lbl">Email:</td><td><input type="text" name="email" size="24" maxlength="36" class="txt" value="$eml" /></td></tr>
<tr><td class="lbl">Location:</td><td><input type="text" name="location" size="24" maxlength="36" class="txt" /></td></tr>
<tr><td class="lbl">Comment:</td><td><textarea name="comment" rows="2" cols="48"></textarea></td></tr>
</table><p><input type="hidden" name="type" value="$type" /><input type="hidden" name="id" value="$id" />
<input type="submit" value="Submit" class="btn" /> <input type="reset" value="Clear" class="btn" />
</p></form>
HTML

  return;
}

#### PrintComment: take arguments and print comment in table layout
sub PrintComment {
  my ($cmnt, $date, $eml, $usr, $loc) = @_;

  print qq{<table class="post" summary="post comment">\n<tr><td colspan="2">$cmnt</td></tr>\n<tr><td>Posted: <i>};
  print &Vessel::GetDateFromFile($date, 'long');
  print qq{</i></td><td align="right">By: <a href="mailto:$eml">$usr</a> in $loc</td></tr>\n};
  print "</table><br />\n";  

  return;
}

#### PrintAllComments: use type and item ID to locate and display comments
sub PrintAllComments {
  my ($type, $id) = @_;
  my ($line, @elems, $found);

  print "<h2>Comments</h2>\n";

  open(INFILE, "<$files{$type}") or &Vessel::CgiError("Unable to open $files{$type}: $!");
  while ($line = <INFILE>) {
    @elems = split(/\|/, $line);

    if ($id == $elems[0]) {
      $found = 1;
      &PrintComment($elems[1], $elems[5], $elems[3], $elems[2], $elems[4]);
    }
  }
  close(INFILE);

  print "<p>No comments entered for this item.</p>\n" if (! $found);
  return;
}


1;
