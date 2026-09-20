package edu.sfsu.csc413.chess.model;

public record Position(int file, int rank) {

    public static final int BOARD_SIZE = 8;

    public Position {
        if (file < 0 || file > 7 || rank < 0 || rank > 7) {
            throw new IllegalArgumentException("Position must be on the board");
        }
    }

    public static Position parse(String algebraic) {
        if (algebraic == null || algebraic.length() != 2) {
            throw new IllegalArgumentException("Invalid position");
        }

        int file = algebraic.charAt(0) - 'a';
        int rank = algebraic.charAt(1) - '1';

        return new Position(file, rank);
    }

    public String toAlgebraic() {
        char fileChar = (char) ('a' + file);
        char rankChar = (char) ('1' + rank);

        return "" + fileChar + rankChar;
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }

    public Position offsetOrNull(int fileOffset, int rankOffset) {
        int newFile = file + fileOffset;
        int newRank = rank + rankOffset;

        if (newFile < 0 || newFile > 7 || newRank < 0 || newRank > 7) {
            return null;
        }

        return new Position(newFile, newRank);
    }
}
