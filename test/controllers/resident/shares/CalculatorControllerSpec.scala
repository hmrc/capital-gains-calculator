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

package controllers.resident.shares

import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CalculatorControllerSpec extends UnitSpec with WithFakeApplication {

  "ShareCalculatorController.calculateTotalGain" when {
    lazy val fakeRequest = FakeRequest("GET", "")

    "numeric values are passed" should {
      lazy val result = CalculatorController.calculateTotalGain(100000, 10000, 50000, 10000)(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a Some" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Option[BigDecimal]] shouldBe Some(BigDecimal(30000.0))
      }
    }
  }

  "ShareCalculatorController.calculateChargeableGain" when {

    lazy val fakeRequest = FakeRequest("GET", "")

    "numeric values are passed" should {

      lazy val result = CalculatorController.calculateChargeableGain(
        disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000,
        allowableLosses = Some(5000),
        broughtForwardLosses = Some(20000),
        annualExemptAmount = 11100
      )(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) shouldBe Some("application/json")
        }

        "has the gain as 33000" in {
          (json \ "gain").as[Double] shouldBe 33000
        }

        "has the chargeableGain as -3100" in {
          (json \ "chargeableGain").as[Double] shouldBe -3100.0
        }

        "has the aeaUsed as 11000" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the aeaRemaining as 0" in {
          (json \ "aeaRemaining").as[Double] shouldBe 0.0
        }

        "has the deductions as 36100" in {
          (json \ "deductions").as[Double] shouldBe 36100
        }

        "has the allowableLossesRemaining as £0" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesRemaining as £3100" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 3100
        }

        "has the broughtForwardLossesUsed as £16900" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 16900
        }

        "has the allowableLossesUsed as £5000" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 5000
        }
      }
    }

    "numeric values are passed with correct rounding" should {

      lazy val result = CalculatorController.calculateChargeableGain(
        disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000,
        allowableLosses = Some(4999.01),
        broughtForwardLosses = Some(19999.01),
        annualExemptAmount = 11100
      )(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) shouldBe Some("application/json")
        }

        "has the gain as 33000" in {
          (json \ "gain").as[Double] shouldBe 33000
        }

        "has the chargeableGain as -3100" in {
          (json \ "chargeableGain").as[Double] shouldBe -3100.0
        }

        "has the aeaUsed as -11000" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the aeaRemaining as 0" in {
          (json \ "aeaRemaining").as[Double] shouldBe 0.0
        }

        "has the deductions as 36100" in {
          (json \ "deductions").as[Double] shouldBe 36100
        }

        "has the allowableLossesRemaining as " in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesRemaining as £3100" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 3100
        }

        "has the broughtForwardLossesUsed as £16900" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 16900
        }

        "has the allowableLossesUsed as £5000" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 5000
        }
      }
    }
  }

  "ShareCalculatorController.calculateTaxOwed" when {
    lazy val fakeRequest = FakeRequest("GET", "")

    "no optional values are provided" should {
      lazy val result = CalculatorController.calculateTaxOwed(
        disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000,
        allowableLosses = None,
        broughtForwardLosses = None,
        annualExemptAmount = 11100,
        previousTaxableGain = None,
        previousIncome = 20000,
        personalAllowance = 11000,
        disposalDate = "2015-10-10"
      )(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) shouldBe Some("application/json")
        }

        "has the gain as 33000" in {
          (json \ "gain").as[Double] shouldBe 33000
        }

        "has the chargeableGain as 21900" in {
          (json \ "chargeableGain").as[Double] shouldBe 21900.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the deductions as 11100" in {
          (json \ "deductions").as[Double] shouldBe 11100.0
        }

        "has the taxOwed as 3942" in {
          (json \ "taxOwed").as[Double] shouldBe 3942.0
        }

        "has a first tax rate of 18%" in {
          (json \ "firstRate").as[Int] shouldBe 18
        }

        "has a first tax band of 21900" in {
          (json \ "firstBand").as[Double] shouldBe 21900
        }

        "has no second tax rate" in {
          (json \ "secondRate").as[Option[Int]] shouldBe None
        }

        "has no second tax band" in {
          (json \ "secondBand").as[Option[Double]] shouldBe None
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 0
        }

        "has the allowableLossesUsed as £0.00" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 0
        }
      }
    }

    "all optional values are provided" should {
      lazy val result = CalculatorController.calculateTaxOwed(
        disposalValue = 250000,
        disposalCosts = 10000,
        acquisitionValue = 100000,
        acquisitionCosts = 10000,
        allowableLosses = Some(20000),
        broughtForwardLosses = Some(10000),
        annualExemptAmount = 11100,
        previousTaxableGain = Some(10000),
        previousIncome = 10000,
        personalAllowance = 11000,
        disposalDate = "2015-10-10"
      )(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) shouldBe Some("application/json")
        }

        "has the gain as 130000" in {
          (json \ "gain").as[Double] shouldBe 130000
        }

        "has the chargeableGain as 88900" in {
          (json \ "chargeableGain").as[Double] shouldBe 88900.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the deductions as 41100" in {
          (json \ "deductions").as[Double] shouldBe 41100.0
        }

        "has the taxOwed as 22713.5" in {
          (json \ "taxOwed").as[Double] shouldBe 22713.5
        }

        "has a first tax rate of 18%" in {
          (json \ "firstRate").as[Int] shouldBe 18
        }

        "has a first tax band of 21785" in {
          (json \ "firstBand").as[Double] shouldBe 21785
        }

        "has a second tax rate of 28%" in {
          (json \ "secondRate").as[Option[Int]] shouldBe Some(28)
        }

        "has a second tax band of 67115" in {
          (json \ "secondBand").as[Option[Double]] shouldBe Some(67115)
        }

        "has the broughtForwardLossesUsed as £10000" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 10000
        }

        "has the allowableLossesUsed as £20000" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 20000
        }
      }
    }

    "when using 2016/17 tax year values" should {
      lazy val result = CalculatorController.calculateTaxOwed(
        disposalValue = 250000,
        disposalCosts = 10000,
        acquisitionValue = 100000,
        acquisitionCosts = 10000,
        allowableLosses = Some(20000),
        broughtForwardLosses = Some(10000),
        annualExemptAmount = 11100,
        previousTaxableGain = Some(10000),
        previousIncome = 10000,
        personalAllowance = 11000,
        disposalDate = "2016-10-10"
      )(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" which {

        lazy val data = contentAsString(result)
        lazy val json = Json.parse(data)

        "has content type application/json" in {
          contentType(result) shouldBe Some("application/json")
        }

        "has the gain as 130000" in {
          (json \ "gain").as[Double] shouldBe 130000
        }

        "has the chargeableGain as 88900" in {
          (json \ "chargeableGain").as[Double] shouldBe 88900.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the deductions as 41100" in {
          (json \ "deductions").as[Double] shouldBe 41100.0
        }

        "has the taxOwed as 15580.0" in {
          (json \ "taxOwed").as[Double] shouldBe 15580.0
        }

        "has a first tax rate of 10%" in {
          (json \ "firstRate").as[Int] shouldBe 10
        }

        "has a first tax band of 22000" in {
          (json \ "firstBand").as[Double] shouldBe 22000
        }

        "has a second tax rate of 20%" in {
          (json \ "secondRate").as[Option[Int]] shouldBe Some(20)
        }

        "has a second tax band of 66900" in {
          (json \ "secondBand").as[Option[Double]] shouldBe Some(66900)
        }

        "has the broughtForwardLossesUsed as £10000" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 10000
        }

        "has the allowableLossesUsed as £20000" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 20000
        }
      }
    }
  }
}
