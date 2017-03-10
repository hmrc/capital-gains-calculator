/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common

import scala.math.BigDecimal.RoundingMode

object Math {

  def round(roundMethod: String, x: Double): Double = {
    roundMethod match {
      case "down" => BigDecimal.valueOf(x).setScale(0, RoundingMode.DOWN).toDouble
      case "up" => BigDecimal.valueOf(x).setScale(0, RoundingMode.UP).toDouble
      case "result" => BigDecimal.valueOf(x).setScale(2, RoundingMode.DOWN).toDouble
      case "gain" => BigDecimal.valueOf(x).setScale(0, RoundingMode.FLOOR).toDouble
      case _ => x
    }
  }

  def min(x: Double, y: Double): Double = {
    if (x < y) x else y
  }

  def negativeToNone(x: Double): Option[Double] = {
    if (x < 0) None else Some(x)
  }

  def negativeToZero(x: Double): Double = {
    if (x < 0) 0 else x
  }
}
