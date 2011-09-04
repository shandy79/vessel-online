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
import org.vesselonline.ai.util.Instrumentable;
import org.vesselonline.ai.util.Instrumentation;

public class BacktrackingMRV<T> implements Instrumentable {
  private List<SimpleNode<T>> nodeList;
  private Instrumentation instrumentation;

  /**
   *
   * @param nodeList  Expected to have domain values set for all elements.
   * @param instrumentation
   */
  public BacktrackingMRV(List<SimpleNode<T>> nodeList, Instrumentation instrumentation) {
    this.nodeList = nodeList;
    this.instrumentation = instrumentation;
  }

  public List<SimpleNode<T>> getNodeList() { return nodeList; }

  @Override
  public Instrumentation getInstrumentation() { return instrumentation; }
  @Override
  public void setInstrumentation(Instrumentation instrumentation) { this.instrumentation = instrumentation; }

  public List<SimpleNode<T>> backtrack(List<SimpleNode<T>> backtrackNodeList) {
    if (goalTest(backtrackNodeList)) {
      System.out.println("Goal met!");
      return backtrackNodeList;
    }

    List<SimpleNode<T>> originalNodeList = GraphUtilities.copyListToNewList(backtrackNodeList);
    SimpleNode<T> node = getMinimumRemainingValuesNode(backtrackNodeList);

    for (T value : getLeastConstrainingValueList(node)) {
      node.setValue(value);
      getInstrumentation().print("Assigning value " + value + " to node " + node.getName());

      if (! isNodeConsistent(node)) {
        continue;
      } else {
        doInference(node);
        return backtrack(backtrackNodeList);
      }
    }

    return originalNodeList;
  }

  protected void doInference(SimpleNode<T> node) { ; }

