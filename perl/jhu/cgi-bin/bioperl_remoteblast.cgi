#!/usr/bin/perl

use strict;
use Bio::DB::GenBank;
use Bio::SearchIO;
use Bio::SearchIO::Writer::HTMLResultWriter;
use Bio::Seq;
use Bio::Tools::Run::RemoteBlast;
use CGI;
use EN605_452;

my $cgi = new CGI;
print $cgi->header();

my $cmd = $cgi->param('cmd');

my $pageID = 1;
my %page = &EN605_452::QueryPage($pageID);

my $depth = 1;
my $subtitle = &EN605_452::FormatSubtitle($page{subtitle});

my %programs = ('blastn' => 'blastn (nucleotide db using nucleotide query)',
                'blastp' => 'blastp (protein db using protein query)',
                'blastx' => 'blastx (protein db using translated nucleotide query)',
                'tblastn' => 'tblastn (translated nucleotide db using protein query)',
                'tblastx' => 'tblastx (translated nucleotide db using translated nucleotide query)');

my %nucleotideDBs = ('nr' => 'Nucleotide collection (nr/nt)',
                     'refseq_rna' => 'Reference mRNA sequences (refseq_rna)',
                     'refseq_genomic' => 'Reference genomic sequences (refseq_genomic)',
                     'chromosome' => 'NCBI Genomes (chromosome)',
                     'est' => 'Expressed sequence tags (est)',
                     'est_others' => 'Non-human, non-mouse ESTs (est_others)',
                     'gss' => 'Genomic survey sequences (gss)',
                     'htgs' => 'High throughput genomic sequences (HTGS)',
                     'pat' => 'Patent sequences(pat)',
                     'pdb' => 'Protein Data Bank (pdb)',
                     'alu' => 'Human ALU repeat elements (alu_repeats)',
                     'dbsts' => 'Sequence tagged sites (dbsts)',
                     'wgs' => 'Whole-genome shotgun reads (wgs)',
                     'env_nt' => 'Environmental samples (env_nt)');

my %proteinDBs = ('nr' => 'Non-redundant protein sequences (nr)',
                  'refseq_protein' => 'Reference proteins (refseq_protein)',
                  'swissprot' => 'Swissprot protein sequences(swissprot)',
                  'pat' => 'Patented protein sequences(pat)',
                  'pdb' => 'Protein Data Bank proteins(pdb)',
                  'env_nr' => 'Environmental samples(env_nr)');

my %matrices = ('PAM30' => 'PAM30', 'PAM70' => 'PAM70', 'BLOSUM80' => 'BLOSUM80', 'BLOSUM62' => 'BLOSUM62', 'BLOSUM45' => 'BLOSUM45');
my %gapCosts = ('PAM30' => '9 1', 'PAM70' => '10 1', 'BLOSUM80' => '10 1', 'BLOSUM62' => '11 1', 'BLOSUM45' => '15 2');

my $nucleotideDBOpts = &EN605_452::CreateOptionList('nr', %nucleotideDBs);
my $proteinDBOpts = &EN605_452::CreateOptionList('nr', %proteinDBs);

&EN605_452::PrintHead('10', $page{title}, $depth);

# Javascript used to enable/disable matrix list and to set database list based on program
print <<HTML;
<script type="text/javascript">
<!--
var nucleotideDBOpts = '$nucleotideDBOpts';
var proteinDBOpts = '$proteinDBOpts';

function onProgramChange(progSelect) {
  var progValue = progSelect.value;

  if (progValue == 'blastn') {
    document.forms[0].MATRIX_NAME.disabled = true;
    document.forms[0].DATABASE.innerHTML = nucleotideDBOpts;
  } else {
    document.forms[0].MATRIX_NAME.disabled = false;

    if (progValue == 'tblastn' || progValue == 'tblastx') {
      document.forms[0].DATABASE.innerHTML = nucleotideDBOpts;
    } else {
      document.forms[0].DATABASE.innerHTML = proteinDBOpts;
    }
  }

  return true;
}

//-->
</script>
HTML

&EN605_452::PrintBodyOpen($page{title}, $depth);

print "$subtitle\n";

if ($cmd eq 'Search') {
  &RunSearch();
} else {
  &PrintForm();
}

&EN605_452::PrintBodyClose($page{title}, $depth);
exit;


