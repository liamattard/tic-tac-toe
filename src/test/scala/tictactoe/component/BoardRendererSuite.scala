package tictactoe.component

import tictactoe.model.{Board, Mark}

/** Checks the player-facing board layout. */
class BoardRendererSuite extends munit.FunSuite {
  test("an empty board shows squares numbered 1 to 9") {
    val expected =
      """1 | 2 | 3
        |---------
        |4 | 5 | 6
        |---------
        |7 | 8 | 9""".stripMargin

    assertEquals(BoardRenderer.render(Board.empty), expected)
  }

  test("occupied squares show marks instead of their numbers") {
    val board = Board.empty.place(0, Mark.X).flatMap(_.place(4, Mark.O)) match {
      case Right(board) => board
      case Left(error)  => fail(s"Expected a valid board, got $error")
    }
    val expected =
      """X | 2 | 3
        |---------
        |4 | O | 6
        |---------
        |7 | 8 | 9""".stripMargin

    assertEquals(BoardRenderer.render(board), expected)
  }
}
