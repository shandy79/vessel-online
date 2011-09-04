#!/usr/bin/perl

# EN605_452.pm
# Parameters and utility function library for EN605.452 Perl Programming Assignment.
# Created by: Steve Handy (last mod: 06/27/2010)

#### Package EN605_452: contains variables to be used as parameters for
#      all web applications
package EN605_452;

  #### EN605_452 Site Info
  $title = 'EN605.452: Biological Databases &amp; Tools';
  $owner = 'Steven Handy';
  $adminEmail = 'shandy3@jhu.edu';
  $copyright = "&copy; 2010 $EN605_452::owner.  All rights reserved.";

  #### Pages
  $pages[1] = {id => 1, title => 'BioPerl Blast Report', subtitle => '', url => 'bioperl_remoteblast.cgi'};


################
####  HTML  ####################################################################
################


#### PrintHead:  HTML <head> with title, content metadata and style info
sub PrintHead {
  my ($dtd, $pageTitle, $depth) = @_;
  my ($dtdTxt, $htmlTxt);
  my $xmlDecl = '';  # '<?xml version="1.0" encoding="UTF-8"?>';  Removed due to issues w/IE6+
  $pageTitle = (':  ' . $pageTitle) if ($pageTitle);
  my $path = &GetPath($depth);

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
$htmlTxt
<head>
  <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
  <meta http-equiv="Content-Script-Type" content="text/javascript" />
  <meta http-equiv="Content-Style-Type" content="text/css" />
  <meta name="description" content="$EN605_452::title - $EN605_452::owner" />
  <meta name="copyright" content="$EN605_452::copyright" />
  <title>$EN605_452::title$pageTitle</title>
  <link rel="stylesheet" type="text/css" href="${path}css/site.css" />
HTML

  return;
}


#### PrintBodyOpen:
sub PrintBodyOpen {
  my ($pageTitle, $depth) = @_;
  my $path = &GetPath($depth);

  print <<HTML;
</head>
<body>
  <div id="header">
    <h1>$EN605_452::title</h1>
  </div>

  <div id="container">
    <div id="content">
      <h2>$pageTitle</h2>
HTML

  return;
}


#### PrintBodyClose:
sub PrintBodyClose {
  my ($pageTitle, $depth) = @_;
  my ($page);
  my $path = &GetPath($depth);
  my $menuPath = &GetPath($depth - 1);

  print <<HTML;
    </div>

    <div id="rail">
      <table id="menu_tbl" summary="menu links"><tbody>
HTML

  for $page (@EN605_452::pages) {
    if ($$page{'title'} && $$page{'title'} eq $pageTitle) {
      print qq{        <tr><td class="this_pg">$$page{'title'}</td></tr>\n};
    } elsif ($$page{'title'}) {
      print qq{        <tr><td><a href="$menuPath$$page{'url'}">$$page{'title'}</a></td></tr>\n};
    }
  }

  print <<HTML;
      </tbody></table>
      <p style="margin-top:20px;text-align:center;">
        <b>$EN605_452::owner</b><br />
        <span style="font-size:8pt;">
          <a href="mailto:$EN605_452::adminEmail">Send an Email</a>
        </span>
      </p>
    </div>
  </div>

  <div id="footer">
    $EN605_452::copyright&nbsp;
    [<a href="http://validator.w3.org/check?uri=referer">XHTML 1.0</a>]&nbsp;
    [<a href="http://jigsaw.w3.org/css-validator/check/referer">CSS 2</a>]&nbsp;
    Design by <a href="http://www.vesselonline.org/">Vessel</a>.
  </div>
</body>
</html>
HTML

  return;
}


#### GetPath:
sub GetPath {
  my ($depth) = @_;
  return '../' x $depth;
}


#### FormatSubtitle:
sub FormatSubtitle {
  my ($subtitle) = @_;
  $subtitle = "      <h3>$subtitle</h3>" if ($subtitle);
  return $subtitle;
}


#### CreateOptionList:
sub CreateOptionList {
  my ($selected, %opts) = @_;
  my ($i, $options);

  for $i (sort keys %opts) {
    $options .= qq{<option value="$i"};
    if ($i eq $selected) {
      $options .= qq{ selected="selected"};
    }
    $options .= qq{>$opts{$i}</option>};
  }

  return $options;
}


##################
####  ERRORS  ##################################################################
##################


#### CGIError:  General purpose error function.  Prints out the error message
#      and exits the script.
sub CGIError {
  my ($msg) = @_;
  print qq{<span class="error">ERROR: $msg</span><br />\n};
  print qq{Please report this error to the <a href="mailto:$EN605_452::adminEmail">webmaster</a>.</body></html>\n};
  exit;
}


#### InputError:  Used for displaying form input or authentication errors.
sub InputError {
  my ($msg) = @_;
  print qq{Your submission has the following errors:<br /><span class="error">$msg</span><br />\n};
  print qq{Please use the browser's <a href="javascript:history.back();">BACK</a> button to revise your input.\n};
  return;
}


######################
####  VALIDATION  ##############################################################
######################


#### InputProtect:  Escapes text in input fields of type text.
sub InputProtect {
  my ($str) = @_;
  return '' if (! defined $str);
  $str =~ s/&/&amp;/g;  # escape ampersand
  $str =~ s/"/&quot;/g;  # escape double quote
  return $str;
}


#### HTMLProtect:  Removes dangerous tags from user-submitted data.
sub HTMLProtect {
  my ($str) = @_;
  return '' if (! defined $str);
  $str =~ s/^\s+|\s+$//g;  # remove heading and trailing spaces

  $str =~ s/<\/?script(\s+.*?)?>//gi;  # remove script tag
  $str =~ s/<\/?object(\s+.*?)?>//gi;  # remove object tag
  $str =~ s/<\/?applet(\s+.*?)?>//gi;  # remove applet tag
  $str =~ s/<\/?embed(\s+.*?)?>//gi;  # remove embed tag
  $str =~ s/<\/?iframe(\s+.*?)?>//gi;  # remove iframe tag

  return $str;
}


#### XMLProtect:  Escapes string for five pre-defined XML entities.
sub XMLProtect {
  my ($str) = @_;
  return '' if (! defined $str);
  $str =~ s/^\s+|\s+$//g;  # remove heading and trailing spaces

  $str =~ s/&/&amp;/g;  # escape ampersand
  $str =~ s/'/&apos;/g;  # escape single quote
  $str =~ s/"/&quot;/g;  # escape double quote
  $str =~ s/</&lt;/g;  # escape less than
  $str =~ s/>/&gt;/g;  # escape greater than

  return $str;
}


#################
####  PAGES  ###################################################################
#################


#### QueryPage:
sub QueryPage {
  my ($pageID) = @_;
  return %{$pages[$pageID]};
}


1;
