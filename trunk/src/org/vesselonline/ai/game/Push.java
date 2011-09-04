package org.vesselonline.ai.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.vesselonline.ai.game.player.AlphaBetaPlayer;
import org.vesselonline.ai.game.player.HumanPlayer;
import org.vesselonline.ai.game.player.MinimaxPlayer;
import org.vesselonline.ai.game.player.Player;
import org.vesselonline.ai.game.player.RandomPlayer;
import org.vesselonline.ai.game.player.VerifyPlayer;
import org.vesselonline.ai.util.Instrumentable;
import org.vesselonline.ai.util.Instrumentation;

public class Push {
  private int boardSize;
  private Board board;
  private Player playerX, playerO;

  private boolean trace;
  private int mmDepth, abDepth;
  private int moveCount;

  private static final int DEFAULT_DEPTH = 2;
  private static final int MAX_MOVES = 100;

  private Push(boolean trace, int mmDepth, int abDepth) {
    this.trace = trace;
    this.mmDepth = mmDepth;
    this.abDepth = abDepth;

    moveCount = 0;
    if (mmDepth <= 0) { this.mmDepth = DEFAULT_DEPTH; }
    if (abDepth <= 0) { this.abDepth = DEFAULT_DEPTH; }
  }

  public Push(int boardSize, char playerXType, char playerOType, boolean trace, int mmDepth, int abDepth) {
    this(trace, mmDepth, abDepth);

    this.boardSize = boardSize;
    board = new Board(boardSize);

    playerX = createPlayer('X', playerXType);
    playerO = createPlayer('O', playerOType);
  }

  public Push(String verify) {
    this(false, 0, 0);

    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(verify));
      String line;
      char nextPlayer = 'X';

      while ((line = in.readLine()) != null) {
        if (! line.startsWith("#")) {
          if (boardSize == 0) {
            boardSize = Integer.parseInt(line);
            board = new Board(boardSize);

            playerX = new VerifyPlayer('X');
            playerO = new VerifyPlayer('O');          
          } else if (nextPlayer == 'X') {
            ((VerifyPlayer) playerX).addMove(line);
            nextPlayer = 'O';
          } else {
            ((VerifyPlayer) playerO).addMove(line);
            nextPlayer = 'X';
          }
        }
      }

