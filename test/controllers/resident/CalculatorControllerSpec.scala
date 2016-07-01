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

package controllers.resident

import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CalculatorControllerSpec extends UnitSpec with WithFakeApplication {

  "CalculatorController.calculateTotalGain" when {
    lazy val fakeRequest = FakeRequest("GET", "")

    "numeric values are passed" should {
      lazy val result = CalculatorController.calculateTotalGain(100000, 10000, 50000, 10000, 10000)(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a Some" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Option[BigDecimal]] shouldBe Some(BigDecimal(20000.0))
      }
    }
  }

  "CalculatorController.calculateChargeableGain" when {

    lazy val fakeRequest = FakeRequest("GET", "")

    "numeric values are passed" should {

      lazy val result = CalculatorController.calculateChargeableGain(
        disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000,
        improvements = 5000,
        reliefs = None,
        allowableLosses = Some(5000),
        broughtForwardLosses = Some(20000),
        annualExemptAmount = 11100
      )(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a Some" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Option[BigDecimal]] shouldBe Some(BigDecimal(-8100.0))
      }
    }
  }
}
