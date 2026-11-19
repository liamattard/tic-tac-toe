# Planning

- [Component notes](components.md)
- [Components and game loop](planning.excalidraw) — open with Excalidraw Editor in VS Code.
- [TODO](../TODO.md)

## First slice

Start with an empty board and a readable terminal printout.

- `Mark`: X or O.
- `Board`: nine cells stored as `Vector[Option[Mark]]`. `None` means empty.
- Terminal renderer: show marks or square numbers, in three rows. Keep printing outside the board.
- `Main`: print the empty board.

A flat vector makes numbered square selection straightforward and supports immutable updates. Use indexes 0–8 internally and show squares 1–9 to the player.

Next: placing a mark and rejecting an occupied square, then turns and game outcomes.

## Still to decide

- Exact types for move errors and outcomes; a win must identify the winner.
- Whether status is stored or calculated from the board. `Started` may not need to be separate from `InProgress`.
- How to guarantee exactly two players with different marks.
- Defaults for blank player names.

The sketch is a draft. During implementation, keep initialisation separate from redisplaying the board, display the final board, and handle end of input cleanly.
