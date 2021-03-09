package com.mikerusoft.citizens.data.outputs

import cats.data.Validated
import com.mikerusoft.citizens.model.Types.ErrorMsg

trait DataOutput[T, O] {
  def outputTo(elem: T): Validated[ErrorMsg, O]
}
