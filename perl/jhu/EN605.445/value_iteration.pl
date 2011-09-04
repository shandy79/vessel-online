# value_iteration.pl
# - Use Value Iteration to determine policy.
use strict;

my (@utils, $s);
my @utilsPrime = (0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
my @rewards = (10, 0, 0, 0, 0, 0, 0, 0, 0, 5);
my $discount = 0.75;
my $pMove = 0.9;
my $delta = 0;
my $error = 0.01;
my $i = 1;

print "<html><head /><body><table>\n<tr><th>Iteration</th>";
for ($s = 0; $s < 10; $s++) {
  print '<th>' . ($s + 1) . '</th>';
}
print "<th>Delta</th></tr>\n";

do {
  @utils = @utilsPrime;
  $delta = 0;
  print "<tr><td><b>$i</b></td>";

  for ($s = 0; $s < 10; $s++) {
    $utilsPrime[$s] = &bellmanUpdate($s);

    if (abs($utilsPrime[$s] - $utils[$s]) > $delta) {
      $delta = abs($utilsPrime[$s] - $utils[$s]);
    }

    print "<td>$utilsPrime[$s]</td>";
  }

  print "<td><b>$delta</b></td></tr>\n";
  $i++;
} until ($delta < ($error * (1 - $delta) / $delta));

print "</table></body></html>\n";
exit;


sub bellmanUpdate {
  my ($state) = @_;
  my %actions = &action($state);
  my $utilPrimeValue = $rewards[$state];
  my ($action, $actionValue, $maxActionValue);

  for $action (keys %actions) {
    $actionValue = ($pMove * $utils[$action]);

    if ($actions{$action} != -1) {
      $actionValue += (1 - $pMove) * $utils[$actions{$action}];
	}

    if ($actionValue > $maxActionValue) {
      $maxActionValue = $actionValue;
    }
  }

  return ($rewards[$state] + $discount * $maxActionValue);
}


sub action {
  my ($state) = @_;

  if ($state == 0) {
    return (1 => -1);
  } elsif ($state == 9) {
    return (8 => -1);
  } else {
    return (($state - 1) => ($state + 1), ($state + 1) => ($state - 1));
  }
}
