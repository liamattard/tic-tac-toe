package tictactoe.component

/** Terminal escape sequences kept separate from game presentation. */
private[component] object Ansi {
  private val escape = "\u001b["
  private val reset  = s"${escape}0m"

  val clearScreen: String = s"${escape}2J${escape}H"

  private def colour(text: String, code: Int): String = s"$escape${code}m$text$reset"

  def red(text: String): String    = colour(text, 31)
  def green(text: String): String  = colour(text, 32)
  def blue(text: String): String   = colour(text, 34)
  def yellow(text: String): String = colour(text, 33)
}
