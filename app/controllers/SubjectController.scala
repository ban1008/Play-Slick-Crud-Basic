package controllers

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc.{AbstractController, ControllerComponents}
import repository.SubjectRepository

import javax.inject._
import models.Subject

class SubjectController @Inject()(cc: ControllerComponents, stRep: SubjectRepository, implicit val ec:ExecutionContext) extends AbstractController(cc) {

  def displayValue = Action.async { implicit request =>
    val resultingSubjects = stRep.fetchAll()
    resultingSubjects.map(subjects => Ok(views.html.displaySubject(subjects)).flashing("msg" -> "None"))
  }

  def postValue = Action.async { request =>
    val postVal = request.body.asFormUrlEncoded
    postVal.map {
      args =>
       val name = args("subjectName").head
         stRep.addSubject(Subject( name = name)).map {
         case -1 => Redirect(routes.SubjectController.displayValue()).flashing("msg" -> "Failure : Data Not Entered")
         case _ => Redirect(routes.SubjectController.displayValue()).flashing("msg" -> "Success : Data Entered")
       }
    }.getOrElse(Future{Redirect(routes.SubjectController.displayValue()).flashing("msg" -> "Failure : Data Not Entered")})
  }

  def updateValue = Action.async { request =>
    val postVal = request.body.asFormUrlEncoded
    postVal.map {
      args =>
        val id = Integer.parseInt(args("id").head)
        val name = args("subjectName").head
        stRep.updateSubject(Subject(id=Some(id), name=name)).map {
          case -1 => Redirect(routes.SubjectController.displayValue()).flashing("msg" -> "Failure : Data Not Updated")
          case _ => Redirect(routes.SubjectController.displayValue()).flashing("msg" -> "Success : Data Updated")
        }

    }.getOrElse(Future{Redirect(routes.SubjectController.displayValue()).flashing("msg" -> "Failure : Data Not Updated")})


  }

  def deleteValue = Action.async { request =>
    val postVal = request.body.asFormUrlEncoded
    postVal.map {
      args =>
        val id = Integer.parseInt(args("id").head)
        stRep.deleteSubject(id).map {
          case -1 => Redirect(routes.SubjectController.displayValue()).flashing("msg" -> "Failure : Data Not Deleted")
          case _ => Redirect(routes.SubjectController.displayValue()).flashing("msg" -> "Success : Data Deleted")
        }
    }.getOrElse(Future{Redirect(routes.SubjectController.displayValue()).flashing("msg"->"Failure : Data Not Deleted")})


  }
}
