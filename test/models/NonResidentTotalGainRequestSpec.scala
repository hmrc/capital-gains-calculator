/*
 * Copyright 2023 HM Revenue & Customs
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

import models.nonResident.NonResidentTotalGainRequestModel
import java.time.LocalDate
import play.api.libs.json.{JsSuccess, Json}
import org.scalatestplus.play.PlaySpec

class NonResidentTotalGainRequestSpec extends PlaySpec {

  "NonResidentTotalGainRequest" must {
    "reads from Json" in {
      val inputJson = Json.parse(
        """
          |{
          |"disposalValue":500000,
          |"disposalCosts":20000,
          |"acquisitionValue":350000,
          |"acquisitionCosts":20000,
          |"improvements":9000,
          |"rebasedValue":450000,
          |"rebasedCosts":20000,
          |"disposalDate":"2017-05-12",
          |"acquisitionDate":"2014-08-14",
          |"improvementsAfterTaxStarted":1000
          |}
        """.stripMargin)

      val model = NonResidentTotalGainRequestModel(
        disposalValue = 500000,
        disposalCosts = 20000,
        acquisitionValue = 350000,
        acquisitionCosts = 20000,
        improvements = 9000,
        rebasedValue = Some(450000),
        rebasedCosts = 20000,
        disposalDate = Some(LocalDate.parse("2017-05-12")),
        acquisitionDate = Some(LocalDate.parse("2014-08-14")),
        improvementsAfterTaxStarted = 1000)

      inputJson.as[NonResidentTotalGainRequestModel] mustBe model
    }
    "non optional values" in {
      val inputJson = Json.parse(
        """
          |{
          |"disposalValue":500000,
          |"disposalCosts":20000,
          |"acquisitionValue":350000,
          |"acquisitionCosts":20000,
          |"improvements":9000,
          |"rebasedCosts":20000,
          |"improvementsAfterTaxStarted":1000
          |}
        """.stripMargin)

      val model = NonResidentTotalGainRequestModel(
        disposalValue = 500000,
        disposalCosts = 20000,
        acquisitionValue = 350000,
        acquisitionCosts = 20000,
        improvements = 9000,
        rebasedValue = None,
        rebasedCosts = 20000,
        disposalDate = None,
        acquisitionDate = None,
        improvementsAfterTaxStarted = 1000)

      inputJson.as[NonResidentTotalGainRequestModel] mustBe model
    }
    "setting defaut values" in {
      val inputJson = Json.parse(
        """
          |{
          |"disposalValue":500000,
          |"disposalCosts":20000,
          |"acquisitionValue":350000,
          |"acquisitionCosts":20000
          |}
        """.stripMargin
      )

      val model = NonResidentTotalGainRequestModel(
        disposalValue = 500000,
        disposalCosts = 20000,
        acquisitionValue = 350000,
        acquisitionCosts = 20000,
        improvements = 0,
        rebasedValue = None,
        rebasedCosts = 0,
        disposalDate = None,
        acquisitionDate = None,
        improvementsAfterTaxStarted = 0
      )

      Json.fromJson[NonResidentTotalGainRequestModel](inputJson) mustBe JsSuccess(model)

    }

  }
}
