# CSC 413 — M7 Submission: Views, Generated and Compared

## 1. Group

[COMPLETE: Enter "Alone" if working individually; otherwise list group members and GitHub usernames.]

## 2. AI Tools Used

- Tool: ChatGPT
- Model: GPT-6
- Purpose: Generate and compare Java Swing chess views and an additional terminal-based chess view.

## 3. Additional View

**Class:** `RotatingConsoleView.java`

**Command-line argument:** `rotate`

The additional view displays a terminal chessboard that rotates automatically based on the current player. The active player's pieces appear at the bottom.

The view accepts moves in long algebraic notation and supports Undo, legal-move listing, Quit, and pawn promotion using the existing controller contract.

## 4. Three Independent Generation Runs

### Run 1 — Basic Swing Implementation

**Branch:** `m7-run-1`

**Commit:** `b137427`

**Instruction difference:** Original `AGENTS.md`.

**Request:** [COMPLETE: Paste the exact Run 1 generation request, word for word.]

**Result:** Generated a playable Swing chessboard with clickable pieces, turn information, game-status messages, and an Undo button.

**Verification:** The graphical window launched successfully. Piece movement, Undo, illegal-move feedback, and Fool's Mate were manually tested.

### Run 2 — Legal-Move Highlighting

**Branch:** `m7-run-2`

**Commit:** `1e71e75`

**Instruction change:** Required visibly highlighting selected pieces and their legal destination squares using `game.legalMoves()`.

**Request:** [COMPLETE: Paste the exact request submitted in the separate Run 2 conversation.]

**Result:** Generated a new Swing implementation with legal-destination highlighting.

**Verification:** All 83 Maven tests passed, and the graphical interface launched successfully. The application displayed a playable chessboard.

### Run 3 — Chessboard Coordinates

**Branch:** `m7-run-3`

**Commit:** `dcde81a`

**Instruction change:** Required visible chessboard coordinates (a–h and 1–8).

**Request:** [COMPLETE: Paste the exact request submitted in the separate Run 3 conversation.]

**Result:** Generated a new Swing interface with coordinate labels.

**Verification:** All 83 Maven tests passed with zero failures and zero errors.

### Additional View Generation

**Branch:** `m7-run-3`

**Commit:** `88fb586`

**Request:** Implement a `RotatingConsoleView` using the existing `BoardView` and `PlayerAction` contracts. The board must rotate according to `game.sideToMove()`, remain playable through `GameController`, and support chess moves and commands.

**Verification:** All 83 existing Maven tests passed. The rotating console was also manually tested.

## 5. AGENTS.md Instruction Change

For Run 2, I changed the selection instruction to require visible legal-destination highlighting based on `game.legalMoves()`.

For Run 3, I changed the selection instruction to require visible chessboard coordinates.

These changes allowed me to evaluate how different instructions influenced the generated interfaces.

## 6. Code Review — Three Important Locations

[COMPLETE: Each review needs the exact file and line number, what the code does, how it was checked, and whether it was kept, changed, or rejected.]

**Review 1 — User interaction**

- File and line: [COMPLETE]
- Behavior: Processing mouse clicks and selecting source/destination squares
- Verification: [COMPLETE]
- Decision and reason: [COMPLETE]

**Review 2 — Controller integration**

- File and line: [COMPLETE]
- Behavior: Passing player actions through `GameController.handle()` instead of directly modifying the game
- Verification: [COMPLETE]
- Decision and reason: [COMPLETE]

**Review 3 — Board rendering**

- File and line: [COMPLETE]
- Behavior: Rendering the chessboard, piece glyphs, and coordinates
- Verification: [COMPLETE]
- Decision and reason: [COMPLETE]

## 7. Comparison of Implementations

### Where does selected-square state live?

- Run 1: [COMPLETE: File, line, and explanation]
- Run 2: [COMPLETE]
- Run 3: [COMPLETE]

### How does each view obtain legal moves?

- Run 1: [COMPLETE: File, line, and explanation]
- Run 2: [COMPLETE]
- Run 3: [COMPLETE]

### How does each implementation handle pawn promotion?

- Run 1: [COMPLETE]
- Run 2: [COMPLETE]
- Run 3: [COMPLETE]

### Which implementation is easier to maintain?

[COMPLETE: Explain the design differences, citing actual code.]

**Provisional choice:** Run 3.

Run 3 was selected as the candidate for final integration because visible board coordinates improve usability and facilitate debugging. Final selection remains subject to code comparison and gameplay verification.

## 8. Applied Idea From a Rejected Run

**Source:** Run 2 — legal-destination highlighting.

**Applied to:** [COMPLETE: Identify your implementation and exact file/line.]

**Explanation:** [COMPLETE: Describe the idea you independently implemented and how it differs from Run 2.]

Do not count this as completed unless the idea has actually been implemented.

## 9. Debugger Evidence

[COMPLETE: Document a genuine debugging session.]

If an actual bug was found, record:

- Symptom
- Stack trace, breakpoint, or log evidence
- Root cause
- Correction
- Verification after the correction

If no bug was found, document a breakpoint in the Swing click handler, one move traced through it, predicted values, and observed values.

## 10. Independent Hand-Written Modification

**Commit:** [COMPLETE: Enter the commit beginning `M7 by hand:`.]

**File and line:** [COMPLETE]

**Change:** [COMPLETE: Describe what you personally modified without AI-generated implementation code.]

**Reason:** [COMPLETE]

**Verification:** [COMPLETE]

## 11. Final Verification

- Original Maven test count: 83
- Existing tests modified or deleted: None, subject to final Git diff verification
- Swing launched: Yes
- Rotating console tested: Yes
- Final `main` build: [COMPLETE]
- Submission tag: `submit-m7` — [COMPLETE after pushing]

## 12. Final Reflection

This milestone demonstrated how an MVC architecture allows different presentation interfaces to operate against the same game logic.

The existing `BoardView` interface and `GameController` enabled graphical and terminal-based interfaces without requiring modifications to chess rules.

The three independent runs provided a basis for comparing generated implementations, while code review and manual verification were necessary to determine which behaviors should be retained.