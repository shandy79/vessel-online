package org.vesselonline.ai.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.vesselonline.ai.game.player.Player;
import org.vesselonline.ai.graph.ComparableNode;
import org.vesselonline.ai.graph.SimpleEdge;
import org.vesselonline.ai.util.Instrumentation;

public class GameAlgorithms {
  private static final int WINNING_SCORE = 500;
  private static final int LOSING_SCORE = -500;
  private static final int NEAR_STRAIGHT_WEIGHT = 5;
  private static final int STRONG_SPOT_WEIGHT = 2;
  private static final int MAX_NEAR_DISTANCE = 1;

  private static Player MAX_PLAYER;
  private static Player MIN_PLAYER;
  private static Instrumentation TRACE;

  public static final ComparableNode<Integer> getGameTree(Board board, int maxPly, Player maxPlayer, Player minPlayer, Instrumentation trace) {
    MAX_PLAYER = maxPlayer;
    MIN_PLAYER = minPlayer;
    TRACE = trace;

    ComparableNode<Integer> gameTreeNode = buildGameTree(board, new ComparableNode<Integer>("MAX"), maxPly, MAX_PLAYER, MIN_PLAYER);
//gameTreeNode.printNodeContents(0);
    return gameTreeNode;
  }

  private static final ComparableNode<Integer> buildGameTree(Board board, ComparableNode<Integer> node, int maxPly,
                                                             Player thisPlayer, Player nextPlayer) {
//    TRACE.print("[buildGameTree]  Node: " + node.getName() + "|" + node.getValue() + ", Depth: " + maxPly + ", Player: " + thisPlayer.getID());

    // If we've reached the leaf node level, simply evaluate the node and return
    if (maxPly == 0) {
      node.setValue(evaluateBoard(board, thisPlayer));
//      TRACE.print("[buildGameTree]  Evaluated " + node.getName() + ": " + node.getValue());
      return node;
    }

    char[] sideAry = {'T', 'R', 'B', 'L'};
    Board evalBoard;
    ComparableNode<Integer> childNode;
    boolean result;

    for (int i = 0; i < sideAry.length; i++) {
      for (int j = 0; j < board.getBoardSize(); j++) {
        evalBoard = new Board(board);
        result = evalBoard.addMove(thisPlayer, "" + sideAry[i] + j);

        // If we can add the move legally, then we first determine if the board
        // is in a winning or losing state.  We assign max or min scores for winning or losing states.
        // At every evaluation, we add the move to the game tree that we are constructing.
        if (result) {
          childNode = new ComparableNode<Integer>(node.getName() + ":" + sideAry[i] + j);
          node.addEdge(new SimpleEdge<Integer>(childNode));
          TRACE.addSecondaryWorkUnits(1);

          if (! evalBoard.canGameContinue(thisPlayer, false)) {
            childNode.setValue(getEndOfGameScore(evalBoard));
          } else {
            buildGameTree(evalBoard, childNode, maxPly - 1, nextPlayer, thisPlayer);
          }
        }
      }
    }

    return node;
  }

  private static final int evaluateBoard(Board board, Player thisPlayer) {
    int score = 0;

    if (! board.canGameContinue(thisPlayer, false)) {
      score = getEndOfGameScore(board);
    } else {
      score += board.getNearStraightScore(MAX_NEAR_DISTANCE, MAX_PLAYER, MIN_PLAYER) * NEAR_STRAIGHT_WEIGHT;
      score += board.getStrongSpotScore(MAX_PLAYER, MIN_PLAYER) * STRONG_SPOT_WEIGHT;
    }
    return score;
  }

  private static final int getEndOfGameScore(Board board) {
    Player winner = null;
    int score = 0;

    if (board.getWinner() == MAX_PLAYER.getID()) {
      winner = MAX_PLAYER;
    } else if (board.getWinner() == MIN_PLAYER.getID()) {
      winner = MIN_PLAYER;
    } else if (board.getLoser() == MAX_PLAYER.getID()) {
      winner = MIN_PLAYER;
    } else if (board.getLoser() == MIN_PLAYER.getID()) {
      winner = MAX_PLAYER;
    }

    if (winner != null) {
      if (MAX_PLAYER.getID() == winner.getID()) {
        score = WINNING_SCORE;
      } else if (MIN_PLAYER.getID() == winner.getID()) {
        score = LOSING_SCORE;
      }
    }

    return score;
  }

