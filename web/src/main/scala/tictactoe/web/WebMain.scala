package tictactoe.web

object WebMain {
  def main(args: Array[String]): Unit = {
    val port = args.headOption.map(_.toInt).getOrElse(8080)
    require(port >= 0 && port <= 65535, "Port must be between 0 and 65535")
    val server = WebServer.start(port)
    sys.addShutdownHook(server.close())
    println(s"Tic-tac-toe prototype: http://127.0.0.1:${server.port}")
    println("Press Ctrl+C to stop. Games are stored only in memory.")
  }
}
