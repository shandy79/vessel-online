#!/usr/local/bin/perl

# WebUtil.pm
# Parameters and utility function library for web service servers.
# Created by: Steve Handy (last mod: 04/29/2005)

#### Package WebUtil: contains variables to be used as parameters for
#      all web service servers
package WebUtil;

  #### Environment (ops=1 or dev=0)
  $ops = 0;
  $dir = '';

  #### Database connection information
  $db_sid = '';
  $db_usr = '';
  $db_pwd = '';

  $db_ip = '';
  $db_port = '';
  $db_home = '';

  #### Contact information
  $email_dev = '';
  $phone_dev = '';


#### UTILITY

#### DbConn:
#
sub DbConn {
  my ($src, $db) = @_;

#### Must be updated for the appropriate database

  # Connect to the database
  my $dbh = STScI::DBI->connect("dbi:Sybase:server=$db_sid", undef, undef);
  if (! $dbh) {
    &DbErr($src, "Connection failed: $DBI::errstr");
  }

  # Set the default database
  $dbh->do("use $db") || &DbErr($src, "Unable to do 'use $db': $DBI::errstr\n", $dbh);

  return $dbh;
}

#### DbErr: general purpose error function.  Prints out the error message,
#      disconnects from the database and exits the script.
sub DbErr {
  my ($src, $msg, $dbh_prd) = @_;

  if ($dbh_prd) {
    $dbh_prd->rollback;
    $dbh_prd->disconnect;
  }

  if ($src eq 'cgi') {
    print qq{<b><font color="red">ERROR: $msg</font></b>\n</body></html>};
    exit;
  } else {
    die "ERROR: $msg\n";
  }
}

#### SqlProtect: escapes string and shortens it, if necessary, before
#      inserting it into the database
sub SqlProtect {
  my ($str, $len) = @_;
  return '' if (! defined $str);
  $str =~ s/^\s+|\s+$//g;  # remove heading and trailing spaces
  $str =~ s/'/''/g;  # escape single quote
  $str = substr($str, 0, $len) if ($len && length($str) > $len);
  return $str;
}

#### XmlProtect: escapes string for five pre-defined entities
sub XmlProtect {
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


1;

