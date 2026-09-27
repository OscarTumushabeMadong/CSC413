package edu.sfsu.csc413.chess;

import edu.sfsu.csc413.chess.model.Board;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;
import edu.sfsu.csc413.chess.view.PieceGlyphs;
import edu.sfsu.csc413.chess.view.TextBoardRenderer;
import edu.sfsu.csc413.chess.factory.BoardFactory;

public final class Main {

    public static void main(String[] args) {
        Board board = BoardFactory.standard();

        TextBoardRenderer renderer =
                new TextBoardRenderer(PieceGlyphs.LETTERS);

        System.out.println(renderer.render(board));
    }

    private Main() {
    }
}