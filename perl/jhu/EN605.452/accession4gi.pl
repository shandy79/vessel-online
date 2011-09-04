#!/usr/bin/perl

use strict;

use Bio::DB::EUtilities;
use Bio::Seq;
use Bio::SeqIO;
use Bio::Tools::Run::StandAloneBlast;

#### EFETCH

my @ids = qw(1621261 89318838 68536103 20807972 730439);

my $factory = Bio::DB::EUtilities->new(-eutil => 'efetch',
                         -db => 'protein',
                         -id => \@ids,
                         -email => 'mymail@foo.bar',
                         -rettype => 'acc');

my @accs = split(m{\n},$factory->get_Response->content);

print join(', ', @accs), "\n";

#### WRITE SEQUENCE TO FILE
my ($seq_fmt, $seq_file, $seq_obj, $seqio_obj);

$seq_fmt = 'genbank';
$seq_file = 'sequence.genbank.txt';

$seq_obj = Bio::Seq->new(-seq => "TTTAAATATATTTTGAAGTATAGATTATATGTT",                        
                         -display_id => "13",                        
                         -desc => "SJH BioPerl Example",                        
                         -alphabet => "dna" );
$seqio_obj = Bio::SeqIO->new(-file => ">$seq_file", -format => $seq_fmt);
$seqio_obj->write_seq($seq_obj);

#### BLASTN
my (@params, $blast_obj, $report_obj, $result_obj);

@params = (program  => 'blastn', database => 'nr', o => 'output.pl.txt');
 
$blast_obj = Bio::Tools::Run::StandAloneBlast->new(@params);
 
$seqio_obj = Bio::SeqIO->new(-file => $seq_file, -format => $seq_fmt);
$seq_obj = $seqio_obj->next_seq;  
print $seq_obj->seq,"\n";

$report_obj = $blast_obj->blastall('input2.txt');
$result_obj = $report_obj->next_result;
print $result_obj->num_hits;
