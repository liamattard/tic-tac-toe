# Component ideas

First sketch of what the game could contain; types are not final.

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

Main / terminal renderer: read input and display the board and messages.

Names belong to Player; board cells only contain marks. See [first slice and open questions](README.md).
