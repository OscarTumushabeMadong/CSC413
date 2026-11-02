package edu.sfsu.csc413.chess.engine;

import java.util.ArrayList;

import edu.sfsu.csc413.chess.model.*;

import java.util.List;

/**
 * Generates the moves a side may play in a given position.
 *
 * <p>This class exists because of a question no single piece can answer. A
 * piece knows how it moves; it does not know the rule that applies to every
 * piece at once: <strong>you may not leave your own king attacked.</strong>
 * That rule needs the whole board, and it arrives in M5. This week the class
 * is where the generation loop lives, moved out of {@code Game}, so that when
 * the rule arrives it has a home.
 *
 * <p>Notice what this class is not. It holds no state: no board, no side to
 * move, no history. Everything it needs arrives as a parameter, so its methods
 * are {@code static} and it cannot be instantiated. Ask it the same question
 * about the same board twice and you get the same answer.
 *
 * <p>Nothing outside the {@code engine} package needs this class. The view and
 * {@code Main} talk to {@link Game}, which decides what "legal" means. That is
 * why one of the two methods below has no access modifier.
 */
public final class MoveGenerator {

    private MoveGenerator() {
    }


    /**
     * Returns all moves that do not leave the specified
     * color's king in check.
     *
     * The board is restored after evaluating each candidate.
     *
     * @param board the current chessboard
     * @param color the side whose legal moves are requested
     * @return a list of legal moves
     */
    public static List<Move> legalMoves(Board board, Color color) {

        List<Move> legal = new ArrayList<>();

        for (Move move : pseudoLegalMoves(board, color)) {

            board.apply(move);

            boolean leavesKingInCheck;

            try {
                leavesKingInCheck = isInCheck(board, color);
            } finally {
                board.undo(move);
            }

            if (!leavesKingInCheck) {
                legal.add(move);
            }
        }

        return legal;
    }


    /**
     * Every geometrically possible move for {@code color}, king safety aside.
     *
     * <p>This is the loop from session 6 §3 and M3's {@code Game.legalMoves()},
     * moved here unchanged. Note how little it knows: it never asks what kind
     * of piece it is looking at. It asks each piece for its own moves.
     *
     * <p>Package-private on purpose. Only {@code Game} and this class's tests,
     * both in {@code engine}, may call it. A view that wants the moves asks
     * {@code Game}.
     */
    static List<Move> pseudoLegalMoves(Board board, Color color) {
        List<Move> moves = new ArrayList<>();

        for (Position from : board.positionsOf(color)) {
            moves.addAll(
                    board.pieceAt(from).pseudoLegalMoves(board, from)
            );
        }

        return moves;
    }


    /**
     * Determines whether the target square is attacked by any
     * piece belonging to the specified attacking color.
     *
     * @param board the chessboard to examine
     * @param target the square being examined
     * @param attacker the color of the attacking pieces
     * @return true if the target is attacked; false otherwise
     */
    public static boolean isAttacked(
            Board board, Position target, Color attacker) {

        for (Position from : board.positionsOf(attacker)) {

            Piece piece = board.pieceAt(from);

            if (piece.attacks(board, from, target)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Determines whether the specified color's king
     * is currently under attack.
     *
     * @param board the current chessboard
     * @param color the color of the king to examine
     * @return true if the king is in check; false otherwise
     */
    public static boolean isInCheck(Board board, Color color) {

        Position king = board.kingPosition(color);

        if (king == null) {
            return false;
        }

        return isAttacked(board, king, color.opposite());
    }

}
