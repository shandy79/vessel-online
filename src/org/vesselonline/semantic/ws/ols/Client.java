package org.vesselonline.semantic.ws.ols;

import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetAllTermsFromOntologyDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetAllTermsFromOntologyResponseDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermByIdDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermByIdResponseDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermChildrenDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermChildrenResponseDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermMetadataDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermMetadataResponseDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermParentsDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermParentsResponseDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermRelationsDocument;
import uk.ac.ebi.www.ontology_lookup.ontologyquery.GetTermRelationsResponseDocument;

public class Client{
  private static final String ONTOLOGY = "GO";  // Gene Ontology

  public static void main(java.lang.String args[]){
    try {
      if (args.length < 1) {
        System.out.println("Client ARGS: <GO ID>");
        return;
      }

      String termId = args[0];  //"GO:0006914";
      QueryServiceStub stub = new QueryServiceStub();

      String term = getTermById(stub, termId);
      String parentXML = getTermParents(stub, termId);
      String childrenXML = getTermChildren(stub, termId);
      String relationXML = getTermRelations(stub, termId);
      String metadataXML = getTermMetadata(stub, termId);

      System.out.println("<GOID>" + termId + "</GOID>\n<GOTerm>" + term + "</GOTerm>\n");
      System.out.println("<Metadata>\n" + metadataXML + "\n</Metadata>\n");
      System.out.println("<Parents>\n" + parentXML + "\n</Parents>\n");
      System.out.println("<Children>\n" + childrenXML + "\n</Children>\n");
      System.out.println("<Relations>\n" + relationXML + "\n</Relations>\n");

//      getAllTermsFromOntology(stub);
    } catch(Exception e){
      e.printStackTrace();
      System.out.println("\n\n\n");
    }
  }

/*
public String getTermById(String termId, String ontologyName);
public Map getTermParents(String termId, String ontologyName);
public Map getTermChildren(String termId, String ontologyName, int distance, int[] relationTypes);
public Map getTermRelations(String termId, String ontologyName);
public Map getTermMetadata(String termId, String ontologyName);
public Map getAllTermsFromOntology(String ontologyName);
*/

  public static String getTermById(QueryServiceStub stub, String termId) {
    String term = null;
    try {
      GetTermByIdDocument req = GetTermByIdDocument.Factory.newInstance();
      GetTermByIdDocument.GetTermById data = req.addNewGetTermById();

      data.setTermId(termId);
      data.setOntologyName(ONTOLOGY);

      GetTermByIdResponseDocument res = stub.getTermById(req);
      term = res.getGetTermByIdResponse().getGetTermByIdReturn();
//      term += "\n" + res.getGetTermByIdResponse().xmlText();
    } catch(Exception e){
      e.printStackTrace();
      System.out.println("\n\n\n");
    }
    return term;
  }

  public static String getTermParents(QueryServiceStub stub, String termId) {
    String parentXML = null;
    try {
      GetTermParentsDocument req = GetTermParentsDocument.Factory.newInstance();
      GetTermParentsDocument.GetTermParents data = req.addNewGetTermParents();

      data.setTermId(termId);
      data.setOntologyName(ONTOLOGY);

      GetTermParentsResponseDocument res = stub.getTermParents(req);
      parentXML = res.getGetTermParentsResponse().getGetTermParentsReturn().toString();
    } catch(Exception e){
      e.printStackTrace();
      System.out.println("\n\n\n");
    }
    return parentXML;
  }

  public static String getTermChildren(QueryServiceStub stub, String termId) {
    String childrenXML = null;
    try {
      GetTermChildrenDocument req = GetTermChildrenDocument.Factory.newInstance();
      GetTermChildrenDocument.GetTermChildren data = req.addNewGetTermChildren();

      data.setTermId(termId);
      data.setOntologyName(ONTOLOGY);
      data.setDistance(1);

      GetTermChildrenResponseDocument res = stub.getTermChildren(req);
      childrenXML = res.getGetTermChildrenResponse().getGetTermChildrenReturn().toString();
    } catch(Exception e){
      e.printStackTrace();
      System.out.println("\n\n\n");
    }
    return childrenXML;
  }

  public static String getTermRelations(QueryServiceStub stub, String termId) {
    String relationXML = null;
    try {
      GetTermRelationsDocument req = GetTermRelationsDocument.Factory.newInstance();
      GetTermRelationsDocument.GetTermRelations data = req.addNewGetTermRelations();

      data.setTermId(termId);
      data.setOntologyName(ONTOLOGY);

      GetTermRelationsResponseDocument res = stub.getTermRelations(req);
      relationXML = res.getGetTermRelationsResponse().getGetTermRelationsReturn().toString();
    } catch(Exception e){
      e.printStackTrace();
      System.out.println("\n\n\n");
    }
    return relationXML;
  }

  public static String getTermMetadata(QueryServiceStub stub, String termId) {
    String metadataXML = null;
    try {
      GetTermMetadataDocument req = GetTermMetadataDocument.Factory.newInstance();
      GetTermMetadataDocument.GetTermMetadata data = req.addNewGetTermMetadata();

      data.setTermId(termId);
      data.setOntologyName(ONTOLOGY);

      GetTermMetadataResponseDocument res = stub.getTermMetadata(req);
      metadataXML = res.getGetTermMetadataResponse().getGetTermMetadataReturn().toString();
    } catch(Exception e){
      e.printStackTrace();
      System.out.println("\n\n\n");
    }
    return metadataXML;
  }


  public static void getAllTermsFromOntology(QueryServiceStub stub){
    try{
      GetAllTermsFromOntologyDocument req = GetAllTermsFromOntologyDocument.Factory.newInstance();
      GetAllTermsFromOntologyDocument.GetAllTermsFromOntology data = req.addNewGetAllTermsFromOntology();

      data.setOntologyName(ONTOLOGY);

      GetAllTermsFromOntologyResponseDocument res = stub.getAllTermsFromOntology(req);
      System.out.println(res.getGetAllTermsFromOntologyResponse().xmlText());
    } catch(Exception e){
      e.printStackTrace();
      System.out.println("\n\n\n");
    }
  }
}
