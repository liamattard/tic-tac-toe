package tictactoe.model

import tictactoe.model.result.MoveError

class BoardSuite extends munit.FunSuite {

  test("an empty board contains nine empty cells") {
    val board = Board.empty

    assertEquals(board.cells.size, 9)
    assert(board.cells.forall(_.isEmpty))
  }

  test(
    "placing X in the centre updates only that cell and leaves the original unchanged"
  ) {
    val original = Board.empty

    original.place(4, Mark.X) match {
      case Right(updated) =>
        assertEquals(updated.cells(4), Some(Mark.X))
        assert(updated.cells.patch(4, Nil, 1).forall(_.isEmpty))
        assert(original.cells.forall(_.isEmpty))
      case Left(error) => fail(s"Expected a valid move, got $error")
    }
  }

  test("an occupied square rejects a move and keeps the existing mark") {
    val board = Board.empty.place(4, Mark.X).toOption.get

    assertEquals(board.place(4, Mark.O), Left(MoveError.OccupiedSquare))
    assertEquals(board.cells(4), Some(Mark.X))
  }

  test("positions outside the board are rejected") {
    val board = Board.empty

    List(-1, 9, Int.MinValue, Int.MaxValue).foreach { position =>
      assertEquals(board.place(position, Mark.X), Left(MoveError.OutOfBounds))
    }
  }

  test("both marks can be placed at the board boundaries") {
    for {
      position <- List(0, 8)
      mark     <- List(Mark.X, Mark.O)
    } {
      val updated = Board.empty.place(position, mark).toOption.get

      assertEquals(updated.cells(position), Some(mark))
      assertEquals(updated.cells.size, 9)
      assertEquals(updated.cells.count(_.isDefined), 1)
    }
  }
}
