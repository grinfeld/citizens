package com.mikerusoft.citizens.data.db

import com.mikerusoft.citizens.model.Types.Validation

trait DBAction[Status] {
  def createConnection[T, B >: DBAction[ConnReady]](): Validation[B]
  def createTableIfNotExists[T, B >: DBAction[TableReady]](db: DBAction[ConnReady]): Validation[B]
  def insertOfAutoIncrement[P](db: DBAction[TableReady], insertStatement: String): Validation[Int]
  def selectUnique[P](db: DBAction[TableReady], selectStatement: String): Validation[P]
}

sealed trait Status
final case class ConnReady() extends Status
final case class TableReady() extends Status
