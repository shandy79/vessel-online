#!/usr/bin/perl

# ffbp_offline.pl
# - Execute a Feedforward Back-propagation Neural Network using off-line training updates.
# - Steven Handy, shandy3@jhu.edu
# - EN605.447 Neural Networks, Spring 2010
use strict;

my (@x, @d);
$x[0] = [1, 2];  # Definition of input values, x.i for each training set
$x[1] = [-1, 1];
$x[2] = [1, 0];
$x[3] = [2, 1];

$d[0] = [0.9, 0.9];  # Definition of desired output values, d.j, for each training set
$d[1] = [0.9, 0.1];
$d[2] = [0.1, 0.1];
$d[3] = [0.1, 0.9];

my $inputLayer = 0;  # Use $x for input layer inputs
my $outputLayer = 1;  # Simpler to define directly than identifying from data, use $y to record output

my (@w, @deltaJ, @deltaW);  # Storage for weights and deltas for w.ix, w.ji values - [layer][toNode][fromNode],
                            # also defines edges between nodes (i.e. if weight = 0, no edge exists)
$w[0][0][0] = $w[0][0][1] = $w[0][1][0] = $w[0][1][1] = 0.3;  # Definition of w.ix values
$w[1][0][0] = $w[1][0][1] = $w[1][1][0] = $w[1][1][1] = 0.8;  # Definition of w.ji values

my $gain = 0.2;  # Definition of epsilon (the gain factor)
my $bias = 0.0;  # Definition of theta (the bias)
my (@v, @y, @e, $sse);  # Storage for v, y, e (= d - y), sum square error

my ($trainingSet, $iter);
my $precision = 0.00000001;

my (@lastDeltaW);  # Storage of most recent weight deltas for print out

# Default values for maximum iterations and training mode
my $maxIter = 30;
my $trainingMode = 'offline';

# Read command-line args for maximum iterations and training mode
if ($ARGV[0]) {
  if ($ARGV[0] =~ /^\d+$/) {
    $maxIter = $ARGV[0];
  } elsif ($ARGV[0] =~ /^online$/i) {
    $trainingMode = 'online';
  }
}

if ($ARGV[1]) {
  if ($ARGV[1] =~ /^\d+$/) {
    $maxIter = $ARGV[1];
  } elsif ($ARGV[1] =~ /^online$/i) {
    $trainingMode = 'online';
  }
}

print "\nFEED-FORWARD BACK-PROPAGATION NEURAL NETWORK\n";
print "- Training Mode: $trainingMode, Max. Iterations: $maxIter\n\n";

# Initialize $sse to a large value to enter the while loop
$sse = 9999;
$iter = 0;
&initWeightDeltas();

while (abs($sse) > $precision && $iter < $maxIter) {
  $iter++;

  &calculateWeightDeltas();
  if ($trainingMode eq 'offline') {
    &applyWeightDeltas();
    &initWeightDeltas();
  }

#  &printNetworkStatus();
}

&printNetworkStatus();
exit;


# Initialize weight deltas to 0 for next iteration
sub initWeightDeltas {
  my ($layer, $toNode, $fromNode);

  for ($layer = 0; $layer < scalar @w; $layer++) {
    for ($toNode = 0; $toNode < scalar @{$w[$layer]}; $toNode++) {
      for ($fromNode = 0; $fromNode < scalar @{$w[$layer][$toNode]}; $fromNode++) {
        $lastDeltaW[$layer][$toNode][$fromNode] = $deltaW[$layer][$toNode][$fromNode];
        $deltaW[$layer][$toNode][$fromNode] = 0;
      }
    }
  }
}


