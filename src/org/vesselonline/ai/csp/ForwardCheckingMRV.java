package org.vesselonline.ai.csp;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.vesselonline.ai.graph.FourColor;
import org.vesselonline.ai.graph.GraphUtilities;
import org.vesselonline.ai.graph.RandomMapCreator;
import org.vesselonline.ai.graph.SimpleEdge;
import org.vesselonline.ai.graph.SimpleNode;
import org.vesselonline.ai.graph.ThreeColor;
import org.vesselonline.ai.util.Instrumentation;

public class ForwardCheckingMRV<T> extends BacktrackingMRV<T> {
  /**
   *
   * @param nodeList  Expected to have domain values set for all elements.
   * @param instrumentation
   */
  public ForwardCheckingMRV(List<SimpleNode<T>> nodeList, Instrumentation instrumentation) {
    super(nodeList, instrumentation);
  }

  /**
   * Implementation of forward-checking algorithm.
   */
  protected void doInference(SimpleNode<T> node) {
    Iterator<T> domainItr = node.getDomain().iterator();
    T domainValue;
  
    // Must use Iterator.remove() here to avoid screwing up the List structure
    while (domainItr.hasNext()) {
      domainValue = domainItr.next();

      if (! domainValue.equals(node.getValue())) {
        domainItr.remove();
        getInstrumentation().print("Removed " + domainValue + " from domain of " + node.getName());
      }
    }

    for (SimpleEdge<T> edge : node.getEdgeList()) {
      if (edge.getToNode().getDomain() != null) {
        if (edge.getToNode().removeFromDomain(node.getValue())) {
          getInstrumentation().print("Removed " + node.getValue() + " from domain of " + edge.getToNode().getName());
        }
      }
    }

    // Add domain.size * edgeList.size work units for forward checking
    getInstrumentation().addWorkUnits(node.getDomain().size() * node.getEdgeList().size());
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("ForwardCheckingMRV ARGS: <Print debug? (true/false)> <# Colors (3/4)> <# Nodes (optional)>");
      System.out.println("  If <# Nodes> is not specified, a map of Australian territories will be used.");
      return;
    }

    Instrumentation instr;
    int colors, nodes = 0;

    if (args[0].equalsIgnoreCase("true")) {
      instr = new Instrumentation(true, System.out);
    } else {
      instr = new Instrumentation(false, System.out);
    }

    try {
      colors = Integer.parseInt(args[1]);

      if (args.length > 2) {
        nodes = Integer.parseInt(args[2]);
      }
    } catch (NumberFormatException nfe) {
      colors = 3;
      nodes = 0;
    }

    if (colors == 4) {
      ForwardCheckingMRV<FourColor> fmrv;

      if (nodes > 0) {
        RandomMapCreator<FourColor> rmc = new RandomMapCreator<FourColor>(nodes, instr);
        fmrv = new ForwardCheckingMRV<FourColor>(rmc.createRandomGraph(Arrays.asList(FourColor.values())), instr);
      } else {
        fmrv = new ForwardCheckingMRV<FourColor>(GraphUtilities.createAustraliaGraph(Arrays.asList(FourColor.values())), instr);
      }

      List<SimpleNode<FourColor>> result = fmrv.backtrack(fmrv.getNodeList());
      System.out.println(GraphUtilities.generateNodeListStr(result));

    } else {
      ForwardCheckingMRV<ThreeColor> fmrv;

      if (nodes > 0) {
        RandomMapCreator<ThreeColor> rmc = new RandomMapCreator<ThreeColor>(nodes, instr);
        fmrv = new ForwardCheckingMRV<ThreeColor>(rmc.createRandomGraph(Arrays.asList(ThreeColor.values())), instr);
      } else {
        fmrv = new ForwardCheckingMRV<ThreeColor>(GraphUtilities.createAustraliaGraph(Arrays.asList(ThreeColor.values())), instr);
      }

      List<SimpleNode<ThreeColor>> result = fmrv.backtrack(fmrv.getNodeList());
      System.out.println(GraphUtilities.generateNodeListStr(result));
    }

    System.out.println("Work performed = " + instr.getWorkUnits() + " units.");
  }
}
