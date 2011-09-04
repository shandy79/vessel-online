#!/usr/local/bin/perl

use strict;
use CGI;

my $cgi = new CGI;

print "Content-type: text/css\n\n<!--\n";

my $css = $cgi->param('css');

if ($css eq 'nav') {

  print <<CSS;
body { margin: 0px;
       padding: 0px;
       background-color: #999999;
       color: white;
       font-family: Verdana, Arial, Helvetica, sans-serif;
       font-size: 10pt;
       font-weight: bold; }
a:link { color: black; }
a:visited { color: black; }
a:active { color: #ffa500; }
a:hover { color: blue; }
img { vertical-align: top; }
.ln { font-family: Verdana, Arial, Helvetica, sans-serif;
      font-size: 9pt;
      font-weight: bold;
      text-decoration: none; }
.bg { background-color: #cccccc; }
CSS

} else {  # $css eq 'main'

  print <<CSS;
body { background-color: white;
       color: black;
       font-family: Verdana, Arial, Helvetica, sans-serif;
       font-size: 10pt; }
a:link { color: blue; }
a:visited { color: blue; }
a:active { color : #ffa500; }
a:hover { color: black; }
img { border-width: 0px; }
.itm {  background-color: #cccccc;
        font-weight: bold; }
.ttl { background-color: #cccccc;
       font-size: 15pt; }
.pln { text-decoration: none; }
CSS

}

print "-->\n";
exit;

