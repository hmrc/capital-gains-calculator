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
import org.joda.time.DateTime
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
        json.as[Int] shouldBe 11100

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
       json.as[Int] shouldBe 5550

      }
    }
  }

  "validating the getMaxPersonalAllowance method" when {

    "calling with the year 2017 and no BPA" should {

      val result = getMaxPersonalAllowance(2017, None)(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return 11000 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Int] shouldBe 11000

      }
    }

    "calling with the year 2016 and with BPA" should {

      val result = getMaxPersonalAllowance(2016, Some(true))(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return 12890 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Int] shouldBe 12890
      }
    }
  }

  "validating the getTaxYear method" when {

    "calling with the date 10/10/2016" should {
      val result = getTaxYear("2016-10-10")(fakeRequest)
      val data = contentAsString(result)
      val json = Json.parse(data)

      "return a status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return a supplied TaxYearModel for 2016/17" in {
        (json \ "taxYearSupplied").as[String] shouldBe "2016/17"
      }

      "return a supplied TaxYearModel with isValidYear as true" in {
        (json \ "isValidYear").as[Boolean] shouldBe true
      }

      "return a supplied TaxYearModel with calculationTaxYear as 2016/17" in {
        (json \ "calculationTaxYear").as[String] shouldBe "2016/17"
      }

    }

    "calling with the date 10/10/2014" should {
      val result = getTaxYear("2014-10-10")(fakeRequest)
      val data = contentAsString(result)
      val json = Json.parse(data)

      "return a status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return a supplied TaxYearModel for 2014/15" in {
        (json \ "taxYearSupplied").as[String] shouldBe "2014/15"
      }

      "return a supplied TaxYearModel with isValidYear as false" in {
        (json \ "isValidYear").as[Boolean] shouldBe false
      }

      "return a supplied TaxYearModel with calculationTaxYear as 2015/16" in {
        (json \ "calculationTaxYear").as[String] shouldBe "2015/16"
      }
    }
  }

  "Calling getTaxStartDate" should {
    val result = getTaxStartDate()(fakeRequest)
    val data = contentAsString(result)
    val json = Json.parse(data)

    "return a status 200" in {
      status(result) shouldBe 200
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
    }

    "return a datestring for the start date" in {
      json.as[String] shouldBe "2015-04-06"
    }
  }

  "Calling getTaxEndDate" should {
    val result = getTaxEndDate()(fakeRequest)
    val data = contentAsString(result)
    val json = Json.parse(data)

    "return a status 200" in {
      status(result) shouldBe 200
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
    }

    "return a datestring for the start date" in {
      json.as[String] shouldBe "2017-04-05"
    }
  }
}
