#!/usr/bin/perl

use strict;
use Bio::DB::GenBank;
use Bio::DB::Query::GenBank;

my $terminalSeq = 'RTDL';  #'KDEL';

# Only search for human proteins within the RefSeq database
my $query = "\"Homo sapiens\"[Organism] AND srcdb_refseq[PROP]";
my $queryObj = Bio::DB::Query::GenBank->new('-db'=>'protein', '-query'=>$query);

print "Query:  $query\nExpected result count:  ", $queryObj->count, "\n";
print "Filtering results by terminal amino acid sequence:  $terminalSeq\n\n";

my $genBankObj = Bio::DB::GenBank->new;
my $resultStreamObj = $genBankObj->get_Stream_by_query($queryObj);

my $resultCount = 0;
my $hitCount = 0;
while (my $sequenceObj = $resultStreamObj->next_seq) {
  if ($sequenceObj->seq =~ /$terminalSeq$/) {
    # For GenBank queries, accession_number returns same string as display_id and display_name
    print $sequenceObj->accession_number, "\t", $sequenceObj->length, "\t";
    print $sequenceObj->alphabet, "\t", $sequenceObj->molecule, "\n";
    print $sequenceObj->description, "\n", $sequenceObj->seq, "\n\n";
    $hitCount++;
  }

  $resultCount++;
}

print "$hitCount matches out of $resultCount results retrieved.\n\n";

exit;

