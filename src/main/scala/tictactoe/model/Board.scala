package tictactoe.model

import tictactoe.model.result.MoveError

/** Immutable nine-cell board; None represents an empty square.
  */
final class Board private (val cells: Vector[Option[Mark]]) {

  /** Returns a new board after a valid placement at index 0–8, or a rejection. */
  def place(position: Int, mark: Mark): Either[MoveError, Board] = {
    if (position < 0 || position >= cells.size) Left(MoveError.OutOfBounds)
    else if (cells(position).isDefined) Left(MoveError.OccupiedSquare)
    else Right(new Board(cells.updated(position, Some(mark))))
  }

  /** Returns true when every square contains a mark, regardless of the winner. */
  def isFull: Boolean = cells.forall(_.isDefined)

  /** Returns the mark occupying a complete winning line, if one exists. */
  def winner: Option[Mark] =
    Board.winningLines.iterator.map(line => line.map(cells)).collectFirst {
      case List(Some(a), Some(b), Some(c)) if a == b && b == c => a
    }

  /** Returns a compact board representation for debugging. */
  override def toString: String =
    cells
      .map(_.fold(".")(_.toString))
      .grouped(3)
      .map(_.mkString(" | "))
      .mkString("\n")

}

object Board {
  private val winningLines = List(
    List(0, 1, 2),
    List(3, 4, 5),
    List(6, 7, 8), // rows

    List(0, 3, 6),
    List(1, 4, 7),
    List(2, 5, 8), // columns

    List(0, 4, 8),
    List(2, 4, 6) // diagonals
  )

  /** Creates a standard board with nine empty cells. */
  def empty: Board = new Board(Vector.fill(9)(None))
}
