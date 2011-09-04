package org.vesselonline.ai.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.vesselonline.ai.game.player.HumanPlayer;
import org.vesselonline.ai.game.player.Player;
import org.vesselonline.ai.graph.ComparableNode;
import org.vesselonline.ai.graph.SimpleEdge;
import org.vesselonline.ai.util.Instrumentation;

public class GameDebug {
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
_node_name = node.getName();
//System.out.println("At node " + node.getName() + " at depth " + maxPly + ", player " + thisPlayer.getID());
    // If we've reached the leaf node level, simply evaluate the node and return
    if (maxPly == 0) {
//System.out.println("  Evaluating " + node.getName());
      node.setValue(evaluateBoard(board, thisPlayer));
      return node;
    }

    char[] sideAry = {'T', 'R', 'B', 'L'};
    Board evalBoard;
    ComparableNode<Integer> childNode;
    boolean result;

//int i = 0; //    for (int i = 0; i < sideAry.length; i++) {
    for (int i = 0; i < sideAry.length; i++) {
      for (int j = 0; j < board.getBoardSize(); j++) {
        evalBoard = new Board(board);
        result = evalBoard.addMove(thisPlayer, "" + sideAry[i] + j);

        // If we can add the move legally, then we first determine if the board
        // is in a winning or losing state.  We assign max or min scores for winning or losing states.
        //
        // At every evaluation, we add the move to the game tree that we are constructing.
        if (result) {
          childNode = new ComparableNode<Integer>(node.getName() + ":" + sideAry[i] + j);
          node.addEdge(new SimpleEdge<Integer>(childNode));
          TRACE.addSecondaryWorkUnits(1);

//if (false) { //          if (! evalBoard.canGameContinue(thisPlayer, false)) {
          if (! evalBoard.canGameContinue(thisPlayer, false)) {
            childNode.setValue(getEndOfGameScore(evalBoard));
//evalBoard.print();
//System.out.println("Game Over 1!");
            continue;
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
//board.print();
//System.out.println("Game Over 2");
    } else {
      score += board.getNearStraightScore(MAX_NEAR_DISTANCE, MAX_PLAYER, MIN_PLAYER) * NEAR_STRAIGHT_WEIGHT;
      score += board.getStrongSpotScore(MAX_PLAYER, MIN_PLAYER) * STRONG_SPOT_WEIGHT;
    }
//board.print();
//System.out.println("Score: " + score);
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
//System.out.println(minOrMax + "  Node: " + node.getName() + "|" + node.getValue());

    List<ComparableNode<T>> childNodeList = new ArrayList<ComparableNode<T>>();
    ComparableNode<T> selectedNode = null;

    for (SimpleEdge<T> edge : node.getEdgeList()) {
//System.out.println("Examining " + edge.getToNode().getName() + "|" + edge.getToNode().getValue());

      if (minOrMax.equals("min")) {
        childNodeList.add(minimax((ComparableNode<T>) edge.getToNode(), maxPly - 1, "max"));
      } else if (minOrMax.equals("max")) {
        childNodeList.add(minimax((ComparableNode<T>) edge.getToNode(), maxPly - 1, "min"));
      }
    }

    Collections.sort(childNodeList);

    if (minOrMax.equals("min")) {
      selectedNode = childNodeList.get(0);
    } else if (minOrMax.equals("max")) {
      selectedNode = childNodeList.get(childNodeList.size() - 1);
    }

//System.out.println("Selected: " + selectedNode.getName() + "|" + selectedNode.getValue());
    node.setValue(selectedNode.getValue());
    return selectedNode;
  }

  private static final ComparableNode<Integer> oldMaxValue(ComparableNode<Integer> node, ComparableNode<Integer> alpha,
                                                        ComparableNode<Integer> beta, int maxPly, Board board,
                                                        Player thisPlayer, Player nextPlayer) {
_node_name = node.getName();
    if (maxPly == 0) {
      node.setValue(evaluateBoard(board, thisPlayer));
//System.out.println("  Evaluated " + node.getName() + ": " + node.getValue());
      return node;
    }
//System.out.println("MAX  Node: " + node.getName() + "|" + node.getValue());
    //System.out.print("  Alpha: " + alpha.getName() + "|" + alpha.getValue());
    //System.out.println("  Beta: " + beta.getName() + "|" + beta.getValue());

    char[] sideAry = {'T', 'R', 'B', 'L'};
    Board evalBoard;
    boolean result;

    ComparableNode<Integer> childNode, selectedNode = null;

//int i = 0; //    for (int i = 0; i < sideAry.length; i++) {
    for (int i = 0; i < sideAry.length; i++) {
      for (int j = 0; j < board.getBoardSize(); j++) {
        evalBoard = new Board(board);
        result = evalBoard.addMove(thisPlayer, "" + sideAry[i] + j);

        // If we can add the move legally, then we first determine if the board
        // is in a winning or losing state.  We assign max or min scores for winning or losing states.
        //
        // At every evaluation, we add the move to the game tree that we are constructing.
        if (result) {
          childNode = new ComparableNode<Integer>(node.getName() + ":" + sideAry[i] + j);
          node.addEdge(new SimpleEdge<Integer>(childNode));
          TRACE.addSecondaryWorkUnits(1);

//if (false) { //! evalBoard.canGameContinue(thisPlayer, false)) {
          if (! evalBoard.canGameContinue(thisPlayer, false)) {
            childNode.setValue(getEndOfGameScore(evalBoard));
//evalBoard.print();
//System.out.println("Game Over 1!");
          } else {
//            minValue(childNode, alpha, beta, maxPly - 1, evalBoard, nextPlayer, thisPlayer);
          }

          if (selectedNode == null || childNode.compareTo(selectedNode) > 0) {
            selectedNode = childNode;
          }

//System.out.println("Selected: " + selectedNode.getName() + "|" + selectedNode.getValue());
      //System.out.println("  a:" + selectedNode.compareTo(alpha) + " b:" + selectedNode.compareTo(beta));
          if (selectedNode.compareTo(beta) >= 0) {
            node.setValue(selectedNode.getValue());
            return node;
          }

          if (selectedNode.compareTo(alpha) > 0) {
            alpha = selectedNode;
          }
        }
      }
    }
 
    node.setValue(selectedNode.getValue());
    return node;
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
maxValueNode.printNodeContents(0);
    return maxValueNode;
  }

  private static final ComparableNode<Integer> maxValue(Board board, ComparableNode<Integer> node, int maxPly, Player thisPlayer,
                                                        Player nextPlayer, ComparableNode<Integer> alpha, ComparableNode<Integer> beta) {
    TRACE.print("[maxValue]  Node: " + node.getName() + "|" + node.getValue() + ", Depth: " + maxPly +
                ", Player: " + thisPlayer.getID() + ", Alpha: " + alpha.getValue() + ", Beta: " + beta.getValue());
_node_name = node.getName();
    // If we've reached the leaf node level, simply evaluate the node and return
    if (maxPly == 0) {
      node.setValue(newEvaluateBoard(board, thisPlayer));
      TRACE.print("[maxValue]  Evaluated " + node.getName() + ": " + node.getValue());
      return node;
    }

    char[] sideAry = {'T', 'R', 'B', 'L'};
    Board evalBoard;
    ComparableNode<Integer> childNode, selectedNode = null;
    boolean result;

int i = 0; //    for (int i = 0; i < sideAry.length; i++) {
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

if (false) { //          if (! evalBoard.canGameContinue(thisPlayer, false)) {
            childNode.setValue(getEndOfGameScore(evalBoard));
          } else {
            minValue(evalBoard, childNode, maxPly - 1, nextPlayer, thisPlayer, alpha, beta);
          }

          if (selectedNode == null || childNode.compareTo(selectedNode) > 0) {
System.out.println("Selected Node value updated to: " + childNode.getValue());
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
System.out.println("Alpha Node value updated to: " + selectedNode.getValue());
            alpha = selectedNode;
          }
        }
      }
//    }

    node.setName(selectedNode.getName());
    node.setValue(selectedNode.getValue());
    return node;
  }

