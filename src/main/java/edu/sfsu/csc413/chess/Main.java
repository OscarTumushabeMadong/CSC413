package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.view.PieceGlyphs;
import edu.sfsu.csc413.chess.view.TextBoardRenderer;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Game game = new Game();
        TextBoardRenderer renderer =
                new TextBoardRenderer(PieceGlyphs.LETTERS);

        // Starting board
        System.out.println(renderer.render(game.board()));

        // Play e2e4 and e7e5
        for (String notation : List.of("e2e4", "e7e5")) {
            game.play(
                    game.findLegalMove(notation).orElseThrow()
            );
        }

        // Board after both moves
        System.out.println(renderer.render(game.board()));

        // Undo Black's e7e5
        game.undoLastMove();

        // Board after undo
        System.out.println(renderer.render(game.board()));
    }
}