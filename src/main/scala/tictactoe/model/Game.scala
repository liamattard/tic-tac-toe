package tictactoe.model

import tictactoe.model.result.MoveError
import tictactoe.model.state.GameStatus

/** Immutable match state with two players and an index identifying whose turn it is. */
final class Game private (
    val board: Board,
    val players: Vector[Player],
    private val currentPlayerIndex: Int
) {

  /** Returns the participant whose turn is recorded; completed games accept no moves. */
  def currentPlayer: Player = players(currentPlayerIndex)

  /** Derives the outcome from the board, checking for a win before a draw. */
  def status: GameStatus = board.winner match {
    case Some(Mark.X)         => GameStatus.Won(players(0))
    case Some(Mark.O)         => GameStatus.Won(players(1))
    case None if board.isFull => GameStatus.Draw
    case None                 => GameStatus.InProgress
  }

  /** Places the current mark and changes turns on success; rejects completed games. */
  def play(position: Int): Either[MoveError, Game] =
    if (status != GameStatus.InProgress) Left(MoveError.GameFinished)
    else
      board.place(position, currentPlayer.mark).map { updatedBoard =>
        new Game(updatedBoard, players, 1 - currentPlayerIndex)
      }
}

object Game {

  /** Starts with an empty board and the first named player assigned X. */
  def start(xName: String, oName: String): Game =
    new Game(Board.empty, Vector(Player(xName, Mark.X), Player(oName, Mark.O)), 0)
}
