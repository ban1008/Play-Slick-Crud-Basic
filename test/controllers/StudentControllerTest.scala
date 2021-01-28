package controllers

import models.Student
import org.mockito.Mockito.when
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test._
import repository.StudentRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}



class StudentControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerTest {
  implicit val mockedRepo: StudentRepository = mock[StudentRepository]

  "StudentController" should {

    "fetch all student" in new WithStudApplication {
      when(mockedRepo.getAll()) thenReturn Future.successful(List(Student(name="Student 1",email="Student1@gmail.com")))
      val result= studentController.displayValue().apply(FakeRequest())
      result.map(r=>r.toString().contains("200") mustBe true)
    }

    "insert a student" in new WithStudApplication {
      when(mockedRepo.insert(Student(name="Student 13",email="student13@gmail.com"))) thenReturn Future{1}
      val result= studentController.postValue().apply(FakeRequest().withFormUrlEncodedBody(("id","13"),("studentName","Student 13"),("studentEmail","student13@gmail.com")))
      result.map(s=>s.toString().contains("303") mustBe(true))
    }

    "update a student" in new WithStudApplication {
      when(mockedRepo.update(Student(Some(13),name="Student 13",email="student13@gmail.com"))) thenReturn Future{1}
      val result= studentController.updateValue().apply(FakeRequest().withFormUrlEncodedBody(("id","13"),("studentName","Student 13"),("studentEmail","student13@gmail.com")))
      result.map(s=>s.toString().contains("303") mustBe(true))
    }

    "delete a student" in new WithStudApplication {
      when(mockedRepo.delete(13)) thenReturn Future{1}
      val result= studentController.deleteValue().apply(FakeRequest().withFormUrlEncodedBody(("id","13")))
      result.map(s=>s.toString().contains("303") mustBe(true))
    }

  }

}
class WithStudApplication(implicit mockedRepo: StudentRepository) extends WithApplication with Injecting {

  implicit val ec = inject[ExecutionContext]
  val studentController: StudentController =
    new StudentController(
      stubControllerComponents(),
      mockedRepo,
      ec
    )
}

