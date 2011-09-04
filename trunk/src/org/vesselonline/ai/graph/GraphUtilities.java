package org.vesselonline.ai.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GraphUtilities {
  public static final <T> void addReciprocalEdges(SimpleNode<T> nodeOne, SimpleNode<T> nodeTwo) {
    nodeOne.addEdge(new SimpleEdge<T>(nodeTwo));
    nodeTwo.addEdge(new SimpleEdge<T>(nodeOne));
  }

  public static final <T> String generateNodeListStr(List<SimpleNode<T>> nodeList) {
    StringBuilder s = new StringBuilder();
    for (SimpleNode<T> node : nodeList) {
      s.append(node.getName() + ":" + node.getValue() + "  ");
    }
    return s.toString();
  }

  public static final <T> List<T> copyListToNewList(List<T> srcList) {
    if (srcList == null) { return null; }

    List<T> newList = new ArrayList<T>();
    for (T value : srcList) {
      newList.add(value);
    }
    return newList;
  }

  // Adapted from http://www.lampos.net/sort-hashmap
  public static final <T extends Comparable<? super T>> LinkedHashMap<T, T> sortMapByValuesWithDuplicates(Map<T, T> map, boolean reverse) {
    List<T> mapKeys = new ArrayList<T>(map.keySet());
    Collections.sort(mapKeys);

    List<T> mapValues = new ArrayList<T>(map.values());
    Collections.sort(mapValues);
    if (reverse) { Collections.reverse(mapValues); }

    LinkedHashMap<T, T> sortedMap = new LinkedHashMap<T, T>();

    Iterator<T> valueIt = mapValues.iterator();
    while (valueIt.hasNext()) {
      T val = valueIt.next();
      Iterator<T> keyIt = mapKeys.iterator();

      while (keyIt.hasNext()) {
        T key = keyIt.next();

        if (map.get(key).equals(val)){
          map.remove(key);
          mapKeys.remove(key);
          sortedMap.put(key, val);
          break;
        }
      }
    }

    return sortedMap;
  }

  /**
   * 
   * @param <T>
   * @param domain
   * @return
   */
  public static final <T> List<SimpleNode<T>> createAustraliaGraph(List<T> domain) {
    List<SimpleNode<T>> nodeList = new ArrayList<SimpleNode<T>>(7);

    nodeList.add(new SimpleNode<T>("WA", copyListToNewList(domain)));
    nodeList.add(new SimpleNode<T>("NT", copyListToNewList(domain)));
    nodeList.add(new SimpleNode<T>("SA", copyListToNewList(domain)));
    nodeList.add(new SimpleNode<T>("Q", copyListToNewList(domain)));
    nodeList.add(new SimpleNode<T>("NSW", copyListToNewList(domain)));
    nodeList.add(new SimpleNode<T>("V", copyListToNewList(domain)));
    nodeList.add(new SimpleNode<T>("T", copyListToNewList(domain)));

    addReciprocalEdges(nodeList.get(0), nodeList.get(1));
    addReciprocalEdges(nodeList.get(0), nodeList.get(2));
    addReciprocalEdges(nodeList.get(1), nodeList.get(2));
    addReciprocalEdges(nodeList.get(1), nodeList.get(3));
    addReciprocalEdges(nodeList.get(2), nodeList.get(3));
    addReciprocalEdges(nodeList.get(2), nodeList.get(4));
    addReciprocalEdges(nodeList.get(2), nodeList.get(5));
    addReciprocalEdges(nodeList.get(3), nodeList.get(4));
    addReciprocalEdges(nodeList.get(4), nodeList.get(5));

    return nodeList;
  }
}