sub PrintForm {
  my ($errors, $accNum, $sequence, $prog, $db, $matrix) = @_;
  my ($disabled, $progOpts, $dbOpts, $matrixOpts);

  # Set defaults if we are not receiving previous form values from
  # a failed submission.  Matrices are disabled for blastn search.
  if (! $prog || $prog eq 'blastn') {
    $prog = 'blastn';
    $disabled = ' disabled="disabled"';
  }

  # Set appropriate database list based on program selection
  if ($prog eq 'blastp' || $prog eq 'blastx') {
    $dbOpts = $proteinDBOpts;
  } else {
    $dbOpts = $nucleotideDBOpts;
  }

  if (! $matrix) { $matrix = 'BLOSUM62'; }

  $progOpts = &EN605_452::CreateOptionList($prog, %programs);
  $matrixOpts = &EN605_452::CreateOptionList($matrix, %matrices);

  print <<HTML;
$errors
<form method="post" action="bioperl_remoteblast.cgi" id="blast_form" name="blast_form">
<table class="form_table" summary="blast form layout table"><tbody>
  <tr><th scope="row"><label for="accession">Accession Number</label>:</th>
    <td><input type="text" id="accession" name="ACCESSION" size="12" maxlength="24" value="$accNum" /></td>
  </tr>
  <tr><th scope="row">- OR - <label for="seq">Sequence:</label></th>
    <td><textarea id="seq" rows="5" cols="80" name="QUERY" >$sequence</textarea></td>
  </tr>
  <tr><th scope="row"><label for="program">Program:</label></th>
    <td><select name="PROGRAM" id="program" onchange="onProgramChange(this);">
$progOpts
    </select></td>
  </tr>
  <tr><th scope="row"><label for="DATABASE">Database:</label></th>
    <td><select name="DATABASE" id="DATABASE">
$dbOpts
    </select></td>
  </tr>
  <tr><th scope="row"><label for="matrixName">Scoring Matrix:</label></th>
    <td><select name="MATRIX_NAME" id="matrixName"$disabled>
$matrixOpts
    </select></td>
  </tr>
</tbody></table>

<p><input type="submit" id="cmd" name="cmd" value="Search" class="btn" />&nbsp;&nbsp;
  <input type="reset" id="reset" name="reset" value="Reset" class="btn" /></p>
</form>
HTML

  return;
}


sub RunSearch {
  my $accNum = $cgi->param('ACCESSION');
  my $sequence = $cgi->param('QUERY');
  my $prog = $cgi->param('PROGRAM');
  my $db = $cgi->param('DATABASE');
  my $matrix = $cgi->param('MATRIX_NAME');

  # Scrub the input
  $accNum = uc($accNum);
  $accNum =~ s/\s//g;

  $sequence = lc($sequence);
  $sequence =~ s/\s|\d//g;

  # Accession number must be in the appropriate format.
  # Defined here rather than in ValidateForm due to need for use below.
  my $accNumError = ($accNum !~ /^[A-Z_]+\d+(\.\d+)?$/) ? 1 : 0;

  my $errors = &ValidateForm($accNum, $sequence, $prog, $db, $matrix, $accNumError);

  if ($errors) {
    $errors = qq{<div class="error">The following errors have occurred:\n<ul>$errors</ul>\n} . 
              qq{Please correct these errors and resubmit the form.</div>\n};
    &PrintForm($errors, $accNum, $sequence, $prog, $db, $matrix);
    return;
  }

  # readmethod tells the parser to use blastxml format for parsing
  my $remoteBlastXML = Bio::Tools::Run::RemoteBlast->new('-prog' => $prog, '-data' => $db, '-readmethod' => 'xml');
  $remoteBlastXML->retrieve_parameter('FORMAT_TYPE', 'XML');  # tells NCBI to send XML back

  # If a protein query and the matrix is defined, then set the MATRIX_NAME and set the GAPCOSTS
  # to the default value for that matrix based on the NCBI BLAST web page.  Otherwise, for blastn
  # set the GAPCOSTS to 5 2. 
  if ($matrix) {
    $Bio::Tools::Run::RemoteBlast::HEADER{'MATRIX_NAME'} = $matrix;
    $Bio::Tools::Run::RemoteBlast::HEADER{'GAPCOSTS'} = $gapCosts{$matrix};
  } else {
    $Bio::Tools::Run::RemoteBlast::HEADER{'GAPCOSTS'} = '5 2';
  }

  # If a valid accession number is set, generate the Seq object using a GenBank query.
  # Otherwise, create a Seq object directly using the submitted sequence and set the
  # alphabet based on the query type.
  my ($seqObj);
  if ($accNum && ! $accNumError) {
    my $genBankObj = Bio::DB::GenBank->new();

    if ($accNum =~ /\.\d+$/) {
      $seqObj = $genBankObj->get_Seq_by_version($accNum);
    } else {
      $seqObj = $genBankObj->get_Seq_by_acc($accNum);
    }
    $seqObj->desc('bprb_sjh_' . time());

  } else {
    $seqObj = Bio::Seq->new(-seq => $sequence, -desc => 'bprb_sjh_' . time());

    if ($prog eq 'blastp' || $prog eq 'tblastn') {
      $seqObj->alphabet('protein');
    } else {
      $seqObj->alphabet('dna');
    }
  }

  # Display info about the query to the user, then set up the HTMLResultWriter
  # for displaying the results on the web page.
  &PrintQueryInfo($remoteBlastXML, $seqObj);
  my $htmlWriter = Bio::SearchIO::Writer::HTMLResultWriter->new();
  $htmlWriter->start_report(\&StripReportHTML);
  $htmlWriter->title(\&StripReportHTML);
  my $htmlOutput = Bio::SearchIO->new(-writer => $htmlWriter);

  # Adapted from the BioPerl RemoteBlast documentation page found at
  # http://doc.bioperl.org/releases/bioperl-current/bioperl-live/Bio/Tools/Run/RemoteBlast.html
  my $r = $remoteBlastXML->submit_blast($seqObj);
  my (@rids, $rid, $rc, $result, $fileName);

  while (@rids = $remoteBlastXML->each_rid) {
    for $rid (@rids) {
      $rc = $remoteBlastXML->retrieve_blast($rid);

      if (! ref($rc)) {
        # Error has occurred for this RID, so stop attempting to process it
        if ($rc < 0) {
          $remoteBlastXML->remove_rid($rid);
        }
        sleep 5;
      } else {
        $result = $rc->next_result();

        # Save the output
        $fileName = $result->query_name() . "\.xml";
        $remoteBlastXML->save_output($fileName);

        # Print the output
        print "<h3>Search Results</h3>";
        print "<div>Local copy of the results saved to $fileName</div>\n";
        print qq{<div style="font-size:smaller;">\n};
        $htmlOutput->write_result($result);
        print "</div>\n";

        # Finished processing this RID
        $remoteBlastXML->remove_rid($rid);
      }
    }
  }

  return;
}


