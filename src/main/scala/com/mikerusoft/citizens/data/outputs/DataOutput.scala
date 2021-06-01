package com.mikerusoft.citizens.data.outputs

import com.mikerusoft.citizens.model.Types.Validation

trait DataOutput[T, O] {
  def outputTo(elem: T): Validation[O]
}
