/*
 * Copyright 2021 HM Revenue & Customs
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

import models.resident.properties.{PropertyCalculateTaxOwedModel, PropertyChargeableGainModel, PropertyTotalGainModel}
import models.resident.shares.TotalGainModel
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.ControllerComponents
import services.CalculationService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CalculatorControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  val service = new CalculationService
  val mockComponents = fakeApplication.injector.instanceOf[ControllerComponents]
  val controller = new CalculatorController(service, mockComponents)

  "CalculatorController.calculateTotalGain" when {
    lazy val fakeRequest = FakeRequest("GET", "")

    "numeric values are passed" should {
      lazy val result = controller.calculateTotalGain(PropertyTotalGainModel(TotalGainModel(100000, 10000, 50000, 10000), 10000))(fakeRequest)

      "return a 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a Some" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.asOpt[BigDecimal] shouldBe Some(BigDecimal(20000.0))
      }
    }
  }

  "CalculatorController.calculateChargeableGain" when {

    lazy val fakeRequest = FakeRequest("GET", "")

    "numeric values are passed" should {

      lazy val result = controller.calculateChargeableGain(PropertyChargeableGainModel(PropertyTotalGainModel(
        TotalGainModel(195000, 1000, 160000, 1000), 5000), None, Some(1000), Some(5000), Some(20000), 11100, DateTime.parse("2015-05-06"))
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

        "has the aeaUsed as 11000" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the aeaRemaining as 0" in {
          (json \ "aeaRemaining").as[Double] shouldBe 0.0
        }

        "has the deductions as 28000" in {
          (json \ "deductions").as[Double] shouldBe 28000
        }

        "has the allowableLossesRemaining as £0" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesRemaining as £8100" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 8100
        }

        "has the letting reliefs used as £0" in {
          (json \ "lettingReliefsUsed").as[Double] shouldBe 0
        }

        "has the prr used as £0" in {
          (json \ "prrUsed").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesUsed as £11900" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 11900
        }

        "has the allowableLossesUsed as £5000" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 5000
        }
      }
    }

    "numeric values are passed with correct rounding" should {
      lazy val result = controller.calculateChargeableGain(PropertyChargeableGainModel(PropertyTotalGainModel(
        TotalGainModel(195000, 1000, 160000, 1000), 5000), None, None, Some(4999.01), Some(19999.01), 11100, DateTime.parse("2015-05-06"))
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

        "has the deductions as 28000" in {
          (json \ "deductions").as[Double] shouldBe 28000
        }

        "has the allowableLossesRemaining as £0" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesRemaining as £8100" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 8100
        }

        "has the letting reliefs used as £0" in {
          (json \ "lettingReliefsUsed").as[Double] shouldBe 0
        }

        "has the prr used as £0" in {
          (json \ "prrUsed").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesUsed as £11900" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 11900
        }

        "has the allowableLossesUsed as £5000" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 5000
        }
      }
    }

    "numeric values are passed with reliefs greater than gain" should {


      lazy val result = controller.calculateChargeableGain(PropertyChargeableGainModel(PropertyTotalGainModel(
        TotalGainModel(195000, 1000, 160000, 1000), 5000), Some(20000), Some(30000), Some(4999.01), Some(19999.01),
        11100, DateTime.parse("2015-05-06"))
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

        "has the deductions as 28000" in {
          (json \ "deductions").as[Double] shouldBe 28000
        }

        "has the allowableLossesRemaining as £5000" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 5000
        }

        "has the broughtForwardLossesRemaining as 20000" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 20000
        }

        "has the letting reliefs used as £8000" in {
          (json \ "lettingReliefsUsed").as[Double] shouldBe 8000
        }

        "has the prr used as £20000" in {
          (json \ "prrUsed").as[Double] shouldBe 20000
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 0
        }

        "has the allowableLossesUsed as £0.00" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 0
        }
      }
    }

    "numeric values are passed with reliefs greater than gain and lettings exceeding 40000" should {

      lazy val result = controller.calculateChargeableGain(PropertyChargeableGainModel(PropertyTotalGainModel(
        TotalGainModel(200000, 0, 100000, 0), 0), Some(55000), Some(45000), Some(4999.01), Some(19999.01), 11100, DateTime.parse("2015-05-06"))
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

        "has the chargeableGain as 20000" in {
          (json \ "chargeableGain").as[Double] shouldBe -20000
        }

        "has the aeaUsed as 0" in {
          (json \ "aeaUsed").as[Double] shouldBe 0
        }

        "has the aeaRemaining as 11100.0" in {
          (json \ "aeaRemaining").as[Double] shouldBe 11100.0
        }

        "has the deductions as 100000" in {
          (json \ "deductions").as[Double] shouldBe 100000
        }

        "has the allowableLossesRemaining as £0" in {
          (json \ "allowableLossesRemaining").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesRemaining as 20000" in {
          (json \ "broughtForwardLossesRemaining").as[Double] shouldBe 20000
        }

        "has the letting reliefs used as £40000" in {
          (json \ "lettingReliefsUsed").as[Double] shouldBe 40000
        }

        "has the prr used as £55000" in {
          (json \ "prrUsed").as[Double] shouldBe 55000
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 0
        }

        "has the allowableLossesUsed as £5000.00" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 5000
        }
      }
    }
  }

  "CalculatorController.calculateTaxOwed" when {
    lazy val fakeRequest = FakeRequest("GET", "")

    "no optional values are provided" should {
      lazy val result = controller.calculateTaxOwed(PropertyCalculateTaxOwedModel(
        PropertyChargeableGainModel(PropertyTotalGainModel(TotalGainModel(disposalValue = 195000,
        disposalCosts = 1000,
        acquisitionValue = 160000,
        acquisitionCosts = 1000),
        improvements = 5000),
        prrValue = None,
        lettingReliefs = None,
        allowableLosses = None,
        broughtForwardLosses = None,
        annualExemptAmount = 11100,
          disposalDate = DateTime.parse("2015-10-10")),
        previousTaxableGain = None,
        previousIncome = 20000,
        personalAllowance = 11000)
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
          (json \ "secondRate").asOpt[Int] shouldBe None
        }

        "has no second tax band" in {
          (json \ "secondBand").asOpt[Double] shouldBe None
        }

        "has the letting reliefs used as £0" in {
          (json \ "lettingReliefsUsed").as[Double] shouldBe 0
        }

        "has the prr used as £0" in {
          (json \ "prrUsed").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 0
        }

        "has the allowableLossesUsed as £0.00" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 0
        }

        "has the baseRateTotal as £3,042.00" in {
          (json \ "baseRateTotal").as[Double] shouldBe 3042
        }

        "has the upperRateTotal as £0.00" in {
          (json \ "upperRateTotal").as[Double] shouldBe 0
        }
      }
    }

    "all optional values are provided" should {
      lazy val result = controller.calculateTaxOwed(PropertyCalculateTaxOwedModel(
        PropertyChargeableGainModel(PropertyTotalGainModel(TotalGainModel(
        disposalValue = 250000,
        disposalCosts = 10000,
        acquisitionValue = 100000,
        acquisitionCosts = 10000),
        improvements = 30000),
        prrValue = Some(1000),
        lettingReliefs = Some(8900),
        allowableLosses = Some(20000),
        broughtForwardLosses = Some(10000),
        annualExemptAmount = 11100,
        disposalDate = DateTime.parse("2015-10-10")),
        previousTaxableGain = Some(10000),
        previousIncome = 10000,
        personalAllowance = 11000)
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

        "has the chargeableGain as 56900.0" in {
          (json \ "chargeableGain").as[Double] shouldBe 56900.0
        }

        "has the aeaUsed as 11100" in {
          (json \ "aeaUsed").as[Double] shouldBe 11100.0
        }

        "has the deductions as 43100" in {
          (json \ "deductions").as[Double] shouldBe 43100.0
        }

        "has the taxOwed as 13753.5" in {
          (json \ "taxOwed").as[Double] shouldBe 13753.5
        }

        "has a first tax rate of 18%" in {
          (json \ "firstRate").as[Int] shouldBe 18
        }

        "has a first tax band of 21785" in {
          (json \ "firstBand").as[Double] shouldBe 21785
        }

        "has a second tax rate of 28%" in {
          (json \ "secondRate").asOpt[Int] shouldBe Some(28)
        }

        "has a second tax band of 35115" in {
          (json \ "secondBand").asOpt[Double] shouldBe Some(35115)
        }

        "has the letting reliefs used as £1000" in {
          (json \ "lettingReliefsUsed").as[Double] shouldBe 1000
        }

        "has the prr used as £1000" in {
          (json \ "prrUsed").as[Double] shouldBe 1000
        }

        "has the broughtForwardLossesUsed as £10000" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 10000
        }

        "has the allowableLossesUsed as £20000" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 20000
        }

        "has the baseRateTotal as £3,921.29" in {
          (json \ "baseRateTotal").as[Double] shouldBe 3921.29
        }

        "has the upperRateTotal as £9,832.20" in {
          (json \ "upperRateTotal").as[Double] shouldBe 9832.2
        }
      }
    }

    "Part allowableLossesUsed" should {
      lazy val result = controller.calculateChargeableGain(PropertyChargeableGainModel(PropertyTotalGainModel(
        TotalGainModel(50000, 0, 0, 0), 0), None, None, Some(100000), Some(0), 11100, DateTime.parse("2015-05-06"))
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

        "has the gain as 50000" in {
          (json \ "gain").as[Double] shouldBe 50000
        }

        "has the chargeableGain as 49000.0" in {
          (json \ "chargeableGain").as[Double] shouldBe -50000
        }

        "has the aeaUsed as 0" in {
          (json \ "aeaUsed").as[Double] shouldBe 0.0
        }

        "has the deductions as 50000" in {
          (json \ "deductions").as[Double] shouldBe 50000
        }

        "has the letting reliefs used as £0" in {
          (json \ "lettingReliefsUsed").as[Double] shouldBe 0
        }

        "has the prr used as £0" in {
          (json \ "prrUsed").as[Double] shouldBe 0
        }

        "has the broughtForwardLossesUsed as £0" in {
          (json \ "broughtForwardLossesUsed").as[Double] shouldBe 0
        }

        "has the allowableLossesUsed as £50000" in {
          (json \ "allowableLossesUsed").as[Double] shouldBe 50000
        }
      }
    }
  }

  "Calling calculateTotalCosts" when {

    "a valid request is supplied" should {

      val propertyTotalGainModel = PropertyTotalGainModel(TotalGainModel(0, 999.99, 0, 299.50), 5000.01)

      lazy val fakeRequest = FakeRequest("GET", "")
      lazy val result = controller.calculateTotalCosts(propertyTotalGainModel)(fakeRequest)

      "return a 200 status" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return totalCosts" in {
        val data = contentAsString(result)
        val json = Json.parse(data)

        json.as[Double] shouldEqual 6301.00
      }
    }

  }
}
