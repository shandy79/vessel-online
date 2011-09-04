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
} elsif ($id) {
  &PrintOneItem();
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
<h1>Vessel Headlines</h1><hr />

<form id="staffItem" method="post" action="$Vessel::cgiRoot${this}">
<table summary="new item form">
<tr><td class="lbl">Title:</td><td><input type="text" name="title" size="36" maxlength="36" class="txt" /></td></tr>
<tr><td class="lbl">URL:</td><td><input type="text" name="link" size="48" maxlength="256" class="txt" /></td></tr>
<tr><td class="lbl">Category:</td><td><input type="text" name="category" size="16" maxlength="24" class="txt" /></td></tr>
<tr><td class="lbl">Source:</td><td><input type="text" name="source" size="16" maxlength="24" class="txt" /></td></tr>
<tr><td class="lbl">Source URL:</td><td><input type="text" name="source_url" size="48" maxlength="256" class="txt" /></td></tr>
<tr><td class="lbl">Description:</td><td><textarea name="description" rows="4" cols="60"></textarea></td></tr>
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
  my ($usr, $eml) = &Vessel::GetSessionUser($cgi);

  # If the user supplied a valid password, retrieve credentials
  if (! $usr && $userKey) {
    $usr = $Vessel::staff{$userKey}{'name'};
    $eml = $Vessel::staff{$userKey}{'email'};
  }

  $links{'Archive'} = "$Vessel::cgiRoot${this}";
  $links{'New Item'} = "$Vessel::cgiRoot${this}?id=new";
  &Vessel::PrintMainOpen('i', %links);
  print "<h1>Vessel Headlines</h1><hr />\n";

  # If no valid user, then print error message and end function
  if (! $usr) {
    &Vessel::InputError("You are not logged in or have submitted an invalid password.<br />\n");
    return;
  } elsif (! $cgi->param('title') || ! $cgi->param('description')) {
    &Vessel::InputError("You must enter a title and description.<br />\n");
    return;
  }

  # AddPost performs file protection prior to file append
  $id = &PostFile::AddPost('hl', "$eml ($usr)", $cgi);

  print <<HTML;
<b>SUCCESS!</b>  <a href="mailto:$eml">$usr</a>, your headline has been added.<br />
Here is a link to your <a href="$Vessel::cgiRoot${this}?id=$id">headline</a>.
<script type="text/javascript">
<!--
  parent.Tools.location.reload(true);
// -->
</script>
HTML

  return;
}

#### PrintOneItem:
sub PrintOneItem {
  my (%item, $usr, $eml, $date);

  print qq{<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/form.css" />\n};
  $links{'Archive'} = "$Vessel::cgiRoot${this}";
  $links{'New Item'} = "$Vessel::cgiRoot${this}?id=new";
  $links{'Next'} = "$Vessel::cgiRoot${this}?id=" . ($id + 1) if ($id < &PostFile::GetMaxItems('hl'));
  $links{'Previous'} = "$Vessel::cgiRoot${this}?id=" . ($id - 1) if ($id > 1);

  &Vessel::PrintMainOpen('i', %links);
  print "<h1>Vessel Headlines</h1><hr />\n";

  %item = &PostFile::GetPost('hl', $id);

  ($usr, $eml) = &Vessel::GetAuthorFromFile($item{'author'});
  $date = &Vessel::GetDateFromFile($item{'pubDate'}, 'long');

  print <<HTML;
<table class="post" summary="post item">
<tr><td><a href="$item{'link'}" class="ttl">$item{'title'}</a></td>
  <td align="right">By: <a href="mailto:$eml">$usr</a></td></tr>
<tr><td colspan="2"><span class="cat">[$item{'category'}]</span>
  $item{'description'}</td></tr>
<tr><td>Posted: <i>$date</i></td><td align="right">Source: <a href="$item{'source_url'}">$item{'source'}</a></td></tr>
</table><br />
HTML

  ($usr, $eml) = &Vessel::GetSessionUser($cgi);
  &PostFile::PrintAllComments('hc', $id);
  &PostFile::PrintCommentForm('hc', $id, $usr, $eml);

  return;
}

#### PrintAllItems:
sub PrintAllItems {
  $links{'New Item'} = "$Vessel::cgiRoot${this}?id=new";
  &Vessel::PrintMainOpen('i', %links);
  print "<h1>Vessel Headlines Archive</h1><hr />\n";

  &PostFile::PrintAllPosts('hl', $this);
  return;
}
