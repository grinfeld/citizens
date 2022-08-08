package com.mikerusoft.citizens.data.db
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.mikerusoft.citizens.data.db.DoobieDbAction.SqlToString
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}
import doobie.Fragment
import doobie.implicits._
import doobie.syntax._
import doobie.util.{Read, fragment}

final case class DoobieDbAction[S <: Status] private[db] (transactor: doobie.Transactor[IO]) extends DBAction[S, Read] {

  override def createConnection[B >: DBAction[ConnReady, Read]](): Validation[B] = {
    Valid(new DoobieDbAction[ConnReady](transactor))
  }

  override def createTable[B >: DBAction[ConnReady, Read]](db: DBAction[ConnReady, Read], sql: String): Validation[B] = {
    createTableInner(db, sql.toSql)
  }

  override def insertOfAutoIncrement[P](db: DBAction[ConnReady, Read], insertStatement: String): Validation[Int] = {
    insertOfAutoIncrementInner(db, insertStatement.toSql)
  }

  override def selectUnique[P](db: DBAction[ConnReady, Read], selectStatement: String)(implicit ev: Read[P]): Validation[P] = {
    selectUniqueInner(db, selectStatement.toSql)
  }

  override def selectList[P](db: DBAction[ConnReady, Read], selectStatement: String)(implicit ev: Read[P]): Validation[List[P]] = {
    selectListInner(db, selectStatement.toSql)
  }

  def createTableInner[B >: DBAction[ConnReady, Read]](db: DBAction[ConnReady, Read], sql: Fragment): Validation[B] = {
    sql.update.run.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(_) => Valid(new DoobieDbAction[ConnReady](transactor))
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  def insertOfAutoIncrementInner[P](db: DBAction[ConnReady, Read], insertStatement: Fragment): Validation[Int] = {
    insertStatement.update.run.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  def selectUniqueInner[P](db: DBAction[ConnReady, Read], selectStatement: Fragment)(implicit ev: Read[P]): Validation[P] = {
    selectStatement.query[P].unique.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  def selectListInner[P](db: DBAction[ConnReady, Read], selectStatement: Fragment)(implicit ev: Read[P]): Validation[List[P]] = {
    selectStatement.query[P].to[List].transact(transactor).attemptSql.unsafeRunSync()match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }
}

object DoobieDbAction {
  case class Pers(id: Int, name: String)

  implicit class DoobieDbActionWrapper(val db: DBAction[ConnReady, Read]) {

    def createTableWithFragment[B >: DBAction[ConnReady, Read]](db: DBAction[ConnReady, Read], sql: Fragment): Validation[B] = {
      db.asInstanceOf[DoobieDbAction[ConnReady]].createTableInner(db, sql)
    }

    def insertOfAutoIncrementWithFragment[P](db: DBAction[ConnReady, Read], sql: Fragment): Validation[Int] = {
      db.asInstanceOf[DoobieDbAction[ConnReady]].insertOfAutoIncrementInner(db, sql)
    }

    def selectUniqueWithFragment[P](db: DBAction[ConnReady, Read], sql: Fragment)(implicit ev: Read[P]): Validation[P] = {
      db.asInstanceOf[DoobieDbAction[ConnReady]].selectUniqueInner(db, sql)
    }

    def selectListWithFragment[P](db: DBAction[ConnReady, Read], sql: Fragment)(implicit ev: Read[P]): Validation[List[P]] = {
      db.asInstanceOf[DoobieDbAction[ConnReady]].selectListInner(db, sql)
    }
  }

  def apply(trans: doobie.Transactor[IO]): Validation[DBAction[ConnReady, Read]] = {
    new DoobieDbAction(trans).createConnection()
  }

  def create(implicit trans: doobie.Transactor[IO]): Validation[DBAction[ConnReady, Read]] = {
    apply(trans)
  }

  implicit class SqlToString(value: String) {
    def toSql: fragment.Fragment = new SqlInterpolator(new StringContext(value)).sql()
  }
}
