<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xhtml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
      doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" encoding="UTF-8" />

  <xsl:variable name="goURL">http://www.ebi.ac.uk/QuickGO/GTerm?id=</xsl:variable>
  <xsl:variable name="title">Gene Ontology-BioMart Mashup</xsl:variable>
  <xsl:variable name="copyright">&#xA9; 2011 Steven Handy.  All rights reserved.</xsl:variable>
  <xsl:variable name="bioMartTH">
    <tr><th scope="col">Gene ID</th><th scope="col">Gene Name</th><th scope="col">Gene/Protein Description</th><th scope="col">Protein ID</th></tr>
  </xsl:variable>

  <xsl:template match="/MashupResponse">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
  <meta http-equiv="Content-Style-Type" content="text/css" />
  <meta name="description" content="{$title}" />
  <meta name="keywords" content="bioinformatics, molecular biology, genomics, proteomics, DNA, protein, gene, genetics, semantic web, Gene Ontology, BioMart" />
  <meta name="copyright" content="{$copyright}" />
  <title><xsl:value-of select="$title" /></title>

  <link rel="stylesheet" type="text/css" href="mashup.css" />
</head>
<body>
  <div id="header"><h1><xsl:value-of select="$title" /></h1></div>

  <div id="container">
    <div id="content">
<h2><i>Search Term:</i>&#160;&#160;<xsl:value-of select="GOID" /></h2>

<h3>Metadata</h3>
<table class="browse_table" summary="GO Metadata"><tbody>
  <tr><th scope="row">GO Term ID</th>
    <td><a href="{$goURL}{GOID}"> <xsl:value-of select="GOID" /> </a></td></tr>
  <tr><th scope="row">GO Term Name</th>
    <td> <xsl:value-of select="GOTerm" /> </td></tr>
  <tr><th scope="row">Description</th>
    <td> <xsl:value-of select="Metadata//item[key='definition']/value" /> </td></tr>
</tbody></table>

<h3>SalmonDB Results</h3>
<table class="list_table" summary="SalmonDB Results"><tbody>
<xsl:copy-of select="$bioMartTH" />
<xsl:copy-of select="SalmonDB/tr" />
</tbody></table>

<h3>WormBase Results</h3>
<table class="list_table" summary="WormBase Results"><tbody>
<xsl:copy-of select="$bioMartTH" />
<xsl:copy-of select="WormBase/tr" />
</tbody></table>
    </div>

    <div id="rail">
      <table id="menu_tbl" summary="related GO term links"><tbody>
        <tr><td>PARENTS<ul>
          <xsl:for-each select="Parents//item">
            <xsl:sort select="key" data-type="text" order="ascending" />
            <li><a href="{$goURL}{key}" target="_blank"> <xsl:value-of select="key" /> </a>&#160; <xsl:value-of select="value" /> </li>
          </xsl:for-each>
       </ul></td></tr>
        <tr><td class="this_pg"> <xsl:value-of select="GOID" /> &#160; <xsl:value-of select="GOTerm" /> </td></tr>
        <tr><td>CHILDREN<ul>
          <xsl:for-each select="Children//item">
            <xsl:sort select="key" data-type="text" order="ascending" />
            <li><a href="{$goURL}{key}" target="_blank"> <xsl:value-of select="key" /> </a>&#160; <xsl:value-of select="value" /> </li>
          </xsl:for-each>
        </ul></td></tr>
        <tr><td>REGULATED BY<ul>
          <xsl:for-each select="Relations//item[contains(value, 'regulates')]">
            <xsl:sort select="key" data-type="text" order="ascending" />
            <li><a href="{$goURL}{key}" target="_blank"> <xsl:value-of select="key" /> </a></li>
          </xsl:for-each>
        </ul></td></tr>
      </tbody></table>
    </div>
  </div>

  <div id="footer">
    <xsl:value-of select="$copyright" />
    [<a href="http://validator.w3.org/check?uri=referer">XHTML 1.0</a>]&#160;
    [<a href="http://jigsaw.w3.org/css-validator/check/referer">CSS 2</a>]&#160;
    Design by <a href="http://www.vesselonline.org/">Vessel</a>.&#160;
  </div>
</body>
</html>
  </xsl:template>
</xsl:stylesheet>
