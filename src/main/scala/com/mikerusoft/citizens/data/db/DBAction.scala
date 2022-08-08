package com.mikerusoft.citizens.data.db

import com.mikerusoft.citizens.model.Types.Validation

import scala.language.higherKinds

trait DBAction[T <: Status, EvidenceRead[_], EvidenceWrite[_]] {
  def createConnection[B >: DBAction[ConnReady, EvidenceRead, EvidenceWrite]](): Validation[B]
  def createTable[B >: DBAction[ConnReady, EvidenceRead, EvidenceWrite]](db: DBAction[ConnReady, EvidenceRead, EvidenceWrite], sql: String): Validation[B]
  def insertOfAutoIncrement(db: DBAction[ConnReady, EvidenceRead, EvidenceWrite], insertStatement: String): Validation[Int]
  def insert[P](db: DBAction[ConnReady, EvidenceRead, EvidenceWrite], insertStatement: String, p: P)(implicit ev: EvidenceWrite[P]): Validation[Int]
  def selectUnique[P](db: DBAction[ConnReady, EvidenceRead, EvidenceWrite], selectStatement: String)(implicit ev: EvidenceRead[P]): Validation[P]
  def selectList[P](db: DBAction[ConnReady, EvidenceRead, EvidenceWrite], selectStatement: String)(implicit ev: EvidenceRead[P]): Validation[List[P]]
}

sealed trait Status
final case class ConnReady() extends Status
