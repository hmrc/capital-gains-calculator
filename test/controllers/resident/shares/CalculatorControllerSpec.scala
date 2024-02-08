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

package controllers.resident.shares

import models.resident.shares.{CalculateTaxOwedModel, ChargeableGainModel, TotalGainModel}
import org.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CalculationService

import java.time.LocalDate


class CalculatorControllerSpec extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar{

  val service = new CalculationService
  val mockComponents = app.injector.instanceOf[ControllerComponents]
  val controller = new CalculatorController(service, mockComponents)

  "ShareCalculatorController.calculateTotalGain" when {
    lazy val fakeRequest = FakeRequest("GET", "")

    "numeric values are passed" must {
      val model = TotalGainModel(100000, 10000, 50000, 10000)
      lazy val result = controller.calculateTotalGain(model)(fakeRequest)

      "return a 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a Some" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.asOpt[BigDecimal] mustBe Some(BigDecimal(30000.0))
      }
    }
  }

  "ShareCalculatorController.calculateChargeableGain" when {

    lazy val fakeRequest = FakeRequest("GET", "")

    "numeric values are passed" must {

      lazy val result = controller.calculateChargeableGain(ChargeableGainModel(
        TotalGainModel(disposalValue = 195000,
          disposalCosts = 1000,
          acquisitionValue = 160000,
          acquisitionCosts = 1000),
        allowableLosses = Some(5000),
        broughtForwardLosses = Some(20000),
        annualExemptAmount = 11100)
      )(fakeRequest)

      "return a 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) mustBe Some("application/json")
        }

        "has the gain as 33000" in {
          (json \ "gain").as[Double] mustBe 33000
        }

        "has the chargeableGain as -3100" in {
          (json \ "chargeableGain").as[Double] mustBe -3100.0
        }

        "has the aeaUsed as 11000" in {
          (json \ "aeaUsed").as[Double] mustBe 11100.0
        }

        "has the aeaRemaining as 0" in {
          (json \ "aeaRemaining").as[Double] mustBe 0.0
        }

        "has the deductions as 33000" in {
          (json \ "deductions").as[Double] mustBe 33000
        }

        "has the allowableLossesRemaining as £0" in {
          (json \ "allowableLossesRemaining").as[Double] mustBe 0
        }

        "has the broughtForwardLossesRemaining as £3100" in {
          (json \ "broughtForwardLossesRemaining").as[Double] mustBe 3100
        }

        "has the broughtForwardLossesUsed as £16900" in {
          (json \ "broughtForwardLossesUsed").as[Double] mustBe 16900
        }

        "has the allowableLossesUsed as £5000" in {
          (json \ "allowableLossesUsed").as[Double] mustBe 5000
        }
      }
    }

    "numeric values are passed with correct rounding" must {

      lazy val result = controller.calculateChargeableGain(ChargeableGainModel(
        TotalGainModel(disposalValue = 195000,
          disposalCosts = 1000,
          acquisitionValue = 160000,
          acquisitionCosts = 1000),
        allowableLosses = Some(4999.01),
        broughtForwardLosses = Some(19999.01),
        annualExemptAmount = 11100)
      )(fakeRequest)

      "return a 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) mustBe Some("application/json")
        }

        "has the gain as 33000" in {
          (json \ "gain").as[Double] mustBe 33000
        }

        "has the chargeableGain as -3100" in {
          (json \ "chargeableGain").as[Double] mustBe -3100.0
        }

        "has the aeaUsed as -11000" in {
          (json \ "aeaUsed").as[Double] mustBe 11100.0
        }

        "has the aeaRemaining as 0" in {
          (json \ "aeaRemaining").as[Double] mustBe 0.0
        }

        "has the deductions as 33000" in {
          (json \ "deductions").as[Double] mustBe 33000
        }

        "has the allowableLossesRemaining as " in {
          (json \ "allowableLossesRemaining").as[Double] mustBe 0
        }

        "has the broughtForwardLossesRemaining as £3100" in {
          (json \ "broughtForwardLossesRemaining").as[Double] mustBe 3100
        }

        "has the broughtForwardLossesUsed as £16900" in {
          (json \ "broughtForwardLossesUsed").as[Double] mustBe 16900
        }

        "has the allowableLossesUsed as £5000" in {
          (json \ "allowableLossesUsed").as[Double] mustBe 5000
        }
      }
    }
  }

  "ShareCalculatorController.calculateTaxOwed" when {
    lazy val fakeRequest = FakeRequest("GET", "")

    "no optional values are provided" must {
      lazy val result = controller.calculateTaxOwed(CalculateTaxOwedModel(
        ChargeableGainModel(TotalGainModel(disposalValue = 195000,
          disposalCosts = 1000,
          acquisitionValue = 160000,
          acquisitionCosts = 1000),
          allowableLosses = None,
          broughtForwardLosses = None,
          annualExemptAmount = 11100),
        previousTaxableGain = None,
        previousIncome = 20000,
        personalAllowance = 11000,
        disposalDate = LocalDate.parse("2015-10-10"))
      )(fakeRequest)

      "return a 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) mustBe Some("application/json")
        }

        "has the gain as 33000" in {
          (json \ "gain").as[Double] mustBe 33000
        }

        "has the chargeableGain as 21900" in {
          (json \ "chargeableGain").as[Double] mustBe 21900.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] mustBe 11100.0
        }

        "has the deductions as 11100" in {
          (json \ "deductions").as[Double] mustBe 11100.0
        }

        "has the taxOwed as 3942" in {
          (json \ "taxOwed").as[Double] mustBe 3942.0
        }

        "has a first tax rate of 18%" in {
          (json \ "firstRate").as[Int] mustBe 18
        }

        "has a first tax band of 21900" in {
          (json \ "firstBand").as[Double] mustBe 21900
        }

        "has no second tax rate" in {
          (json \ "secondRate").asOpt[Int] mustBe None
        }

        "has no second tax band" in {
          (json \ "secondBand").asOpt[Double] mustBe None
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] mustBe 0
        }

        "has the allowableLossesUsed as £0.00" in {
          (json \ "allowableLossesUsed").as[Double] mustBe 0
        }

        "has the baseRateTotal as £3,942.00" in {
          (json \ "baseRateTotal").as[Double] mustBe 3942.0
        }

        "has the upperRateTotal as £0.00" in {
          (json \ "upperRateTotal").as[Double] mustBe 0
        }
      }
    }

    "all optional values are provided" must {
      lazy val result = controller.calculateTaxOwed(CalculateTaxOwedModel(
         ChargeableGainModel(TotalGainModel(disposalValue = 250000,
          disposalCosts = 10000,
          acquisitionValue = 100000,
          acquisitionCosts = 10000), allowableLosses = Some(20000),
          broughtForwardLosses = Some(10000),
          annualExemptAmount = 11100),
        previousTaxableGain = Some(10000),
        previousIncome = 10000,
        personalAllowance = 11000,
        disposalDate = LocalDate.parse("2015-10-10"))
      )(fakeRequest)

      "return a 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) mustBe Some("application/json")
        }

        "has the gain as 130000" in {
          (json \ "gain").as[Double] mustBe 130000
        }

        "has the chargeableGain as 88900" in {
          (json \ "chargeableGain").as[Double] mustBe 88900.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] mustBe 11100.0
        }

        "has the deductions as 41100" in {
          (json \ "deductions").as[Double] mustBe 41100.0
        }

        "has the taxOwed as 22713.5" in {
          (json \ "taxOwed").as[Double] mustBe 22713.5
        }

        "has a first tax rate of 18%" in {
          (json \ "firstRate").as[Int] mustBe 18
        }

        "has a first tax band of 21785" in {
          (json \ "firstBand").as[Double] mustBe 21785
        }

        "has a second tax rate of 28%" in {
          (json \ "secondRate").asOpt[Int] mustBe Some(28)
        }

        "has a second tax band of 67115" in {
          (json \ "secondBand").asOpt[Double] mustBe Some(67115)
        }

        "has the broughtForwardLossesUsed as £10000" in {
          (json \ "broughtForwardLossesUsed").as[Double] mustBe 10000
        }

        "has the allowableLossesUsed as £20000" in {
          (json \ "allowableLossesUsed").as[Double] mustBe 20000
        }

        "has the baseRateTotal as £3,921.29" in {
          (json \ "baseRateTotal").as[Double] mustBe 3921.29
        }

        "has the upperRateTotal as £18,792.20" in {
          (json \ "upperRateTotal").as[Double] mustBe 18792.2
        }
      }
    }

    "when using 2016/17 tax year values" must {

      lazy val result = controller.calculateTaxOwed(CalculateTaxOwedModel(
         ChargeableGainModel(TotalGainModel(disposalValue = 250000,
          disposalCosts = 10000,
          acquisitionValue = 100000,
          acquisitionCosts = 10000), allowableLosses = Some(20000),
          broughtForwardLosses = Some(10000),
          annualExemptAmount = 11100),
        previousTaxableGain = Some(10000),
        previousIncome = 10000,
        personalAllowance = 11000,
        disposalDate = LocalDate.parse("2016-10-10")
      ))(fakeRequest)

      "return a 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) mustBe Some("application/json")
        }

        "has the gain as 130000" in {
          (json \ "gain").as[Double] mustBe 130000
        }

        "has the chargeableGain as 88900" in {
          (json \ "chargeableGain").as[Double] mustBe 88900.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] mustBe 11100.0
        }

        "has the deductions as 41100" in {
          (json \ "deductions").as[Double] mustBe 41100.0
        }

        "has the taxOwed as 15580.0" in {
          (json \ "taxOwed").as[Double] mustBe 15580.0
        }

        "has a first tax rate of 10%" in {
          (json \ "firstRate").as[Int] mustBe 10
        }

        "has a first tax band of 22000" in {
          (json \ "firstBand").as[Double] mustBe 22000
        }

        "has a second tax rate of 20%" in {
          (json \ "secondRate").asOpt[Int] mustBe Some(20)
        }

        "has a second tax band of 66900" in {
          (json \ "secondBand").asOpt[Double] mustBe Some(66900)
        }

        "has the broughtForwardLossesUsed as £10000" in {
          (json \ "broughtForwardLossesUsed").as[Double] mustBe 10000
        }

        "has the allowableLossesUsed as £20000" in {
          (json \ "allowableLossesUsed").as[Double] mustBe 20000
        }

        "has the baseRateTotal as £2,200.00" in {
          (json \ "baseRateTotal").as[Double] mustBe 2200.0
        }

        "has the upperRateTotal as £13,380.00" in {
          (json \ "upperRateTotal").as[Double] mustBe 13380.0
        }
      }
    }

    "Part allowableLossesUsed" must {
      lazy val result = controller.calculateChargeableGain(ChargeableGainModel(
        TotalGainModel(disposalValue = 50000,
          disposalCosts = 0,
          acquisitionValue = 0,
          acquisitionCosts = 0),
        allowableLosses = Some(100000),
        broughtForwardLosses = Some(0),
        annualExemptAmount = 11100)
      )(fakeRequest)

      "return a 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) mustBe Some("application/json")
        }

        "has the gain as 50000" in {
          (json \ "gain").as[Double] mustBe 50000
        }

        "has the chargeableGain as 49000.0" in {
          (json \ "chargeableGain").as[Double] mustBe -50000
        }

        "has the aeaUsed as 0" in {
          (json \ "aeaUsed").as[Double] mustBe 0.0
        }

        "has the deductions as 50000" in {
          (json \ "deductions").as[Double] mustBe 50000
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] mustBe 0
        }

        "has the allowableLossesUsed as £50000" in {
          (json \ "allowableLossesUsed").as[Double] mustBe 50000
        }
      }
    }
  }

  "Calling calculateTotalCosts" when {

    "a valid request is supplied" must {

      val totalGainModel = TotalGainModel(0, 999.99, 0, 299.50)

      lazy val fakeRequest = FakeRequest("GET", "")
      lazy val result = controller.calculateTotalCosts(totalGainModel)(fakeRequest)

      "return a 200 status" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return totalCosts" in {
        val data = contentAsString(result)
        val json = Json.parse(data)

        json.as[Double] mustEqual 1300.0
      }
    }
  }
}
