# Planning

- [Original component notes](planning.md)
- [Components and game loop](planning.excalidraw) — editable with Excalidraw.
- [PNG export](planning.png) — view without an extension.
- [Version 1 release plan](v1.md)
- [TODO](../TODO.md)

The notes and drawing are early drafts, not exact specifications of the final code.

## Decisions made for v1

- Fixed 3×3 board stored as `Vector[Option[Mark]]`; terminal squares 1–9 map to indexes 0–8.
- Named players have fixed X/O assignments; X starts. Private game construction keeps two players and a valid turn index.
- `Either` represents accepted or rejected moves. Rejections preserve state and turn.
- Status is derived from the board: win first, then draw, otherwise in progress.
- Blank names default to Player X/Player O; EOF exits cleanly.
- Rules, rendering, terminal interaction, and logging have separate responsibilities.

The first implementation slice was an empty board and a terminal printout. Tests
then guided placement, winning lines, fullness, turns, outcomes, and the terminal loop.
