package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

/** Event-driven chess UI. The controller is the only writer of game state. */
public final class SwingView implements BoardView {
    private static final java.awt.Color LIGHT = new java.awt.Color(240, 217, 181);
    private static final java.awt.Color DARK = new java.awt.Color(181, 136, 99);
    private static final java.awt.Color SELECTED = new java.awt.Color(246, 246, 105);
    private static final String[][] GLYPHS = {
            {"♔", "♕", "♖", "♗", "♘", "♙"},
            {"♚", "♛", "♜", "♝", "♞", "♟"}
    };

    private final JButton[][] squares = new JButton[8][8];
    private JFrame frame;
    private JLabel turnLabel;
    private JLabel messageLabel;
    private GameController controller;
    private Game currentGame;
    private Position selected;
    private String lastMessage = "";
    private boolean finished;

    @Override
    public void start(GameController controller) {
        Objects.requireNonNull(controller, "controller");
        onEdt(() -> {
            this.controller = controller;
            if (frame == null) buildWindow();
            finished = controller.game().isGameOver();
            renderOnEdt(controller.game());
            frame.setVisible(true);
        });
    }

    private void buildWindow() {
        frame = new JFrame("CSC 413 Chess — Swing");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel board = new JPanel(new GridLayout(9, 9, 0, 0));
        board.setBorder(new EmptyBorder(8, 8, 0, 8));
        board.add(new JLabel("", SwingConstants.CENTER));
        for (int file = 0; file < 8; file++) {
            JLabel fileLabel = new JLabel(String.valueOf((char) ('a' + file)), SwingConstants.CENTER);
            fileLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            board.add(fileLabel);
        }
        for (int row = 0; row < 8; row++) {
            int rank = 7 - row;
            JLabel rankLabel = new JLabel(String.valueOf(rank + 1), SwingConstants.CENTER);
            rankLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            board.add(rankLabel);
            for (int file = 0; file < 8; file++) {
                final int f = file;
                final int r = rank;
                JButton square = new JButton();
                square.setFont(new Font("Dialog", Font.PLAIN, 38));
                square.setOpaque(true);
                square.setFocusPainted(false);
                square.setBorder(BorderFactory.createLineBorder(new java.awt.Color(90, 70, 50)));
                square.setPreferredSize(new Dimension(68, 68));
                square.addActionListener(event -> squareClicked(new Position(f, r)));
                squares[file][rank] = square;
                board.add(square);
            }
        }
        frame.add(board, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(8, 6));
        footer.setBorder(new EmptyBorder(0, 12, 12, 12));
        turnLabel = new JLabel("White to move");
        turnLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        messageLabel = new JLabel("Select a piece, then a destination.");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton undo = new JButton("Undo");
        undo.addActionListener(event -> {
            if (this.controller == null) return;
            selected = null;
            lastMessage = "";
            finished = false;
            this.controller.handle(new PlayerAction.Undo());
        });
        buttons.add(undo);
        JButton quit = new JButton("Quit");
        quit.addActionListener(event -> {
            if (this.controller != null) this.controller.handle(new PlayerAction.Quit());
            if (frame != null) frame.dispose();
        });
        buttons.add(quit);
        JPanel labels = new JPanel(new GridLayout(2, 1, 0, 4));
        labels.add(turnLabel);
        labels.add(messageLabel);
        footer.add(labels, BorderLayout.CENTER);
        footer.add(buttons, BorderLayout.EAST);
        frame.add(footer, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void squareClicked(Position clicked) {
        if (controller == null || currentGame == null || finished || currentGame.isGameOver()) return;
        Piece piece = currentGame.board().pieceAt(clicked);
        if (selected == null) {
            if (piece == null || piece.color() != currentGame.sideToMove()) {
                showMessage("Select a " + currentGame.sideToMove() + " piece.");
                return;
            }
            selected = clicked;
            lastMessage = "Selected " + coordinate(clicked) + ". Choose a destination.";
            renderOnEdt(currentGame);
            return;
        }
        if (selected.equals(clicked)) {
            selected = null;
            lastMessage = "Selection cleared.";
            renderOnEdt(currentGame);
            return;
        }
        if (piece != null && piece.color() == currentGame.sideToMove()) {
            selected = clicked;
            lastMessage = "Selected " + coordinate(clicked) + ". Choose a destination.";
            renderOnEdt(currentGame);
            return;
        }
        String notation = coordinate(selected) + coordinate(clicked);
        selected = null;
        lastMessage = "";
        controller.handle(new PlayerAction.Move(notation));
    }

    private static String coordinate(Position position) {
        return "" + (char) ('a' + position.file()) + (position.rank() + 1);
    }

    @Override
    public void render(Game game) {
        onEdt(() -> renderOnEdt(game));
    }

    private void renderOnEdt(Game game) {
        currentGame = Objects.requireNonNull(game, "game");
        if (frame == null) return;
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                Position position = new Position(file, rank);
                Piece piece = game.board().pieceAt(position);
                JButton square = squares[file][rank];
                square.setText(piece == null ? "" : glyph(piece));
                square.setBackground(position.equals(selected) ? SELECTED
                        : ((file + rank) % 2 == 0 ? DARK : LIGHT));
                square.setToolTipText(coordinate(position)
                        + (piece == null ? "" : " — " + piece.color() + " " + piece.type()));
                square.setEnabled(!finished && !game.isGameOver());
            }
        }
        GameStatus status = game.status();
        String turn = game.sideToMove() + " to move";
        if (status == GameStatus.CHECK) turn += " — CHECK";
        else if (status == GameStatus.CHECKMATE) turn = "CHECKMATE — " + game.sideToMove().opposite() + " wins";
        else if (status == GameStatus.STALEMATE) turn = "STALEMATE — Draw";
        turnLabel.setText(turn);
        messageLabel.setText(lastMessage.isBlank() ? "Select a piece, then a destination." : lastMessage);
    }

    private static String glyph(Piece piece) {
        int side = piece.color() == Color.WHITE ? 0 : 1;
        int kind = switch (piece.type()) {
            case KING -> 0;
            case QUEEN -> 1;
            case ROOK -> 2;
            case BISHOP -> 3;
            case KNIGHT -> 4;
            case PAWN -> 5;
        };
        return GLYPHS[side][kind];
    }

    @Override
    public void showMessage(String message) {
        onEdt(() -> {
            lastMessage = message == null ? "" : message;
            if (messageLabel != null) messageLabel.setText(lastMessage);
        });
    }

    @Override
    public PieceType promotionChoice(Game game) {
        if (!SwingUtilities.isEventDispatchThread()) return PieceType.QUEEN;
        Object[] choices = {"Queen", "Rook", "Bishop", "Knight"};
        Object selected = JOptionPane.showInputDialog(frame, "Promote pawn to:",
                "Pawn promotion", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
        if (selected == null) return PieceType.QUEEN;
        return switch (selected.toString()) {
            case "Rook" -> PieceType.ROOK;
            case "Bishop" -> PieceType.BISHOP;
            case "Knight" -> PieceType.KNIGHT;
            default -> PieceType.QUEEN;
        };
    }

    @Override
    public void close() {
        onEdt(() -> {
            finished = true;
            selected = null;
            if (currentGame != null) renderOnEdt(currentGame);
        });
    }

    private static void onEdt(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) task.run();
        else SwingUtilities.invokeLater(task);
    }
}
