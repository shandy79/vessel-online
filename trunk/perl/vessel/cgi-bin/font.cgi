#!/usr/bin/perl

use CGI;
require 'site/pl/Vessel.pl';

my $cgi = new CGI;
my $this = $ENV{'SCRIPT_NAME'};

print $cgi->header();
&Vessel::PrintHeader('11', 'main');
print <<HEAD;
<script type="text/javascript">
<!--
  var imgRoot = "$Vessel::htmlRoot/img/font/";
  var imgExt = ".gif";

  function updateText(txtFld) {
    var str = txtFld.value;
    str = str.replace(/^\\s+/g, '');
    str = str.toLowerCase();
    str = str.replace(/[^a-z ]/g, '');
    str = str.replace(/\\s+/g, ' ');

    var txtStr = '';
    var strAry = str.split('');

    for (var i = 0; i < strAry.length; i++) {
      ltr = strAry[i];
      if (ltr != ' ') {
        txtStr += '<img src="' + imgRoot + ltr + imgExt + '" alt="' + ltr + '" />';
      } else {
        txtStr += "&nbsp;&nbsp;&nbsp;";
      }
    }

//    txtFld.value = str;
    document.getElementById('imgTxt').innerHTML = txtStr;
    return true;
  }
// -->
</script>
<link rel="stylesheet" type="text/css" href="$Vessel::htmlRoot/site/css/form.css" />
HEAD

my %links = ('Events'=>'event.cgi', 'Headlines'=>'headline.cgi', 'Playlist'=>'playlist.cgi');
&Vessel::PrintMainOpen('i', %links);

print <<HTML;
  <h1>Vessel Font Generator</h1><hr />
  <form id="vesselType" method="post" action="$Vessel::cgiRoot${this}">
    <p>If the text you enter below does not appear as it is being typed, please wait a few seconds and then press <b>Display</b> if it
      still does not appear.  This feature has been tested in Firefox 1.5, Netscape 7.2, and Internet Explorer 6.0 on Windows.<br />
      &nbsp;<br />Enter text to display:<br />
      <textarea name="string" rows="2" cols="60" class="txt" onkeyup="updateText(this);"></textarea><br />
      <input type="submit" value="Display" class="btn" /> <input type="reset" value="Clear" class="btn" /></p>
  </form>
  <h2>Your Text</h2><br />
  <div id="imgTxt">
HTML

if ($cgi->param()) {
  my $str = $cgi->param('string');
  my ($ltr);

  $str =~ s/^\s+//;
  $str = lc($str);
  $str =~ s/[^a-z ]//g;
  $str =~ s/\s+/ /g;

  foreach $ltr (split(//, $str)) {
    if ($ltr ne ' ') {
      print qq{<img src="$Vessel::htmlRoot/img/font/$ltr.gif" alt="$ltr" />};
    } else {
      print '&nbsp;&nbsp;&nbsp;';
    }
  }
}

print "</div>\n";
&Vessel::PrintMainClose();
exit;
