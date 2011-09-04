package org.vesselonline.ai.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vesselonline.ai.game.player.Player;

public class Board {
  private char[][] board, previousBoard, tmpBoard;

  private List<String> moveHistory;
  private char winner, loser;

  public Board(int boardSize) {
    this.board = new char[boardSize][boardSize];
    this.previousBoard = new char[boardSize][boardSize];
    this.tmpBoard = new char[boardSize][boardSize];

    for (int i = 0; i < board.length; i++) {
      Arrays.fill(board[i], ' ');
    }

    this.moveHistory = new ArrayList<String>();
  }

  public Board(Board copyBoard) {
    this(copyBoard.getBoardSize());

    boardCopy(copyBoard.board, this.board);
    boardCopy(copyBoard.previousBoard, this.previousBoard);
  }

  public List<String> getMoveHistory() { return moveHistory; }

  public boolean addMove(Player player, String move) {
    char[] moveAry = move.toCharArray();
    int i;  // 0 = 48 in Unicode/ASCII

    moveHistory.add(move);

    if (moveAry.length < 2 || (i = moveAry[1] - 48) >= board.length || i < 0) {
      loser = player.getID();
      return false;
    }

    // Save current board, save as previousBoard if move is accepted
    boardCopy(board, tmpBoard);

    switch(moveAry[0]) {
      case 'T':
      case 't':
        moveTop(player.getID(), 0, i);
        break;
      case 'B':
      case 'b':
        moveBottom(player.getID(), board.length - 1, i);
        break;
      case 'L':
      case 'l':
        moveLeft(player.getID(), i, 0);
        break;
      case 'R':
      case 'r':
        moveRight(player.getID(), i, board.length - 1);
        break;
      default:
        loser = player.getID();
        return false;
    }

    return true;
  }

  public boolean canGameContinue(Player player, boolean print) {
    boolean deepEquals = true;
    for (int i = 0; i < board.length; i++) {
      if (! Arrays.equals(board[i], previousBoard[i])) {
        deepEquals = false;
        break;
      }
    }

    if (deepEquals) {
      if (print) { System.out.println("\nPrevious game state detected.  Player " + player.getID() + " has lost the game!"); }
      loser = player.getID();
      return false;
    }

    char playerID = countStraights(print);

    if (playerID != ' ') {
      if (print) { System.out.println("\nWith more straights, player " + playerID + " has won the game!"); }
      winner = playerID;
      return false;
    }

    boardCopy(tmpBoard, previousBoard);
    return true;
  }

  public void print() {
    System.out.print("\n  ");

    for (int i = 0; i < board.length; i++) {
      System.out.print(" " + i);
    }

    printSeparatorRow();

    for (int i = 0; i < board.length; i++) {
      printBoardRow(i);
      printSeparatorRow();
    }

    System.out.println();
  }

  public int getBoardSize() { return board.length; }
  public char getWinner() { return winner; }
  public char getLoser() { return loser; }

  private void moveTop(char playerID, int row, int col) {
    if (board[row][col] != ' ' && (row + 1) < board.length) {
      moveTop(board[row][col], row + 1, col);
    }

    board[row][col] = playerID;
  }
  
  private void moveBottom(char playerID, int row, int col) {
    if (board[row][col] != ' ' && row > 0) {
      moveBottom(board[row][col], row - 1, col);
    }

    board[row][col] = playerID;
  }

  private void moveLeft(char playerID, int row, int col) {
    if (board[row][col] != ' ' && (col + 1) < board.length) {
      moveLeft(board[row][col], row, col + 1);
    }

    board[row][col] = playerID;
  }

  private void moveRight(char playerID, int row, int col) {
    if (board[row][col] != ' ' && col > 0) {
      moveRight(board[row][col], row, col - 1);
    }

    board[row][col] = playerID;
  }

