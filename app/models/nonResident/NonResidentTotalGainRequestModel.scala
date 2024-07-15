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

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import java.time.LocalDate


case class NonResidentTotalGainRequestModel(disposalValue: Double,
                                            disposalCosts: Double,
                                            acquisitionValue: Double,
                                            acquisitionCosts: Double,
                                            improvements: Double,
                                            rebasedValue: Option[Double],
                                            rebasedCosts: Double,
                                            disposalDate: Option[LocalDate],
                                            acquisitionDate: Option[LocalDate],
                                            improvementsAfterTaxStarted: Double)

object NonResidentTotalGainRequestModel {
  implicit val writes: Writes[NonResidentTotalGainRequestModel] = Json.writes[NonResidentTotalGainRequestModel]

  implicit val reads: Reads[NonResidentTotalGainRequestModel] = (
    (__ \ "disposalValue").read[Double] and
      (__ \ "disposalCosts").read[Double] and
      (__ \ "acquisitionValue").read[Double] and
      (__ \ "acquisitionCosts").read[Double].or(Reads.pure[Double](0)) and
      (__ \ "improvements").read[Double].or(Reads.pure[Double](0)) and
      (__ \ "rebasedValue").readNullable[Double] and
      (__ \ "rebasedCosts").read[Double].or(Reads.pure[Double](0)) and
      (__ \ "disposalDate").readNullable[LocalDate] and
      (__ \ "acquisitionDate").readNullable[LocalDate] and
      (__ \ "improvementsAfterTaxStarted").read[Double].or(Reads.pure[Double](0))
    )(NonResidentTotalGainRequestModel.apply _)

}
