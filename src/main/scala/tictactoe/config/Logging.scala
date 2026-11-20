package tictactoe.config

import java.nio.file.{Files, Paths}
import java.util.logging.{FileHandler, Formatter, Level, LogRecord, Logger}
import scala.util.control.NonFatal

/** Configures file-only diagnostics independently of terminal output. */
object Logging {
  private var fileHandler: Option[FileHandler] = None

  val logger: Logger = Logger.getLogger("tictactoe")
  logger.setUseParentHandlers(false)

  /** Enables file logging at startup, or reports failure without preventing play. */
  def initialise(): Unit = {
    if (fileHandler.isEmpty) {
      try fileHandler = Some(configure())
      catch {
        case NonFatal(error) =>
          Console.err.println(s"File logging unavailable: ${error.getMessage}")
      }
    }
  }

  /** Releases the log file; safe to call even if initialisation failed. */
  def close(): Unit = {
    fileHandler.foreach { handler =>
      logger.removeHandler(handler)
      handler.close()
    }
    fileHandler = None
  }

  /** Appends to a log capped at 1 MB; FINE is Java logging's debug level. */
  private def configure(): FileHandler = {
    Files.createDirectories(Paths.get("logs"))
    val handler = new FileHandler("logs/tictactoe.log", 1024 * 1024, 1, true)
    handler.setLevel(Level.FINE)
    handler.setFormatter(new Formatter {
      override def format(record: LogRecord): String =
        s"${record.getInstant} ${record.getLevel} ${formatMessage(record)}${System.lineSeparator()}"
    })
    logger.setLevel(Level.FINE)
    logger.addHandler(handler)
    handler
  }
}
