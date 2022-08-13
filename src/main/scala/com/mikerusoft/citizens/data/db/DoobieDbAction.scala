package com.mikerusoft.citizens.data.db
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.mikerusoft.citizens.data.db.DoobieDbAction.SqlToString
import com.mikerusoft.citizens.model.Types.{Invalid, Valid, Validation}
import doobie.Fragment
import doobie.implicits._
import doobie.syntax._
import doobie.util.update.Update
import doobie.util.{Read, Write, fragment}

final case class DoobieDbAction[S <: Status] private[db] (transactor: doobie.Transactor[IO]) extends DBAction[S, Read, Write] {

  override def createConnection[B >: DBAction[ConnReady, Read, Write]](): Validation[B] = {
    Valid(new DoobieDbAction[ConnReady](transactor))
  }

  override def createTable[B >: DBAction[ConnReady, Read, Write]](db: DBAction[ConnReady, Read, Write], sql: String): Validation[B] = {
    createTable(db, sql.toSql)
  }

  override def insert[P](db: DBAction[ConnReady, Read, Write], insertStatement: String, p: P)(implicit ev: Write[P]): Validation[Int] = {
    Update[P](insertStatement)(ev).run(p).transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  override def insertOfAutoIncrement[TT](db: DBAction[ConnReady, Read, Write], insertStatement: String, nameAutoIncrementField: String)(implicit ev: Read[TT]): Validation[TT] = {
    insertOfAutoIncrement(db, insertStatement.toSql, nameAutoIncrementField)
  }

  override def selectUnique[P](db: DBAction[ConnReady, Read, Write], selectStatement: String)(implicit ev: Read[P]): Validation[P] = {
    selectUnique(db, selectStatement.toSql)
  }

  override def selectList[P](db: DBAction[ConnReady, Read, Write], selectStatement: String)(implicit ev: Read[P]): Validation[List[P]] = {
    selectList(db, selectStatement.toSql)
  }

  def createTable[B >: DBAction[ConnReady, Read, Write]](db: DBAction[ConnReady, Read, Write], sql: Fragment): Validation[B] = {
    sql.update.run.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(_) => Valid(new DoobieDbAction[ConnReady](transactor))
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  def insertOfAutoIncrement[TT](db: DBAction[ConnReady, Read, Write], insertStatement: Fragment, nameAutoIncrementField: String)(implicit ev: Read[TT]): Validation[TT] = {
    insertStatement.update.withUniqueGeneratedKeys[TT](nameAutoIncrementField).transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  def selectUnique[P](db: DBAction[ConnReady, Read, Write], selectStatement: Fragment)(implicit ev: Read[P]): Validation[P] = {
    selectStatement.query[P].unique.transact(transactor).attemptSql.unsafeRunSync() match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }

  def selectList[P](db: DBAction[ConnReady, Read, Write], selectStatement: Fragment)(implicit ev: Read[P]): Validation[List[P]] = {
    selectStatement.query[P].to[List].transact(transactor).attemptSql.unsafeRunSync()match {
      case Right(value) => Valid(value)
      case Left(exception) => Invalid(exception.getMessage)
    }
  }
}

object DoobieDbAction {

  implicit class DoobieDbActionWrapper(val db: DBAction[ConnReady, Read, Write]) {

    def createTableWithFragment[B >: DBAction[ConnReady, Read, Write]](db: DBAction[ConnReady, Read, Write], sql: Fragment): Validation[B] = {
      db.asInstanceOf[DoobieDbAction[ConnReady]].createTable(db, sql)
    }

    def insertOfAutoIncrementWithFragment[TT](db: DBAction[ConnReady, Read, Write], sql: Fragment, nameAutoIncrementField: String)(implicit ev: Read[TT]): Validation[TT] = {
      db.asInstanceOf[DoobieDbAction[ConnReady]].insertOfAutoIncrement(db, sql, nameAutoIncrementField)
    }

    def selectUniqueWithFragment[P](db: DBAction[ConnReady, Read, Write], sql: Fragment)(implicit ev: Read[P]): Validation[P] = {
      db.asInstanceOf[DoobieDbAction[ConnReady]].selectUnique(db, sql)
    }

    def selectListWithFragment[P](db: DBAction[ConnReady, Read, Write], sql: Fragment)(implicit ev: Read[P]): Validation[List[P]] = {
      db.asInstanceOf[DoobieDbAction[ConnReady]].selectList(db, sql)
    }
  }

  def apply(trans: doobie.Transactor[IO]): Validation[DBAction[ConnReady, Read, Write]] = {
    new DoobieDbAction(trans).createConnection()
  }

  def create(implicit trans: doobie.Transactor[IO]): Validation[DBAction[ConnReady, Read, Write]] = {
    apply(trans)
  }

  implicit class SqlToString(value: String) {
    def toSql: fragment.Fragment = new SqlInterpolator(new StringContext(value)).sql()
  }
}
