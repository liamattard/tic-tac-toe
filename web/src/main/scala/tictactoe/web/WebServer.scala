package tictactoe.web

import com.sun.net.httpserver.{HttpExchange, HttpServer}
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID
import scala.collection.mutable
import scala.util.control.NonFatal
import tictactoe.model.{Game, Player}
import tictactoe.model.result.MoveError
import tictactoe.model.state.GameStatus

/** Local prototype adapter; all game rules remain in the shared model. */
final class WebServer private (private val server: HttpServer) extends AutoCloseable {
  def port: Int              = server.getAddress.getPort
  override def close(): Unit = server.stop(0)
}

object WebServer {
  private final case class RequestError(status: Int, message: String)
      extends RuntimeException(message)
  private val assets = Map(
    "/"          -> ("index.html", "text/html; charset=utf-8"),
    "/style.css" -> ("style.css", "text/css; charset=utf-8"),
    "/app.js"    -> ("app.js", "text/javascript; charset=utf-8")
  )

  /** Port zero selects an available port, useful for isolated HTTP tests. */
  def start(port: Int = 8080): WebServer = {
    val server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0)
    val games  = mutable.Map.empty[String, Game]
    server.createContext(
      "/",
      exchange => {
        try {
          val path = exchange.getRequestURI.getPath
          assets.get(path) match {
            case Some((file, mime)) =>
              requireMethod(exchange, "GET")
              val stream = Option(getClass.getResourceAsStream(s"/web/$file"))
                .getOrElse(throw RequestError(404, "Asset not found"))
              val bytes =
                try stream.readAllBytes()
                finally stream.close()
              respond(exchange, 200, bytes, mime)
            case None if path == "/api/games" =>
              requireMethod(exchange, "POST")
              val body = readJson(exchange)
              val game =
                Game.start(name(body, "xName", "Player X"), name(body, "oName", "Player O"))
              val id = UUID.randomUUID().toString
              games.synchronized { games(id) = game }
              json(exchange, 201, state(id, game))
            case None if path.matches("/api/games/[^/]+/moves") =>
              requireMethod(exchange, "POST")
              val id   = path.split("/")(3)
              val body = readJson(exchange)
              val position = body.obj.get("position") match {
                case Some(ujson.Num(n)) if n.isValidInt => n.toInt
                case _ => throw RequestError(400, "position must be an integer")
              }
              val updated = games.synchronized {
                val game = games.getOrElse(id, throw RequestError(404, "Game not found"))
                game.play(position) match {
                  case Right(next) => games(id) = next; next
                  case Left(error) => throw RequestError(409, moveMessage(error))
                }
              }
              json(exchange, 200, state(id, updated))
            case _ => throw RequestError(404, "Not found")
          }
        } catch {
          case RequestError(status, message) =>
            json(exchange, status, ujson.Obj("error" -> message))
          case NonFatal(_) => json(exchange, 500, ujson.Obj("error" -> "Unexpected server error"))
        } finally exchange.close()
      }
    )
    server.start()
    new WebServer(server)
  }

  private def requireMethod(exchange: HttpExchange, expected: String): Unit =
    if (exchange.getRequestMethod != expected) {
      exchange.getResponseHeaders.set("Allow", expected)
      throw RequestError(405, s"Use $expected")
    }

  private def readJson(exchange: HttpExchange): ujson.Value = {
    val bytes = exchange.getRequestBody.readNBytes(8193)
    if (bytes.length > 8192) throw RequestError(413, "Request body exceeds 8 KiB")
    val value =
      try ujson.read(new String(bytes, UTF_8))
      catch { case NonFatal(_) => throw RequestError(400, "Invalid JSON") }
    if (!value.isInstanceOf[ujson.Obj]) throw RequestError(400, "Expected a JSON object")
    value
  }

  private def name(body: ujson.Value, key: String, default: String): String =
    body.obj.get(key) match {
      case None => default
      case Some(ujson.Str(value)) if value.length <= 60 =>
        Option(value.trim).filter(_.nonEmpty).getOrElse(default)
      case _ => throw RequestError(400, s"$key must be a string of at most 60 characters")
    }

  private def player(value: Player): ujson.Value =
    ujson.Obj("name" -> value.name, "mark" -> value.mark.toString)

  private def state(id: String, game: Game): ujson.Value = {
    val (status, winner) = game.status match {
      case GameStatus.InProgress => ("inProgress", ujson.Null)
      case GameStatus.Draw       => ("draw", ujson.Null)
      case GameStatus.Won(value) => ("won", player(value))
    }
    ujson.Obj(
      "id" -> id,
      "cells" -> ujson.Arr.from(
        game.board.cells.map(_.fold[ujson.Value](ujson.Null)(m => ujson.Str(m.toString)))
      ),
      "players"       -> ujson.Arr.from(game.players.map(player)),
      "currentPlayer" -> player(game.currentPlayer),
      "status"        -> status,
      "winner"        -> winner
    )
  }

  private def moveMessage(error: MoveError): String = error match {
    case MoveError.OutOfBounds    => "Choose a square from 0 to 8"
    case MoveError.OccupiedSquare => "That square is already occupied"
    case MoveError.GameFinished   => "This game has finished"
  }

  private def json(exchange: HttpExchange, status: Int, value: ujson.Value): Unit =
    respond(exchange, status, ujson.write(value).getBytes(UTF_8), "application/json; charset=utf-8")

  private def respond(
      exchange: HttpExchange,
      status: Int,
      bytes: Array[Byte],
      mime: String
  ): Unit = {
    exchange.getResponseHeaders.set("Content-Type", mime)
    exchange.getResponseHeaders.set("Cache-Control", "no-store")
    exchange.sendResponseHeaders(status, bytes.length.toLong)
    exchange.getResponseBody.write(bytes)
  }
}
