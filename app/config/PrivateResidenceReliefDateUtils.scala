/*
 * Copyright 2021 HM Revenue & Customs
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

package config

import common.Date
import org.joda.time.DateTime

case class PrivateResidenceReliefDateDetails(shortedPeriod: Boolean, months: Int, dateDeducted: DateTime)

case class PrivateResidenceReliefDateUtils(date: DateTime) {

  /**
   * Should Private Residence Relief be 9 months or 18
   */
  def pRRMonthDeductionApplicable(): PrivateResidenceReliefDateDetails = {
    val dateAfter = date.isAfter(Date.PRRDeductionApplicableDate)
    val monthsToDeduct = dateAfter match {
      case true => 9
      case false => 18
    }
    val dateWithDeduction = date.minusMonths(monthsToDeduct)
    PrivateResidenceReliefDateDetails(dateAfter, monthsToDeduct, dateWithDeduction)
  }
}
