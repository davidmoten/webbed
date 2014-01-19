package controllers

import play.api._
import play.api.mvc._
import models.Task
import play.api.data._
import play.api.data.Forms._
import play.api.libs.iteratee.Iteratee
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.iteratee.Concurrent

object Application extends Controller {

//  def index = Action {
//    Redirect(routes.Application.tasks)
//  }
  
  def index = WebSocket.using[String] { request => 
  
  val (out,channel) = Concurrent.broadcast[String]
  val in = Iteratee.foreach[String] {
    msg =>
      channel.push(msg)
  }
  (in, out)
}

  def tasks = Action {
    Ok(views.html.index(Task.all(), taskForm))
  }

def deleteTask(id: Long) = Action {
  Task.delete(id)
  Redirect(routes.Application.tasks)
}

	def newTask = Action { implicit request =>
	  taskForm.bindFromRequest.fold(
		errors => BadRequest(views.html.index(Task.all(), errors)),
		label => {
		  Task.create(label)
		  Redirect(routes.Application.tasks)
		}
	  )
	}

  val taskForm = Form(
    "label" -> nonEmptyText
  )
}
