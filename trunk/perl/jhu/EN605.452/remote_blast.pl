#!/usr/bin/perl

#Remote-blast "factory object" creation and blast-parameter initialization
use Bio::DB::GenBank;
use Bio::Seq;
  use Bio::Tools::Run::RemoteBlast;
  use strict;
  my $prog = 'blastn';
  my $db   = 'est_others';
#  my $e_val= '1e-10';

  my @params = ( '-prog' => $prog,
         '-data' => $db,
#         '-expect' => $e_val,
         '-readmethod' => 'xml' );

  my $factory = Bio::Tools::Run::RemoteBlast->new(@params);
  $factory->retrieve_parameter('FORMAT_TYPE', 'XML');

  #change a query paramter
#  $Bio::Tools::Run::RemoteBlast::HEADER{'ENTREZ_QUERY'} = 'Homo sapiens [ORGN]';

  #change a retrieval parameter
#  $Bio::Tools::Run::RemoteBlast::RETRIEVALHEADER{'DESCRIPTIONS'} = 1000;

  #remove a parameter
#  delete $Bio::Tools::Run::RemoteBlast::HEADER{'FILTER'};

  #$v is just to turn on and off the messages
  my $v = 1;

#  my $str = Bio::SeqIO->new(-file=>'amino.fa' , -format => 'fasta' );

#  while (my $input = $str->next_seq()){
    #Blast a sequence against a database:

    #Alternatively, you could  pass in a file with many
    #sequences rather than loop through sequence one at a time
    #Remove the loop starting 'while (my $input = $str->next_seq())'
    #and swap the two lines below for an example of that.
#    my $r = $factory->submit_blast($input);
 
my $db_obj = Bio::DB::GenBank->new;
 
my $input = $db_obj->get_Seq_by_acc('S75433');

#my $input = Bio::Seq->new(-accession_number => 'S75433', -alphabet => 'dna', -desc => 'IFI16');

my $r = $factory->submit_blast($input);
    #my $r = $factory->submit_blast('amino.fa');

    print STDERR "waiting..." if( $v > 0 );
    while ( my @rids = $factory->each_rid ) {
      foreach my $rid ( @rids ) {
        my $rc = $factory->retrieve_blast($rid);
        if( !ref($rc) ) {
          if( $rc < 0 ) {
            $factory->remove_rid($rid);
          }
          print STDERR "." if ( $v > 0 );
          sleep 5;
        } else {
          my $result = $rc->next_result();
          #save the output
          my $filename = $result->query_name()."\.out";
          $factory->save_output($filename);
          $factory->remove_rid($rid);
          print "\nQuery Name: ", $result->query_name(), "\n";
          while ( my $hit = $result->next_hit ) {
            next unless ( $v > 0);
            print "\thit name is ", $hit->name, "\n";
            while( my $hsp = $hit->next_hsp ) {
              print "\t\tscore is ", $hsp->score, "\n";
            }
          }
        }
      }
    }
#  }
