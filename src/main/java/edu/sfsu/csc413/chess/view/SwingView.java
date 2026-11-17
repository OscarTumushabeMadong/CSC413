
package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SwingView implements BoardView {

    private static final Color LIGHT = new Color(240, 217, 181);
    private static final Color DARK = new Color(181, 136, 99);
    private static final Color SELECTED = new Color(255, 215, 70);

    private final JButton[][] squares = new JButton[8][8];
    private final JLabel statusLabel = new JLabel("Welcome to Chess!");

    private JFrame frame;
    private GameController controller;
    private Game game;
    private Position selected;
    private String message = "";
    private int previousMoveCount = -1;

    @Override
    public void start(GameController controller) {
        this.controller = controller;

        SwingUtilities.invokeLater(() -> {
            createWindow();
            updateBoard();
        });
    }

    private void createWindow() {
        frame = new JFrame("CSC 413 - Chess");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        root.add(statusLabel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                final int file = col;
                final int rank = 7 - row;

                JButton square = new JButton();
                square.setFont(new Font("Serif", Font.PLAIN, 38));
                square.setFocusPainted(false);
                square.setMargin(new Insets(0, 0, 0, 0));
                square.setOpaque(true);
                square.setPreferredSize(new Dimension(68, 68));

                square.addActionListener(e -> handleClick(file, rank));

                squares[row][col] = square;
                boardPanel.add(square);
            }
        }

        root.add(boardPanel, BorderLayout.CENTER);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> {
            selected = null;
            controller.handle(new PlayerAction.Undo());
        });

        JPanel controls = new JPanel();
        controls.add(undoButton);

        root.add(controls, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void handleClick(int file, int rank) {
        if (game == null || game.isGameOver()) {
            return;
        }

        Position clicked = new Position(file, rank);

        if (selected == null) {
            Piece piece = game.board().pieceAt(clicked);

            if (piece == null || piece.color() != game.sideToMove()) {
                showMessage("Select one of your own pieces.");
                return;
            }

            selected = clicked;
            updateBoard();
            return;
        }

        if (selected.equals(clicked)) {
            selected = null;
            updateBoard();
            return;
        }

        String notation = selected.toString() + clicked.toString();
        selected = null;

        controller.handle(new PlayerAction.Move(notation));
    }

    @Override
    public void render(Game game) {
        this.game = game;

        // A successful move or undo changes the game history.
        int moveCount = game.history().size();

        if (moveCount != previousMoveCount) {
            previousMoveCount = moveCount;
            message = "";
        }

        if (SwingUtilities.isEventDispatchThread()) {
            updateBoard();
        } else {
            SwingUtilities.invokeLater(this::updateBoard);
        }
    }

    private void updateBoard() {
        if (frame == null || game == null) {
            return;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position position = new Position(col, 7 - row);
                Piece piece = game.board().pieceAt(position);

                JButton square = squares[row][col];

                square.setText(piece == null ? "" : glyph(piece));
                square.setBackground(
                        (row + col) % 2 == 0 ? LIGHT : DARK
                );

                square.setBorder(selected != null
                        && selected.equals(position)
                        ? new LineBorder(SELECTED, 4)
                        : BorderFactory.createEmptyBorder(2, 2, 2, 2));

                square.setEnabled(!game.isGameOver());
            }
        }

        String status = "Turn: " + game.sideToMove()
                + " | Status: " + game.status();

        if (!message.isBlank()) {
            status += " | " + message;
        }

        statusLabel.setText(status);
    }

    private String glyph(Piece piece) {
        char letter = Character.toUpperCase(piece.symbol());
        boolean white = Character.isUpperCase(piece.symbol());

        return switch (letter) {
            case 'K' -> white ? "\u2654" : "\u265A";
            case 'Q' -> white ? "\u2655" : "\u265B";
            case 'R' -> white ? "\u2656" : "\u265C";
            case 'B' -> white ? "\u2657" : "\u265D";
            case 'N' -> white ? "\u2658" : "\u265E";
            case 'P' -> white ? "\u2659" : "\u265F";
            default -> "?";
        };
    }

    @Override
    public void showMessage(String message) {
        this.message = message;

        if (SwingUtilities.isEventDispatchThread()) {
            updateBoard();
        } else {
            SwingUtilities.invokeLater(this::updateBoard);
        }
    }

    @Override
    public void close() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                if (game != null && game.isGameOver()) {
                    JOptionPane.showMessageDialog(
                            frame,
                            message,
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                frame.dispose();
            }
        });
    }
}
