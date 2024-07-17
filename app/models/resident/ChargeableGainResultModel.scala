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

package models.resident

import play.api.libs.json.{Format, Json}

case class ChargeableGainResultModel
(
  gain: Double,
  chargeableGain: Double,
  aeaUsed: Double,
  aeaRemaining: Double,
  deductions: Double,
  allowableLossesRemaining: Double,
  broughtForwardLossesRemaining: Double,
  lettingReliefsUsed: Option[Double],
  prrUsed: Option[Double],
  broughtForwardLossesUsed: Option[Double],
  allowableLossesUsed: Double
)

object ChargeableGainResultModel {
  implicit val formats: Format[ChargeableGainResultModel] = Json.format
}
