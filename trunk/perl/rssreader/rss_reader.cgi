#!/usr/local/bin/perl

# rss_reader.cgi:  uses the XML::Parser (Expat) module to parse a well-formed
#   XML file and display in formatted structure to the web

use strict;
use CGI;
use HTTP::Request;
use LWP::UserAgent;
use XML::Parser;
use DBI;
use WebUtil;

my $cgi = new CGI;
print $cgi->header();

my $this = $ENV{'SCRIPT_NAME'};
my $pid = $cgi->param('pid');
my $switch = $cgi->param('switch');
my $feed = $cgi->param('feed');
my ($dbh, $sql, $sth, $rc);
my (%channel, %items, $item, $curr_el, $curr_val, $image, %sorting);

$dbh = &PRDUtil::DbConn('cgi', '');

&PageHead();

if ($feed) {
  &PrintRSS();
} else {
  &PrintNav();
}

print "</body></html>\n";
$dbh->disconnect;
exit;


sub PrintRSS {
  my ($link, $agent, $request, $response, $rss_doc, $prsr, $sort_html, $order);
  my ($i, $cat, $cpy, $pub, $bld, $edit_nm, $edit_eml, $web_nm, $web_eml);

  print <<HTML;
<link rel="stylesheet" type="text/css" href="css.cgi?css=main">
<script type="text/javascript">
<!--
function setItem(jshref) {
  parent.Title.document.jsdata.jsitem.value = jshref;
  document.location.href = jshref;
  return;
}
// -->
</script>
</head><body>
<form name="feed_form" id="feed_form_id" method="POST" action="$this">
<input type="hidden" name="feed" id="feed_id" value="$feed">
HTML

  # Build SQL to get channel URL 
  $sql = qq{SELECT link FROM channel WHERE id = $feed};
  $sth = $dbh->prepare($sql) || &PRDUtil::DbErr('cgi', "Unable to prepare statement: $DBI::errstr\n", $dbh);
  $sth->execute || &PRDUtil::DbErr('cgi', "Unable to execute statement: $DBI::errstr\n", $dbh);

  ($link) = $sth->fetchrow_array;
  $rc = $sth->finish;

  # Request and parse the RSS feed URL
  $agent = new LWP::UserAgent;
  $request = new HTTP::Request('GET', $link);
  $response = $agent->request($request);
  $rss_doc = $response->content;

  $prsr = new XML::Parser(Handlers => {Start => \&HandleStart, End => \&HandleEnd, Char => \&HandleChar});
  $prsr->parse($rss_doc);

  $cat = qq{<b>[$channel{'category'}]</b>} if ($channel{'category'});

  if ($channel{'copyright'}) {
    $cpy = qq{[<a href="javascript:alert('$channel{'copyright'}');">copyright</a>]};
  }

  $pub = &DateFromRfcToIso($channel{'pubDate'});
  $bld = &DateFromRfcToIso($channel{'lastBuildDate'});

  ($edit_nm, $edit_eml) = &GetNameEmail($channel{'managingEditor'});
  ($web_nm, $web_eml) = &GetNameEmail($channel{'webMaster'});

  $order = $cgi->param('order');
  $sort_html = &GetSortOptions($cgi->param('sort'), $order);
  &BuildSortingHash($cgi->param('sort'));

  print <<HTML;
<table border="1" cellpadding="4" cellspacing="0" summary="This is a layout table.">
<tr><td colspan="2" class="ttl"><a href="javascript:setItem('$channel{'link'}');" class="pln">$channel{'title'}</a></td>
  <td align="center"><a href="javascript:setItem('$channel{'image'}{'link'}');"><img src="$channel{'image'}{'url'}" alt="$channel{'image'}{'title'}" /></a></td></tr>
<tr><td colspan="3">$cat $channel{'description'} $cpy</td></tr>
<tr><td>Published: <i>$pub</i><br />Last Build: <i>$bld</i></td>
  <td>Editor: <a href="mailto:$edit_eml">$edit_nm</a><br />Webmaster: <a href="mailto:$web_eml">$web_nm</a></td>
  <td align="center"><label for="sort_id">Sort:</label> $sort_html</td></tr>
</table></form>
<br /><hr /><br />
HTML

  if ($order eq 'desc') {
    foreach $i (reverse sort keys %sorting) {
      foreach $item (@{$sorting{$i}}) {
        &PrintItem($item);
  } } }
  else {
    foreach $i (sort keys %sorting) {
      foreach $item (@{$sorting{$i}}) {
        &PrintItem($item);
  } } }  

  return;
}