  private static final ComparableNode<Integer> minValue(Board board, ComparableNode<Integer> node, int maxPly, Player thisPlayer,
                                                        Player nextPlayer, ComparableNode<Integer> alpha, ComparableNode<Integer> beta) {
    TRACE.print("[minValue]  Node: " + node.getName() + "|" + node.getValue() + ", Depth: " + maxPly +
                ", Player: " + thisPlayer.getID() + ", Alpha: " + alpha.getValue() + ", Beta: " + beta.getValue());
_node_name = node.getName();
    // If we've reached the leaf node level, simply evaluate the node and return
    if (maxPly == 0) {
      node.setValue(newEvaluateBoard(board, thisPlayer));
      TRACE.print("[minValue]  Evaluated " + node.getName() + ": " + node.getValue());
      return node;
    }

    char[] sideAry = {'T', 'R', 'B', 'L'};
    Board evalBoard;
    ComparableNode<Integer> childNode, selectedNode = null;
    boolean result;

int i = 0; //    for (int i = 0; i < sideAry.length; i++) {
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

if (false) { //          if (! evalBoard.canGameContinue(thisPlayer, false)) {
            childNode.setValue(getEndOfGameScore(evalBoard));
          } else {
            maxValue(evalBoard, childNode, maxPly - 1, nextPlayer, thisPlayer, alpha, beta);
          }

          if (selectedNode == null || childNode.compareTo(selectedNode) < 0) {
System.out.println("Selected Node value updated to: " + childNode.getValue());
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
System.out.println("Beta Node value updated to: " + selectedNode.getValue());
            beta = selectedNode;
          }
        }
      }
