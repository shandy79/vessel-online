package org.vesselonline.ai.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.vesselonline.ai.util.AIUtilities;
import org.vesselonline.ai.util.Instrumentable;
import org.vesselonline.ai.util.Instrumentation;

public class RandomMapCreator<T> implements Instrumentable {
  private List<SimpleNode<T>> nodeList;
  private int nodeCount;
  private Set<Integer> completedNodeCache;
  private Instrumentation instrumentation;

  private static final boolean DEBUG = false;

  public RandomMapCreator(int nodeCount, Instrumentation instrumentation) {
    this.nodeCount = nodeCount;
    this.instrumentation = instrumentation;

    nodeList = new ArrayList<SimpleNode<T>>(nodeCount);
    completedNodeCache = new HashSet<Integer>();
  }

  private List<SimpleNode<T>> getNodeList() { return nodeList; }

  @Override
  public Instrumentation getInstrumentation() { return instrumentation; }
  @Override
  public void setInstrumentation(Instrumentation instrumentation) { this.instrumentation = instrumentation; }

  public List<SimpleNode<T>> createRandomGraph(List<T> domain) {
    int x, y;

    // Generate nodeCount randomly placed nodes on a nodeCount x nodeCount grid
    for (int i = 0; i < nodeCount; i++) {
      x = AIUtilities.getInstance().nextRandomInt(nodeCount);
      y = AIUtilities.getInstance().nextRandomInt(nodeCount);

      while (doesNodeExistAtPoint(x, y)) {
        x = AIUtilities.getInstance().nextRandomInt(nodeCount);
        y = AIUtilities.getInstance().nextRandomInt(nodeCount);
      }

      getNodeList().add(new SimpleNode<T>("n" + i, GraphUtilities.copyListToNewList(domain), x, y));
      if (DEBUG) getInstrumentation().print("n" + i + ": " + x + "," + y);
    }
/*
    getNodeList().add(new SimpleNode<T>("n0", GraphUtilities.copyListToNewList(domain), 3, 4));
    getNodeList().add(new SimpleNode<T>("n1", GraphUtilities.copyListToNewList(domain), 1, 0));
    getNodeList().add(new SimpleNode<T>("n2", GraphUtilities.copyListToNewList(domain), 1, 4));
    getNodeList().add(new SimpleNode<T>("n3", GraphUtilities.copyListToNewList(domain), 3, 2));
    getNodeList().add(new SimpleNode<T>("n4", GraphUtilities.copyListToNewList(domain), 1, 2));
    getNodeList().add(new SimpleNode<T>("n5", GraphUtilities.copyListToNewList(domain), 4, 2));
*/
    int failureCount = 0;
    boolean allFail;

    // Generate edges between the nodes
    while (failureCount < getNodeList().size()) {
      // If the current node failed to add a line, increment the failure count
      if (! createEdgeFromNode(AIUtilities.getInstance().nextRandomInt(getNodeList().size()))) {
        failureCount++;
        if (DEBUG) getInstrumentation().print("Incremented failureCount to " + failureCount);
      }

      // If the failure count reaches the node count, do an in-order pass through of all the nodes
      // not in the completed node cache and attempt to draw missing lines.  For any node which has
      // no remaining lines, add to completed node cache.
      if (failureCount >= getNodeList().size()) {
        allFail = true;
        if (DEBUG) getInstrumentation().print("IN FAILURE MODE!!!!");

        for (int i = 0; i < getNodeList().size(); i++) {
          if (createEdgeFromNode(i)) {
            allFail = false;
          }
        }

        // If all nodes failed to add a line, then we're done, else reset the
        // failure count and resume normal iteration.
        if (allFail) {
          if (DEBUG) getInstrumentation().print("ALL FAIL REACHED!!!!");
          break;
        } else {
          failureCount = 0;
        }
      }
    }

    return getNodeList();
  }

  private boolean doesNodeExistAtPoint(int x, int y) {
    for (SimpleNode<T> node : getNodeList()) {
      if (node.getX() == x && node.getY() == y) {
        return true;
      }
    }

    return false;
  }

  private boolean createEdgeFromNode(int nodeIndex) {
    if (completedNodeCache.contains(nodeIndex)) { return false; }

    SimpleNode<T> fromNode = getNodeList().get(nodeIndex);
    LinkedHashMap<Double, Double> sortedDistanceMap = getNearestDisconnectedNeighbors(fromNode);
    boolean thisSucceed = false;
    SimpleNode<T> toNode;

    if (DEBUG) getInstrumentation().print("  Examining " + fromNode.getName());

    // Iterate through nearest disconnect neighbors and attempt to draw lines
    for (Double d : sortedDistanceMap.keySet()) {
      toNode = getNodeList().get(d.intValue());

      // If line is okay to be drawn, add the edges to each node and reset the failure count
      if (! doesEdgeIntersect(fromNode, toNode)) {
        GraphUtilities.addReciprocalEdges(fromNode, toNode);
        thisSucceed = true;
        if (DEBUG) getInstrumentation().print(fromNode.getName() + ": " + fromNode.getX() + "," + fromNode.getY() + "  " +
                                              toNode.getName() + ": " + toNode.getX() + "," + toNode.getY());
        break;
      }
    }

    // If the current node failed to add a line, add it to the completed node cache.
    if (! thisSucceed) {
      completedNodeCache.add(nodeIndex);
      if (DEBUG) getInstrumentation().print("  Added " + fromNode.getName() + " to completed node cache.");
    }
            
    return thisSucceed;
  }

