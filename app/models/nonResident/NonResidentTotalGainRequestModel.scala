/*
 * Copyright 2022 HM Revenue & Customs
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

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.JodaWrites._
import play.api.libs.json.JodaReads._
import play.api.libs.json.{__, _}


case class NonResidentTotalGainRequestModel(disposalValue: Double,
                                            disposalCosts: Double,
                                            acquisitionValue: Double,
                                            acquisitionCosts: Double,
                                            improvements: Double,
                                            rebasedValue: Option[Double],
                                            rebasedCosts: Double,
                                            disposalDate: Option[DateTime],
                                            acquisitionDate: Option[DateTime],
                                            improvementsAfterTaxStarted: Double)

object NonResidentTotalGainRequestModel {
  implicit val writes =  Json.writes[NonResidentTotalGainRequestModel]

  implicit val reads: Reads[NonResidentTotalGainRequestModel] = (
    (__ \ "disposalValue").read[Double] and
      (__ \ "disposalCosts").read[Double] and
      (__ \ "acquisitionValue").read[Double] and
      (__ \ "acquisitionCosts").read[Double].or(Reads.pure[Double](0)) and
      (__ \ "improvements").read[Double].or(Reads.pure[Double](0)) and
      (__ \ "rebasedValue").readNullable[Double] and
      (__ \ "rebasedCosts").read[Double].or(Reads.pure[Double](0)) and
      (__ \ "disposalDate").readNullable[DateTime] and
      (__ \ "acquisitionDate").readNullable[DateTime] and
      (__ \ "improvementsAfterTaxStarted").read[Double].or(Reads.pure[Double](0))
    ) (NonResidentTotalGainRequestModel.apply _)

}
