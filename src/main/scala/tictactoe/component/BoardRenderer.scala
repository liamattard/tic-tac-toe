package tictactoe.component

import tictactoe.model.Board

/** Formats the board for terminal display. */
object BoardRenderer {

  /** Returns a string of 3x3 grid of the board used for terminal rendering* */
  def render(board: Board): String =
    board.cells.zipWithIndex
      .map { case (cell, index) => cell.fold((index + 1).toString)(_.toString) }
      .grouped(3)
      .map(_.mkString(" | "))
      .mkString("\n---------\n")
}
