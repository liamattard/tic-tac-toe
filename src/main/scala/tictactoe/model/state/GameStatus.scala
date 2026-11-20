package tictactoe.model.state

import tictactoe.model.Player

/** The current outcome of a match. */
sealed trait GameStatus

/** A match is ongoing, won by a player, or drawn. */
object GameStatus {
  case object InProgress extends GameStatus
  final case class Won(player: Player) extends GameStatus
  case object Draw extends GameStatus
}
