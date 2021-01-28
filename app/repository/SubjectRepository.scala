package repository

import javax.inject.{Inject, Singleton}
import models.Subject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.meta.MTable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton()
class SubjectRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends SubjectTable with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def createSchemaIfNotExists:Future[List[Unit]] = {
    val existing = db.run(MTable.getTables)
    val tables=List(subjectTableQuery)
    val f = existing.flatMap( v => {
      val names = v.map(mt => mt.name.name)
      val createIfNotExist = tables.filter( table =>
        (!names.contains(table.baseTableRow.tableName))).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
    f
  }

  def addSubject(subject:Subject)=db.run{
    subjectTableQueryInc +=subject
  }.map(res => res).recover {
    case ex: Exception => -1
  }

  def updateSubject(subject:Subject)=db.run{
    subjectTableQuery.filter(_.id===subject.id).update(subject)
  }.map(res => res).recover {
    case ex: Exception => -1
  }

  def deleteSubject(id:Int)=db.run{
    subjectTableQuery.filter(_.id===id).delete
  }.map(res => res).recover {
    case ex: Exception => -1
  }

  def fetchAll()=db.run{
    subjectTableQuery.to[List].result
  }

  def ddl = subjectTableQuery.schema

}

trait SubjectTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  private[SubjectTable] class SubjectTable(tag: Tag) extends Table[Subject](tag, "subject") {
    val id = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name = column[String]("subname")
    def * = (id.?, name) <> (Subject.tupled, Subject.unapply)
  }

  lazy protected val subjectTableQuery = TableQuery[SubjectTable]
  lazy protected val subjectTableQueryInc = subjectTableQuery returning subjectTableQuery.map(_.id)
}
