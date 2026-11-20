package tictactoe.component

import tictactoe.model.Board

final class TerminalDisplay(enabled: Boolean) {

  /** Clears and redraws a styled board, or returns the plain grid. */
  def board(board: Board): String = {
    val plain = BoardRenderer.render(board)
    if (enabled)
      Ansi.clearScreen + plain.replace("X", Ansi.blue("X")).replace("O", Ansi.yellow("O"))
    else plain
  }

  /** Highlights invalid input and rejected moves in red. */
  def error(message: String): String = if (enabled) Ansi.red(message) else message

  /** Highlights the winner in green. */
  def success(message: String): String = if (enabled) Ansi.green(message) else message
}
