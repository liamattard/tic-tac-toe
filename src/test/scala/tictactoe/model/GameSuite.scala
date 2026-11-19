package tictactoe.model

import tictactoe.model.result.MoveError
import tictactoe.component.BoardRenderer

/** Checks game setup and turn management. */
class GameSuite extends munit.FunSuite {

  private def accepted(result: Either[MoveError, Game]): Game = result match {
    case Right(game) => game
    case Left(error) => fail(s"Expected an accepted move, got $error")
  }

  test("a new game has an empty board and two named players with X starting") {
    val game = Game.start("Liam", "Ġwanni")

    assertEquals(game.players, Vector(Player("Liam", Mark.X), Player("Ġwanni", Mark.O)))
    assertEquals(game.currentPlayer, Player("Liam", Mark.X))
    assertEquals(game.board.cells.size, 9)
    assert(game.board.cells.forall(_.isEmpty))
  }

  test("accepted moves place the current mark and alternate players") {
    val original = Game.start("Liam", "Ġwanni")
    val afterX   = accepted(original.play(4))
    val afterO   = accepted(afterX.play(0))

    BoardRenderer.render(afterO.board)
    print("hi")

    assertEquals(afterX.board.cells(4), Some(Mark.X))
    assertEquals(afterX.currentPlayer, Player("Ġwanni", Mark.O))
    assertEquals(afterO.board.cells(0), Some(Mark.O))
    assertEquals(afterO.board.cells(4), Some(Mark.X))
    assertEquals(afterO.currentPlayer, Player("Liam", Mark.X))
    assert(original.board.cells.forall(_.isEmpty))
    assertEquals(original.currentPlayer, Player("Liam", Mark.X))
  }

  test("an occupied square is rejected without changing the turn or board") {
    val game = accepted(Game.start("Liam", "Ġwanni").play(4))

    assertEquals(game.play(4), Left(MoveError.OccupiedSquare))
    assertEquals(game.currentPlayer, Player("Ġwanni", Mark.O))
    assertEquals(game.board.cells(4), Some(Mark.X))
    val retried = accepted(game.play(0))
    assertEquals(retried.board.cells(0), Some(Mark.O))
  }

  test("an out-of-range position is rejected without changing the turn or board") {
    val game = Game.start("Liam", "Ġwanni")

    List(-1, 9).foreach { position =>
      assertEquals(game.play(position), Left(MoveError.OutOfBounds))
    }
    assertEquals(game.currentPlayer, Player("Liam", Mark.X))
    assert(game.board.cells.forall(_.isEmpty))
    assertEquals(accepted(game.play(4)).board.cells(4), Some(Mark.X))
  }
}