      in.close();
    } catch (IOException ioe) {
      System.out.println("An error occurred while reading the game file " + verify + ":\n" + ioe.getMessage());
      System.exit(1);
    }
  }

  public int getBoardSize() { return boardSize; }
  public Board getBoard() { return board; }

  public Player getPlayerX() { return playerX; }
  public Player getPlayerO() { return playerO; }

  public boolean getTrace() { return trace; }
  public int getMmDepth() { return mmDepth; }
  public int getAbDepth() { return abDepth; }

  public void play() {
    String move = "";
    boolean result = true;
    char winner = ' ';

    getBoard().print();

    while (result && moveCount < MAX_MOVES) {
      move = getPlayerX().selectMove(getBoard(), getPlayerO());
      result = getBoard().addMove(getPlayerX(), move);

      if (result) {
        getBoard().print();
        result = getBoard().canGameContinue(getPlayerX(), true);
      } else {
        System.out.println("\nPlayer X has entered an invalid move.  Player O is the winner!");
        break;
      }

      if (result) {
        move = getPlayerO().selectMove(getBoard(), getPlayerX());
        result = getBoard().addMove(getPlayerO(), move);
      } else {
        break;
      }

      if (result) {
        getBoard().print();
        result = getBoard().canGameContinue(getPlayerO(), true);
      } else {
        System.out.println("\nPlayer O has entered an invalid move.  Player X is the winner!");
      }

      moveCount++;
      if (moveCount >= MAX_MOVES) {
        System.out.println("\nThe game has reached the maximum move limit.  The result is a draw!");
      }
    }

    System.out.println("\nThe game has ended.  Thank you for playing!");
    
    if (getBoard().getWinner() == 'X' || getBoard().getWinner() == 'O') {
      winner = getBoard().getWinner();
    } else if (getBoard().getLoser() == 'X') {
      winner = 'O';
    } else if (getBoard().getLoser() == 'O') {
      winner = 'X';
    }

    String fileName = createGameFile(winner);
    System.out.println("The results of this game can be viewed in " + fileName);

    if (playerX instanceof MinimaxPlayer || playerX instanceof AlphaBetaPlayer) {
      printPlayerReport(((Instrumentable) playerX).getInstrumentation(), playerX.getID());
      if (playerX instanceof AlphaBetaPlayer) {
//        System.out.println("Player " + playerX.getID() + " changed " + ((AlphaBetaPlayer) playerX).getChangedMoves() + " moves.");
      }
    }

    if (playerO instanceof MinimaxPlayer || playerO instanceof AlphaBetaPlayer) {
      printPlayerReport(((Instrumentable) playerO).getInstrumentation(), playerO.getID());
      if (playerO instanceof AlphaBetaPlayer) {
//        System.out.println("Player " + playerO.getID() + " changed " + ((AlphaBetaPlayer) playerO).getChangedMoves() + " moves.");
      }
    }
  }

  private Player createPlayer(char playerID, char playerType) {
    Player player;
    Instrumentation instrumentation = new Instrumentation(getTrace(), System.out);

    switch(playerType) {
      case 'H':
      case 'h':
        player = new HumanPlayer(playerID);
        break;
      case 'M':
      case 'm':
        player = new MinimaxPlayer(playerID, getMmDepth(), instrumentation);
        break;
      case 'A':
      case 'a':
        player = new AlphaBetaPlayer(playerID, getAbDepth(), instrumentation);
        break;
      default:
        player = new RandomPlayer(playerID);
    }

    return player;
  }

  private String createGameFile(char winner) {
    Format dateFormatter = new SimpleDateFormat("yyyyMMdd'T'kkmmss'.push'");
    String fileName = dateFormatter.format(new Date());

    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
      out.write(getPlayerX().toString());
      out.newLine();
      out.write(getPlayerO().toString());
      out.newLine();
      out.write("" + getBoardSize());
      out.newLine();

      for (String move : getBoard().getMoveHistory()) {
        out.write(move);
        out.newLine();
      }

      out.write("# Winner was Player " + winner);
      out.newLine();

      out.close();
    } catch (IOException e) {
      System.out.print("There was an error producing the game file.");
    } 

    return fileName;
  }

  private void printPlayerReport(Instrumentation trace, char playerID) {
    System.out.println("\nPlayer " + playerID + " End of Game Report\n---------------------------");
    trace.printStatus("turns", "nodes expanded");
    System.out.println("  Avg. CPU time per turn: " + (trace.getCPUTime() / trace.getWorkUnits()) +
                       ", Avg. nodes expanded per turn: " + (trace.getSecondaryWorkUnits() / trace.getWorkUnits()));
  }

  /**
   * @param args
   */
  @SuppressWarnings("static-access")
  public static void main(String[] args) {
    // create Options object
    Options options = new Options();
    
    Option traceOpt = new Option("trace", "print trace output");
    Option verifyOpt = OptionBuilder.withArgName("file").hasArg().withDescription("use given file for verification").
                        create("verify");
    Option mmdepthOpt = OptionBuilder.withArgName("plies").hasArg().withDescription("max search depth for minimax").
                         create("mmdepth");
    Option abdepthOpt = OptionBuilder.withArgName("plies").hasArg().withDescription("max search depth for alpha-beta").
                         create("abdepth");

    options.addOption(traceOpt);
    options.addOption(verifyOpt);
    options.addOption(mmdepthOpt);
    options.addOption(abdepthOpt);

    // Create the parser
    CommandLineParser parser = new GnuParser();
    CommandLine line = null;
    try {
      // Parse the command line arguments
      line = parser.parse(options, args);
    } catch (ParseException pe) {
      // System.out.println("Command-line parsing failed.  Reason:\n" + pe.getMessage());
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("Push", options);
      return;
    }

    Push push = null;
    boolean trace = false;
    int mmDepth = 0, abDepth = 0; 
    
    if (line.hasOption("trace")) {
      System.out.println("Trace output ON");
      trace = true;
    }
    if (line.hasOption("mmdepth")) {
      System.out.println("Minimax max depth of " + line.getOptionValue("mmdepth") + " plies");
      mmDepth = Integer.parseInt(line.getOptionValue("mmdepth"));
    }
    if (line.hasOption("abdepth")) {
      System.out.println("Alpha-beta max depth of " + line.getOptionValue("abdepth") + " plies");
      abDepth = Integer.parseInt(line.getOptionValue("abdepth"));
    }

    if (line.hasOption("verify")) {
      String verify = null;
      System.out.println("Verifying file " + line.getOptionValue("verify"));
      verify = line.getOptionValue("verify");

      push = new Push(verify);

    } else {
      Scanner input = new Scanner(System.in);
      System.out.print("\nPlease select a game board size:  ");
      while (! input.hasNextInt()) {
        input.next();
        System.out.print("\nPlease enter an integer value for the game board size:  ");
      }
      int boardSize = input.nextInt();
      System.out.print("Please choose a player for X (H/M/A/R):  ");
      char playerXType = input.next().charAt(0);
      System.out.print("Please choose a player for O (H/M/A/R):  ");
      char playerOType = input.next().charAt(0);

      push = new Push(boardSize, playerXType, playerOType, trace, mmDepth, abDepth);
    }

    push.play();
  }
}
