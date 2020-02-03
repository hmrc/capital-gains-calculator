/*
 * Copyright 2020 HM Revenue & Customs
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

package models

import play.api.libs.json.Json

case class CalculationResultModel(taxOwed: Double,
                                  totalGain: Double,
                                  baseTaxGain: Double,
                                  baseTaxRate: Int,
                                  usedAnnualExemptAmount: Double,
                                  aeaRemaining: Double,
                                  upperTaxGain: Option[Double] = None,
                                  upperTaxRate: Option[Int] = None,
                                  simplePRR: Option[Double] = None,
                                  baseRateTotal: Double = 0,
                                  upperRateTotal: Double = 0)

object CalculationResultModel {
  implicit val formats = Json.format[CalculationResultModel]
}
