#!/usr/bin/perl

# Vessel.pl
# Parameters and utility function library for Vessel website.
# Created by: Steve Handy (last mod: 08/10/2008)

#### Package Vessel: contains variables to be used as parameters for
#      all web applications
package Vessel;

  #### Environment
  $cgiRoot = '/cgi-bin';
  $dynRoot = 'http://sjhandy1.tripod.com';
  $htmlRoot = "$dynRoot$cgiRoot";

  #### Vessel staff
  $staff{'<admin_hash>'} = {'name'=>'steve', 'email'=>'agbadza79_AT_yahoo_DOT_com'};
  $siteAdmin = '<admin_hash>';
  $siteEmail = $staff{$siteAdmin}{'email'};

  #### Translation hashes
  %wkdy = (0=>'Sun', 1=>'Mon', 2=>'Tue', 3=>'Wed', 4=>'Thu', 5=>'Fri', 6=>'Sat');
  %mon = ('01'=>'Jan', '02'=>'Feb', '03'=>'Mar', '04'=>'Apr', '05'=>'May', '06'=>'Jun',
          '07'=>'Jul', '08'=>'Aug', '09'=>'Sep', '10'=>'Oct', '11'=>'Nov', '12'=>'Dec');


#### CgiError: general purpose error function.  Prints out the error message
#      and exits the script.
sub CgiError {
  my ($msg) = @_;
  print qq{<b><span style="color:red;">ERROR: $msg</span></b><br />\n};
  print qq{Please report this error to the <a href="mailto:$siteEmail">webmaster</a>.</body></html>\n};
  exit;
}

