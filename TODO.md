# TODO

## Planning
- [x] components
- [x] Game Loop

## Core game
- [x] Standard 3x3 grid
- [x] First slice: empty immutable board, marks, terminal renderer, and Main
- [x] Place a mark; reject occupied and out-of-range squares
- [x] Two player mode (Terminal)
- [x] Create the components
- [x] Error Handling
- [x] Clear terminal messages for input errors and results
- [x] Detect all winning lines and draws; stop after completion
- [x] Test rules, rejected moves, and a win on the final square
- [x] Document run/test commands and pin tool versions
- [x] Reproducible formatting checks
- [x] GitHub CI workflow for formatting and tests
- [ ] Verify the first hosted CI run after pushing
- [x] Terminal clearing screen each time
- [x] Terminal colours

## Nice to Have
- [ ] Player vs Computer
- [ ] Web Interface (vue?)
- [ ] Docker build and run instructions
- [x] Export the planning sketch as PNG for easy viewing


## Bot (future)
- [ ] Easy: select a random empty square.
- [ ] Medium: take an immediate winning move; otherwise advance an unblocked winning line containing the bot's own marks, without deliberately blocking the human. Define tie-breaking and fallback rules before implementation.
- [ ] Decide the hard difficulty approach:
  - Heuristic: take an immediate win, otherwise block an immediate human win, then choose a strategic move. This approach is not necessarily unbeatable because of forks.
  - Minimax: explore future moves and choose an optimal move. This approach can provide an unbeatable opponent.
