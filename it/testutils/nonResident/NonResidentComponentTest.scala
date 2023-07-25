/*
 * Copyright 2018 HM Revenue & Customs
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

package testutils.nonResident

import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.libs.ws.{WSClient, WSResponse}
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}

class NonResidentComponentTest extends PlaySpec with GuiceOneServerPerSuite {

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .build()

  val baseUrl = s"http://localhost:$port/capital-gains-calculator"

  lazy val ws: WSClient = app.injector.instanceOf[WSClient]

  "Hitting the /non-resident/calculate-total-gain route" must {
    val calculateUrl = s"$baseUrl/non-resident/calculate-total-gain"

    s"return a $OK with a valid result" when {

      "non-residential" in {
        def request: WSResponse = Await.result(ws.url(s"$calculateUrl")
          .post(
            Json.parse(
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
              """.
                stripMargin)
          ), Duration(60, SECONDS))

        val responseJson = Json.parse(
          """
            |{
            |"flatGain":100000,
            |"rebasedGain":9000,
            |"timeApportionedGain":76570
            |}
          """.stripMargin)

        request.status mustBe 200
        request.json mustBe responseJson
      }
    }

    s"return a $BAD_REQUEST" when {

      "data is missing" in {
        def request: WSResponse = Await.result(ws.url(calculateUrl)
          .post(
            Json.parse(
              """
                |{
                |"disposalCosts":200000,
                |"acquisitionValue":350000,
                |"acquisitionCosts":200000,
                |"improvements":9000,
                |"rebasedValue":450000,
                |"rebasedCosts":20000,
                |"disposalDate":"2017-05-12",
                |"acquisitionDate":"2014-08-14",
                |"improvementsAfterTaxStarted":250000
                |}
              """.
                stripMargin)
          ), Duration(60, SECONDS))

        request.status mustBe 400
        request.body mustBe "Validation failed with errors: List((/disposalValue,List(JsonValidationError(List(error.path.missing),List()))))"
      }

      "no data is provided" in {
        def request: WSResponse = Await.result(ws.url(calculateUrl)
          .post(""), Duration(60, SECONDS))

        request.status mustBe 400
        request.body mustBe "No Json provided"
      }
    }
  }
}