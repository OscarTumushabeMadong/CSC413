package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

/**
 * The pawn — the piece that breaks every rule the others follow.
 *
 * <p>It is the only piece that moves in just one direction, the only one whose
 * capture differs from its move, the only one with a special first move, and
 * the only one that turns into something else. It is worth noticing that all of
 * that awkwardness is contained in this one file. No other class in the engine
 * knows that pawns are strange. That containment is the payoff of polymorphism:
 * the irregular case costs one class, not a special case in every method that
 * touches a piece.
 *
 * <p>En passant is not handled here. Like castling, it depends on the previous
 * move rather than on the current board, so it waits for Week 15 when
 * {@code Game} owns the move history.
 */
public class Pawn extends Piece {

    /**
     * What a pawn may become on reaching the far rank.
     */
    private void addPawnMove(
            List<Move> moves,
            Position from,
            Position to,
            Piece captured) {

        if (to.rank() == color().promotionRank()) {
            for (PieceType promotion : PROMOTION_CHOICES) {
                moves.add(Move.promotion(from, to, this, captured, promotion));
            }
        } else if (captured == null) {
            moves.add(Move.quiet(from, to, this));
        } else {
            moves.add(Move.capture(from, to, this, captured));
        }
    }
    private static final PieceType[] PROMOTION_CHOICES = { PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT };

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public List<Move> pseudoLegalMoves(Board board, Position from) {
        List<Move> moves = new ArrayList<>();

        int direction = color().pawnDirection();

        Position oneForward = from.offsetOrNull(0, direction);

        if (oneForward != null && board.pieceAt(oneForward) == null) {
            addPawnMove(moves, from, oneForward, null);

            if (from.rank() == color().pawnStartRank()) {
                Position twoForward = from.offsetOrNull(0, 2 * direction);

                if (twoForward != null && board.pieceAt(twoForward) == null) {
                    moves.add(Move.quiet(from, twoForward, this));
                }
            }
        }

        for (int fileDelta : new int[]{-1, 1}) {
            Position captureSquare = from.offsetOrNull(fileDelta, direction);

            if (captureSquare == null) {
                continue;
            }

            Piece captured = board.pieceAt(captureSquare);

            if (captured != null && captured.color() != color()) {
                addPawnMove(moves, from, captureSquare, captured);
            }
        }

        return moves;
    }

    /**
     * A pawn attacks the two squares diagonally ahead of it, whether or not
     * anything stands there.
     *
     * <p>This override exists because the inherited version answers "can this
     * piece move to that square", and for a pawn that is the wrong question.
     * An empty square in front of a pawn is a square the pawn can move to but
     * does <em>not</em> attack — which matters enormously for king safety: a
     * king may not be blocked from a square merely because a pawn could advance
     * onto it, but it certainly may not step onto a square a pawn guards.
     */
    @Override
    public boolean attacks(Board board, Position from, Position target) {
        int direction = color().pawnDirection();

        Position leftAttack = from.offsetOrNull(-1, direction);
        Position rightAttack = from.offsetOrNull(1, direction);

        return target.equals(leftAttack) || target.equals(rightAttack);
    }
}
