package bootstrap

import com.google.inject.Inject
import models.{Student, Subject}
import org.slf4j.LoggerFactory
import repository.{StudentRepository, SubjectRepository}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class InitialData @Inject()(studRepo: StudentRepository,subRepo: SubjectRepository)(implicit ec: ExecutionContext) {

  val logger = LoggerFactory.getLogger(this.getClass)

  def insert: Future[Unit] = {
    Await.result(studRepo.createSchemaIfNotExists,Duration.Inf)
    for {
      students <- studRepo.getAll() if students.isEmpty
      _ <- studRepo.insert(Data.students)
    } yield {}
  }

  def insertSub: Future[Unit] = {
    Await.result(subRepo.createSchemaIfNotExists,Duration.Inf)
    for {
      subjects <- subRepo.fetchAll() if subjects.isEmpty
      _ <- subRepo.addSubject(Data.subjects)
    } yield {}
  }

  try {
    logger.info("DB initialization.................")
    Await.result(insert, Duration.Inf)
    Await.result(insertSub, Duration.Inf)
  } catch {
    case ex: Exception =>
      logger.error("Error in database initialization ", ex)
  }

}

object Data {

  val students = Student(name="Student 1",email="Student1@gmail.com")

  val subjects = Subject(name="Subject 1")

}

