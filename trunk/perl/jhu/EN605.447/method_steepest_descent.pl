#!/usr/bin/perl

# method_steepest_descent.pl
# - Calculate a local minimum for an equation using the Method of Steepest Descent.
use strict;

my $xOld = 0;
my $xNew = 3;  # Definition of x0
my $eps = 0.5;  # Definition of epsilon
my $precision = 0.00001;
my $i = 0;
my $maxIter = 50;

print "x$i = $xNew, f(x$i) = " . &f($xNew) . "\n";
while (abs($xNew - $xOld) > $precision && $i < $maxIter) {
  $i++;
  $xOld = $xNew;
  $xNew = $xOld - $eps * &f_prime($xOld);  # Application of Method of Steepest Descent
  print "x$i = $xNew, f(x$i) = " . &f($xNew) . "\n";
}
print "Local minimum occurs at x = $xNew\n";
exit;

# Definition of f(x)
sub f {
  my ($x) = @_;
  return ($x**2 + 2);
}

# Definition of f'(x)
sub f_prime {
  my ($x) = @_;
  return (2 * $x);
}
