# Tic-tac-toe

A two-player terminal game written in Scala 2.13.

## Run

Requires JDK 17 and sbt. Scala 2.13.18 and sbt 1.12.15 are pinned in the build.
The first build downloads dependencies.

```sh
sbt run
```

Enter both players' names, or leave them blank for Player X and Player O.
X starts. Enter an available square from 1 to 9:

```text
1 | 2 | 3
---------
4 | 5 | 6
---------
7 | 8 | 9

Liam (X), choose a square (1–9):
```

Invalid input and occupied squares keep the same player's turn. The game shows
the final board and announces a win or draw. End terminal input with Ctrl+D on
Linux/macOS to exit early. One match is played per run.

## Assumptions and scope

The brief asks for tic-tac-toe using Scala without specifying a version or interface.
Scala 2.13 matches my professional experience. This version uses standard 3×3 rules,
two local human players, and X moving first. Names are optional; duplicate names are
allowed because marks identify the players.

Bots, configurable board sizes, a web interface, colours, and screen clearing are
outside this version. Delivery ideas are tracked in [TODO](TODO.md).

## Design

- `model`: immutable board and match state. Moves return `Either[MoveError, ...]`;
  expected rejections are values, not exceptions.
- `model.state`: match status, derived from the board. A win takes precedence over
  a draw, including on the ninth move.
- `component.BoardRenderer`: converts a board to text without printing it.
- `service.GameLoopService`: reads input, coordinates turns, and displays results.
  Input/output functions can be replaced for scripted tests.
- `config.Logging`: owns file diagnostics. `Main` initialises logging and closes
  it in `finally` after the match.

The board uses a flat immutable `Vector[Option[Mark]]`. Numbered terminal squares
map to indexes 0–8. Eight explicit winning lines keep the fixed-size rules easy
to inspect; arbitrary grid sizes are not implemented.

## Tests and development

Game behaviour was developed through failing tests, implementation, and refactoring.

```sh
sbt clean test
```

Tests cover board placement and winning lines, turn management, win/draw precedence,
completion guards, and scripted terminal sessions with invalid input and EOF.
A real terminal session was also checked, since scripted tests do not exercise
sbt's stdin handling. The game runs in a forked JVM with stdin connected.

The [planning notes](planning/README.md) link the early sketches and
[v1 release plan](planning/v1.md). These document proposals as well as final decisions.
The Git workflow uses feature branches into `develop`, then a release into `main`.

Scalafmt configuration is included for editor formatting. A command-line formatting
check, CI, and Docker packaging are still follow-up work.

## Logs

Diagnostics are written to `logs/tictactoe.log`, separate from the game display.
`FINE` is Java logging's debug level. Positions in diagnostics use indexes 0–8.
The file appends across runs and resets at approximately 1 MB, retaining one file.
Logs are excluded from Git. If the file cannot be opened, the game reports a warning
and continues without file logging.
