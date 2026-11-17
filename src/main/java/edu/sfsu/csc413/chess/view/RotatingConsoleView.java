
package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import java.util.Scanner;

/**
 * A console chess view that rotates the board so the
 * player whose turn it is always appears at the bottom.
 *
 * White to move: ranks 8 to 1, files a to h.
 * Black to move: ranks 1 to 8, files h to a.
 *
 * All game actions are delegated to GameController.
 */
public class RotatingConsoleView implements BoardView {

    private final Scanner scanner;

    public RotatingConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the current chessboard in the correct
     * orientation for the side whose turn it is.
     */
    @Override
    public void render(Game game) {

        boolean whiteAtBottom =
                game.sideToMove() == Color.WHITE;

        System.out.println();
        System.out.println("==================================");
        System.out.println("         ROTATING CHESS");
        System.out.println("==================================");

        System.out.println(
                "Orientation: "
                        + (whiteAtBottom ? "WHITE" : "BLACK")
                        + " at bottom"
        );

        System.out.println();

        printFileLabels(whiteAtBottom);
        System.out.println("  +-------------------------------+");

        for (int row = 0; row < 8; row++) {

            int rank = whiteAtBottom
                    ? 7 - row
                    : row;

            System.out.print((rank + 1) + " |");

            for (int column = 0; column < 8; column++) {

                int file = whiteAtBottom
                        ? column
                        : 7 - column;

                Position position =
                        new Position(file, rank);

                Piece piece =
                        game.board().pieceAt(position);

                char symbol = piece == null
                        ? '.'
                        : piece.symbol();

                System.out.print(" " + symbol + " ");
            }

            System.out.println("| " + (rank + 1));
        }

        System.out.println("  +-------------------------------+");
        printFileLabels(whiteAtBottom);

        System.out.println();
        System.out.println("Turn: " + game.sideToMove());

        GameStatus status = game.status();

        switch (status) {

            case CHECK -> System.out.println(
                    "CHECK! " + game.sideToMove()
                            + " must protect the king."
            );

            case CHECKMATE -> System.out.println(
                    "CHECKMATE! "
                            + game.sideToMove().opposite()
                            + " wins."
            );

            case STALEMATE -> System.out.println(
                    "STALEMATE! The game is a draw."
            );

            default -> {
                // Game is continuing normally.
            }
        }

        System.out.println();
    }

    /**
     * Prints file coordinates for the current orientation.
     */
    private void printFileLabels(boolean whiteAtBottom) {

        System.out.print("    ");

        for (int column = 0; column < 8; column++) {

            int file = whiteAtBottom
                    ? column
                    : 7 - column;

            char label = (char) ('a' + file);

            System.out.print(" " + label + " ");
        }

        System.out.println();
    }

    /**
     * Displays messages from the controller.
     */
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Starts the normal controller-driven console loop.
     */
    @Override
    public void start(GameController controller) {

        System.out.println(
                "Enter moves using long algebraic notation."
        );

        System.out.println(
                "Examples: e2e4, g1f3, e7e8q"
        );

        System.out.println(
                "Commands: undo, moves, quit"
        );

        System.out.println();

        controller.runLoop();
    }

    /**
     * Reads the next player action.
     *
     * PlayerAction.parse handles moves, undo,
     * moves, quit, and exit.
     */
    @Override
    public PlayerAction nextAction(Game game) {

        System.out.print(
                game.sideToMove() + " > "
        );

        if (!scanner.hasNextLine()) {
            return new PlayerAction.Quit();
        }

        String input = scanner.nextLine();

        return PlayerAction.parse(input);
    }

    /**
     * Do not close System.in because it is shared
     * with the rest of the application.
     */
    @Override
    public void close() {
        // No owned external resources to release.
    }
}
