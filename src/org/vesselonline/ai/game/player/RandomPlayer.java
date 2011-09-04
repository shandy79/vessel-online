package org.vesselonline.ai.game.player;

import org.vesselonline.ai.game.Board;
import org.vesselonline.ai.util.AIUtilities;

public class RandomPlayer implements Player {
  private char id;

  public RandomPlayer(char id) {
    this.id = id;
  }

  @Override
  public char getID() { return id; }

  @Override
  public String selectMove(Board board, Player nextPlayer) {
    char[] sideAry = {'T', 'R', 'B', 'L'};
    String move = "" + sideAry[AIUtilities.getInstance().nextRandomInt(sideAry.length)] + AIUtilities.getInstance().nextRandomInt(board.getBoardSize());

    System.out.println("\nPlayer " + getID() + " choose a move:  " + move);
    return move;
  }

  @Override
  public String toString() {
    return "# Player " + getID() + " was Random";
  }
}
