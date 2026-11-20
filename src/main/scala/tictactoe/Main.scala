package tictactoe

import tictactoe.service.GameLoopService
import tictactoe.config.{Logging, TerminalSettings}

object Main {

  def main(args: Array[String]): Unit = {
    val settings = TerminalSettings.fromArgs(args)

    Logging.initialise()
    try GameLoopService.run(terminalEffects = settings.ansiEnabled)
    finally Logging.close()
  }
}
