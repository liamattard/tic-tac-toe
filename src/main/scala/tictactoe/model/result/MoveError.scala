package tictactoe.model.result

/** A move rejection returned as a value, not thrown as an exception. */
sealed trait MoveError

/** Reasons a board or game cannot accept a move. */
object MoveError {
  case object OutOfBounds    extends MoveError
  case object OccupiedSquare extends MoveError
  case object GameFinished   extends MoveError
}
