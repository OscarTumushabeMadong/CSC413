package edu.sfsu.csc413.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final Piece[][] squares;

    public Board() {
        squares = new Piece[Position.BOARD_SIZE][Position.BOARD_SIZE];
    }

    public Piece pieceAt(Position position) {
        return squares[position.file()][position.rank()];
    }

    public boolean isEmpty(Position position) {
        return pieceAt(position) == null;
    }

    public void place(Position position, Piece piece) {
        squares[position.file()][position.rank()] = piece;
    }

    public List<Position> positionsOf(Color color) {

        List<Position> positions = new ArrayList<>();

        for (int file = 0; file < Position.BOARD_SIZE; file++) {
            for (int rank = 0; rank < Position.BOARD_SIZE; rank++) {
                Piece piece = squares[file][rank];

                if (piece != null && piece.color() == color) {
                    positions.add(new Position(file, rank));
                }
            }
        }

        return positions;
    }

    @Override
    public String toString() {
        StringBuilder fen = new StringBuilder();

        for (int rank = Position.BOARD_SIZE - 1; rank >= 0; rank--) {
            int emptyCount = 0;

            for (int file = 0; file < Position.BOARD_SIZE; file++) {
                Piece piece = squares[file][rank];

                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }

                    fen.append(piece.symbol());
                }
            }

            if (emptyCount > 0) {
                fen.append(emptyCount);
            }

            if (rank > 0) {
                fen.append('/');
            }
        }

        return fen.toString();
    }
    public void apply(Move move) {
        place(move.from(), null);

        if (move.isPromotion()) {
            Piece promotedPiece = switch (move.promotesTo()) {
                case QUEEN -> new Queen(move.moved().color());
                case ROOK -> new Rook(move.moved().color());
                case BISHOP -> new Bishop(move.moved().color());
                case KNIGHT -> new Knight(move.moved().color());
                default -> throw new IllegalArgumentException(
                        "Invalid promotion type: " + move.promotesTo());
            };

            place(move.to(), promotedPiece);
        } else {
            place(move.to(), move.moved());
        }
    }
    public void undo(Move move) {
        place(move.from(), move.moved());
        place(move.to(), move.captured());
    }


    /**
     * Finds the position of the king belonging to the specified color.
     *
     * @param color the color of the king to find
     * @return the king's position, or null if no king exists
     */
    public Position kingPosition(Color color) {

        for (Position position : positionsOf(color)) {

            Piece piece = pieceAt(position);

            if (piece.type() == PieceType.KING) {
                return position;
            }
        }

        return null;
    }

}