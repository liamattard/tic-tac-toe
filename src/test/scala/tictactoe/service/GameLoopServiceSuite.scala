package tictactoe.service

import scala.collection.mutable.ListBuffer

/** Exercises complete terminal sessions with scripted input. */
class GameLoopServiceSuite extends munit.FunSuite {
  private def session(lines: String*): List[String] = {
    val input = lines.iterator
    val output = ListBuffer.empty[String]

    GameLoopService.run(
      readLine = () => if (input.hasNext) input.next() else null,
      writeLine = line => { output += line; () }
    )
    output.toList
  }

  test("a complete game displays the final board and named X winner") {
    val output = session("Liam", "Ġwanni", "1", "4", "2", "5", "3", "9")

    assertEquals(output.last, "Liam (X) wins!")
    assert(output(output.size - 2).startsWith("X | X | X"))
    assertEquals(output.count(_.contains("choose a square")), 5)
  }

  test("an O win announces O's name rather than the next player") {
    val output = session("Liam", "Ġwanni", "1", "4", "2", "5", "9", "6")

    assertEquals(output.last, "Ġwanni (O) wins!")
  }

  test("a complete draw displays the result") {
    val output = session("Liam", "Ġwanni", "1", "2", "3", "5", "4", "6", "8", "7", "9")

    assertEquals(output.last, "It's a draw!")
    assertEquals(output.count(_.contains("choose a square")), 9)
  }

  test("malformed and out-of-range input retry the same player") {
    val output = session("Liam", "Ġwanni", "hello", "", "0", "10", "999999999999", " 5 ")

    assertEquals(output.count(_ == "Please enter a number from 1 to 9."), 5)
    assertEquals(output.count(_ == "Liam (X), choose a square (1–9):"), 6)
    assert(output.exists(_.contains("4 | X | 6")))
    assert(output.contains("Ġwanni (O), choose a square (1–9):"))
  }

  test("an occupied square retries the same player without overwriting the board") {
    val output = session("Liam", "Ġwanni", "5", "5", "1")

    assert(output.contains("That square is taken. Choose an empty square."))
    assertEquals(output.count(_ == "Ġwanni (O), choose a square (1–9):"), 2)
    assert(output.exists(board => board.startsWith("O | 2 | 3") && board.contains("4 | X | 6")))
  }

  test("blank names use defaults and names are trimmed") {
    val output = session("  ", "  Ġwanni  ", "1")

    assert(output.contains("Player X (X), choose a square (1–9):"))
    assert(output.contains("Ġwanni (O), choose a square (1–9):"))
  }

  test("end of input during setup exits without starting a match") {
    List(List.empty[String], List("Liam")).foreach { input =>
      val output = session(input: _*)

      assertEquals(output.last, "Input ended. Goodbye!")
      assert(!output.exists(_.contains("choose a square")))
    }
  }

  test("end of input during a match exits cleanly") {
    val output = session("Liam", "Ġwanni")

    assertEquals(output.last, "Input ended. Goodbye!")
    assertEquals(output.count(_.contains("choose a square")), 1)
  }
}
