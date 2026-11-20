package tictactoe.web

object WebMain {
  def main(args: Array[String]): Unit = {
    val (port, host) = settings(args, sys.env)
    val server       = WebServer.start(port, host)
    sys.addShutdownHook(server.close())
    println(s"Tic-tac-toe: listening on $host:${server.port}")
    println("Press Ctrl+C to stop. Games are stored only in memory.")
  }

  /** Hosting supplies PORT; local runs remain accessible only from this machine. */
  private[web] def settings(args: Array[String], env: Map[String, String]): (Int, String) = {
    val port = args.headOption.orElse(env.get("PORT")).map(_.toInt).getOrElse(8080)
    require(port >= 0 && port <= 65535, "Port must be between 0 and 65535")
    val defaultHost = if (env.contains("PORT")) "0.0.0.0" else "127.0.0.1"
    (port, env.getOrElse("HOST", defaultHost))
  }
}