//    }

    node.setName(selectedNode.getName());
    node.setValue(selectedNode.getValue());
    return node;
  }

  private static String _node_name;

  private static final int newEvaluateBoard(Board board, Player thisPlayer) {
    int score = 0;

    if (_node_name.equals("MAX:T0:T0:T0:T0")) {
      score = 61;
    } else if (_node_name.equals("MAX:T0:T0:T0:T1")) {
      score = 47;
    } else if (_node_name.equals("MAX:T0:T0:T1:T0")) {
      score = 34;
    } else if (_node_name.equals("MAX:T0:T0:T1:T1")) {
      score = 44;
    } else if (_node_name.equals("MAX:T0:T1:T0:T0")) {
      score = 72;
    } else if (_node_name.equals("MAX:T0:T1:T0:T1")) {
      score = 67;
    } else if (_node_name.equals("MAX:T0:T1:T1:T0")) {
      score = 43;
    } else if (_node_name.equals("MAX:T0:T1:T1:T1")) {
      score = 44;
    } else if (_node_name.equals("MAX:T1:T0:T0:T0")) {
      score = 21;
    } else if (_node_name.equals("MAX:T1:T0:T0:T1")) {
      score = 45;
    } else if (_node_name.equals("MAX:T1:T0:T1:T0")) {
      score = 67;
    } else if (_node_name.equals("MAX:T1:T0:T1:T1")) {
      score = 14;
    } else if (_node_name.equals("MAX:T1:T1:T0:T0")) {
      score = 98;
    } else if (_node_name.equals("MAX:T1:T1:T0:T1")) {
      score = 45;
    } else if (_node_name.equals("MAX:T1:T1:T1:T0")) {
      score = 23;
    } else if (_node_name.equals("MAX:T1:T1:T1:T1")) {
      score = 78;
    }

    return score;
  }


  /**
   * @param args
   */
  public static void main(String[] args) {
    ComparableNode<Double> max = new ComparableNode<Double>("max");

    ComparableNode<Double> l = new ComparableNode<Double>("l");
    ComparableNode<Double> r = new ComparableNode<Double>("r");

    max.addEdge(new SimpleEdge<Double>(l));
    max.addEdge(new SimpleEdge<Double>(r));

    ComparableNode<Double> ll = new ComparableNode<Double>("l:l");
    ComparableNode<Double> lr = new ComparableNode<Double>("l:r");

    l.addEdge(new SimpleEdge<Double>(ll));
    l.addEdge(new SimpleEdge<Double>(lr));

    ComparableNode<Double> rl = new ComparableNode<Double>("r:l");
    ComparableNode<Double> rr = new ComparableNode<Double>("r:r");

    r.addEdge(new SimpleEdge<Double>(rl));
    r.addEdge(new SimpleEdge<Double>(rr));

    ComparableNode<Double> lll = new ComparableNode<Double>("l:l:l");
    ComparableNode<Double> llr = new ComparableNode<Double>("l:l:r");
    
    ll.addEdge(new SimpleEdge<Double>(lll));
    ll.addEdge(new SimpleEdge<Double>(llr));

    ComparableNode<Double> lrl = new ComparableNode<Double>("l:r:l");
    ComparableNode<Double> lrr = new ComparableNode<Double>("l:r:r");

    lr.addEdge(new SimpleEdge<Double>(lrl));
    lr.addEdge(new SimpleEdge<Double>(lrr));

    ComparableNode<Double> rll = new ComparableNode<Double>("r:l:l");
    ComparableNode<Double> rlr = new ComparableNode<Double>("r:l:r");

    rl.addEdge(new SimpleEdge<Double>(rll));
    rl.addEdge(new SimpleEdge<Double>(rlr));

    ComparableNode<Double> rrl = new ComparableNode<Double>("r:r:l");
    ComparableNode<Double> rrr = new ComparableNode<Double>("r:r:r");

    rr.addEdge(new SimpleEdge<Double>(rrl));
    rr.addEdge(new SimpleEdge<Double>(rrr));

    ComparableNode<Double> llll = new ComparableNode<Double>("l:l:l:l");
    ComparableNode<Double> lllr = new ComparableNode<Double>("l:l:l:r");
    
    lll.addEdge(new SimpleEdge<Double>(llll));
    lll.addEdge(new SimpleEdge<Double>(lllr));

    ComparableNode<Double> llrl = new ComparableNode<Double>("l:l:r:l");
    ComparableNode<Double> llrr = new ComparableNode<Double>("l:l:r:r");
    
    llr.addEdge(new SimpleEdge<Double>(llrl));
    llr.addEdge(new SimpleEdge<Double>(llrr));

    ComparableNode<Double> lrll = new ComparableNode<Double>("l:r:l:l");
    ComparableNode<Double> lrlr = new ComparableNode<Double>("l:r:l:r");

    lrl.addEdge(new SimpleEdge<Double>(lrll));
    lrl.addEdge(new SimpleEdge<Double>(lrlr));

    ComparableNode<Double> lrrl = new ComparableNode<Double>("l:r:r:l");
    ComparableNode<Double> lrrr = new ComparableNode<Double>("l:r:r:r");

    lrr.addEdge(new SimpleEdge<Double>(lrrl));
    lrr.addEdge(new SimpleEdge<Double>(lrrr));

    ComparableNode<Double> rlll = new ComparableNode<Double>("r:l:l:l");
    ComparableNode<Double> rllr = new ComparableNode<Double>("r:l:l:r");

    rll.addEdge(new SimpleEdge<Double>(rlll));
    rll.addEdge(new SimpleEdge<Double>(rllr));

    ComparableNode<Double> rlrl = new ComparableNode<Double>("r:l:r:l");
    ComparableNode<Double> rlrr = new ComparableNode<Double>("r:l:r:r");

    rlr.addEdge(new SimpleEdge<Double>(rlrl));
    rlr.addEdge(new SimpleEdge<Double>(rlrr));

    ComparableNode<Double> rrll = new ComparableNode<Double>("r:r:l:l");
    ComparableNode<Double> rrlr = new ComparableNode<Double>("r:r:l:r");

    rrl.addEdge(new SimpleEdge<Double>(rrll));
    rrl.addEdge(new SimpleEdge<Double>(rrlr));

    ComparableNode<Double> rrrl = new ComparableNode<Double>("r:r:r:l");
    ComparableNode<Double> rrrr = new ComparableNode<Double>("r:r:r:r");

    rrr.addEdge(new SimpleEdge<Double>(rrrl));
    rrr.addEdge(new SimpleEdge<Double>(rrrr));

    llll.setValue(47.0);
    lllr.setValue(61.0);

    llrl.setValue(34.0);
    llrr.setValue(44.0);

    lrll.setValue(72.0);
    lrlr.setValue(67.0);

    lrrl.setValue(43.0);
    lrrr.setValue(44.0);

    rlll.setValue(21.0);
    rllr.setValue(45.0);

    rlrl.setValue(67.0);
    rlrr.setValue(14.0);

    rrll.setValue(98.0);
    rrlr.setValue(45.0);

    rrrl.setValue(23.0);
    rrrr.setValue(78.0);

    // For testing ply-depth of 2
//    ll.setValue(47);
//    lr.setValue(67);

//    rl.setValue(21);
//    rr.setValue(44);

    // For testing ply-depth of 1
//    l.setValue(47);
//    r.setValue(21);
/*
    llll.setValue(23);
    lllr.setValue(16);

    llrl.setValue(43);
    llrr.setValue(44);

    lrll.setValue(89);
    lrlr.setValue(76);

    lrrl.setValue(34);
    lrrr.setValue(44);

    rlll.setValue(63);
    rllr.setValue(54);

    rlrl.setValue(51);
    rlrr.setValue(41);

    rrll.setValue(89);
    rrlr.setValue(54);

    rrrl.setValue(32);
    rrrr.setValue(87);

    // For testing ply-depth of 2
//    ll.setValue(43);
//    lr.setValue(76);

//    rl.setValue(54);
//    rr.setValue(44);

    // For testing ply-depth of 1
//    l.setValue(43);
//    r.setValue(54);
*/
//    ComparableNode<Integer> maxNode = minimax(max, 4, "max");
    ComparableNode<Integer> alphaBetaTree = alphaBeta(new Board(2), 4, new HumanPlayer('X'), new HumanPlayer('O'),
                                                      new Instrumentation(false, System.out));
    ComparableNode<Integer> maxNode = minimax(alphaBetaTree, 4, "max");
    System.out.println(alphaBetaTree.getName() + " = " + alphaBetaTree.getValue());
    System.out.println(maxNode.getName() + " = " + maxNode.getValue());
//    System.out.println(l.getValue() + " | " + r.getValue());
//    System.out.println(ll.getValue() + " | " + lr.getValue() + " | " + rl.getValue() + " | " + rr.getValue());
  }
}
