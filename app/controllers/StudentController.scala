package controllers

import models.Student
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.StudentRepository

import javax.inject._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class StudentController @Inject()(cc: ControllerComponents, stRep: StudentRepository, implicit val ec: ExecutionContext) extends AbstractController(cc) {


  def displayValue = Action.async { implicit request =>
    stRep.getAll().map(students => Ok(views.html.display(students)).flashing("msg"->"None"))
  }


  def postValue = Action.async{ implicit request =>
    val postVal = request.body.asFormUrlEncoded
    postVal.map {
      args =>
        val name = args("studentName").head
        val email = args("studentEmail").head
        stRep.insert(Student(name= name, email=email)).map {
          case -1 => Redirect(routes.StudentController.displayValue()).flashing("msg" -> "Failure : Data Not Entered")
          case _ => Redirect(routes.StudentController.displayValue()).flashing("msg" -> "Success : Data Entered")
        }
    }.getOrElse(Future{Redirect(routes.StudentController.displayValue()).flashing("msg" -> "Failure : Bad Request")})

  }


  def updateValue = Action.async{ request =>
    val postVal = request.body.asFormUrlEncoded
    postVal.map {
      args =>
        val id = Integer.parseInt(args("id").head)
        val name = args("studentName").head
        val email = args("studentEmail").head
        stRep.update(Student(Some(id),name= name, email=email)).map {
        case -1 => Redirect(routes.StudentController.displayValue()).flashing("msg" -> "Failure : Data Not Updated")
        case _ => Redirect(routes.StudentController.displayValue()).flashing("msg" -> "Success : Data Updated")
      }
    }.getOrElse(Future{Redirect(routes.StudentController.displayValue()).flashing("msg" -> "Failure : Bad Request")})

  }

  def deleteValue = Action.async { request =>
    val postVal = request.body.asFormUrlEncoded
    var id=0;
    postVal.map {
      args =>
        id = Integer.parseInt(args("id").head)
    }
    stRep.delete(id)
    Future{Redirect(routes.StudentController.displayValue()).flashing("msg"->"Success : Data Deleted")}
  }

}