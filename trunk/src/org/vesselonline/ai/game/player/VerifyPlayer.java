package org.vesselonline.ai.game.player;

import java.util.ArrayList;
import java.util.List;
import org.vesselonline.ai.game.Board;

public class VerifyPlayer implements Player {
  private char id;
  private List<String> moveList;
  private int moveIdx;

  public VerifyPlayer(char id) {
    this.id = id;
    moveList = new ArrayList<String>();
    moveIdx = 0;
  }

  @Override
  public char getID() { return id; }

  @Override
  public String selectMove(Board board, Player nextPlayer) {
    if (moveIdx >= moveList.size()) { moveIdx = 0; }

    String move = moveList.get(moveIdx);
    moveIdx++;

    System.out.println("\nPlayer " + getID() + " choose a move:  " + move);
    return move;
  }

  @Override
  public String toString() {
    return "# Player " + getID() + " was Verify";
  }

  public void addMove(String move) { moveList.add(move); }
}