  private LinkedHashMap<Double, Double> getNearestDisconnectedNeighbors(SimpleNode<T> fromNode) {
    Map<Double, Double> distanceMap = new HashMap<Double, Double>();

    for (int i = 0; i < getNodeList().size(); i++) {
      if (! fromNode.isConnectedToNode(getNodeList().get(i))) {
        distanceMap.put((double) i, fromNode.getDistanceToNode(getNodeList().get(i)));
        if (DEBUG) getInstrumentation().print("  Add to DistMap for " + fromNode.getName() + ": n" + (int) i + ", " +
                                              fromNode.getDistanceToNode(getNodeList().get(i)));
      }
    }

    LinkedHashMap<Double, Double> sortedDistanceMap = GraphUtilities.sortMapByValuesWithDuplicates(distanceMap, false);
    return sortedDistanceMap;
  }

  // From Cormen, Introduction to Algorithms: Determining whether two line segments intersect
  private boolean doesEdgeIntersect(SimpleNode<T> fromNode, SimpleNode<T> toNode) {
    // For fromNode bounding box ONLY!  Do not use for straddle.
    int x1 = Math.min(fromNode.getX(), toNode.getX());
    int x2 = Math.max(fromNode.getX(), toNode.getX());
    int y1 = Math.min(fromNode.getY(), toNode.getY());
    int y2 = Math.max(fromNode.getY(), toNode.getY());
    int x3, x4, y3, y4;
    boolean intersect = false;

    // Cache of checked nodes
    Set<String> edgeCache = new HashSet<String>();

    for (SimpleNode<T> itrFromNode : getNodeList()) {
      // No need to check edges of either the fromNode or toNode
      if (itrFromNode.getName().equals(fromNode.getName()) || itrFromNode.getName().equals(toNode.getName())) { continue; }

      for (SimpleEdge<T> edge : itrFromNode.getEdgeList()) {
        // If already checked opposite direction, can skip
        if (! edgeCache.contains(edge.getToNode().getName() + "|" + itrFromNode.getName())) {
            edgeCache.add(itrFromNode.getName() + "|" + edge.getToNode().getName());

          x3 = Math.min(itrFromNode.getX(), edge.getToNode().getX());
          x4 = Math.max(itrFromNode.getX(), edge.getToNode().getX());
          y3 = Math.min(itrFromNode.getY(), edge.getToNode().getY());
          y4 = Math.max(itrFromNode.getY(), edge.getToNode().getY());

          // Attempt quick rejection based on bounding box
          if (x2 >= x3 && x4 >= x1 && y2 >= y3 && y4 >= y1) {
            if (DEBUG) getInstrumentation().print("BoundingBox intersect for " + fromNode.getName() + "-" + toNode.getName() + " & " +
                                                  itrFromNode.getName() + "-" + edge.getToNode().getName());
            intersect = doEdgesStraddle(fromNode.getX(), fromNode.getY(), toNode.getX(), toNode.getY(),
                                        itrFromNode.getX(), itrFromNode.getY(), edge.getToNode().getX(), edge.getToNode().getY());            
          }
        }

        if (intersect) { return intersect; }
      }
    }

    return intersect;
  }

  private boolean doEdgesStraddle(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
    // Determine cross-products
    int p3 = ((x2 - x1) * (y3 - y1)) - ((y2 - y1) * (x3 - x1));
    int p4 = ((x2 - x1) * (y4 - y1)) - ((y2 - y1) * (x4 - x1));
    if (DEBUG) getInstrumentation().print("p3=" + p3 + ", p4=" + p4);

    // If signs are different then segments straddle
    if ((p3 < 0 && p4 > 0) || (p3 > 0 && p4 < 0)) {
      return true;

    // In the case where both are 0, then segments are collinear and may either overlap or join
    } else if (p3 == 0 && p4 == 0) {
      // Find common point (designated B) and other endpoints (A, C)
      int xB = 0, yB = 0, xA = 0, yA = 0, xC = 0, yC = 0;

      if (x1 == x3 && y1 == y3) {
        xB = x1; yB = y1;
        xA = x2; yA = y2;
        xC = x4; yC = y4;
      } else if (x1 == x4 && y1 == y4) {
        xB = x1; yB = y1;
        xA = x2; yA = y2;
        xC = x3; yC = y3;
      } else if (x2 == x3 && y2 == y3) {
        xB = x2; yB = y2;
        xA = x1; yA = y1;
        xC = x4; yC = y4;
      } else if (x2 == x4 && y2 == y4) {
        xB = x2; yB = y2;
        xA = x1; yA = y1;
        xC = x3; yC = y3;
      }
      if (DEBUG) getInstrumentation().print("A:" + xA + "," + yA + "  B:" + xB + "," + yB + "  C:" + xC + "," + yC);

      // If values for A < B < C, then in order and segments join at B, but don't overlap
      // Vertically overlapped
      if (xA == xB) {
        if (Math.min(yA, yC) < yB && Math.max(yA, yC) > yB) {
          return false;
        } else {
          return true;
        }

      // Horizontally overlapped
      } else if (yA == yB) {
        if (Math.min(xA, xC) < xB && Math.max(xA, xC) > xB) {
          return false;
        } else {
          return true;
        }
      }

      // Something unexpected occurred here
      return true;

    // If signs are non-zero and identical, then segments do not straddle
    } else {
      return false;
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Instrumentation instr = new Instrumentation(true, System.out);
    RandomMapCreator<ThreeColor> rmc = new RandomMapCreator<ThreeColor>(6, instr);
    rmc.createRandomGraph(Arrays.asList(ThreeColor.values()));
  }
}
