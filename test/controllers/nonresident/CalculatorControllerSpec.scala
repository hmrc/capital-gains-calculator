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

package controllers.nonresident

import models.CalculationResultModel
import models.nonResident.{CalculationRequestModel,  TimeApportionmentCalculationRequestModel}
import org.joda.time.DateTime
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CalculationService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CalculatorControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  "GET /capital-gains-calculator/calculate-flat" should {

    val mockCalculationService = mock[CalculationService]
    when(mockCalculationService.calculateCapitalGainsTax(
      Matchers.anyString,
      Matchers.anyString,
      Matchers.anyString,
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Option(Matchers.any[DateTime]()),
      Matchers.any[DateTime](),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Matchers.anyBoolean()
    )).thenReturn(CalculationResultModel(1800, 21100, 10000, 18, 0, 0))

    val target: CalculatorController = new CalculatorController {
      override val calculationService = mockCalculationService
    }

    val fakeRequest = FakeRequest("GET", "/capital-gains-calculator/calculate-flat")
    val result = target.calculateFlat(
      CalculationRequestModel(
        customerType = "individual",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        otherPropertiesAmount = Some(0),
        isVulnerable = None,
        currentIncome = Some(7000),
        personalAllowanceAmount = Some(11000),
        disposalValue = 21100,
        disposalCosts = 0,
        initialValue = 0,
        initialCosts = 0,
        improvementsAmount = 0,
        reliefsAmount = 0,
        allowableLosses = 0,
        acquisitionDate = None,
        disposalDate = DateTime.parse("2016-10-10"),
        isClaimingPRR = None,
        daysClaimed = None
      )
    ) (fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
      charset(result) shouldBe Some("utf-8")
    }

    "return a valid result" in {
      val data = contentAsString(result)
      val json = Json.parse(data)
      (json \ "taxOwed").as[Double] shouldBe 1800.0
    }

  }

  "GET /capital-gains-calculator/calculate-rebased" should {

    val mockCalculationService = mock[CalculationService]
    when(mockCalculationService.calculateCapitalGainsTax(
      Matchers.anyString,
      Matchers.anyString,
      Matchers.anyString,
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Option(Matchers.any[DateTime]()),
      Matchers.any[DateTime](),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Matchers.anyBoolean()
    )).thenReturn(CalculationResultModel(1800, 21100, 10000, 18, 0, 0))

    val target: CalculatorController = new CalculatorController {
      override val calculationService = mockCalculationService
    }

    val fakeRequest = FakeRequest("GET", "/capital-gains-calculator/calculate-rebased")
    val result = target.calculateRebased(
      CalculationRequestModel(
        customerType = "individual",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        otherPropertiesAmount = Some(0),
        isVulnerable = None,
        currentIncome = Some(7000),
        personalAllowanceAmount = Some(11000),
        disposalValue = 21100,
        disposalCosts = 0,
        initialValue = 0,
        initialCosts = 0,
        improvementsAmount = 0,
        reliefsAmount = 0,
        allowableLosses = 0,
        acquisitionDate = None,
        disposalDate = DateTime.parse("2016-1-1"),
        isClaimingPRR = None,
        daysClaimed = None
      )
    ) (fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
      charset(result) shouldBe Some("utf-8")
    }

    "return a valid result" in {
      val data = contentAsString(result)
      val json = Json.parse(data)
      (json \ "taxOwed").as[Double] shouldBe 1800.0
    }

  }

  "GET /capital-gains-calculator/calculate-time-apportioned" should {

    val mockCalculationService = mock[CalculationService]
    when(mockCalculationService.calculateCapitalGainsTax(
      Matchers.anyString,
      Matchers.anyString,
      Matchers.anyString,
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Matchers.anyDouble,
      Option(Matchers.any[DateTime]()),
      Matchers.any[DateTime](),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble),
      Matchers.anyBoolean()
    )).thenReturn(CalculationResultModel(1800, 21100, 10000, 18, 0, 0))

    val target: CalculatorController = new CalculatorController {
      override val calculationService = mockCalculationService
    }

    val fakeRequest = FakeRequest("GET", "/capital-gains-calculator/calculate-time-apportioned")
    val result = target.calculateTA(
      TimeApportionmentCalculationRequestModel(
      customerType = "individual",
      priorDisposal = "No",
      annualExemptAmount = Some(0),
      otherPropertiesAmount = Some(0),
      isVulnerable = None,
      currentIncome = Some(7000),
      personalAllowanceAmount = Some(11000),
      disposalValue = 31100,
      disposalCosts = 0,
      initialValue = 0,
      initialCosts = 0,
      improvementsAmount = 0,
      reliefsAmount = 0,
      allowableLosses = 0,
      acquisitionDate = DateTime.parse("2014-4-1"),
      disposalDate = DateTime.parse("2016-1-1"),
      isClaimingPRR = None,
      daysClaimed = None
      )
    ) (fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
      charset(result) shouldBe Some("utf-8")
    }

    "return a valid result" in {
      val data = contentAsString(result)
      val json = Json.parse(data)
      (json \ "taxOwed").as[Double] shouldBe 368.64
    }

  }

  "Calling timeApportionedCalculationApplied" should {

    "return a true when provided with valid dates" in {
      val acquisitionDate = DateTime.parse("2013-03-10")
      val disposalDate = DateTime.parse("2016-01-14")
      val result = CalculatorController.timeApportionedCalculationApplicable(Some(disposalDate), Some(acquisitionDate))

      result shouldBe true
    }

    "return a false when provided with an acquisition date after the tax start date" in {
      val acquisitionDate = DateTime.parse("2015-07-10")
      val disposalDate = DateTime.parse("2016-01-14")
      val result = CalculatorController.timeApportionedCalculationApplicable(Some(disposalDate), Some(acquisitionDate))

      result shouldBe false
    }

    "return a false when provided a disposal date before the tax start date" in {
      val acquisitionDate = DateTime.parse("2013-07-10")
      val disposalDate = DateTime.parse("2013-01-14")
      val result = CalculatorController.timeApportionedCalculationApplicable(Some(disposalDate), Some(acquisitionDate))

      result shouldBe false
    }

    "return a false when not provided with an acquisition date" in {
      val disposalDate = DateTime.parse("2016-01-14")
      val result = CalculatorController.timeApportionedCalculationApplicable(Some(disposalDate), None)

      result shouldBe false
    }

    "return a false when not provided with a disposal date" in {
      val acquisitionDate = DateTime.parse("2013-07-10")
      val result = CalculatorController.timeApportionedCalculationApplicable(None, Some(acquisitionDate))

      result shouldBe false
    }
  }

  "Calling .calculateTotalGain" when {

    "only provided with mandatory values" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]

      when(mockService.calculateGainFlat(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(1.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val improvementsBefore = 2.0
      val improvementsAfter = improvementsBefore + 2
      val totalImprovements = improvementsBefore + improvementsAfter
      val result = target.calculateTotalGain(1, 1, 1, 1, improvementsBefore, None, 0, None, None, improvementsAfter)(fakeRequest)

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flatGain of 1.0" in {
          (json \ "flatGain").as[Double] shouldBe 1.0
        }

        "should have no value for rebasedGain" in {
          (json \ "rebasedGain").asOpt[Double] shouldBe None
        }

        "should have no value for timeApportionedGain" in {
          (json \ "timeApportionedGain").asOpt[Double] shouldBe None
        }
      }

      "call the flat gain function on the calculator service once" in {
        verify(mockService, times(1)).calculateGainFlat(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }

      "pass the flat gain function on the calculator service the total of the improvements" in {
        verify(mockService).calculateGainFlat(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.eq(totalImprovements))
      }

      "not call the rebased gain function on the calculator service" in {
        verify(mockService, times(0)).calculateGainRebased(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }

      "not call the time apportioned gain function on the calculator service" in {
        verify(mockService, times(0))
          .calculateGainTA(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }
    }

    "provided with the values for the rebased calculation" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]

      when(mockService.calculateGainFlat(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(1.0)
      when(mockService.calculateGainRebased(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(2.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val improvementsBefore = 2.0
      val improvementsAfter = improvementsBefore + 2
      val result = target.calculateTotalGain(1, 1, 1, 1, improvementsBefore, Some(1), 1, None, None, improvementsAfter)(fakeRequest)

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flatGain of 1.0" in {
          (json \ "flatGain").as[Double] shouldBe 1.0
        }

        "should have a rebasedGain of 2.0" in {
          (json \ "rebasedGain").asOpt[Double] shouldBe Some(2.0)
        }

        "should have no value for timeApportionedGain" in {
          (json \ "timeApportionedGain").asOpt[Double] shouldBe None
        }
      }

      "call the flat gain function on the calculator service once" in {
        verify(mockService, times(1)).calculateGainFlat(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }

      "call the rebased gain function on the calculator service once" in {
        verify(mockService, times(1)).calculateGainRebased(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }

      "pass the rebased gain function on the calculator service with the improvements after value" in {
        verify(mockService, times(1)).calculateGainRebased(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.eq(improvementsAfter))
      }

      "not call the time apportioned gain function on the calculator service" in {
        verify(mockService, times(0))
          .calculateGainTA(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }
    }

    "provided with the values for the time apportioned calculation" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]
      val disposalDate = DateTime.parse("2016-05-08")
      val acquisitionDate = DateTime.parse("2012-04-09")

      when(mockService.calculateGainFlat(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(1.0)
      when(mockService.calculateGainTA(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(3.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val improvementsBefore = 2.0
      val improvementsAfter = improvementsBefore + 2
      val totalImprovements = improvementsBefore + improvementsAfter
      val result = target.calculateTotalGain(1, 1, 1, 1, improvementsBefore, None, 0, Some(disposalDate), Some(acquisitionDate), improvementsAfter)(fakeRequest)

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flatGain of 1.0" in {
          (json \ "flatGain").as[Double] shouldBe 1.0
        }

        "should have no value for rebasedGain" in {
          (json \ "rebasedGain").asOpt[Double] shouldBe None
        }

        "should have a timeApportionedGain of 3.0" in {
          (json \ "timeApportionedGain").asOpt[Double] shouldBe Some(3.0)
        }
      }

      "call the flat gain function on the calculator service once" in {
        verify(mockService, times(1)).calculateGainFlat(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }

      "not call the rebased gain function on the calculator service" in {
        verify(mockService, times(0)).calculateGainRebased(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }

      "call the time apportioned gain function on the calculator service once" in {
        verify(mockService, times(1))
          .calculateGainTA(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())
      }

      "pass the time apportioned gain function on the calculator service the total of the improvements" in {
        verify(mockService)
          .calculateGainTA(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.eq(totalImprovements), Matchers.any(), Matchers.any())
      }
    }
  }

  "Calling .calculateTaxableGainAfterPRR" when {

    "only provided with mandatory values" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]
      val disposalDate = DateTime.parse("2016-05-08")
      val acquisitionDate = DateTime.parse("2012-04-09")

      when(mockService.calculateGainFlat(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(1.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val claimingPRR = false
      val daysClaimed = 0
      val daysClaimedAfter = 0

      val result = target.calculateTaxableGainAfterPRR(1, 1, 1, 1, 2.0, None, 0, Some(disposalDate), Some(acquisitionDate),
        4.0, claimingPRR, daysClaimed, daysClaimedAfter)(fakeRequest)

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flatGain of 1.0" in {
          (json \ "flatResult" \ "totalGain").as[Double] shouldEqual 1.0
        }

        "should have a rebasedGain of None" in {
          (json \ "rebasedGain").asOpt[Double] shouldBe None
        }

        "should have a timeApportionedGain of None" in {
          (json \ "timeApportionedGain").asOpt[Double] shouldBe None
        }
      }
    }
  }
}
