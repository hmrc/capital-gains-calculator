/*
 * Copyright 2016 HM Revenue & Customs
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

package services

import java.util.Date

import models.{DateModel, CalculationResult}

object CalculationService extends CalculationService

trait CalculationService {

  val format = new java.text.SimpleDateFormat("ddMMyyyy")
  val lowerDate = format.parse("05042015")
  val higherDate = format.parse("06042016")

  def add(a: Int, b: Int) = CalculationResult(a, b, a + b)

  def annualExemptYear(date : String): BigDecimal = {
    format.parse(date) match {
      case d if d.after(lowerDate) && d.before(higherDate)  => 11000
      case _ => 10500
    }
  }

  def isVulnerableTrustee(date : String, signal : String): BigDecimal = {
    val amount = annualExemptYear(date)

    signal match {
      case "Yes" => amount
      case "No" => amount/2
    }
  }
}