  public static final <T extends Comparable<T>> ComparableNode<T> minimax(ComparableNode<T> node, int maxPly, String minOrMax) {
    if (maxPly == 0 || node.getDegree() == 0) {
      return node;
    }
    TRACE.print("[minimax (" + minOrMax + ")]  Node: " + node.getName() + "|" + node.getValue());

    List<ComparableNode<T>> childNodeList = new ArrayList<ComparableNode<T>>();
    ComparableNode<T> selectedNode = null;

    for (SimpleEdge<T> edge : node.getEdgeList()) {
      TRACE.print("[minimax (" + minOrMax + ")]  Examining " + edge.getToNode().getName() + "|" + edge.getToNode().getValue());

      if (minOrMax.equals("min")) {
        childNodeList.add(minimax((ComparableNode<T>) edge.getToNode(), maxPly - 1, "max"));
      } else if (minOrMax.equals("max")) {
        childNodeList.add(minimax((ComparableNode<T>) edge.getToNode(), maxPly - 1, "min"));
      }
    }

//    Collections.sort(childNodeList);

    if (minOrMax.equals("min")) {
selectedNode = Collections.min(childNodeList);
//      selectedNode = childNodeList.get(0);
    } else if (minOrMax.equals("max")) {
selectedNode = Collections.max(childNodeList);
//      selectedNode = childNodeList.get(childNodeList.size() - 1);
    }

    TRACE.print("[minimax (" + minOrMax + ")]  Selected: " + selectedNode.getName() + "|" + selectedNode.getValue());
    node.setValue(selectedNode.getValue());
    return selectedNode;
  }

  public static final ComparableNode<Integer> alphaBeta(Board board, int maxPly, Player maxPlayer, Player minPlayer, Instrumentation trace) {
    MAX_PLAYER = maxPlayer;
    MIN_PLAYER = minPlayer;
    TRACE = trace;

    ComparableNode<Integer> alpha = new ComparableNode<Integer>("alpha");
    ComparableNode<Integer> beta = new ComparableNode<Integer>("beta");    
    alpha.setValue(Integer.MIN_VALUE);
    beta.setValue(Integer.MAX_VALUE);

    ComparableNode<Integer> maxValueNode = maxValue(board, new ComparableNode<Integer>("MAX"), maxPly, MAX_PLAYER, MIN_PLAYER, alpha, beta);
//maxValueNode.printNodeContents(0);
    return maxValueNode;
  }

  private static final ComparableNode<Integer> maxValue(Board board, ComparableNode<Integer> node, int maxPly, Player thisPlayer,
          Player nextPlayer, ComparableNode<Integer> alpha, ComparableNode<Integer> beta) {
    TRACE.print("[maxValue]  Node: " + node.getName() + "|" + node.getValue() + ", Depth: " + maxPly +
                ", Player: " + thisPlayer.getID() + ", Alpha: " + alpha.getValue() + ", Beta: " + beta.getValue());

    // If we've reached the leaf node level, simply evaluate the node and return
    if (maxPly == 0 || (node.getValue() != null && 
            (node.getValue().intValue() == WINNING_SCORE || node.getValue().intValue() == LOSING_SCORE))) {
      if (node.getValue() == null || (node.getValue().intValue() != WINNING_SCORE && node.getValue().intValue() != LOSING_SCORE)) {
        node.setValue(evaluateBoard(board, thisPlayer));
      }
      TRACE.print("[maxValue]  Evaluated " + node.getName() + ": " + node.getValue());
      return node;
    }

    char[] sideAry = {'T', 'R', 'B', 'L'};
    Board evalBoard;
    ComparableNode<Integer> childNode, selectedNode = null;
    boolean result;

    for (int i = 0; i < sideAry.length; i++) {
      for (int j = 0; j < board.getBoardSize(); j++) {
        evalBoard = new Board(board);
        result = evalBoard.addMove(thisPlayer, "" + sideAry[i] + j);

        // If we can add the move legally, then we first determine if the board
        // is in a winning or losing state.  We assign max or min scores for winning or losing states.
        // At every evaluation, we add the move to the game tree that we are constructing.
        if (result) {
          childNode = new ComparableNode<Integer>(node.getName() + ":" + sideAry[i] + j);
          node.addEdge(new SimpleEdge<Integer>(childNode));
          TRACE.addSecondaryWorkUnits(1);

          if (! evalBoard.canGameContinue(thisPlayer, false)) {
            childNode.setValue(getEndOfGameScore(evalBoard));
          } else {
            minValue(evalBoard, childNode, maxPly - 1, nextPlayer, thisPlayer, alpha, beta);
          }

          if (selectedNode == null || childNode.compareTo(selectedNode) > 0) {
            selectedNode = childNode;
          }

          TRACE.print("[maxValue]  Selected: " + selectedNode.getName() + "|" + selectedNode.getValue() +
                      ", Alpha:" + alpha.getValue() + ", Beta:" + beta.getValue());

          if (selectedNode.compareTo(beta) >= 0) {
            node.setName(selectedNode.getName());
            node.setValue(selectedNode.getValue());
            return node;
          }

          if (selectedNode.compareTo(alpha) > 0) {
            alpha = selectedNode;
          }
        }
      }
    }

    node.setName(selectedNode.getName());
    node.setValue(selectedNode.getValue());
    return node;
  }

