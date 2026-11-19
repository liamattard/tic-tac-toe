package tictactoe.model

import tictactoe.model.result.MoveError

/** Immutable nine-cell board; None represents an empty square.
  */
final class Board private (val cells: Vector[Option[Mark]]) {

  def place(position: Int, mark: Mark): Either[MoveError, Board] = {
    // Position out of bounds
    if (position < 0 || position >= cells.size) Left(MoveError.OutOfBounds)
    // Already taken square
    else if (cells(position).isDefined) Left(MoveError.OccupiedSquare)
    // Correct
    else Right(new Board(cells.updated(position, Some(mark))))
  }

  /** Returns true when every square contains a mark, regardless of the winner. */
  def isFull: Boolean = cells.forall(_.isDefined)

  /** Returns the mark occupying a complete winning line, if one exists. */
  def winner: Option[Mark] =
    Board.winningLines.iterator.map(line => line.map(cells)).collectFirst {
      case List(Some(a), Some(b), Some(c)) if a == b && b == c => a
    }

  /** will only be used in the Debug console by me :)
    */
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

  def empty: Board = new Board(Vector.fill(9)(None))
}
