package tictactoe.model

import tictactoe.model.result.MoveError
import tictactoe.model.state.GameStatus

/** Checks game setup and turn management. */
class GameSuite extends munit.FunSuite {

  private def accepted(result: Either[MoveError, Game]): Game = result match {
    case Right(game) => game
    case Left(error) => fail(s"Expected an accepted move, got $error")
  }

  private def played(positions: Int*): Game =
    positions.foldLeft(Game.start("Liam", "Ġwanni")) { (game, position) =>
      accepted(game.play(position))
    }

  test("a new game is in progress") {
    assertEquals(Game.start("Liam", "Ġwanni").status, GameStatus.InProgress)
  }

  test("a game with no winner and empty squares stays in progress") {
    assertEquals(played(0, 4).status, GameStatus.InProgress)
  }

  test("a winning X line identifies the named X player") {
    val game = played(0, 3, 1, 4, 2)

    assertEquals(game.status, GameStatus.Won(Player("Liam", Mark.X)))
  }

  test("a winning O line identifies the named O player") {
    val game = played(0, 3, 1, 4, 8, 5)

    assertEquals(game.status, GameStatus.Won(Player("Ġwanni", Mark.O)))
  }

  test("a full board with no winner is a draw") {
    assertEquals(played(0, 1, 2, 4, 3, 5, 7, 6, 8).status, GameStatus.Draw)
  }

  test("a winning ninth move takes precedence over a draw") {
    val before = played(0, 1, 2, 3, 4, 5, 7, 6)
    val finished = accepted(before.play(8))

    assertEquals(before.status, GameStatus.InProgress)
    assert(finished.board.isFull)
    assertEquals(finished.status, GameStatus.Won(Player("Liam", Mark.X)))
  }

  test("moves after a win are rejected before checking the square") {
    val game = played(0, 3, 1, 4, 2)
    val cellsBefore = game.board.cells
    val playerBefore = game.currentPlayer

    List(6, 0, -1).foreach { position =>
      assertEquals(game.play(position), Left(MoveError.GameFinished))
    }
    assertEquals(game.board.cells, cellsBefore)
    assertEquals(game.currentPlayer, playerBefore)
    assertEquals(game.status, GameStatus.Won(Player("Liam", Mark.X)))
  }

  test("moves after a draw are rejected") {
    val game = played(0, 1, 2, 4, 3, 5, 7, 6, 8)

    assertEquals(game.play(0), Left(MoveError.GameFinished))
    assertEquals(game.status, GameStatus.Draw)
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
