package tictactoe.model

import tictactoe.model.result.MoveError

class BoardSuite extends munit.FunSuite {

  // Just a utility function for the tests
  private def boardWith(moves: (Int, Mark)*): Board =
    moves.foldLeft(Board.empty) { case (board, (position, mark)) =>
      board.place(position, mark) match {
        case Right(updated) => updated
        case Left(error)    => fail(s"Invalid test move at $position: $error")
      }
    }

  private def assertWinningLines(lines: List[List[Int]]): Unit =
    lines.foreach { line =>
      List(Mark.X, Mark.O).foreach { mark =>
        val board = boardWith(line.map(position => position -> mark): _*)

        assertEquals(board.winner, Some(mark), s"Expected $mark to win on $line")
      }
    }

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

  test("an empty board has no winner") {
    assertEquals(Board.empty.winner, None)
  }

  test("an empty board is not full") {
    assert(!Board.empty.isFull)
  }

  test("a board with one empty square is not full") {
    val board = boardWith((0 until 8).map(position => position -> Mark.X): _*)

    assert(!board.isFull)
  }

  test("a board with nine marks is full even when it has a winner") {
    val board = boardWith((0 until 9).map(position => position -> Mark.X): _*)

    assert(board.isFull)
    assertEquals(board.winner, Some(Mark.X))
  }

  test("matching marks in any row win") {
    assertWinningLines(List(List(0, 1, 2), List(3, 4, 5), List(6, 7, 8)))
  }

  test("matching marks in any column win") {
    assertWinningLines(List(List(0, 3, 6), List(1, 4, 7), List(2, 5, 8)))
  }

  test("matching marks on either diagonal win") {
    assertWinningLines(List(List(0, 4, 8), List(2, 4, 6)))
  }

  test("two matching marks do not win") {
    assertEquals(boardWith(0 -> Mark.X, 1 -> Mark.X).winner, None)
  }

  test("a line containing different marks does not win") {
    assertEquals(boardWith(0 -> Mark.X, 1 -> Mark.O, 2 -> Mark.X).winner, None)
  }

  test("a full board without a winning line has no winner") {
    val marks = List(Mark.X, Mark.O, Mark.X, Mark.X, Mark.O, Mark.O, Mark.O, Mark.X, Mark.X)
    val board = boardWith(marks.zipWithIndex.map { case (mark, index) => index -> mark }: _*)

    assertEquals(board.winner, None)
    assert(board.isFull)
  }
}
