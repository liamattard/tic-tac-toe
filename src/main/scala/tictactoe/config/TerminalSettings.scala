package tictactoe.config

/** Presentation options selected at startup. */
final case class TerminalSettings(ansiEnabled: Boolean)

/** Enables ANSI by default, with explicit plain-output and environment overrides. */
object TerminalSettings {

  /** Parses display options; --plain takes precedence over --ansi. */
  def fromArgs(args: Array[String]): TerminalSettings =
    fromArgs(args, sys.env)

  private[config] def fromArgs(
      args: Array[String],
      environment: Map[String, String]
  ): TerminalSettings = {
    val enabledByDefault = !environment.get("TERM").contains("dumb") &&
      !environment.contains("NO_COLOR")
    TerminalSettings(!args.contains("--plain") && (args.contains("--ansi") || enabledByDefault))
  }
}
