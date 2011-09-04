#!/usr/bin/perl

# perceptron_delta.pl
# - Calculate weight changes using the Perceptron Delta Rule.
use strict;

my @x = (1, 0);  # Definition of x.i values
my @w = (-0.3, 0.6);  # Definition of w.ij values
my @delW = (0, 0);  # Storage for deltas for w.ij values
my $eps = 0.1;  # Definition of epsilon (the gain factor)
my $b = 0.2;  # Definition of theta (the bias)
my $d = 0.8;  # Definition of desired output, d.j
my ($v, $y, $e, $sse);  # Storage for v.j, y.j, e.j (= d.j - y.j), sum square error

my $precision = 0.00000001;
my $i = 0;
my $j = 0;
my $maxIter = 10000;

print "Network Definition:\n- Gain: $eps, Bias: $b, Desired Output: $d\n";
print '- Inputs:';
for ($i = 0; $i < scalar @x; $i++) {
  print "\t$x[$i]";
}
print "\n";

&printNetworkStatus();

# Initialize $e to a large value to enter the while loop
$e = 9999;

while (abs($e) > $precision && $j < $maxIter) {
  $j++;
  &calculateWeightDeltas();
#  print "After iteration $j . . .\n";
#  &printNetworkStatus();
}

print "After iteration $j . . .\n";
&printNetworkStatus();
exit;


# Definition of v.j, y.j, e.j, and sum square error, and
# computation of weight deltas and new weight values
sub calculateWeightDeltas {
  # Calculation of v.j
  $v = 0;
  for ($i = 0; $i < scalar @x; $i++) {
    $v += $w[$i] * $x[$i];
  }
  $v += $b;

  # Calculation of sigmoidal function y.j
  $y = 1.0 / (1 + exp(-1.0 * $v));

  # Calculation of e.j and sum square error
  $e = $d - $y;
  $sse = 0.5 * $e**2;

  # Calculation of delta.j
  my $delJ = $y * (1 - $y) * $e;

  # Calculation and application of weight deltas
  for ($i = 0; $i < scalar @x; $i++) {
    $delW[$i] = $eps * $delJ * $x[$i];
    $w[$i] += $delW[$i];
  }
}

# Function to print network status
sub printNetworkStatus {
  print "- v.j: $v, y.j: $y, e.j: $e, sum square err: $sse\n";

  print '- Deltas:';
  for ($i = 0; $i < scalar @x; $i++) {
    print "\t$delW[$i]";
  }
  print "\n";

  print '- New Weights:';
  for ($i = 0; $i < scalar @x; $i++) {
    print "\t$w[$i]";
  }
  print "\n";
}