sub PrintNav {
  my ($p_opt, %pc, %c, $pc_tr, $c_opt, $mod, $ttl, $lnk);

  $p_opt = &GetPersonOptions($pid);

  print <<HTML;
<link rel="stylesheet" type="text/css" href="css.cgi?css=nav">
<script type="text/javascript">
<!--
function setChannel(jshref) {
  parent.Content.Title.document.jsdata.jschannel.value = jshref;
  return;
}
// -->
</script>
<base target="Body">
</head><body marginheight="0" marginwidth="0">
<form name="nav_form" id="nav_form_id" method="POST" action="$this" target="_self">
<table border="0" cellpadding="2" cellspacing="0" width="220">
<tr><td align="center" bgcolor="white"><img src="stsci_full.gif" alt="STScI Logo" /></td></tr>
<tr><td bgcolor="blue" height="15">&nbsp;</td></tr>
<tr><td align="center"><span style="font-size:13pt"><label for="pid_id">YOUR CHANNELS</label></span></td></tr>
<tr><td align="center"><select name="pid" id="pid_id">$p_opt</select>&nbsp;
  <input type="submit" name="switch" value="Go" /></td></tr>
HTML

  %pc = &GetPersonChannelHash($pid);
  %c = &GetChannelHash();

  $mod = $cgi->param('mod');
  $ttl = $cgi->param('ttl');
  $lnk = $cgi->param('lnk');

  # If a channel is selected for modification, add or drop it according to the button selected
  if ($mod) {
    if ($switch eq 'Add' && ! $pc{$mod}) {
      $sql = qq{INSERT INTO person_x_channel (person, channel) VALUES ($pid, $mod)};
      $rc = $dbh->do($sql) || &PRDUtil::DbErr('cgi', "Unable to do '$sql': $DBI::errstr\n", $dbh);
    } elsif ($switch eq 'Drop') {
      $sql = qq{DELETE FROM person_x_channel WHERE person = $pid And channel = $mod};
      $rc = $dbh->do($sql) || &PRDUtil::DbErr('cgi', "Unable to do '$sql': $DBI::errstr\n", $dbh);
    }
  }

  # If new channel requested, add it to the channel table, then to the user's channels
  if ($switch eq 'Create' && $ttl && $lnk && ! $c{$ttl}) {
    $ttl = &PRDUtil::SqlProtect($ttl, 128);
    $lnk = &PRDUtil::SqlProtect($lnk, 128);

    $sql = qq{SELECT Max(id) FROM channel};
    $sth = $dbh->prepare($sql) || &PRDUtil::DbErr('cgi', "Unable to prepare statement: $DBI::errstr\n", $dbh);
    $sth->execute || &PRDUtil::DbErr('cgi', "Unable to execute statement: $DBI::errstr\n", $dbh);

    ($mod) = $sth->fetchrow_array;
    $rc = $sth->finish;
    $mod++;

    $sql = qq{INSERT INTO channel (id, title, link) VALUES ($mod, '$ttl', '$lnk')};
    $rc = $dbh->do($sql) || &PRDUtil::DbErr('cgi', "Unable to do '$sql': $DBI::errstr\n", $dbh);

    $sql = qq{INSERT INTO person_x_channel (person, channel) VALUES ($pid, $mod)};
    $rc = $dbh->do($sql) || &PRDUtil::DbErr('cgi', "Unable to do '$sql': $DBI::errstr\n", $dbh);
  }

  if ($pid) {
    $pc_tr = &GetPersonChannelList($pid);
    $c_opt = &GetChannelOptions();

    print <<HTML;
$pc_tr<tr><td><hr /></td></tr>
<tr><td align="center"><label for="mod_id">MODIFY YOUR CHANNELS</label><br />
  <select name="mod" id="mod_id">$c_opt</select><br />
  <input type="submit" name="switch" value="Add" />&nbsp;<input type="submit" name="switch" value="Drop" /></td></tr>
<tr><td><hr /></td></tr>
<tr><td align="center">CREATE A CHANNEL<br />
  <label for="ttl_id">Title</label>&nbsp;<input type="text" name="ttl" id="ttl_id" size="16" maxlength="128" /><br />
  <label for="lnk_id">Link</label>&nbsp;<input type="text" name="lnk" id="lnk_id" size="16" maxlength="128" /><br />
  <input type="submit" name="switch" value="Create" /></td></tr>
HTML
  }

  print "</table></form>\n";
  return;
}