  private void boardCopy(char[][] fromBoard, char[][] toBoard) {
    for (int i = 0; i < fromBoard.length; i++) {
      toBoard[i] = Arrays.copyOf(fromBoard[i], fromBoard.length);
    }
  }

  private char countStraights(boolean print) {
    Map<Character, Integer> straightMap = new HashMap<Character, Integer>();
    char playerID;

    for (int i = 0; i < board.length; i++) {
      playerID = board[i][0];
      if (straightMap.get(playerID) == null) {
        straightMap.put(playerID, 0);
      }

      if (isHorizontalStraight(playerID, i, 0)) {
        straightMap.put(playerID, straightMap.get(playerID).intValue() + 1);
      }

      playerID = board[0][i];
      if (straightMap.get(playerID) == null) {
        straightMap.put(playerID, 0);
      }

      if (isVerticalStraight(playerID, i, 0)) {
        straightMap.put(playerID, straightMap.get(playerID).intValue() + 1);
      }
    }

    if (isLeftDiagonalStraight(board[0][0], 0)) {
      straightMap.put(board[0][0], straightMap.get(board[0][0]).intValue() + 1);
    }

    if (isRightDiagonalStraight(board[board.length - 1][0], 0)) {
      straightMap.put(board[board.length - 1][0], straightMap.get(board[board.length - 1][0]).intValue() + 1);
    }

    playerID = ' ';
    straightMap.put(playerID, 0);

    for (Character c : straightMap.keySet()) {
      if (straightMap.get(c) > 0) {
        if (print) { System.out.println("\nPlayer " + c + " has " + straightMap.get(c) + " straight(s)."); }
      }

      if (straightMap.get(c) > straightMap.get(playerID)) {
        playerID = c;
      }
    }

    return playerID;
  }

  public int getNearStraightScore(int maxDistance, Player thisPlayer, Player nextPlayer) {
    Map<Character, Integer> straightMap = new HashMap<Character, Integer>();

    straightMap.put(thisPlayer.getID(), 0);
    straightMap.put(nextPlayer.getID(), 0);

    for (int i = 0; i < board.length; i++) {
      if (isHorizontalStraight(thisPlayer.getID(), i, maxDistance)) {
        straightMap.put(thisPlayer.getID(), straightMap.get(thisPlayer.getID()).intValue() + 1);
      }

      if (isHorizontalStraight(nextPlayer.getID(), i, maxDistance)) {
        straightMap.put(nextPlayer.getID(), straightMap.get(nextPlayer.getID()).intValue() + 1);
      }

      if (isVerticalStraight(thisPlayer.getID(), i, maxDistance)) {
        straightMap.put(thisPlayer.getID(), straightMap.get(thisPlayer.getID()).intValue() + 1);
      }

      if (isVerticalStraight(nextPlayer.getID(), i, maxDistance)) {
        straightMap.put(nextPlayer.getID(), straightMap.get(nextPlayer.getID()).intValue() + 1);
      }
    }

    if (isLeftDiagonalStraight(thisPlayer.getID(), maxDistance)) {
      straightMap.put(thisPlayer.getID(), straightMap.get(thisPlayer.getID()).intValue() + 1);
    }

    if (isLeftDiagonalStraight(nextPlayer.getID(), maxDistance)) {
      straightMap.put(nextPlayer.getID(), straightMap.get(nextPlayer.getID()).intValue() + 1);
    }

    if (isRightDiagonalStraight(thisPlayer.getID(), maxDistance)) {
      straightMap.put(thisPlayer.getID(), straightMap.get(thisPlayer.getID()).intValue() + 1);
    }

    if (isRightDiagonalStraight(nextPlayer.getID(), maxDistance)) {
      straightMap.put(nextPlayer.getID(), straightMap.get(nextPlayer.getID()).intValue() + 1);
    }

    return calculateScore(straightMap, thisPlayer, nextPlayer);
  }

