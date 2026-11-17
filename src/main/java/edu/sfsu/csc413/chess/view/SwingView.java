
package edu.sfsu.csc413.chess.view;

import edu.sfsu.csc413.chess.engine.Game;
import edu.sfsu.csc413.chess.engine.GameStatus;
import edu.sfsu.csc413.chess.model.Color;
import edu.sfsu.csc413.chess.model.Move;
import edu.sfsu.csc413.chess.model.Piece;
import edu.sfsu.csc413.chess.model.PieceType;
import edu.sfsu.csc413.chess.model.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Event-driven graphical chess interface for CSC 413.
 *
 * Implements BoardView using Java Swing.
 *
 * Responsibilities:
 * - Display the chessboard and pieces.
 * - Handle mouse clicks.
 * - Highlight legal destinations.
 * - Display game status and messages.
 * - Provide Undo functionality.
 * - Support queen promotion.
 *
 * All chess rules remain in Game and the engine.
 * All player actions go through GameController.
 *
 * This view never calls controller.runLoop().
 */
public final class SwingView implements BoardView {

    // ---------------------------------------------------------
    // Board appearance
    // ---------------------------------------------------------

    private static final java.awt.Color LIGHT =
            new java.awt.Color(240, 217, 181);

    private static final java.awt.Color DARK =
            new java.awt.Color(181, 136, 99);

    private static final java.awt.Color SELECTED =
            new java.awt.Color(255, 210, 65);

    private static final java.awt.Color TARGET =
            new java.awt.Color(65, 170, 95);

    private static final int SQUARE = 72;

    // ---------------------------------------------------------
    // Swing components
    // ---------------------------------------------------------

    private JFrame frame;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private JLabel messageLabel;

    // ---------------------------------------------------------
    // MVC references
    // ---------------------------------------------------------

    private GameController controller;
    private Game game;

    // ---------------------------------------------------------
    // UI-only state
    // ---------------------------------------------------------

    private Position selected;

    private final Set<Position> destinations = new HashSet<>();

    private boolean finished;

    // ---------------------------------------------------------
    // Start graphical interface
    // ---------------------------------------------------------

    @Override
    public void start(GameController controller) {

        this.controller = controller;

        SwingUtilities.invokeLater(() -> {

            if (frame != null) {
                return;
            }

            frame = new JFrame("CSC 413 Chess - SwingView");

            frame.setDefaultCloseOperation(
                    WindowConstants.DISPOSE_ON_CLOSE
            );

            frame.setLayout(new BorderLayout(8, 8));

            // Create chessboard.
            boardPanel = new BoardPanel();

            frame.add(boardPanel, BorderLayout.CENTER);

            // Create bottom information panel.
            JPanel footer = new JPanel(new BorderLayout(8, 8));

            JPanel labels = new JPanel(new GridLayout(2, 1));

            statusLabel = new JLabel();

            statusLabel.setFont(
                    statusLabel.getFont().deriveFont(Font.BOLD, 15f)
            );

            messageLabel = new JLabel(
                    "Select a piece, then a destination."
            );

            labels.add(statusLabel);
            labels.add(messageLabel);

            footer.add(labels, BorderLayout.CENTER);

            // Undo button.
            JButton undo = new JButton("Undo");

            undo.addActionListener(e -> {

                if (finished) {
                    showMessage("Game finished.");
                    return;
                }

                clearSelection();

                controller.handle(new PlayerAction.Undo());
            });

            footer.add(undo, BorderLayout.EAST);

            footer.setBorder(
                    BorderFactory.createEmptyBorder(6, 10, 10, 10)
            );

            frame.add(footer, BorderLayout.SOUTH);

            // Clean up references when window closes.
            frame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent e) {

                    frame = null;
                    boardPanel = null;
                    statusLabel = null;
                    messageLabel = null;
                }
            });

            frame.pack();

            frame.setLocationRelativeTo(null);

            updateStatus();

