#!/usr/bin/perl

use Digest::SHA1  qw(sha1_hex sha1_base64);

print "Create passwd: ";
chomp($word = <STDIN>);

$pwd = sha1_hex($word);
print "SHA1-encrypted Hex passwd = $pwd\n";

$pwd = sha1_base64($word);
print "SHA1-encrypted Base64 passwd = $pwd\n";

exit;