#### InputError: used for displaying form input or authentication errors.
sub InputError {
  my ($msg) = @_;
  print qq{<b>Your submission has the following errors:</b><br /><span style="color:red;">$msg</span>\n};
  print qq{<br />Please use the browser's <a href="javascript:history.back();">BACK</a> button to revise your input.\n};
  return;
}

#### GetSessionUser: obtain the current user and email (if any) based on the userKey cookie.
sub GetSessionUser {
  my ($cgi) = @_;
  my $userKey = $cgi->cookie('userKey');
  return ($staff{$userKey}{'name'}, $staff{$userKey}{'email'});
}

#### SetSessionUser: prints a line to set a cookie for the current authenticated user
#      Set-Cookie: cust_prefer=orange; expires=04-Jul-2003 00:00:0 GMT
sub SetSessionUser {
  my ($passwd) = @_;
  my $userKey = &Authenticate($passwd);

  print "Set-Cookie: userKey=$userKey;\n" if ($userKey);
  return $userKey;
}

#### Authenticate: determine if the user-supplied password matches any of the staff passwords
#      $salt = join '', ('.', '/', 0..9, 'A'..'Z', 'a'..'z')[rand 64, rand 64, rand 64, rand 64];
#      $pwd = crypt('new_passwd', $salt);
sub Authenticate {
  my ($passwd) = @_;
  my ($i);

  foreach $i (keys %staff) {
    return $i if (crypt($passwd, $i) eq $i);
  }

  return;
}

#### GetDateString: translate system date to specified format (file=YYYY/MM/DD/HH:MI:SS/WD, 
#      rfc=WDY, MM MON YYYY HH:MI:SS EST)
sub GetDateString {
  my ($format) = @_;
  my @dateParts = localtime();
  $dateParts[5] += 1900;
  $dateParts[4]++;

  # Pad day, hour, minute, second, month with a zero if single digit
  for (my $i = 0; $i < 5; $i++) {
    $dateParts[$i] = '0' . $dateParts[$i] if (length($dateParts[$i]) == 1);
  }

  if ($format eq 'rfc') {
    return "$wkdy{$dateParts[6]}, $dateParts[3] $mon{$dateParts[4]} $dateParts[5] $dateParts[2]:$dateParts[1]:$dateParts[0] EST";
  } else {
    return "$dateParts[5]/$dateParts[4]/$dateParts[3]/$dateParts[2]:$dateParts[1]:$dateParts[0]/$dateParts[6]";
  }
}

#### GetDateFromFile: translate stored file date to display format (long=MM/DD/YYYY HH:MI:SS,
#      short=MM/DD, rfc=WDY, MM MON YYYY HH:MI:SS EST)
sub GetDateFromFile {
  my ($fileDate, $format) = @_;
  my @dateParts = split(/\//, $fileDate);

  if ($format eq 'rfc') {
    return "$wkdy{$dateParts[4]}, $dateParts[2] $mon{$dateParts[1]} $dateParts[0] $dateParts[3] EST";
  } elsif ($format eq 'long') {
    return "$dateParts[1]/$dateParts[2]/$dateParts[0] $dateParts[3]";
  } else {
    return "$dateParts[1]/$dateParts[2]";
  }
}

#### GetAuthorFromFile: translate stored file author.  Returns username, email as list.
sub GetAuthorFromFile {
  my ($author) = @_;
  $author =~ /^([^ ]+) \((\w+)\)$/;
  return ($2, $1);
}

#### PrintHeader: HTML page header with title, content metadata and style info.
sub PrintHeader {
  my ($dtd, $css, $title) = @_;
  my ($dtdTxt);
  my $xmlDecl = '<?xml version="1.0" encoding="iso-8859-1"?>';
  $title = 'Vessel Online' if (! $title);

  if ($dtd eq '11') {
    $dtdTxt = qq{$xmlDecl\n<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">};
    $htmlTxt = '<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">';
  } elsif ($dtd eq '10') {
    $dtdTxt = qq{$xmlDecl\n<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">};
    $htmlTxt = '<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">';
  } else {
    $dtdTxt = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">';
    $htmlTxt = '<html>';
  }

  print <<HTML;
$dtdTxt
$htmlTxt<head>
<title>$title</title>
<meta http-equiv="Content-Type" content="text/html;charset=iso-8859-1" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/$css.css" />
<script type="text/javascript" src="site/js/dhtmlApi.js"></script>
<script type="text/javascript">
<!--
  MM_reloadPage(true);
// -->
</script>
HTML

  return;
}

#### PrintMainOpen: 
sub PrintMainOpen {
  my ($titleImg, %links) = @_;
  my ($ln);

  if ($titleImg eq 'i') {
    $titleImg = 'vesselInt.gif';
  } else {  # $title_img eq 'o'
    $titleImg = 'vesselTtl.gif';
  }

  print <<HTML;
</head>
<body onload="initDHTMLAPI()">
<div id="topBk"><img src="$Vessel::htmlRoot/img/topBk.gif" alt="bkgrnd" /></div>
<div id="title" style="position:absolute; left:12px; top:2px; width:500px; height:35px; z-index:1">
  <a href="javascript:top.location.href='$Vessel::htmlRoot/index.html';" onmouseout="return statusMsg('')"
    onmouseover="return statusMsg('Vessel Home')"><img src="$Vessel::htmlRoot/img/$titleImg" alt="Vessel" /></a>
</div>
<div id="links" style="position:absolute; left:12px; top:42px; width:500px; height:18px; z-index:2">
HTML

  foreach $ln (sort keys %links) {
    print qq{  <a href="$links{$ln}" class="ln">. $ln</a>&nbsp;&nbsp;&nbsp;&nbsp;\n};
  }

  print <<HTML;
</div>
<div id="bar"><img src="$Vessel::htmlRoot/img/bar.gif" alt="bar" /></div>
<div id="contents" style="position:absolute; left:25px; top:87px; width:570px">
HTML

  return;
}

#### PrintMainClose:
sub PrintMainClose {
  print <<HTML;

<div id="w3c" style="position:relative; top:75px;">
  <a href="http://validator.w3.org/check?uri=referer"><img src="http://www.w3.org/Icons/valid-xhtml11" alt="Valid XHTML 1.1!" /></a>
  <a href="http://jigsaw.w3.org/css-validator/check/referer"><img src="http://jigsaw.w3.org/css-validator/images/vcss" alt="Valid CSS!" /></a>
</div></div></body></html>
HTML

  return;
}


1;
