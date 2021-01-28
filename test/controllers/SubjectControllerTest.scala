package controllers

import models.Subject
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers.stubControllerComponents
import play.api.test.{FakeRequest, Injecting, WithApplication}
import repository.SubjectRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class SubjectControllerTest extends PlaySpec with MockitoSugar with GuiceOneAppPerTest {
  implicit val mockedRepo: SubjectRepository = mock[SubjectRepository]

  "SubjectController" should {

    "fetch all subject" in new WithSubApplication() {
      when(mockedRepo.fetchAll()) thenReturn Future.successful(List(Subject(Some(1),"Subject 1")))
      val result= subjectController.displayValue().apply(FakeRequest())
      Await.result(result,Duration.Inf).toString().contains("200") mustBe true
//      result.map(r=>r.toString().contains("200") mustBe true)
    }

    "insert a subject" in new WithSubApplication() {
      when(mockedRepo.addSubject(Subject(name="Subject 2"))) thenReturn Future{1}
      val result= subjectController.postValue().apply(FakeRequest().withFormUrlEncodedBody(("subjectName","Subject 2")))
      Await.result(result,Duration.Inf).toString().contains("303") mustBe true
//      result.map(s=>s.toString().contains("303") mustBe(true))
    }

    "update a subject" in new WithSubApplication() {
      when(mockedRepo.updateSubject(Subject(id=Some(1),name="Subject 1"))) thenReturn Future{1}
      val result= subjectController.updateValue().apply(FakeRequest().withFormUrlEncodedBody(("id","1"),("subjectName","Subject 1")))
      Await.result(result,Duration.Inf).toString().contains("303") mustBe true
    }

    "delete a subject" in new WithSubApplication() {
      when(mockedRepo.deleteSubject(1 )) thenReturn Future{1}
      val result= subjectController.deleteValue().apply(FakeRequest().withFormUrlEncodedBody(("id","1")))
      Await.result(result,Duration.Inf).toString().contains("303") mustBe true
    }

  }

}
class WithSubApplication(implicit mockedRepo: SubjectRepository) extends WithApplication with Injecting {

  implicit val ec = inject[ExecutionContext]
  val subjectController: SubjectController =
    new SubjectController(
      stubControllerComponents(),
      mockedRepo,
      ec
    )
}
