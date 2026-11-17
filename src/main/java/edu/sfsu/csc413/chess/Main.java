
package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.BoardView;
import edu.sfsu.csc413.chess.view.ConsoleView;
import edu.sfsu.csc413.chess.view.EmojiView;
import edu.sfsu.csc413.chess.view.GameController;
import edu.sfsu.csc413.chess.view.SwingView;
import edu.sfsu.csc413.chess.view.RotatingConsoleView;

/**
 * Entry point for the interactive chess game.
 */
public class Main {

    public static void main(String[] args) {

        Game game = new Game();


        BoardView view;

        if (args.length > 0
                && args[0].equalsIgnoreCase("rotate")) {

            view = new RotatingConsoleView();

        } else {

            // Keep your existing default view construction here.
            view = new ConsoleView();
        }

        GameController controller = new GameController(game, view);
        controller.start();

    }
}
