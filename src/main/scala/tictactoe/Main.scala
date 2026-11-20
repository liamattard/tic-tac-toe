package tictactoe

import tictactoe.service.GameLoopService
import tictactoe.config.Logging

object Main {

  def main(args: Array[String]): Unit = {
    Logging.initialise()
    try GameLoopService.run()
    finally Logging.close()
  }
}
