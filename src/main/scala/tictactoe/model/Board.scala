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
  def empty: Board = new Board(Vector.fill(9)(None))
}