sub PrintItem {
  my ($i) = @_;
  my ($pub, $cat, $auth_nm, $auth_eml, $auth);

  $pub = &DateFromRfcToIso($items{$i}{'pubDate'});
  $cat = qq{<b>[$items{$i}{'category'}]</b>} if ($items{$i}{'category'});

  ($auth_nm, $auth_eml) = &GetNameEmail($items{$i}{'author'});
  $auth = qq{[<a href="mailto:$auth_eml">$auth_nm</a>]} if ($items{$i}{'author'});

  print <<HTML;
<table border="1" cellpadding="4" cellspacing="0" summary="This is a layout table.">
<tr><td colspan="2" class="itm"><a href="javascript:setItem('$items{$i}{'link'}');" class="pln">$items{$i}{'title'}</a>
<tr><td colspan="2">$cat $items{$i}{'description'} $auth</td></tr>
<tr><td>Published: <i>$pub</i></td><td align="right">Source: <a href="javascript:setItem('$items{$i}{'source_url'}');">$items{$i}{'source'}</a></td></tr>
</table><br />
HTML

  return;
}

sub BuildSortingHash {
  my ($sort) = @_;
  my ($i, $pub, $auth, $itm);
  my $itm = '0001';

  if ($sort eq 'pubDate') {
    foreach $i (sort {$a<=>$b} keys %items) {
      $pub = &DateFromRfcToIso($items{$i}{'pubDate'});
      push (@{$sorting{$pub}}, $i);
    }
  } elsif ($sort eq 'author') {
    foreach $i (sort {$a<=>$b} keys %items) {
      ($auth) = &GetNameEmail($items{$i}{'author'});
      push (@{$sorting{$auth}}, $i);
    }
  } elsif ($sort eq 'title' || $sort eq 'category' || $sort eq 'source') {
    foreach $i (sort {$a<=>$b} keys %items) {
      push (@{$sorting{$items{$i}{$sort}}}, $i);
    }
  } else {  # 'default' sort
    foreach $i (sort {$a<=>$b} keys %items) {
      push (@{$sorting{$itm}}, $i);
      $itm++;
    }
  }

  return;
}


sub HandleStart {
  my ($p, $el, %atts) = @_;

  if ($el eq 'image') {
    $image = 1;
  } elsif ($el eq 'item') {
    $item++;
  } elsif ($el eq 'source') {
    $items{$item}{'source_url'} = $atts{'url'};
  }

  $curr_el = $el;
  $curr_val = '';
  return;
}


sub HandleEnd {
  my ($p, $el) = @_;
  $curr_val =~ s/^\s+|\s+$//g;  # remove heading and trailing spaces

  if ($image) {
    $channel{'image'}{$curr_el} = $curr_val;
  } elsif (! $item) {
    $channel{$curr_el} = $curr_val;
  } else {
    $items{$item}{$curr_el} = $curr_val;
  }

  $image = 0 if ($el eq 'image');
  return;
}


sub HandleChar {
  my ($p, $str) = @_;
  $curr_val .= $str;
  return;
}


sub GetPersonOptions {
  my ($selected) = @_;
  my ($id, $name);
  my $html = '<option></option>';

  # Build SQL to get persons with assigned channels 
  $sql = qq{SELECT p.id, p.fname||' '||p.lname FROM person p WHERE p.id In (} .
         qq{SELECT x.person FROM person_x_channel x) ORDER BY p.lname, p.fname};
  $sth = $dbh->prepare($sql) || &PRDUtil::DbErr('cgi', "Unable to prepare statement: $DBI::errstr\n", $dbh);
  $sth->execute || &PRDUtil::DbErr('cgi', "Unable to execute statement: $DBI::errstr\n", $dbh);

  while (($id, $name) = $sth->fetchrow_array) {
    $html .= qq{<option value="$id"};
    if ($selected == $id) { $html .= " selected"; }
    $html .= qq{>$name</option>\n};
  }
  $rc = $sth->finish;

  return $html;
}


sub GetPersonChannelHash {
  my ($person) = @_;
  my ($id, %pc);

  return if (! $person);

  # Build SQL to get a person's channels 
  $sql = qq{SELECT channel FROM person_x_channel WHERE person = $person};
  $sth = $dbh->prepare($sql) || &PRDUtil::DbErr('cgi', "Unable to prepare statement: $DBI::errstr\n", $dbh);
  $sth->execute || &PRDUtil::DbErr('cgi', "Unable to execute statement: $DBI::errstr\n", $dbh);

  while (($id) = $sth->fetchrow_array) {
    $pc{$id} = 1;
  }
  $rc = $sth->finish;

  return %pc;
}


sub GetChannelHash {
  my ($title, %c);

  # Build SQL to get channels 
  $sql = qq{SELECT title FROM channel};
  $sth = $dbh->prepare($sql) || &PRDUtil::DbErr('cgi', "Unable to prepare statement: $DBI::errstr\n", $dbh);
  $sth->execute || &PRDUtil::DbErr('cgi', "Unable to execute statement: $DBI::errstr\n", $dbh);

  while (($title) = $sth->fetchrow_array) {
    $c{$title} = 1;
  }
  $rc = $sth->finish;

  return %c;
}