# Definition of v.j, y.j, e.j, and sum square error, and
# computation of weight deltas and new weight values
sub calculateWeightDeltas {
  my ($layer, $toNode, $fromNode, $kSum);

  for ($trainingSet = 0; $trainingSet < scalar @x; $trainingSet++) {
    for ($layer = 0; $layer < scalar @w; $layer++) {
      for ($toNode = 0; $toNode < scalar @{$w[$layer]}; $toNode++) {

        # Calculation of v.j for node [layer][toNode]
        $v[$layer][$toNode] = 0;
        for ($fromNode = 0; $fromNode < scalar @{$w[$layer][$toNode]}; $fromNode++) {
          if ($layer == $inputLayer) {
            $v[$layer][$toNode] += $w[$layer][$toNode][$fromNode] * $x[$trainingSet][$fromNode];
          } else {
            $v[$layer][$toNode] += $w[$layer][$toNode][$fromNode] * $y[$layer - 1][$fromNode];
          }
        }
        $v[$layer][$toNode] += $bias;

        # Calculation of sigmoid activation function, y.j, for node [layer][toNode]
        $y[$layer][$toNode] = 1.0 / (1 + exp(-1.0 * $v[$layer][$toNode]));
      }
    }

    $sse = 0;
    for ($toNode = 0; $toNode < scalar @{$y[$outputLayer]}; $toNode++) {
      # Calculation of e.j and sum square error
      $e[$toNode] = $d[$trainingSet][$toNode] - $y[$outputLayer][$toNode];
      $sse += $e[$toNode]**2;
    }
    $sse *= 0.5;

    # Calculation of delta.j
    for ($layer = scalar @w - 1; $layer >= 0; $layer--) {
      for ($toNode = 0; $toNode < scalar @{$w[$layer]}; $toNode++) {

        # Calculation of delta.j for [layer][toNode] for output, then hidden layers
        if ($layer == $outputLayer) {
          $deltaJ[$layer][$toNode] = $y[$layer][$toNode] * (1 - $y[$layer][$toNode]) * $e[$toNode];
        } else {
          $kSum = 0;
          # In this case, $fromNode and $toNode are reversed
          for ($fromNode = 0; $fromNode < scalar @{$w[$layer + 1][$toNode]}; $fromNode++) {
            $kSum += $deltaJ[$layer + 1][$fromNode] * $w[$layer + 1][$fromNode][$toNode];
#           print "kSum = $deltaJ[$layer + 1][$fromNode] * $w[$layer + 1][$fromNode][$toNode]\n";
          }

          $deltaJ[$layer][$toNode] = $y[$layer][$toNode] * (1 - $y[$layer][$toNode]) * $kSum;
#         print "deltaJ = $y[$layer][$toNode] * (1 - $y[$layer][$toNode]) * $kSum\n";
        }
      }
    }

    # Calculation of weight deltas
    for ($layer = scalar @w - 1; $layer >= 0; $layer--) {
      for ($toNode = 0; $toNode < scalar @{$w[$layer]}; $toNode++) {
        for ($fromNode = 0; $fromNode < scalar @{$w[$layer][$toNode]}; $fromNode++) {

          if ($layer == $inputLayer) {
#            print "deltaW[$layer][$toNode][$fromNode]: " . ($gain * $deltaJ[$layer][$toNode] * $x[$trainingSet][$fromNode]) . "\n";
            $deltaW[$layer][$toNode][$fromNode] += $gain * $deltaJ[$layer][$toNode] * $x[$trainingSet][$fromNode];
          } else {
#             print "deltaW[$layer][$toNode][$fromNode]: " . ($gain * $deltaJ[$layer][$toNode] * $y[$layer - 1][$fromNode]) . "\n";
            $deltaW[$layer][$toNode][$fromNode] += $gain * $deltaJ[$layer][$toNode] * $y[$layer - 1][$fromNode];
          }
        }
      }
    }
#    print "\n";

    if ($trainingMode eq 'online') {
      &applyWeightDeltas();
      &initWeightDeltas();
    }
  }
}


# Apply new weight deltas to existing weights
sub applyWeightDeltas {
  my ($layer, $toNode, $fromNode);

  for ($layer = 0; $layer < scalar @w; $layer++) {
    for ($toNode = 0; $toNode < scalar @{$w[$layer]}; $toNode++) {
      for ($fromNode = 0; $fromNode < scalar @{$w[$layer][$toNode]}; $fromNode++) {
        $w[$layer][$toNode][$fromNode] += $deltaW[$layer][$toNode][$fromNode];
      }
    }
  }
}


# Function to print network status
sub printNetworkStatus {
  my ($layer, $toNode, $fromNode);

  print '-' x 60 . "\n";
  print "After Iteration $iter\n";
  print '-' x 60 . "\n";

  print "- v & y Values:\n";
  for ($layer = 0; $layer < scalar @w; $layer++) {
    for ($toNode = 0; $toNode < scalar @{$w[$layer]}; $toNode++) {
      print "  node[$layer][$toNode]:\tv=$v[$layer][$toNode]\ty=$y[$layer][$toNode]\n";
    }
  }

  print '- e Values:';
  for ($toNode = 0; $toNode < scalar @{$y[$outputLayer]}; $toNode++) {
    print "\t$e[$toNode]"
  }
  print "\n- Sum Square Error:\t$sse\n";

  print "- New Weights & Deltas:\n";
  for ($layer = 0; $layer < scalar @w; $layer++) {
    for ($toNode = 0; $toNode < scalar @{$w[$layer]}; $toNode++) {
      for ($fromNode = 0; $fromNode < scalar @{$w[$layer][$toNode]}; $fromNode++) {
        print "  weight[$layer][$toNode][$fromNode]:\tw=$w[$layer][$toNode][$fromNode]\t" .
              "delta-w=$lastDeltaW[$layer][$toNode][$fromNode]\tdelta-j=$deltaJ[$layer][$toNode]\n";
      }
    }
  }
  print "\n";
}


# Function to print information about the current training set
sub printTrainingSetInfo {
  my ($i);

  print '-' x 80 . "\n";
  print "TRAINING SET $trainingSet\t(Gain: $gain, Bias: $bias)\n";
  print '-' x 80 . "\n";
  print '- Inputs:';
  for ($i = 0; $i < scalar @{$x[$trainingSet]}; $i++) {
    print "\t$x[$trainingSet][$i]";
  }
  print "\n";
  print '- Desired Outputs:';
  for ($i = 0; $i < scalar @{$x[$trainingSet]}; $i++) {
    print "\t$d[$trainingSet][$i]";
  }
  print "\n";
}

