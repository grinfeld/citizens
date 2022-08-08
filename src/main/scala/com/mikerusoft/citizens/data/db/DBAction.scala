package com.mikerusoft.citizens.data.db

import com.mikerusoft.citizens.model.Types.Validation

import scala.language.higherKinds

trait DBAction[T <: Status, Evidence[_]] {
  def createConnection[B >: DBAction[ConnReady, Evidence]](): Validation[B]
  def createTable[B >: DBAction[ConnReady, Evidence]](db: DBAction[ConnReady, Evidence], sql: String): Validation[B]
  def insertOfAutoIncrement[P](db: DBAction[ConnReady, Evidence], insertStatement: String): Validation[Int]
  def selectUnique[P](db: DBAction[ConnReady, Evidence], selectStatement: String)(implicit ev: Evidence[P]): Validation[P]
  def selectList[P](db: DBAction[ConnReady, Evidence], selectStatement: String)(implicit ev: Evidence[P]): Validation[List[P]]
}

sealed trait Status
final case class ConnReady() extends Status
