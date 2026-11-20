# Early planning draft

These notes were written before implementation. See [decisions made for v1](README.md#decisions-made-for-v1) for the resulting design.


## Components

First sketch of what the game could contain;

Game  {
  board: Board
  players: Player[]
  currentPlayer: Player
  status: Enum(Started | In Progress | Won | Draw )
}


Player {
  name: String
  mark: Enum (X | O)
}

Board {
  cells: Vector[Option[Mark]]
}

Mark: X | O

Read Excalidraw to see game loop


## File structure planning

├── model/
│   ├── Mark.scala
│   ├── Player.scala
│   ├── Board.scala
│   └── Game.scala
├── service/
|   └── GameLoopService.scala
├── component/
|   └── BoardRenderer ? (lets see)
├── utils...

## Testing Approach..

TDD for the game behaviour
- An empty board contains nine empty cells
- Placing a mark updated a cell
- Rejected moves for occupied
- wins/draws..etc
