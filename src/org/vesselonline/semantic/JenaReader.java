package org.vesselonline.semantic;

import java.io.IOException;
import java.io.InputStream;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class JenaReader {
  private static final String RDF_FILE = "file:///home/shandy/workspace/jhu/library.rdf";
  
  public static void main (String args[]) {
    // create an empty model
    Model model = ModelFactory.createDefaultModel();

    InputStream in = FileManager.get().open(RDF_FILE);
    if (in == null) {
      throw new IllegalArgumentException("File: " + RDF_FILE + " not found");
    }

    // read the RDF/XML file
    model.read(in, "");

    try {
      in.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    // Quick sanity check
    System.out.println("Number of statements:  " + model.size());

    // Show the title and publisher for books written by Charles Dickens
    String queryString = "\nPREFIX lib:  <http://vesselonline.org/jhu/library#> \n" +
                         "SELECT ?title ?publisher WHERE \n" +
                         "{ ?y lib:writtenBy \"Charles Dickens\" . \n" +
                         "  ?y lib:title ?title . \n" +
                         "  ?y lib:publishedBy ?publisher . }";
    System.out.println(queryString);

    Query query = QueryFactory.create(queryString);
    QueryExecution qe = QueryExecutionFactory.create(query, model);
    ResultSet results = qe.execSelect();
    ResultSetFormatter.out(System.out, results, query);
    qe.close();

    // Show the title and author of books published in or after 2008
    queryString = "\nPREFIX lib:  <http://vesselonline.org/jhu/library#> \n" +
                  "SELECT ?title ?author WHERE \n" +
                  "{ ?y lib:writtenBy ?author . \n" +
                  "  ?y lib:title ?title . \n" +
                  "  ?y lib:publishedInYear ?year . \n" +
                  "  FILTER (?year >= 2008) }";
    System.out.println(queryString);

    query = QueryFactory.create(queryString);
    qe = QueryExecutionFactory.create(query, model);
    results = qe.execSelect();
    ResultSetFormatter.out(System.out, results, query);
    qe.close();

    // Show the authors of all books in sorted order
    queryString = "\nPREFIX lib:  <http://vesselonline.org/jhu/library#> \n" +
                  "SELECT ?author WHERE \n" +
                  "{ ?y lib:writtenBy ?author . }\n" +
                  "ORDER BY ?author";
    System.out.println(queryString);

    query = QueryFactory.create(queryString);
    qe = QueryExecutionFactory.create(query, model);
    results = qe.execSelect();
    ResultSetFormatter.out(System.out, results, query);
    qe.close();

    model.close();
  }
}
