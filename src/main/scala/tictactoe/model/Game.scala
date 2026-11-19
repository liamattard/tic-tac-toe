package tictactoe.model

import tictactoe.model.result.MoveError

/** Immutable match state with two players and an index identifying whose turn it is. */
final class Game private (
    val board: Board,
    val players: Vector[Player],
    private val currentPlayerIndex: Int
) {

  /** Returns the participant whose mark will be placed next. */
  def currentPlayer: Player = players(currentPlayerIndex)

  /** Places the current mark at index and changes turns only on success. */
  def play(position: Int): Either[MoveError, Game] =
    board.place(position, currentPlayer.mark).map { updatedBoard =>
      new Game(updatedBoard, players, 1 - currentPlayerIndex)
    }
}

object Game {

  /** Starts with an empty board and the first named player assigned X. */
  def start(xName: String, oName: String): Game =
    new Game(Board.empty, Vector(Player(xName, Mark.X), Player(oName, Mark.O)), 0)
}
