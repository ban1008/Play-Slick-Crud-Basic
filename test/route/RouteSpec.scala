package route

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import models._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import utils.JsonFormat._

class RouteSpec extends PlaySpec with GuiceOneAppPerSuite {

  "Routes" should {

    "get student list" in new WithApplication {
      val Some(result) = route(app, FakeRequest(GET, "/"))
      status(result) mustBe 200
    }

    "post student value" in new WithApplication {
      val Some(result) = route(app, FakeRequest(POST, "/postValue"))
      status(result) mustBe 303
    }

    "update student value" in new WithApplication {
      val Some(result) = route(app, FakeRequest(POST, "/updateValue"))
      status(result) mustBe 303
    }


    "delete student value" in new WithApplication {
      val Some(result) = route(app, FakeRequest(POST, "/deleteValue"))
      status(result) mustBe 303
    }

    "get subject list" in new WithApplication {
      val Some(result) = route(app, FakeRequest(GET, "/displaySubject"))
      status(result) mustBe 200
    }

    "post subject value" in new WithApplication {
      val Some(result) = route(app, FakeRequest(POST, "/postSubjectValue"))
      status(result) mustBe 303
    }

    "update subject value" in new WithApplication {
      val Some(result) = route(app, FakeRequest(POST, "/updateSubjectValue"))
      status(result) mustBe 303
    }

    "delete subject value" in new WithApplication {
      val Some(result) = route(app, FakeRequest(POST, "/deleteSubjectValue"))
      status(result) mustBe 303
    }

  }

}
