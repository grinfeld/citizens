package com.mikerusoft.citizens.data.parsers

import cats.data.Validated
import com.mikerusoft.citizens.model.Types.ErrorMsg

trait LineParser[T] {
  def readLine(line: String): Validated[ErrorMsg, T]
}
