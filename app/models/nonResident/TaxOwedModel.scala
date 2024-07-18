/*
 * Copyright 2024 HM Revenue & Customs
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

package models.nonResident

import play.api.libs.json.{Format, Json}

case class TaxOwedModel (taxOwed: BigDecimal,
                         taxGain: Double,
                         taxRate: Int,
                         upperTaxGain: Option[Double] = None,
                         upperTaxRate: Option[Int] = None,
                         totalGain: BigDecimal,
                         taxableGain: Double,
                         prrUsed: Option[Double],
                         otherReliefsUsed: Option[Double],
                         allowableLossesUsed: Option[Double],
                         aeaUsed: Option[Double],
                         aeaRemaining: Double,
                         broughtForwardLossesUsed: Option[Double],
                         reliefsRemaining: Option[Double],
                         allowableLossesRemaining: Option[Double],
                         broughtForwardLossesRemaining: Option[Double],
                         totalDeductions: Option[Double],
                         taxOwedAtBaseRate: Option[Double],
                         taxOwedAtUpperRate: Option[Double])

object TaxOwedModel {
  implicit val formats: Format[TaxOwedModel] = Json.format[TaxOwedModel]
}