  private boolean goalTest(List<SimpleNode<T>> backtrackNodeList) {
    for (SimpleNode<T> node : backtrackNodeList) {
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

  private SimpleNode<T> getMinimumRemainingValuesNode(List<SimpleNode<T>> backtrackNodeList) {
    int minRemainingValues = Integer.MAX_VALUE;
    int currentNodeMRV;
    List<SimpleNode<T>> mrvNodeList = null;

    for (SimpleNode<T> node : backtrackNodeList) {
      if (node.getValue() != null) { continue; }

      currentNodeMRV = getRemainingValues(node).size();

      if (currentNodeMRV < minRemainingValues) {
        mrvNodeList = new ArrayList<SimpleNode<T>>();
        mrvNodeList.add(node);
        minRemainingValues = currentNodeMRV;
        getInstrumentation().print("New MRV " + minRemainingValues + " for node " + node.getName());
      } else if (currentNodeMRV == minRemainingValues) {
        mrvNodeList.add(node);
        getInstrumentation().print("  Node " + node.getName() + " also at MRV " + minRemainingValues);
      }
    }

    // Add nodeList.size units of work
    getInstrumentation().addWorkUnits(backtrackNodeList.size());

    if (mrvNodeList.size() > 1) {
      return getMaximumDegreeNode(mrvNodeList);
    } else {
      return mrvNodeList.get(0);
    }
  }

  private List<T> getLeastConstrainingValueList(SimpleNode<T> node) {
    // Use a map w/domain index as key and total remaining domain values of neighbors as value
    Map<Integer, Integer> domainLCVMap = new HashMap<Integer, Integer>();
    int totalRemainingValues;

    // For the current node, for each element in its domain, determine the
    // total remaining values of each of its unassigned neighbors.
    for (int i = 0; i < node.getDomain().size(); i++) {
      node.setValue(node.getDomain().get(i));
      totalRemainingValues = 0;

      for (SimpleEdge<T> edge : node.getEdgeList()) {
        if (edge.getToNode().getValue() == null) {
          totalRemainingValues += getRemainingValues(edge.getToNode()).size();
        }
      }

      domainLCVMap.put(i, totalRemainingValues);
    }

    LinkedHashMap<Integer, Integer> sortedDomainLCVMap = GraphUtilities.sortMapByValuesWithDuplicates(domainLCVMap, true);
    List<T> lcvOrderList = new ArrayList<T>(node.getDomain().size());

    // Create the LCV order list from the reverse sorted map
    for (Integer i : sortedDomainLCVMap.keySet()) {
      lcvOrderList.add(node.getDomain().get(i));
      getInstrumentation().print("For " + node.getName() + ", remaining values for " + node.getDomain().get(i) + "=" + sortedDomainLCVMap.get(i));
    }

    // Add domain.size * edgeList.size work units for finding least-constraining value list
    getInstrumentation().addWorkUnits(node.getDomain().size() * node.getEdgeList().size());

    return lcvOrderList;
  }

  private boolean isNodeConsistent(SimpleNode<T> node) {
    for (SimpleEdge<T> edge : node.getEdgeList()) {
      if (node.getValue() != null && edge.getToNode().getValue() != null && node.getValue().equals(edge.getToNode().getValue())) {
        return false;
      }
    }

    return true;
  }

  private List<T> getRemainingValues(SimpleNode<T> node) {
    List<T> remainingValues = GraphUtilities.copyListToNewList(node.getDomain());

    for (SimpleEdge<T> edge : node.getEdgeList()) {
      remainingValues.remove(edge.getToNode().getValue());
    }

//    for (T value : remainingValues) {
//      getInstrumentation().print("For node " + node.getName() + ", remainingValues contains " + value.toString());
//    }

    // Add edgeList.size units of work
    getInstrumentation().addWorkUnits(node.getEdgeList().size());

    return remainingValues;
  }

  /**
   * 
   * @param mrvNodeList  Since receives an mrvNodeList, unassigned nodes are excluded.
   * @return
   */
  protected SimpleNode<T> getMaximumDegreeNode(List<SimpleNode<T>> mrvNodeList) {
    SimpleNode<T> maxDegreeNode = null;

    for (SimpleNode<T> node : mrvNodeList) {
      if (maxDegreeNode == null || node.getDegree() > maxDegreeNode.getDegree()) {
        maxDegreeNode = node;
      }
    }

    // Add mrvNodeList.size units of work
    getInstrumentation().addWorkUnits(mrvNodeList.size());

    getInstrumentation().print("Max degree node is " + maxDegreeNode.getName());
    return maxDegreeNode;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("BacktrackingMRV ARGS: <Print debug? (true/false)> <# Colors (3/4)> <# Nodes (optional)>");
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
      BacktrackingMRV<FourColor> bmrv;

      if (nodes > 0) {
        RandomMapCreator<FourColor> rmc = new RandomMapCreator<FourColor>(nodes, instr);
        bmrv = new BacktrackingMRV<FourColor>(rmc.createRandomGraph(Arrays.asList(FourColor.values())), instr);
      } else {
        bmrv = new BacktrackingMRV<FourColor>(GraphUtilities.createAustraliaGraph(Arrays.asList(FourColor.values())), instr);
      }

      List<SimpleNode<FourColor>> result = bmrv.backtrack(bmrv.getNodeList());
      System.out.println(GraphUtilities.generateNodeListStr(result));

    } else {
      BacktrackingMRV<ThreeColor> bmrv;

      if (nodes > 0) {
        RandomMapCreator<ThreeColor> rmc = new RandomMapCreator<ThreeColor>(nodes, instr);
        bmrv = new BacktrackingMRV<ThreeColor>(rmc.createRandomGraph(Arrays.asList(ThreeColor.values())), instr);
      } else {
        bmrv = new BacktrackingMRV<ThreeColor>(GraphUtilities.createAustraliaGraph(Arrays.asList(ThreeColor.values())), instr);
      }

      List<SimpleNode<ThreeColor>> result = bmrv.backtrack(bmrv.getNodeList());
      System.out.println(GraphUtilities.generateNodeListStr(result));
    }

    System.out.println("Work performed = " + instr.getWorkUnits() + " units.");
  }
}
