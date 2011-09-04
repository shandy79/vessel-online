package org.vesselonline.ai.csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.vesselonline.ai.graph.FourColor;
import org.vesselonline.ai.graph.GraphUtilities;
import org.vesselonline.ai.graph.RandomMapCreator;
import org.vesselonline.ai.graph.SimpleEdge;
import org.vesselonline.ai.graph.SimpleNode;
import org.vesselonline.ai.graph.ThreeColor;
import org.vesselonline.ai.util.AIUtilities;
import org.vesselonline.ai.util.Instrumentable;
import org.vesselonline.ai.util.Instrumentation;

public class MinConflicts<T> implements Instrumentable {
  private List<SimpleNode<T>> nodeList;
  private int maxSteps;
  private Instrumentation instrumentation;

  /**
   *
   * @param nodeList  Expected to have domain values set for all elements.
   * @param maxSteps  Limit the number of iterations.
   * @param instrumentation
   */
  public MinConflicts(List<SimpleNode<T>> nodeList, int maxSteps, Instrumentation instrumentation) {
    this.nodeList = nodeList;
    this.maxSteps = maxSteps;
    this.instrumentation = instrumentation;

    initNodeAssignments();
    instrumentation.print(GraphUtilities.generateNodeListStr(nodeList));
  }

  public List<SimpleNode<T>> getNodeList() { return nodeList; }

  public int getMaxSteps() { return maxSteps; }

  @Override
  public Instrumentation getInstrumentation() { return instrumentation; }
  @Override
  public void setInstrumentation(Instrumentation instrumentation) { this.instrumentation = instrumentation; }

  public boolean minimizeConflicts() {
    SimpleNode<T> node;
    List<T> lcvList;
    int j = 0;

    for (int i = 0; i < getMaxSteps(); i++) {
      if (goalTest()) {
        System.out.println("Goal met after " + i + " steps!  Work performed = " + getInstrumentation().getWorkUnits() + " units.");
        return true;
      }

      node = getRandomConflictedNode();
      getInstrumentation().print("Selected " + node.getName() + ":" + node.getValue() + " in iteration " + i);

      lcvList = getLeastConflictingValueList(node);

      // Use "not the same as the current value" check to avoid infinite loops.
      // There is an issue w/missing better solutions when skipping values w/zero conflicts.
      j = 0;
      while (lcvList.get(j).equals(node.getValue())) { // || lcvList.get(j).equals(node.getPreviousValue())) {
        j++;
      }

//      node.setPreviousValue(node.getValue());
      node.setValue(lcvList.get(j));
      getInstrumentation().print("Set to " + lcvList.get(j));

      // Set only for purpose of recording step count to output
      j = i;
    }

    System.out.println("Work performed = " + getInstrumentation().getWorkUnits() + " units over " + j + " steps.");
    return false;
  }

  private void initNodeAssignments() {
    for (SimpleNode<T> node : getNodeList()) {
      node.setValue(node.getDomain().get(AIUtilities.getInstance().nextRandomInt(node.getDomain().size())));
    }

    // Add nodeList.size work units
    getInstrumentation().addWorkUnits(getNodeList().size());
  }

  private boolean goalTest() {
    for (SimpleNode<T> node : getNodeList()) {
      if (node.getValue() == null) {
        getInstrumentation().print("Goal test failed for " + node.getName() + " (" + node.getValue() + ")");
        return false;
      }

      for (SimpleEdge<T> edge : node.getEdgeList()) {
        if (edge.getToNode().getValue() == null || node.getValue().equals(edge.getToNode().getValue())) {
          getInstrumentation().print("Goal test failed for " + node.getName() + " (" + node.getValue() + ") and " +
                                     edge.getToNode().getName() + " (" + edge.getToNode().getValue() + ")");
          return false;
        }
      }
    }

    return true;
  }

  private SimpleNode<T> getRandomConflictedNode() {
    SimpleNode<T> node = null;

    while (node == null || ! isNodeConflicted(node)) {
      node = getNodeList().get(AIUtilities.getInstance().nextRandomInt(getNodeList().size()));
    }

    return node;
  }

  private boolean isNodeConflicted(SimpleNode<T> node) {
    for (SimpleEdge<T> edge : node.getEdgeList()) {
      if (node.getValue().equals(edge.getToNode().getValue())) {
        return true;
      }
    }

    return false;
  }

  private List<T> getLeastConflictingValueList(SimpleNode<T> node) {
    // Use a map w/domain index as key and remaining conflicts with neighbors as value.
    Map<Integer, Integer> domainLCVMap = new HashMap<Integer, Integer>();
    int remainingConflicts;

    // For the current node, for each element in its domain, determine the
    // remaining conflicts with each of its neighbors.
    for (int i = 0; i < node.getDomain().size(); i++) {
      remainingConflicts = 0;

      for (SimpleEdge<T> edge : node.getEdgeList()) {
        if (edge.getToNode().getValue().equals(node.getDomain().get(i))) {
          remainingConflicts++;
        }
      }

      domainLCVMap.put(i, remainingConflicts);
    }

    LinkedHashMap<Integer, Integer> sortedDomainLCVMap = GraphUtilities.sortMapByValuesWithDuplicates(domainLCVMap, false);
    List<T> lcvOrderList = new ArrayList<T>(node.getDomain().size());

    // Create the LCV order list from the reverse sorted map
    for (Integer i : sortedDomainLCVMap.keySet()) {
      lcvOrderList.add(node.getDomain().get(i));
      getInstrumentation().print("  For " + node.getName() + ", conflicts for " + node.getDomain().get(i) + "=" + sortedDomainLCVMap.get(i));
    }

    // Add domain.size * edgeList.size work units for finding least-constraining value list
    getInstrumentation().addWorkUnits(node.getDomain().size() * node.getEdgeList().size());

    return lcvOrderList;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("MinConflicts ARGS: <Print debug? (true/false)> <# Colors (3/4)> <# Nodes (optional)>");
      System.out.println("  If <# Nodes> is not specified, a map of Australian territories will be used.");
      return;
    }

    Instrumentation instr;
    int colors, nodes = 0;
    int maxSteps = 200000;

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
      MinConflicts<FourColor> mc;

      if (nodes > 0) {
        RandomMapCreator<FourColor> rmc = new RandomMapCreator<FourColor>(nodes, instr);
        mc = new MinConflicts<FourColor>(rmc.createRandomGraph(Arrays.asList(FourColor.values())), maxSteps, instr);
      } else {
        mc = new MinConflicts<FourColor>(GraphUtilities.createAustraliaGraph(Arrays.asList(FourColor.values())), maxSteps, instr);
      }

      if (mc.minimizeConflicts()) {
        System.out.println(GraphUtilities.generateNodeListStr(mc.getNodeList()));
      }

    } else {
      MinConflicts<ThreeColor> mc;

      if (nodes > 0) {
        RandomMapCreator<ThreeColor> rmc = new RandomMapCreator<ThreeColor>(nodes, instr);
        mc = new MinConflicts<ThreeColor>(rmc.createRandomGraph(Arrays.asList(ThreeColor.values())), maxSteps, instr);
      } else {
        mc = new MinConflicts<ThreeColor>(GraphUtilities.createAustraliaGraph(Arrays.asList(ThreeColor.values())), maxSteps, instr);
      }

      if (mc.minimizeConflicts()) {
        System.out.println(GraphUtilities.generateNodeListStr(mc.getNodeList()));
      }
    }
  }
}
