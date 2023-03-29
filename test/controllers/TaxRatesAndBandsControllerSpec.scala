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

package controllers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.joda.time.DateTime
import org.mockito.MockitoSugar
import play.api.libs.json.JodaWrites._
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class TaxRatesAndBandsControllerSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar {

  val fakeRequest = FakeRequest()
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()
  val components = app.injector.instanceOf[ControllerComponents]
  val controller = new TaxRatesAndBandsController(components)

  "validating the getMaxAEA method" when {

    "calling with the year 2017" must {

      val result = controller.getMaxAEA(2017)(fakeRequest)

      "return status 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 11100 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Int] mustBe 11100

      }
    }

    "calling with the year 2015 (boundary check)" must {

      val result = controller.getMaxAEA(2015)(fakeRequest)

      "return status 200" in {
        status(result) mustBe 400
      }
    }

    "calling with the year 2014" must {

      val result = controller.getMaxAEA(2014)(fakeRequest)

      "return status 400" in {
        status(result) mustBe 400
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 'This tax year is not valid' as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] mustBe "This tax year is not valid"

      }
    }

    "calling with the year after the valid tax year" must {

      val result = controller.getMaxAEA(DateTime.now().getYear + 2)(fakeRequest)

      "return status 400" in {
        status(result) mustBe 400
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 'This tax year is not valid' as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] mustBe "This tax year is not valid"

      }
    }
  }

  "validating the getMaxPersonalAllowance method" when {

    "calling with the year 2021 and with no BPA or MA" must {
      val result = controller.getMaxPersonalAllowance(2021, None, None)(fakeRequest)

      "return status 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 12300 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Int] mustBe 12500
      }

    }

    "calling with the year 2020 and with no BPA or MA" must {
      val result = controller.getMaxPersonalAllowance(2020, None, None)(fakeRequest)

      "return status 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 12500 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Int] mustBe 12500
      }

    }

    "calling with the year 2018 and with no BPA or MA" must {
      val result = controller.getMaxPersonalAllowance(2018, None, None)(fakeRequest)

      "return status 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 11500 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Int] mustBe 11500
      }

    }

    "calling with the year 2017 and no BPA or MA" must {

      val result = controller.getMaxPersonalAllowance(2017, None, None)(fakeRequest)

      "return status 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 11100 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Int] mustBe 11000
      }

    }

    "calling with the year 2016 and with BPA and MA" must {

      val result = controller.getMaxPersonalAllowance(2016, Some(true), Some(true))(fakeRequest)

      "return status 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 12890 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Int] mustBe 14150
      }
    }

    "calling with the year 2015 and with BPA" must {

      val result = controller.getMaxPersonalAllowance(2015, Some(true), Some(true))(fakeRequest)

      "return status 200" in {
        status(result) mustBe 400
      }
    }

    "calling with the year 2014 and no BPA or MA" must {

      val result = controller.getMaxPersonalAllowance(2014, None, None)(fakeRequest)

      "return status 400" in {
        status(result) mustBe 400
      }
      }
    }

    "calling with the year 2014 and with BPA and MA" must {

      val result = controller.getMaxPersonalAllowance(2014, Some(true), Some(true))(fakeRequest)

      "return status 400" in {
        status(result) mustBe 400
      }
    }

    "calling with an invalid tax year (current year plus 2) and with BPA and MA" must {

      val result = controller.getMaxPersonalAllowance(DateTime.now().getYear + 2, Some(true), Some(true))(fakeRequest)

      "return status 400" in {
        status(result) mustBe 400
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return 11000 as the annual exempt amount" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] mustBe "This tax year is not valid"
      }
    }

  "validating the getTaxYear method" when {

    "calling with the date 10/10/2016" must {
      val result = controller.getTaxYear("2016-10-10")(fakeRequest)
      val data = contentAsString(result)
      val json = Json.parse(data)

      "return a status 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a supplied TaxYearModel for 2016/17" in {
        (json \ "taxYearSupplied").as[String] mustBe "2016/17"
      }

      "return a supplied TaxYearModel with isValidYear as true" in {
        (json \ "isValidYear").as[Boolean] mustBe true
      }

      "return a supplied TaxYearModel with calculationTaxYear as 2016/17" in {
        (json \ "calculationTaxYear").as[String] mustBe "2016/17"
      }

    }

    "calling with the date 10/10/2014" must {
      val result = controller.getTaxYear("2014-10-10")(fakeRequest)
      val data = contentAsString(result)
      val json = Json.parse(data)

      "return a status 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a supplied TaxYearModel for 2014/15" in {
        (json \ "taxYearSupplied").as[String] mustBe "2014/15"
      }

      "return a supplied TaxYearModel with isValidYear as false" in {
        (json \ "isValidYear").as[Boolean] mustBe false
      }

      "return a supplied TaxYearModel with calculationTaxYear as 2015/16" in {
        (json \ "calculationTaxYear").as[String] mustBe "2015/16"
      }
    }

    "calling with an invalid date 10/100/2014" must {
      val result = controller.getTaxYear("2014-100-10")(fakeRequest)
      val data = contentAsString(result)
      val json = Json.parse(data)

      "return a status 400" in {
        status(result) mustBe 400
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      s"return a message with the text ${assets.ValidationMessageLookup.invalidDateFormat("2014-100-10")}" in {
        json.as[String] mustBe assets.ValidationMessageLookup.invalidDateFormat("2014-100-10")
      }
    }
  }

  "Calling the .getMinimumYear method" must {
    lazy val result = controller.getMinimumDate(fakeRequest)

    "return a status of 200" in {
      status(result) mustBe 200
    }

    "return a JSON result" in {
      contentType(result) mustBe Some("application/json")
    }

    "contain a body with the earliest date" in {
      contentAsJson(result) mustBe Json.toJson(DateTime.parse("2015-04-05"))
    }
  }
}
