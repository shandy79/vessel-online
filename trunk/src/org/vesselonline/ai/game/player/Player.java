package org.vesselonline.ai.game.player;

import org.vesselonline.ai.game.Board;

public interface Player {
  char getID();
  String selectMove(Board board, Player nextPlayer);
}
