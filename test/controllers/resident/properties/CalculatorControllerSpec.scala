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

package controllers.resident.properties

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
        prrType = "None",
        prrValue = None,
        reliefs = Some(1000),
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

        "has the gain as 28000" in {
          (json \ "gain").as[Double] shouldBe 28000
        }

        "has the chargeableGain as -9100" in {
          (json \ "chargeableGain").as[Double] shouldBe -9100.0
        }

        "has the aeaUsed as 11000" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the aeaRemaining as 0" in {
          (json \ "aeaRemaining").as[Double] shouldBe 0.0
        }

        "has the deductions as 37100" in {
          (json \ "deductions").as[Double] shouldBe 37100
        }

        "has the allowableLossesRemaining as £0" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesRemaining as £9100" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 9100
        }

        "has the reliefs used as £1000" in {
          (json \ "reliefsUsed").as[Double] shouldBe 1000
        }

        "has the prr used as £0" in {
          (json \ "prrUsed").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesUsed as £10900" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 10900
        }
      }
    }

    "numeric values are passed with correct rounding" should {

      lazy val result = CalculatorController.calculateChargeableGain(
        disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000,
        improvements = 5000,
        prrType = "None",
        prrValue = None,
        reliefs = None,
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

        "has the gain as 28000" in {
          (json \ "gain").as[Double] shouldBe 28000
        }

        "has the chargeableGain as -8100" in {
          (json \ "chargeableGain").as[Double] shouldBe -8100.0
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

        "has the allowableLossesRemaining as £0" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesRemaining as £8100" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 8100
        }

        "has the reliefs used as £0" in {
          (json \ "reliefsUsed").as[Double] shouldBe 0
        }

        "has the prr used as £0" in {
          (json \ "prrUsed").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesUsed as £11900" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 11900
        }
      }
    }

    "numeric values are passed with reliefs greater than gain" should {

      lazy val result = CalculatorController.calculateChargeableGain(
        disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000,
        improvements = 5000,
        prrType = "Part",
        prrValue = Some(200),
        reliefs = Some(50000),
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

        "has the gain as 28000" in {
          (json \ "gain").as[Double] shouldBe 28000
        }

        "has the chargeableGain as 0" in {
          (json \ "chargeableGain").as[Double] shouldBe 0.0
        }

        "has the aeaUsed as 0" in {
          (json \ "aeaUsed").as[Double] shouldBe 0
        }

        "has the aeaRemaining as 11100.0" in {
          (json \ "aeaRemaining").as[Double] shouldBe 11100.0
        }

        "has the deductions as 53000" in {
          (json \ "deductions").as[Double] shouldBe 53000
        }

        "has the allowableLossesRemaining as £5000" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 5000
        }

        "has the broughtForwardLossesRemaining as 20000" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 20000
        }

        "has the reliefs used as £27800" in {
          (json \ "reliefsUsed").as[Double] shouldBe 27800
        }

        "has the prr used as £200" in {
          (json \ "prrUsed").as[Double] shouldBe 200
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 0
        }
      }
    }

    "numeric values are passed with full PRR claimed" should {

      lazy val result = CalculatorController.calculateChargeableGain(
        disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000,
        improvements = 5000,
        prrType = "Full",
        prrValue = None,
        reliefs = None,
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

        "has the gain as 28000" in {
          (json \ "gain").as[Double] shouldBe 28000
        }

        "has the chargeableGain as 0" in {
          (json \ "chargeableGain").as[Double] shouldBe 0.0
        }

        "has the aeaUsed as 0" in {
          (json \ "aeaUsed").as[Double] shouldBe 0.0
        }

        "has the aeaRemaining as 11,100" in {
          (json \ "aeaRemaining").as[Double] shouldBe 11100.0
        }

        "has the deductions as 53000" in {
          (json \ "deductions").as[Double] shouldBe 53000.0
        }

        "has the allowableLossesRemaining as £5000" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 5000
        }

        "has the broughtForwardLossesRemaining as £20000" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 20000.0
        }

        "has the reliefs used as £0" in {
          (json \ "reliefsUsed").as[Double] shouldBe 0
        }

        "has the prr used as £28000" in {
          (json \ "prrUsed").as[Double] shouldBe 28000
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 0
        }
      }
    }
  }

  "CalculatorController.calculateTaxOwed" when {
    lazy val fakeRequest = FakeRequest("GET", "")

    "no optional values are provided" should {
      lazy val result = CalculatorController.calculateTaxOwed(
        disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000,
        improvements = 5000,
        prrType = "None",
        prrValue = None,
        reliefs = None,
        allowableLosses = None,
        broughtForwardLosses = None,
        annualExemptAmount = 11100,
        previousTaxableGain = None,
        previousIncome = 20000,
        personalAllowance = 11000
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

        "has the gain as 28000" in {
          (json \ "gain").as[Double] shouldBe 28000
        }

        "has the chargeableGain as 16900" in {
          (json \ "chargeableGain").as[Double] shouldBe 16900.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the deductions as 11100" in {
          (json \ "deductions").as[Double] shouldBe 11100.0
        }

        "has the taxOwed as 3042" in {
          (json \ "taxOwed").as[Double] shouldBe 3042.0
        }

        "has a first tax rate of 18%" in {
          (json \ "firstRate").as[Int] shouldBe 18
        }

        "has a first tax band of 16900" in {
          (json \ "firstBand").as[Double] shouldBe 16900
        }

        "has no second tax rate" in {
          (json \ "secondRate").as[Option[Int]] shouldBe None
        }

        "has no second tax band" in {
          (json \ "secondBand").as[Option[Double]] shouldBe None
        }

        "has the reliefs used as £0" in {
          (json \ "reliefsUsed").as[Double] shouldBe 0
        }

        "has the prr used as £0" in {
          (json \ "prrUsed").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 0
        }
      }
    }

    "all optional values are provided" should {
      lazy val result = CalculatorController.calculateTaxOwed(
        disposalValue = 250000,
        disposalCosts = 10000,
        acquisitionValue = 100000,
        acquisitionCosts = 10000,
        improvements = 30000,
        prrType = "Part",
        prrValue = Some(1000),
        reliefs = Some(8900),
        allowableLosses = Some(20000),
        broughtForwardLosses = Some(10000),
        annualExemptAmount = 11100,
        previousTaxableGain = Some(10000),
        previousIncome = 10000,
        personalAllowance = 11000
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

        "has the gain as 100000" in {
          (json \ "gain").as[Double] shouldBe 100000
        }

        "has the chargeableGain as 49000.0" in {
          (json \ "chargeableGain").as[Double] shouldBe 49000.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the deductions as 51000" in {
          (json \ "deductions").as[Double] shouldBe 51000.0
        }

        "has the taxOwed as 11541.5" in {
          (json \ "taxOwed").as[Double] shouldBe 11541.5
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

        "has a second tax band of 27215" in {
          (json \ "secondBand").as[Option[Double]] shouldBe Some(27215)
        }

        "has the reliefs used as £8900" in {
          (json \ "reliefsUsed").as[Double] shouldBe 8900
        }

        "has the prr used as £1000" in {
          (json \ "prrUsed").as[Double] shouldBe 1000
        }

        "has the broughtForwardLossesUsed as £10000" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 10000
        }
      }
    }
  }
}