sub ValidateForm {
  my ($accNum, $sequence, $prog, $db, $matrix, $accNumError) = @_;

  # Sequences may only contain characters appropriate for the search type
  my ($sequenceError);
  if (($prog eq 'blastn' || $prog eq 'blastx' || $prog eq 'tblastx') && $sequence !~ /^[acgt]+$/) {
    $sequenceError = 1;
  } elsif (($prog eq 'blastp' || $prog eq 'tblastn') && $sequence !~ /^[^bjouxz]+$/) {
    $sequenceError = 1;
  }

  # Check input for errors
  my ($errors);
  # Accession Number and Sequence format and appropriate char set based on search type
  if (! $sequence && $accNumError) {
    $errors .= qq{<li>The accession number must be of the format [A-Z_]+[0-9]+(.[0-9]+)?.</li>\n};
  } elsif (! $accNum && $sequenceError) {
    $errors .= qq{<li>The sequence must contain only characters appropriate for the search type.</li>\n};
  } elsif ($accNumError && $sequenceError) {
    $errors .= qq{<li>The accession number must be of the format [A-Z]+[0-9]+(.[0-9]+)?.</li>\n};
    $errors .= qq{<li>The sequence must contain only characters appropriate for the search type.</li>\n};
  }
  # Program must be from select list
  if (! $programs{$prog}) {
    $errors .= qq{<li>The selected program, <i>$prog</i>, is invalid.</li>\n};
  }
  # Database must be from select list associated with the selected program
  if ((($prog eq 'blastp' || $prog eq 'blastx') && ! $proteinDBs{$db}) || ($prog ne 'blastp' && $prog ne 'blastx' && ! $nucleotideDBs{$db})) {
    $errors .= qq{<li>The selected database, <i>$db</i>, is invalid for the selected program, <i>$prog</i>.</li>\n};
  }
  # Matrix must be from select list associated with the selected program
  if (($matrix && $prog eq 'blastn') || ($prog ne 'blastn' && ! $matrices{$matrix})) {
    $errors .= qq{<li>The selected matrix, <i>$matrix</i>, is invalid for the selected program, <i>$prog</i>.</li>\n};
  }

  return $errors;
}


sub PrintQueryInfo {
  my ($remoteBlast, $seqObj) = @_;
  my ($header);

  print "<h3>BLAST Configuration</h3>\n<ul>\n";
  print "  <li>Program:  " . $remoteBlast->program() . "</li>\n";
  print "  <li>Database:  " . $remoteBlast->database() . "</li>\n";
  print "  <li>Read Method:  " . $remoteBlast->readmethod() . "</li>\n";
  print "<li>Request Headers:<ul>\n";

  for $header (sort keys %Bio::Tools::Run::RemoteBlast::HEADER) {
    print qq{    <li>$header - $Bio::Tools::Run::RemoteBlast::HEADER{$header}</li>\n};
  }

  print "  </ul></li>\n  <li>Retrieval Headers:<ul>";

  for $header (sort keys %Bio::Tools::Run::RemoteBlast::RETRIEVALHEADER) {
    print qq{    <li>$header - $Bio::Tools::Run::RemoteBlast::RETRIEVALHEADER{$header}</li>\n};
  }

  print "  </ul></li>\n</ul>\n<h3>Search Terms</h3>\n<ul>\n";

  print "  <li>Accession Number:  " . $seqObj->accession_number() . "</li>\n";
  print "  <li>Sequence:  " . $seqObj->seq() . "</li>\n";
  print "  <li>Alphabet:  " . $seqObj->alphabet() . "</li>\n";

  print "</ul>\n";
}

sub StripReportHTML {
  print "<br />\n";
}
