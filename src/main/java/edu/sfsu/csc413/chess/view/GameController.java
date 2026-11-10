package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.PieceType;

import java.util.Optional;

/**
 * Connects the player's decisions to the game: carry out what was asked, then
 * ask the view to redraw.
 *
 * <p>This is the C in MVC. It is the only class that knows about both the
 * {@link Game} and the {@link BoardView}. It holds no game state of its own:
 * the model owns the position, the view owns the display, and the controller
 * owns only the conversation between them.
 *
 * <p>When you are done, this class mentions no {@code Scanner}, no
 * {@code System.out}, no window, and none of the words "undo", "moves", or
 * "quit". Those belong to the view and to {@link PlayerAction#parse}.
 */
public class GameController {

    private final Game game;
    private final BoardView view;

    public GameController(Game game, BoardView view) {
        this.game = game;
        this.view = view;
    }

    /** The game being played, so a view can read it in order to draw it. */
    public Game game() {
        return game;
    }


    /**
     * Greets the player, draws the opening position,
     * and hands control to the view.
     */
    public void start() {
        view.showMessage("Welcome to Chess!");
        view.render(game);
        view.start(this);
    }



    /**
     * Repeatedly processes player actions until the game
     * finishes or the player quits.
     */
    public void runLoop() {
        while (!game.isGameOver()) {

            PlayerAction action = view.nextAction(game);

            handle(action);

            if (action instanceof PlayerAction.Quit) {
                break;
            }
        }
    }



    /**
     * Processes one player action and updates the view.
     *
     * @param action the player's requested action
     */
    public void handle(PlayerAction action) {

        switch (action) {

            case PlayerAction.Move moveAction -> {

                String notation = moveAction.notation();

                Optional<Move> legalMove =
                        game.findLegalMove(notation);

                // If promotion was not specified, ask the view.
                if (legalMove.isEmpty() && notation.length() == 4) {

                    PieceType choice = view.promotionChoice(game);

                    String promotionNotation = notation
                            + Character.toLowerCase(choice.symbol());

                    legalMove = game.findLegalMove(promotionNotation);
                }

                if (legalMove.isPresent()) {
                    game.play(legalMove.get());
                } else {
                    view.showMessage(
                            "Not a legal move: '" + notation + "'."
                    );
                }
            }

            case PlayerAction.Undo ignored -> {

                if (game.undoLastMove().isEmpty()) {
                    view.showMessage("Nothing to undo.");
                }
            }

            case PlayerAction.ShowMoves ignored -> {

                java.util.List<String> moves = game.legalMoves()
                        .stream()
                        .map(Move::toString)
                        .sorted()
                        .toList();

                view.showMessage(
                        moves.size() + " legal moves: "
                                + String.join(" ", moves)
                );
            }

            case PlayerAction.Quit ignored -> {

                view.showMessage("Goodbye.");
                view.close();
                return;
            }
        }

        // Redraw after every action except Quit.
        view.render(game);

        // Announce the result when the game ends.
        GameStatus status = game.status();

        if (status == GameStatus.CHECKMATE) {

            view.showMessage(
                    "Checkmate. "
                            + game.sideToMove().opposite()
                            + " wins."
            );

            view.close();

        } else if (status == GameStatus.STALEMATE) {

            view.showMessage(
                    "Stalemate. The game is a draw."
            );

            view.close();
        }
    }

}
