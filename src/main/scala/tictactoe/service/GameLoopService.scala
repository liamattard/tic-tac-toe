package tictactoe.service

import scala.annotation.tailrec
import scala.io.StdIn
import tictactoe.component.TerminalDisplay
import tictactoe.config.Logging
import tictactoe.model.Game
import tictactoe.model.result.MoveError
import tictactoe.model.state.GameStatus

final class GameLoopService private (
    readLine: () => String,
    writeLine: String => Unit,
    terminalEffects: Boolean
) {
  private val display = new TerminalDisplay(terminalEffects)
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
  private def loop(game: Game, error: Option[String] = None): Unit = {
    writeLine(display.board(game.board))
    error.foreach(message => writeLine(display.error(message)))
    game.status match {

      case GameStatus.Won(player) =>
        logger.info(s"Game finished: winner=${player.mark}")
        writeLine(display.success(s"${player.name} (${player.mark}) wins!"))

      case GameStatus.Draw =>
        logger.info("Game finished: draw")
        writeLine("It's a draw!")

      case GameStatus.InProgress =>
        readTurn(game) match {
          case Some(Right(next))   => loop(next)
          case Some(Left(message)) => loop(game, Some(message))
          case None                => endInput("Game ended: input closed")
        }
    }
  }

  private def readName(mark: String): Option[String] = {
    writeLine(s"Enter name for player $mark:")
    Option(readLine()).map(_.trim).map(name => if (name.isEmpty) s"Player $mark" else name)
  }

  private def readTurn(game: Game): Option[Either[String, Game]] = {
    val player = game.currentPlayer

    writeLine(s"${player.name} (${player.mark}), choose a square (1–9):")

    Option(readLine()).map { input =>
      input.trim.toIntOption.filter(number => number >= 1 && number <= 9) match {
        case Some(number) => attemptMove(game, number - 1)
        case None =>
          logger.fine("Move rejected: invalid square input")
          Left("Please enter a number from 1 to 9.")
      }
    }
  }

  private def attemptMove(game: Game, position: Int): Either[String, Game] =
    game.play(position) match {

      case Right(updated) =>
        logger.fine(s"Move accepted: mark=${game.currentPlayer.mark}, position=$position")
        Right(updated)

      case Left(error) =>
        logger.fine(s"Move rejected: position=$position, reason=$error")
        Left(errorMessage(error))
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
      writeLine: String => Unit = line => println(line),
      terminalEffects: Boolean = false
  ): Unit = new GameLoopService(readLine, writeLine, terminalEffects).run()
}