  private static final ComparableNode<Integer> minValue(Board board, ComparableNode<Integer> node, int maxPly, Player thisPlayer,
          Player nextPlayer, ComparableNode<Integer> alpha, ComparableNode<Integer> beta) {
    TRACE.print("[minValue]  Node: " + node.getName() + "|" + node.getValue() + ", Depth: " + maxPly +
                ", Player: " + thisPlayer.getID() + ", Alpha: " + alpha.getValue() + ", Beta: " + beta.getValue());

    // If we've reached the leaf node level, simply evaluate the node and return
    if (maxPly == 0 || (node.getValue() != null && 
            (node.getValue().intValue() == WINNING_SCORE || node.getValue().intValue() == LOSING_SCORE))) {
      if (node.getValue() == null || (node.getValue().intValue() != WINNING_SCORE && node.getValue().intValue() != LOSING_SCORE)) {
        node.setValue(evaluateBoard(board, thisPlayer));
      }
      TRACE.print("[minValue]  Evaluated " + node.getName() + ": " + node.getValue());
      return node;
    }

    char[] sideAry = {'T', 'R', 'B', 'L'};
    Board evalBoard;
    ComparableNode<Integer> childNode, selectedNode = null;
    boolean result;

    for (int i = 0; i < sideAry.length; i++) {
      for (int j = 0; j < board.getBoardSize(); j++) {
        evalBoard = new Board(board);
        result = evalBoard.addMove(thisPlayer, "" + sideAry[i] + j);

        // If we can add the move legally, then we first determine if the board
        // is in a winning or losing state.  We assign max or min scores for winning or losing states.
        // At every evaluation, we add the move to the game tree that we are constructing.
        if (result) {
          childNode = new ComparableNode<Integer>(node.getName() + ":" + sideAry[i] + j);
          node.addEdge(new SimpleEdge<Integer>(childNode));
          TRACE.addSecondaryWorkUnits(1);

          if (! evalBoard.canGameContinue(thisPlayer, false)) {
            childNode.setValue(getEndOfGameScore(evalBoard));
          } else {
            maxValue(evalBoard, childNode, maxPly - 1, nextPlayer, thisPlayer, alpha, beta);
          }

          if (selectedNode == null || childNode.compareTo(selectedNode) < 0) {
            selectedNode = childNode;
          }

          TRACE.print("[minValue]  Selected: " + selectedNode.getName() + "|" + selectedNode.getValue() +
                      ", Alpha:" + alpha.getValue() + ", Beta:" + beta.getValue());

          if (selectedNode.compareTo(alpha) <= 0) {
            node.setName(selectedNode.getName());
            node.setValue(selectedNode.getValue());
            return node;
          }

          if (selectedNode.compareTo(beta) < 0) {
            beta = selectedNode;
          }
        }
      }
    }

    node.setName(selectedNode.getName());
    node.setValue(selectedNode.getValue());
    return node;
  }
}
