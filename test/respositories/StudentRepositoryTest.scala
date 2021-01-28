package respositories

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{Injecting, WithApplication}
import repository.StudentRepository
import scala.concurrent.ExecutionContext.Implicits.global

class StudentRepositoryTest extends PlaySpec with GuiceOneAppPerTest {

  import models._

  "Student repository" should {

    "get all rows" in new WithStuRepository {
      val result = stuRepo.getAll
      await(result.map(l=>l.length mustBe(1)))
    }

    "insert a row" in new WithStuRepository {
      val result = stuRepo.insert(Student(name="Student 2",email = "Student2@gmail.com"))
      await(result.map(i=>i mustBe 2))
    }

    "update a row" in new WithStuRepository {
      val result = stuRepo.update(Student(Some(1),name="Student 1",email = "Student1@aol.com"))
      await(result.map(i=>i mustBe 1))
    }
    "delete a row" in new WithStuRepository() {
      val result = await(stuRepo.delete(1))
      result mustBe 1
    }
  }
}
  trait WithStuRepository extends WithApplication with Injecting {

    val stuRepo = inject[StudentRepository]
  }

