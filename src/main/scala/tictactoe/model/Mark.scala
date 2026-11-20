package tictactoe.model

sealed trait Mark

/** X & O */
object Mark {

  case object X extends Mark

  case object O extends Mark
}
