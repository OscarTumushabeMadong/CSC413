
# CSC 413 — M7 Coding Agent Instructions

## Project
This repository contains a Java 25 chess application.
Package root: edu.sfsu.csc413.chess

The application uses a model-view-controller architecture.

- model/: chess pieces, positions, moves, and board
- engine/: game state, legal moves, and game status
- factory/: object construction
- view/: presentation and user interaction
- Main.java: application entry point

Main currently selects ConsoleView or EmojiView and
constructs GameController with the selected BoardView.

## M7 Objective
Implement two additional playable BoardView implementations:

1. SwingView: a graphical chess interface using Swing.
2. A second non-Swing view with meaningful functionality
   beyond changing the existing piece glyphs.

Use only the Java standard library. Do not add dependencies.

## Files That Must Not Change
Never modify:
- model/
- engine/
- factory/
- GameController.java
- BoardView.java
- PlayerAction.java
- ConsoleView.java
- EmojiView.java
- pom.xml
- existing tests

If a requested feature appears to require changing one
of these files, stop and report the problem.

## Allowed Changes
- Add new Java classes under view/
- Add view tests under src/test/java/
- Modify Main.java only to select and launch new views
- Add project instruction and submission documentation

## Architecture Rules
- Keep chess rules in Game and the existing engine.
- Obtain legal moves from game.legalMoves().
- Obtain check, checkmate, and stalemate from game.status().
- Never call game.play() or game.undoLastMove()
  directly from a view.
- Route user actions through the existing controller.
- Do not duplicate move validation inside a view.
- Keep UI-only state, such as selected squares,
  inside the corresponding view.

## SwingView Requirements
- Display an 8x8 board with high-contrast chess pieces
  that remain clearly distinguishable on both light
  and dark squares.
- Allow selecting a piece and clicking its destination.
- Show the current turn and game-status messages.
- Display illegal-move feedback.
- Provide an Undo button using PlayerAction.Undo().
- Support promotion without crashing; queen is acceptable.
- Start the window and return without blocking.
- Never call controller.runLoop() from SwingView.
- Do not perform blocking input or long-running work
  inside a Swing event handler.
- Modal dialogs are not categorically prohibited;
  use them carefully for short, appropriate UI prompts.

## Second View Requirements
- Implement BoardView in a separate class.
- Support playing a complete game.
- Select it through a command-line argument in Main.
- Implement behavior beyond changing piece glyphs.
- Use only the Java standard library.

## Verification
- Run ./mvnw test after changes.
- Preserve all 83 existing passing tests.
- Test the graphical interface manually.
- Verify legal moves, illegal moves, Undo, and Fool's Mate.
- Do not modify protected files to make tests pass.

## Review and Reporting
Explain every new or modified file.
Report any failed tests or unresolved problems.
Do not claim a feature works without verification.
Keep generated runs separate for later comparison.
