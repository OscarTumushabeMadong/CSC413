
# CSC 413 — M5 Chess Engine Design

This document describes the engine and model architecture
after Milestone M5.

## 1. Class Diagram

The class diagram will describe the classes, public methods,
inheritance relationships, composition, and dependencies
in the engine and model packages.

```mermaid
classDiagram
    direction TB

    namespace Engine {
        class Game {
            -Board board
            -List~Move~ history
            -Color sideToMove
            +Game()
            +Game(Board board, Color sideToMove)
            +board() Board
            +sideToMove() Color
            +history() List~Move~
            +legalMoves() List~Move~
            +findLegalMove(String notation) Optional~Move~
            +play(Move move) void
            +undoLastMove() Optional~Move~
        }

        class MoveGenerator {
            <<utility>>
            -MoveGenerator()
            +legalMoves(Board board, Color color)$ List~Move~
            ~pseudoLegalMoves(Board board, Color color)$ List~Move~
            +isAttacked(Board board, Position target, Color attacker)$ boolean
            +isInCheck(Board board, Color color)$ boolean
        }
    }

    namespace Model {
        class Board {
            -Piece[][] squares
            +Board()
            +pieceAt(Position position) Piece
            +isEmpty(Position position) boolean
            +place(Position position, Piece piece) void
            +positionsOf(Color color) List~Position~
            +toString() String
            +apply(Move move) void
            +undo(Move move) void
            +kingPosition(Color color) Position
        }

        class Piece {
            <<abstract>>
            -Color color
            -PieceType type
            #Piece(Color color, PieceType type)
            +pseudoLegalMoves(Board board, Position from)* List~Move~
            +attacks(Board board, Position from, Position target) boolean
            #steppingMoves(Board board, Position from, int[][] offsets) List~Move~
            #slidingMoves(Board board, Position from, int[][] directions) List~Move~
            +color() Color
            +type() PieceType
            +symbol() char
            +toString() String
        }

        class Pawn {
            +Pawn(Color color)
            +pseudoLegalMoves(Board board, Position from) List~Move~
            +attacks(Board board, Position from, Position target) boolean
        }

        class Knight {
            +Knight(Color color)
        }

        class Bishop {
            +Bishop(Color color)
        }

        class Rook {
            +Rook(Color color)
        }

        class Queen {
            +Queen(Color color)
        }

        class King {
            +King(Color color)
        }

        class Position {
            <<record>>
            +file() int
            +rank() int
            +parse(String algebraic)$ Position
            +toAlgebraic() String
            +toString() String
            +offsetOrNull(int fileOffset, int rankOffset) Position
        }

        class Move {
            <<record>>
            +from() Position
            +to() Position
            +moved() Piece
            +captured() Piece
            +promotesTo() PieceType
            +quiet(Position from, Position to, Piece moved)$ Move
            +capture(Position from, Position to, Piece moved, Piece captured)$ Move
            +promotion(Position from, Position to, Piece moved, Piece captured, PieceType promotesTo)$ Move
            +isCapture() boolean
            +isPromotion() boolean
            +toString() String
        }

        class Color {
            <<enumeration>>
            WHITE
            BLACK
            +opposite() Color
            +pawnDirection() int
            +pawnStartRank() int
            +promotionRank() int
        }

        class PieceType {
            <<enumeration>>
            PAWN
            KNIGHT
            BISHOP
            ROOK
            QUEEN
            KING
            +symbol() char
            +fromSymbol(char letter)$ PieceType
        }
    }

    Piece <|-- Pawn
    Piece <|-- Knight
    Piece <|-- Bishop
    Piece <|-- Rook
    Piece <|-- Queen
    Piece <|-- King

    Game "1" *-- "1" Board : owns
    Game "1" *-- "0..*" Move : history
    Game --> Color : sideToMove
    Game ..> MoveGenerator : delegates

    Board "1" o-- "0..*" Piece : squares
    Board ..> Position : indexes
    Board ..> Move : apply and undo

    MoveGenerator ..> Board : examines
    MoveGenerator ..> Piece : asks for moves
    MoveGenerator ..> Move : filters
    MoveGenerator ..> Color : selects side
    MoveGenerator ..> Position : checks squares

    Move --> Position : from and to
    Move --> Piece : moved and captured
    Move --> PieceType : promotesTo

    Piece --> Color : color
    Piece --> PieceType : type
    Piece ..> Board : movement rules
    Piece ..> Position : coordinates
    Piece ..> Move : generates
```


## 2. Sequence Diagram — Game.legalMoves()

The following sequence shows how Game requests legal moves
and how MoveGenerator filters moves that would leave the
current player's king in check.

```mermaid
sequenceDiagram
    actor Caller
    participant Game
    participant MG as MoveGenerator
    participant Board
    participant Piece

    Caller->>Game: legalMoves()
    Game->>MG: legalMoves(board, sideToMove)
    MG->>MG: pseudoLegalMoves(board, color)
    MG->>Board: positionsOf(color)
    Board-->>MG: occupied positions

    loop For each occupied position
        MG->>Board: pieceAt(from)
        Board-->>MG: piece
        MG->>Piece: pseudoLegalMoves(board, from)
        Piece-->>MG: candidate moves
    end

    loop For each candidate move
        MG->>Board: apply(move)
        MG->>MG: isInCheck(board, color)
        MG->>Board: kingPosition(color)
        Board-->>MG: king position

        opt King exists
            MG->>MG: isAttacked(board, king, oppositeColor)
            MG->>Board: positionsOf(oppositeColor)
            Board-->>MG: attacker positions

            loop For each attacking piece
                MG->>Board: pieceAt(from)
                Board-->>MG: piece
                MG->>Piece: attacks(board, from, king)
                Piece-->>MG: attack result
            end
        end

        MG->>Board: undo(move)

        alt King remains safe
            MG->>MG: legal.add(move)
        else King is in check
            Note over MG: Discard candidate
        end
    end

    MG-->>Game: legal moves
    Game-->>Caller: legal moves
```

## 3. Design Decisions

- Game owns the board, move history, and side to move.
- MoveGenerator is stateless and uses static methods.
- Each Piece subclass determines its own movement rules.
- Board manages piece placement and move application.
- Legal-move filtering temporarily applies each candidate,
  checks king safety, and restores the board using undo.
- The model and engine remain independent of presentation code.
