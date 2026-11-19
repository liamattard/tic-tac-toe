package tictactoe

import tictactoe.component.BoardRenderer
import tictactoe.model.Board

object Main {

  def main(args: Array[String]): Unit = {
    println("Tic Tac Toe :)")
    println(BoardRenderer.render(Board.empty))
  }
}
