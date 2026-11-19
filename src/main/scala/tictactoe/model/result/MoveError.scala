package tictactoe.model.result

sealed trait MoveError

/** Reasons a board cannot accept a move. */
object MoveError {
  case object OutOfBounds    extends MoveError
  case object OccupiedSquare extends MoveError
}
