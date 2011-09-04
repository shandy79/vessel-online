package org.vesselonline.ai.game.player;

import java.util.Scanner;
import org.vesselonline.ai.game.Board;

public class HumanPlayer implements Player {
  private char id;
  private Scanner input;

  public HumanPlayer(char id) {
    this.id = id;
    input = new Scanner(System.in);
  }

  @Override
  public char getID() { return id; }

  @Override
  public String selectMove(Board board, Player nextPlayer) {
    System.out.print("\nPlayer " + getID() + " choose a move:  ");
    return input.next();
  }

  @Override
  public String toString() {
    return "# Player " + getID() + " was Human";
  }
}
