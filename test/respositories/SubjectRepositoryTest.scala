package respositories

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{Injecting, WithApplication}
import repository.SubjectRepository
import scala.concurrent.ExecutionContext.Implicits.global

class SubjectRepositoryTest extends PlaySpec with GuiceOneAppPerTest {

  import models._

  "Student repository" should {

    "get all rows" in new WithSubRepository {
      val result = subRepo.fetchAll()
      await(result.map(l=>l.length mustBe(1)))
    }

    "insert a row" in new WithSubRepository {
      val result = subRepo.addSubject(Subject(name="Subject 2"))
      await(result.map(i=>i mustBe 2))
    }

    "update a row" in new WithSubRepository {
      val result = subRepo.updateSubject(Subject(Some(1),name="Student One"))
      await(result.map(i=>i mustBe 1))
    }
    "delete a row" in new WithSubRepository() {
      val result = await(subRepo.deleteSubject(1))
      result mustBe 1
    }
  }
}
trait WithSubRepository extends WithApplication with Injecting {

  val subRepo = inject[SubjectRepository]
}

