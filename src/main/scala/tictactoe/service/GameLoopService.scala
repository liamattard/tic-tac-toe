package tictactoe.service

import scala.annotation.tailrec
import scala.io.StdIn
import tictactoe.component.BoardRenderer
import tictactoe.config.Logging
import tictactoe.model.Game
import tictactoe.model.result.MoveError
import tictactoe.model.state.GameStatus

final class GameLoopService private (
    readLine: () => String,
    writeLine: String => Unit
) {
  private val logger  = Logging.logger
  private val goodbye = "Input ended. Goodbye!"

  /**   - Reads player names
    *   - starts a match
    *   - runs until completion or end of input
    */
  def run(): Unit = {

    writeLine("----------------------------------")
    writeLine("Welcome to the Tic Tac Toe game :)")
    writeLine("----------------------------------")

    val game = for {
      xName <- readName("X")
      oName <- readName("O")
    } yield Game.start(xName, oName)

    game match {
      case Some(started) =>
        logger.info("Game started")
        loop(started)
      case None => endInput("Game setup ended: input closed")
    }
  }

  @tailrec
  private def loop(game: Game): Unit = {
    writeLine(BoardRenderer.render(game.board))
    game.status match {

      case GameStatus.Won(player) =>
        logger.info(s"Game finished: winner=${player.mark}")
        writeLine(s"${player.name} (${player.mark}) wins!")

      case GameStatus.Draw =>
        logger.info("Game finished: draw")
        writeLine("It's a draw!")

      case GameStatus.InProgress =>
        readTurn(game) match {
          case Some(next) => loop(next)
          case None       => endInput("Game ended: input closed")
        }
    }
  }

  private def readName(mark: String): Option[String] = {
    writeLine(s"Enter name for player $mark:")
    Option(readLine()).map(_.trim).map(name => if (name.isEmpty) s"Player $mark" else name)
  }

  private def readTurn(game: Game): Option[Game] = {
    val player = game.currentPlayer

    writeLine(s"${player.name} (${player.mark}), choose a square (1–9):")

    Option(readLine()).map { input =>
      input.trim.toIntOption.filter(number => number >= 1 && number <= 9) match {
        case Some(number) => attemptMove(game, number - 1)
        case None =>
          logger.fine("Move rejected: invalid square input")
          writeLine("Please enter a number from 1 to 9.")
          game
      }
    }
  }

  private def attemptMove(game: Game, position: Int): Game =
    game.play(position) match {

      case Right(updated) =>
        logger.fine(s"Move accepted: mark=${game.currentPlayer.mark}, position=$position")
        updated

      case Left(error) =>
        logger.fine(s"Move rejected: position=$position, reason=$error")
        writeLine(errorMessage(error))
        game
    }

  private def errorMessage(error: MoveError): String = error match {
    case MoveError.OccupiedSquare => "That square is taken. Choose an empty square."
    case MoveError.OutOfBounds    => "Please enter a number from 1 to 9."
    case MoveError.GameFinished   => "The game has already finished."
  }

  private def endInput(message: String): Unit = {
    logger.info(message)
    writeLine(goodbye)
  }
}

/** Creates terminal sessions with console I/O by default. */
object GameLoopService {

  /** Runs one match; tests can provide scripted input and captured output. */
  def run(
      readLine: () => String = () => StdIn.readLine(),
      writeLine: String => Unit = line => println(line)
  ): Unit = new GameLoopService(readLine, writeLine).run()
}