sub GetPersonChannelList {
  my ($person) = @_;
  my ($id, $title);
  my $html = "<tr><td><hr /></td></tr>\n";

  # Build SQL to get a person's channels 
  $sql = qq{SELECT c.id, c.title FROM channel c, person_x_channel x } .
         qq{WHERE c.id = x.channel And x.person = $person ORDER BY Lower(c.title)};
  $sth = $dbh->prepare($sql) || &PRDUtil::DbErr('cgi', "Unable to prepare statement: $DBI::errstr\n", $dbh);
  $sth->execute || &PRDUtil::DbErr('cgi', "Unable to execute statement: $DBI::errstr\n", $dbh);

  while (($id, $title) = $sth->fetchrow_array) {
    $html .= qq{<tr><td class="bg">|&nbsp;<a href="$this?feed=$id" class="ln" onClick="setChannel(this.href)">$title</a></td></tr>\n};
  }
  $rc = $sth->finish;

  return $html;
}


sub GetChannelOptions {
  my ($id, $title);
  my $html = '<option></option>';

  # Build SQL to get channels 
  $sql = qq{SELECT id, title FROM channel ORDER BY Lower(title)};
  $sth = $dbh->prepare($sql) || &PRDUtil::DbErr('cgi', "Unable to prepare statement: $DBI::errstr\n", $dbh);
  $sth->execute || &PRDUtil::DbErr('cgi', "Unable to execute statement: $DBI::errstr\n", $dbh);

  while (($id, $title) = $sth->fetchrow_array) {
    $html .= qq{<option value="$id">$title</option>\n};
  }
  $rc = $sth->finish;

  return $html;
}


sub GetSortOptions {
  my ($selected, $checked) = @_;
  my (%opts, $html, $i, $chk_a, $chk_d);
  $opts{1} = {'v'=>'default', 'l'=>'Default'};
  $opts{2} = {'v'=>'pubDate', 'l'=>'Pub Date'};
  $opts{3} = {'v'=>'title', 'l'=>'Title'};
  $opts{4} = {'v'=>'author', 'l'=>'Author'};
  $opts{5} = {'v'=>'category', 'l'=>'Category'};
  $opts{6} = {'v'=>'source', 'l'=>'Source'};

  $html = qq{<select name="sort" id="sort_id">\n};
  
  foreach $i (sort keys %opts) {
    $html .= qq{<option value="$opts{$i}{'v'}"};
    $html .= ' selected' if ($opts{$i}{'v'} eq $selected);
    $html .= qq{>$opts{$i}{'l'}</option>\n};
  }

  ($checked eq 'desc') ?  $chk_d = 'checked' : $chk_a = 'checked';

  $html .= qq{</select>&nbsp;<input type="submit" name="switch" value="Go" /><br />\n};
  $html .= qq{<input type="radio" name="order" id="ord_a_id" value="asc" $chk_a /><label for="ord_a_id">ASC</label>};
  $html .= '&nbsp;&nbsp;&nbsp;&nbsp;';
  $html .= qq{<input type="radio" name="order" id="ord_d_id" value="desc" $chk_d /><label for="ord_d_id">DESC</label>\n};

  return $html;
}


sub DateFromRfcToIso {
  my ($rfc_date) = @_;
  my ($iso_date, $mm);
  my %mon = ('Jan'=>'01', 'Feb'=>'02', 'Mar'=>'03', 'Apr'=>'04', 'May'=>'05', 'Jun'=>'06',
               'Jul'=>'07', 'Aug'=>'08', 'Sep'=>'09', 'Oct'=>'10', 'Nov'=>'11', 'Dec'=>'12');

  # Sample RFC date - Fri, 29 Oct 2004 15:44:09 EST
  $rfc_date =~ /(\d{2}) ([A-Za-z]{3}) (\d{2,4}) (\d{2}):(\d{2})/;
  $mm = $mon{$2};  

  # Sample ISO date - 2004-10-29 15:44
  $iso_date = "$3-$mm-$1 $4:$5";

  $iso_date = '' if ($iso_date !~ /\d/);
  return $iso_date;
}


sub GetNameEmail {
  my ($nm_eml) = @_;
  my ($nm, $eml);

  # RSS 2.0 format - user@email.address (Fname Lname)
  $nm_eml =~ /^(.*?)( \((.*?)\))?$/;
  $nm = $3;
  $eml = $1;

  $nm = $eml if (! $nm);

  return ($nm, $eml);
}


#### PageHead: prints the standard HTML <head> for all pages associated with this script
sub PageHead {
  print <<HTML;
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html><head>
<title>WebRSS News Reader</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta name="Description" lang="en" content="CGI web-based news reader.">
<meta name="Keywords" lang="en" content="RSS, news reader">
HTML

  return;
}

