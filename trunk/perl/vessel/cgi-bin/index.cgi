#!/usr/bin/perl

use CGI;

my $cgi = new CGI;
print $cgi->header();

print <<HTML;
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en"><head>
<title>Vessel Online</title>
<meta http-equiv="Content-Type" content="text/html;charset=iso-8859-1" />
</head>
<!-- border="0" is non-standard, but required to eliminate gap created by browsers -->
<!-- frameset cols="160,620,*" border="0">
  <frame src="nav.html" name="Nav" frameborder="0" noresize="noresize" / -->
<frameset cols="620,*" border="0">
  <frame src="home.cgi" name="Main" frameborder="0" noresize="noresize" />
  <frame src="tools.cgi" name="Tools" frameborder="0" noresize="noresize" />
  <noframes>
    <body>You must upgrade to a frames-compatible browser to view this site.</body>
  </noframes>
</frameset></html>
HTML

exit;
