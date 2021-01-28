package repository
import javax.inject.{Inject, Singleton}
import models.Student
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class StudentRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends StudentTable with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def createSchemaIfNotExists: Future[List[Unit]] = {
    val existing = db.run(MTable.getTables)
    val tables = List(studTableQuery)
    val f = existing.flatMap(v => {
      val names = v.map(mt => mt.name.name)
      val createIfNotExist = tables.filter(table =>
        !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
    f
  }


  def insert(student: Student): Future[Int] =
    db.run {
      studTableQueryInc += student
    }.map(res => res).recover {
      case ex: Exception => -1
    }


  def update(student: Student) =
    db.run {
      studTableQuery.filter(_.id === student.id).update(student)
    }.map(res => res).recover {
      case ex: Exception => -1
    }

  def delete(id: Int): Future[Int] =
    db.run {
      studTableQuery.filter(_.id === id).delete
    }.map(res => res).recover {
      case ex: Exception => -1
    }

  def getAll(): Future[List[Student]] =
    db.run {
      studTableQuery.to[List].result
    }

  def getById(empId: Int): Future[Option[Student]] =
    db.run {
      studTableQuery.filter(_.id === empId).result.headOption
    }

  //  def ddl = studTableQuery.schema

}

private[repository] trait StudentTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  lazy protected val studTableQuery = TableQuery[StudentTable]
  lazy protected val studTableQueryInc = studTableQuery returning studTableQuery.map(_.id)

  private[StudentTable] class StudentTable(tag: Tag) extends Table[Student](tag, "student") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.SqlType("VARCHAR(200)"))
    val email: Rep[String] = column[String]("email", O.SqlType("VARCHAR(200)"))

    def emailUnique = index("email_unique_key", email, unique = true)

    def * = (id.?,name, email) <> (Student.tupled, Student.unapply)
  }

}