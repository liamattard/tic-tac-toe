package tictactoe.web

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}

class WebServerSuite extends munit.FunSuite {
  private val client = HttpClient.newHttpClient()

  private def withServer(test: WebServer => Unit): Unit = {
    val server = WebServer.start(0)
    try test(server)
    finally server.close()
  }

  private def post(server: WebServer, path: String, body: String): (Int, ujson.Value) = {
    val request = HttpRequest
      .newBuilder(URI.create(s"http://127.0.0.1:${server.port}$path"))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(body))
      .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    (response.statusCode(), ujson.read(response.body()))
  }

  test("HTTP adapter starts a named game, applies shared rules, and rejects play after a win") {
    withServer { server =>
      val (created, initial) = post(server, "/api/games", """{"xName":" Alice ","oName":"Bob"}""")
      assertEquals(created, 201)
      assertEquals(initial("currentPlayer")("name").str, "Alice")
      assert(initial("cells").arr.forall(_ == ujson.Null))
      val path  = s"/api/games/${initial("id").str}/moves"
      val first = post(server, path, """{"position":0}""")
      assertEquals(first._1, 200)
      assertEquals(first._2("cells")(0).str, "X")
      assertEquals(first._2("currentPlayer")("mark").str, "O")
      assertEquals(post(server, path, """{"position":0}""")._1, 409)
      assertEquals(post(server, path, """{"position":9}""")._1, 409)
      Seq(3, 1, 4).foreach { position =>
        assertEquals(post(server, path, s"""{"position":$position}""")._1, 200)
      }
      val won = post(server, path, """{"position":2}""")._2
      assertEquals(won("status").str, "won")
      assertEquals(won("winner")("name").str, "Alice")
      assertEquals(post(server, path, """{"position":8}""")._1, 409)
    }
  }

  test("malformed requests and unknown games have explicit HTTP errors") {
    withServer { server =>
      assertEquals(post(server, "/api/games", "{")._1, 400)
      assertEquals(post(server, "/api/games", "[]")._1, 400)
      assertEquals(post(server, "/api/games", """{"xName":12}""")._1, 400)
      assertEquals(post(server, "/api/games", " " * 8193)._1, 413)
      assertEquals(post(server, "/api/games/missing/moves", """{"position":0}""")._1, 404)
      assertEquals(post(server, "/api/games/missing/moves", """{"position":0.5}""")._1, 400)
      val request = HttpRequest
        .newBuilder(URI.create(s"http://127.0.0.1:${server.port}/api/games"))
        .GET()
        .build()
      val response = client.send(request, HttpResponse.BodyHandlers.ofString())
      assertEquals(response.statusCode(), 405)
      assertEquals(response.headers().firstValue("Allow").orElse(""), "POST")
    }
  }
}
