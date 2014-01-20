package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Concurrent
import scala.sys.process._

object Application extends Controller {

  def index = Action {
    Redirect(routes.Assets.at(file="editor.html"))
  }

  def ws = WebSocket.using[String] { request =>

    val (out, channel) = Concurrent.broadcast[String]
    val in = Iteratee.foreach[String] {
      msg =>
        Process(msg)
          .lines
          .foreach(channel.push(_))
    }
    (in, out)
  }
}
