package org.vesselonline.ai.game.player;

import org.vesselonline.ai.game.Board;
import org.vesselonline.ai.game.GameAlgorithms;
import org.vesselonline.ai.graph.ComparableNode;
import org.vesselonline.ai.util.Instrumentable;
import org.vesselonline.ai.util.Instrumentation;

public class MinimaxPlayer implements Player, Instrumentable {
  private char id;
  private int depth;
  private Instrumentation instrumentation;

  public MinimaxPlayer(char id, int depth, Instrumentation instrumentation) {
    this.id = id;
    this.depth = depth;
    this.instrumentation = instrumentation;
  }

  @Override
  public char getID() { return id; }

  @Override
  public String selectMove(Board board, Player nextPlayer) {
    long startTime = System.currentTimeMillis();
    ComparableNode<Integer> gameTree = GameAlgorithms.getGameTree(board, getDepth(), this, nextPlayer, getInstrumentation());
    ComparableNode<Integer> maxNode = GameAlgorithms.minimax(gameTree, getDepth(), "max");
    long endTime = System.currentTimeMillis();

    String move = maxNode.getName().split(":")[1];
    System.out.println("\nPlayer " + getID() + " choose a move:  " + move);

    getInstrumentation().addCPUTime(endTime - startTime);
    getInstrumentation().addWorkUnits(1);

    return move;
  }

  @Override
  public String toString() {
    return "# Player " + getID() + " was Minimax";
  }

  @Override
  public Instrumentation getInstrumentation() { return instrumentation; }
  @Override
  public void setInstrumentation(Instrumentation instrumentation) { this.instrumentation = instrumentation; }

  public int getDepth() { return depth; }
}
