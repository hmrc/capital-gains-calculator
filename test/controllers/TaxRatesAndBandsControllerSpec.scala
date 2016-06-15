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

package controllers

import controllers.TaxRatesAndBandsController._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class TaxRatesAndBandsControllerSpec extends UnitSpec with WithFakeApplication {

  val fakeRequest = FakeRequest()

  "validating the getMaxAEA method" when {

    "calling with the year 2017" should {

      val result = getMaxAEA(2017)(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return 11100 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        (json \ "annualExemptAmount").as[Int] shouldBe 11100

      }
    }
  }

  "validating the getMaxNonVulnerableAEA method" when {

    "calling with the year 2017" should {

      val result = getMaxNonVulnerableAEA(2017)(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return 5550 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        (json \ "annualExemptAmount").as[Int] shouldBe 5550

      }
    }
  }

  "validating the getMaxPersonalAllowance method" when {

    "calling with the year 2017" should {

      val result = getMaxPersonalAllowance(2017)(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return 5550 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        (json \ "personalAllowance").as[Int] shouldBe 11000

      }
    }
  }
}
