#!/usr/bin/perl

# newtons_method.pl
# - Calculate a root (zero) point for an equation using Newton's Method.
use strict;

my $xOld = 0;
my $xNew = -4;  # Definition of x0
my $fxNew = &f($xNew);
my $precision = 0.00001;
my $i = 0;
my $maxIter = 10;

print "x$i = $xNew, f(x$i) = " . &f($xNew) . "\n";
while ($fxNew != 0 && abs($fxNew - $precision) > 0 && $i < $maxIter) {
  $i++;
  $xOld = $xNew;
  $xNew = $xOld - &f($xOld) / &f_prime($xOld);    # Application of Newton's Method
  $fxNew = &f($xNew);
  print "x$i = $xNew, f(x$i) = $fxNew\n";
}
print "Root occurs at x = $xNew\n";
exit;

# Definition of f(x)
sub f {
  my ($x) = @_;
  return ($x**2 - 5 * $x - 10);
}

# Definition of f'(x)
sub f_prime {
  my ($x) = @_;
  return (2 * $x - 5);
}