  private boolean isHorizontalStraight(char playerID, int row, int maxDistance) {
    if (playerID == ' ') { return false; }
    boolean isStraight = true;
    int falseCount = 0;

    for (int i = 0; i < board[row].length; i++) {
      if (board[row][i] != playerID) {
        falseCount++;
        if (falseCount > maxDistance) {
          isStraight = false;
          break;
        }
      }
    }

    return isStraight;
  }

  private boolean isVerticalStraight(char playerID, int col, int maxDistance) {
    if (playerID == ' ') { return false; }
    boolean isStraight = true;
    int falseCount = 0;

    for (int i = 0; i < board.length; i++) {
      if (board[i][col] != playerID) {
        falseCount++;
        if (falseCount > maxDistance) {
          isStraight = false;
          break;
        }
      }
    }

    return isStraight;
  }

  private boolean isLeftDiagonalStraight(char playerID, int maxDistance) {
    if (playerID == ' ') { return false; }
    boolean isStraight = true;
    int falseCount = 0;

    for (int i = 0; i < board.length; i++) {
      if (board[i][i] != playerID) {
        falseCount++;
        if (falseCount > maxDistance) {
          isStraight = false;
          break;
        }
      }
    }

    return isStraight;
  }

  private boolean isRightDiagonalStraight(char playerID, int maxDistance) {
    if (playerID == ' ') { return false; }
    boolean isStraight = true;
    int falseCount = 0;

    for (int i = 0; i < board.length; i++) {
      if (board[(board.length - 1) - i][i] != playerID) {
        falseCount++;
        if (falseCount > maxDistance) {
          isStraight = false;
          break;
        }
      }
    }

    return isStraight;
  }

  // "Strong" spots are those in the corners or along the diagonal, since they
  // can take part in three different straights.  If the board has an odd-valued
  // size (e.g. 3 or 5), the middle spot will be counted twice.
  public int getStrongSpotScore(Player thisPlayer, Player nextPlayer) {
    Map<Character, Integer> strongMap = new HashMap<Character, Integer>();

    strongMap.put(thisPlayer.getID(), 0);
    strongMap.put(nextPlayer.getID(), 0);

    for (int i = 0; i < board.length; i++) {
      if (board[i][i] == thisPlayer.getID()) {
        strongMap.put(thisPlayer.getID(), strongMap.get(thisPlayer.getID()).intValue() + 1);
      } else if (board[i][i] == nextPlayer.getID()) {
        strongMap.put(nextPlayer.getID(), strongMap.get(nextPlayer.getID()).intValue() + 1);
      }

      if (board[(board.length - 1) - i][i] == thisPlayer.getID()) {
        strongMap.put(thisPlayer.getID(), strongMap.get(thisPlayer.getID()).intValue() + 1);
      } else if (board[(board.length - 1) - i][i] == nextPlayer.getID()) {
        strongMap.put(nextPlayer.getID(), strongMap.get(nextPlayer.getID()).intValue() + 1);
      }
    }

    return calculateScore(strongMap, thisPlayer, nextPlayer);
  }

  private int calculateScore(Map<Character, Integer> scoreMap, Player thisPlayer, Player nextPlayer) {
    int score = 0;

    if (scoreMap.containsKey(thisPlayer.getID()) && scoreMap.containsKey(nextPlayer.getID())) {
      score = scoreMap.get(thisPlayer.getID()) - scoreMap.get(nextPlayer.getID());
    } else if (scoreMap.containsKey(thisPlayer.getID())) {
      score = scoreMap.get(thisPlayer.getID());
    } else if (scoreMap.containsKey(nextPlayer.getID())) {
      score = scoreMap.get(nextPlayer.getID());
    }

    return score;
  }

  private void printSeparatorRow() {
    System.out.print("\n  +");

    for (int i = 0; i < board.length; i++) {
      System.out.print("-+");
    }
  }

  private void printBoardRow(int row) {
    System.out.print("\n" + row + " |");

    for (int i = 0; i < board[row].length; i++) {
      System.out.print(board[row][i] + "|");
    }
  }
}