            frame.setVisible(true);
        });
    }

    // ---------------------------------------------------------
    // Render current game position
    // ---------------------------------------------------------

    @Override
    public void render(Game game) {

        SwingUtilities.invokeLater(() -> {

            this.game = game;

            updateStatus();

            if (boardPanel != null) {
                boardPanel.repaint();
            }
        });
    }

    // ---------------------------------------------------------
    // Display messages
    // ---------------------------------------------------------

    @Override
    public void showMessage(String message) {

        SwingUtilities.invokeLater(() -> {

            if (messageLabel != null) {
                messageLabel.setText(message);
            }
        });
    }

    // ---------------------------------------------------------
    // Pawn promotion
    // ---------------------------------------------------------

    @Override
    public PieceType promotionChoice(Game game) {

        // Default promotion is queen.
        return PieceType.QUEEN;
    }

    // ---------------------------------------------------------
    // Close graphical view
    // ---------------------------------------------------------

    @Override
    public void close() {

        SwingUtilities.invokeLater(() -> {

            // Preserve the final board after checkmate or stalemate.
            if (game != null && game.isGameOver()) {

                finished = true;

                clearSelection();

                updateStatus();

            } else if (frame != null) {

                frame.dispose();
            }
        });
    }

    // ---------------------------------------------------------
    // Update game-status label
    // ---------------------------------------------------------

    private void updateStatus() {

        if (statusLabel == null || game == null) {
            return;
        }

        GameStatus status = game.status();

        String text = switch (status) {

            case CHECKMATE ->
                    "Checkmate - "
                            + game.sideToMove().opposite()
                            + " wins";

            case STALEMATE ->
                    "Stalemate - draw";

            case CHECK ->
                    game.sideToMove() + " to move - CHECK";

            default ->
                    game.sideToMove() + " to move";
        };

        statusLabel.setText(text);
    }

    // ---------------------------------------------------------
    // Clear piece selection
    // ---------------------------------------------------------

    private void clearSelection() {

        selected = null;

        destinations.clear();

        if (boardPanel != null) {
            boardPanel.repaint();
        }
    }

    // ---------------------------------------------------------
    // Handle chessboard mouse clicks
    // ---------------------------------------------------------

    private void clickSquare(Position square) {

        if (game == null
                || controller == null
                || finished
                || game.isGameOver()) {

            return;
        }

        Piece piece = game.board().pieceAt(square);

        // First click: select a piece.
        if (selected == null) {

            select(square, piece);

            return;
        }

        // Clicking the selected square cancels selection.
        if (selected.equals(square)) {

            clearSelection();

            return;
        }

        // Clicking another friendly piece changes selection.
        if (piece != null
                && piece.color() == game.sideToMove()) {

            select(square, piece);

            return;
        }

        // Second click: attempt the move.
        Position from = selected;

        clearSelection();

        String moveNotation =
                notation(from) + notation(square);

        // The controller validates and executes the move.
        controller.handle(
                new PlayerAction.Move(moveNotation)
        );
    }

    // ---------------------------------------------------------
    // Select piece and highlight legal destinations
    // ---------------------------------------------------------

    private void select(Position square, Piece piece) {

        if (piece == null
                || piece.color() != game.sideToMove()) {

            showMessage(
                    "Select a piece belonging to "
                            + game.sideToMove()
                            + "."
            );

            return;
        }

        selected = square;

        destinations.clear();

        // Legal destinations come exclusively from Game.
        for (Move move : game.legalMoves()) {

            if (move.from().equals(square)) {

                destinations.add(move.to());
            }
        }

        if (destinations.isEmpty()) {

            showMessage(
                    "This piece has no legal moves."
            );

        } else {

            showMessage(
                    "Choose a highlighted destination."
            );
        }

        boardPanel.repaint();
    }

    // ---------------------------------------------------------
    // Convert Position into chess notation
    // ---------------------------------------------------------

    private static String notation(Position position) {

        return ""
                + (char) ('a' + position.file())
                + (char) ('1' + position.rank());
    }

    // ---------------------------------------------------------
    // Unicode chess-piece symbols
    // ---------------------------------------------------------

    private static String glyph(Piece piece) {

        if (piece == null) {
            return "";
        }

        boolean white = piece.color() == Color.WHITE;

        return switch (piece.type()) {

            case KING ->
                    white ? "\u2654" : "\u265A";

            case QUEEN ->
                    white ? "\u2655" : "\u265B";

            case ROOK ->
                    white ? "\u2656" : "\u265C";

            case BISHOP ->
                    white ? "\u2657" : "\u265D";

            case KNIGHT ->
                    white ? "\u2658" : "\u265E";

            case PAWN ->
                    white ? "\u2659" : "\u265F";
        };
    }

    // =========================================================
    // Inner class: graphical chessboard
    // =========================================================

    private final class BoardPanel extends JPanel {

        // -----------------------------------------------------
        // BoardPanel constructor
        // -----------------------------------------------------

        BoardPanel() {

            setPreferredSize(
                    new Dimension(SQUARE * 8, SQUARE * 8)
            );

            setFont(
                    new Font("Serif", Font.PLAIN, 54)
            );

            // Register mouse-click listener.
            addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseClicked(
                                java.awt.event.MouseEvent e) {

                            int file = e.getX() / SQUARE;

                            int row = e.getY() / SQUARE;

                            if (file >= 0
                                    && file < 8
                                    && row >= 0
                                    && row < 8) {

                                // Convert screen coordinates
                                // to chessboard coordinates.
                                Position square =
                                        new Position(file, 7 - row);

                                clickSquare(square);
                            }
                        }
                    }
            );
        }

        // -----------------------------------------------------
        // Draw chessboard and pieces
        // -----------------------------------------------------

        @Override
        protected void paintComponent(Graphics graphics) {

            super.paintComponent(graphics);

            Graphics2D g =
                    (Graphics2D) graphics.create();

            try {

                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g.setFont(getFont());

                FontMetrics fm = g.getFontMetrics();

                // Draw all 64 squares.
                for (int row = 0; row < 8; row++) {

                    for (int file = 0; file < 8; file++) {

                        int x = file * SQUARE;

                        int y = row * SQUARE;

                        Position square =
                                new Position(file, 7 - row);

                        // -------------------------------------
                        // Square background
                        // -------------------------------------

                        if ((file + row) % 2 == 0) {

                            g.setColor(LIGHT);

                        } else {

                            g.setColor(DARK);
                        }

                        g.fillRect(
                                x,
                                y,
                                SQUARE,
                                SQUARE
                        );

                        // -------------------------------------
                        // Selected square highlight
                        // -------------------------------------

                        if (square.equals(selected)) {

                            g.setColor(SELECTED);

                            g.setStroke(
                                    new BasicStroke(5)
                            );

                            g.drawRect(
                                    x + 3,
                                    y + 3,
                                    SQUARE - 6,
                                    SQUARE - 6
                            );
                        }

                        // -------------------------------------
                        // Legal destination indicator
                        // -------------------------------------

                        if (destinations.contains(square)) {

                            g.setColor(TARGET);

                            g.fillOval(
                                    x + SQUARE / 2 - 11,
                                    y + SQUARE / 2 - 11,
                                    22,
                                    22
                            );
                        }

                        // -------------------------------------
                        // Retrieve piece from game model
                        // -------------------------------------

                        if (game == null) {
                            continue;
                        }

                        Piece piece =
                                game.board().pieceAt(square);

                        if (piece == null) {
                            continue;
                        }

                        // -------------------------------------
                        // Obtain chess-piece symbol
                        // -------------------------------------

                        String symbol = glyph(piece);

                        int tx =
                                x + (SQUARE
                                        - fm.stringWidth(symbol)) / 2;

                        int ty =
                                y + (SQUARE
                                        - fm.getHeight()) / 2
                                        + fm.getAscent();

                        // -------------------------------------
                        // Piece fill and outline colors
                        // -------------------------------------

                        java.awt.Color fill =
                                piece.color() == Color.WHITE
                                        ? java.awt.Color.WHITE
                                        : new java.awt.Color(
                                        24, 28, 35
                                );

                        java.awt.Color outline =
                                piece.color() == Color.WHITE
                                        ? new java.awt.Color(
                                        24, 28, 35
                                )
                                        : java.awt.Color.WHITE;

                        // -------------------------------------
                        // Create vector outline of chess piece
                        // -------------------------------------

                        Shape shape =
                                getFont()
                                        .createGlyphVector(
                                                g.getFontRenderContext(),
                                                symbol
                                        )
                                        .getOutline(tx, ty);

                        // -------------------------------------
                        // Fill chess piece
                        // -------------------------------------

                        g.setColor(fill);

                        g.fill(shape);

                        // -------------------------------------
                        // Draw contrasting outline
                        // -------------------------------------

                        g.setStroke(
                                new BasicStroke(1.8f)
                        );

                        g.setColor(outline);

                        g.draw(shape);
                    }
                }

            } finally {

                // Release Graphics2D resources.
                g.dispose();
            }
        }
    }
}
