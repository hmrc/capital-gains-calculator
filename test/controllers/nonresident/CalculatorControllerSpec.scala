/*
 * Copyright 2017 HM Revenue & Customs
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
import models.nonResident._
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers
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
      ArgumentMatchers.anyString,
      ArgumentMatchers.anyString,
      ArgumentMatchers.anyString,
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyString),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      Option(ArgumentMatchers.any[DateTime]()),
      ArgumentMatchers.any[DateTime](),
      Option(ArgumentMatchers.anyString),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      ArgumentMatchers.anyBoolean()
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
    )(fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
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
      ArgumentMatchers.anyString,
      ArgumentMatchers.anyString,
      ArgumentMatchers.anyString,
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyString),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      Option(ArgumentMatchers.any[DateTime]()),
      ArgumentMatchers.any[DateTime](),
      Option(ArgumentMatchers.anyString),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      ArgumentMatchers.anyBoolean()
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
    )(fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
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
      ArgumentMatchers.anyString,
      ArgumentMatchers.anyString,
      ArgumentMatchers.anyString,
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyString),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      ArgumentMatchers.anyDouble,
      Option(ArgumentMatchers.any[DateTime]()),
      ArgumentMatchers.any[DateTime](),
      Option(ArgumentMatchers.anyString),
      Option(ArgumentMatchers.anyDouble),
      Option(ArgumentMatchers.anyDouble),
      ArgumentMatchers.anyBoolean()
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
    )(fakeRequest)

    "return 200" in {
      status(result) shouldBe Status.OK
    }

    "return a JSON result" in {
      contentType(result) shouldBe Some("application/json")
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

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(1.0)

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
        verify(mockService, times(1)).calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }

      "pass the flat gain function on the calculator service the total of the improvements" in {
        verify(mockService).calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(totalImprovements))
      }

      "not call the rebased gain function on the calculator service" in {
        verify(mockService, times(0)).calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }

      "not call the time apportioned gain function on the calculator service" in {
        verify(mockService, times(0))
          .calculateGainTA(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }
    }

    "provided with the values for the rebased calculation" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(1.0)
      when(mockService.calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(2.0)

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
        verify(mockService, times(1)).calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }

      "call the rebased gain function on the calculator service once" in {
        verify(mockService, times(1)).calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }

      "pass the rebased gain function on the calculator service with the improvements after value" in {
        verify(mockService, times(1)).calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(improvementsAfter))
      }

      "not call the time apportioned gain function on the calculator service" in {
        verify(mockService, times(0))
          .calculateGainTA(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }
    }

    "provided with the values for the time apportioned calculation" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]
      val disposalDate = DateTime.parse("2016-05-08")
      val acquisitionDate = DateTime.parse("2012-04-09")

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(1.0)
      when(mockService.calculateGainTA(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
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
        verify(mockService, times(1)).calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }

      "not call the rebased gain function on the calculator service" in {
        verify(mockService, times(0)).calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }

      "call the time apportioned gain function on the calculator service once" in {
        verify(mockService, times(1))
          .calculateGainTA(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())
      }

      "pass the time apportioned gain function on the calculator service the total of the improvements" in {
        verify(mockService)
          .calculateGainTA(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(totalImprovements), ArgumentMatchers.any(), ArgumentMatchers.any())
      }
    }
  }

  "Calling .calculateTaxableGainAfterPRR" when {

    "provided with a flat calculation with no acquisition date" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]
      val disposalDate = DateTime.parse("2016-05-08")

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(6.0)
      when(mockService.calculateChargeableGain(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(6.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val claimingPRR = false
      val daysClaimed = 0
      val daysClaimedAfter = 0

      val result = target.calculateTaxableGainAfterPRR(10.0, 1, 1, 1, 1, None, 0, Some(disposalDate), None,
        0, claimingPRR, daysClaimed, daysClaimedAfter)(fakeRequest)

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flat result" which {

          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain shouldEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain shouldEqual 6.0
          }

          "should have a prrUsed of 0.0" in {
            flatResultJson.prrUsed shouldEqual 0.0
          }
        }

        "should have a rebasedGain of None" in {
          json.toString should not include "rebasedGain"
        }

        "should have a timeApportionedGain of None" in {
          json.toString should not include "timeApportionedGain"
        }
      }
    }

    "only provided with values for the flat calculation with an acquisition date" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(6.0)
      when(mockService.calculateChargeableGain(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(6.0)
      when(mockService.calculateFlatPRR(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(5.0)
      when(mockService.determineReliefsUsed(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(100.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val claimingPRR = false
      val daysClaimed = 0
      val daysClaimedAfter = 1
      val disposalDate = DateTime.parse("2016-05-08")
      val acquisitionDate = DateTime.parse("2012-04-09")

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

        "should have a flat result" which {

          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain shouldEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain shouldEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            flatResultJson.prrUsed shouldEqual 100.0
          }
        }

        "should have a rebasedGain of None" in {
          json.toString should not include "rebasedGain"
        }

        "should have a timeApportionedGain of None" in {
          json.toString should not include "timeApportionedGain"
        }
      }
    }

    "only provided with values for the flat and rebased calculation without an acquisition date" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(6.0)
      when(mockService.calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(100.0)
      when(mockService.calculateChargeableGain(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(6.0)
      when(mockService.calculateFlatPRR(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(5.0)
      when(mockService.calculateRebasedPRR(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(87.0)
      when(mockService.determineReliefsUsed(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(100.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val claimingPRR = true
      val daysClaimed = 2847
      val daysClaimedAfter = 1
      val disposalDate = DateTime.parse("2017-01-02")

      val result = target.calculateTaxableGainAfterPRR(1000.0, 55.0, 750.0, 50.0, 2.0, Some(150.0), 5.0, Some(disposalDate), None,
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

        "should have a flat result" which {

          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain shouldEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain shouldEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            flatResultJson.prrUsed shouldEqual 100.0
          }
        }

        "should have a rebased result" which {

          val rebasedResultJson = (json \ "rebasedResult").as[GainsAfterPRRModel]

          "should have a totalGain of 100" in {
            rebasedResultJson.totalGain shouldEqual 100.0
          }

          "should have a taxableGain of 6.0" in {
            rebasedResultJson.taxableGain shouldEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            rebasedResultJson.prrUsed shouldEqual 100.0
          }
        }

        "should have a timeApportionedGain of None" in {
          json.toString should not include "timeApportionedGain"
        }
      }
    }

    "provided with values for the flat, rebased and time apportioned calculations" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(6.0)
      when(mockService.calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(100.0)
      when(mockService.calculateGainTA(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(50.0)
      when(mockService.calculateChargeableGain(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(6.0)
      when(mockService.calculateFlatPRR(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(5.0)
      when(mockService.calculateRebasedPRR(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(87.0)
      when(mockService.calculateTimeApportionmentPRR(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(44.0)
      when(mockService.determineReliefsUsed(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(100.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val claimingPRR = true
      val daysClaimed = 2847
      val daysClaimedAfter = 100
      val disposalDate = DateTime.parse("2017-01-02")
      val acquisitionDate = DateTime.parse("2005-10-16")

      val result = target.calculateTaxableGainAfterPRR(1000.0, 55.0, 750.0, 50.0, 2.0, Some(150.0), 5.0, Some(disposalDate), Some(acquisitionDate),
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

        "should have a flat result" which {

          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain shouldEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain shouldEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            flatResultJson.prrUsed shouldEqual 100.0
          }
        }

        "should have a rebased result" which {

          val rebasedResultJson = (json \ "rebasedResult").as[GainsAfterPRRModel]

          "should have a totalGain of 100" in {
            rebasedResultJson.totalGain shouldEqual 100.0
          }

          "should have a taxableGain of 6.0" in {
            rebasedResultJson.taxableGain shouldEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            rebasedResultJson.prrUsed shouldEqual 100.0
          }
        }

        "should have a timeApportionedGain result" which {

          val timeApportionedResultJson = (json \ "timeApportionedResult").as[GainsAfterPRRModel]

          "should have a totalGain of 50" in {
            timeApportionedResultJson.totalGain shouldEqual 50.0
          }

          "should have a taxableGain of 6.0" in {
            timeApportionedResultJson.taxableGain shouldEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            timeApportionedResultJson.prrUsed shouldEqual 100.0
          }
        }
      }
    }

    "provided with values for the flat, rebased and time apportioned calculations but no disposal date" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(6.0)
      when(mockService.calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(100.0)
      when(mockService.calculateGainTA(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(50.0)
      when(mockService.calculateChargeableGain(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(6.0)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val claimingPRR = true
      val daysClaimed = 2847
      val daysClaimedAfter = 1
      val acquisitionDate = DateTime.parse("2005-10-16")
      val disposalDate = DateTime.parse("2017-10-16")

      val result = target.calculateTaxableGainAfterPRR(1000.0, 55.0, 750.0, 50.0, 2.0, Some(150.0), 5.0, Some(disposalDate), Some(acquisitionDate),
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

        "should have a flat result" which {
          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain shouldEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain shouldEqual 6.0
          }

          "should have a prrUsed of 0.0" in {
            flatResultJson.prrUsed shouldEqual 0.0
          }
        }

        "should have a rebasedGain of None" in {
          json.toString should not include "rebasedGain"
        }

        "should have a timeApportionedGain of None" in {
          json.toString should not include "timeApportionedGain"
        }
      }
    }
  }

  "Calling calculateTaxOwed" when {

    "only a flat calculation result is available" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]
      val returnModel = CalculationResultModel(8.0, 9.0, 10.0, 20, 0.0, 0.0)

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(15.0)
      when(mockService.brRemaining(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(1.0)
      when(mockService.calculateChargeableGain(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(2.0)
      when(mockService.determineReliefsUsed(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(3.0)
      when(mockService.determineLossLeft(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(4.0)
      when(mockService.annualExemptAmountUsed(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(5.0)
      when(mockService.annualExemptAmountLeft(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(6.0)
      when(mockService.determineLossLeft(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(7.0)
      when(mockService.calculationResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(returnModel)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val result = target.calculateTaxOwed(1, 0, 1, 0, 0, None, 0, DateTime.parse("2017-10-10"), None, 0,
        PrivateResidenceReliefModel(false, None, None), "Individual", None, 1, 1, 0, 0, 0, 0, OtherReliefsModel(1, 1, 1))(fakeRequest)

      "should have a status of 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)
        val flatResultModel = TaxOwedModel(
          returnModel.taxOwed,
          returnModel.baseTaxGain,
          returnModel.baseTaxRate,
          returnModel.upperTaxGain,
          returnModel.upperTaxRate,
          15.0,
          2,
          Some(3.0),
          Some(3.0),
          None,
          Some(5.0),
          6.0,
          None
        )

        "should have a flat result" in {
          (json \ "flatResult").as[TaxOwedModel] shouldBe flatResultModel
        }

        "should not have a rebased result" in {
          (json \ "rebasedResult").asOpt[TaxOwedModel] shouldBe None
        }

        "should not have a timeApportioned result" in {
          (json \ "timeApportionedResult").asOpt[TaxOwedModel] shouldBe None
        }
      }
    }

    "all calculation methods are available" should {
      val fakeRequest = FakeRequest("GET", "")
      val mockService = mock[CalculationService]
      val returnModel = CalculationResultModel(8.0, 9.0, 10.0, 20, 0.0, 0.0)

      when(mockService.calculateGainFlat(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(15.0)
      when(mockService.calculateGainRebased(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(16.0)
      when(mockService.calculateGainTA(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(17.0)
      when(mockService.brRemaining(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(1.0)
      when(mockService.calculateChargeableGain(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(2.0)
      when(mockService.determineReliefsUsed(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(3.0)
      when(mockService.determineLossLeft(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(4.0)
      when(mockService.annualExemptAmountUsed(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(5.0)
      when(mockService.annualExemptAmountLeft(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(6.0)
      when(mockService.determineLossLeft(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(7.0)
      when(mockService.calculationResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(returnModel)

      val target = new CalculatorController {
        override val calculationService: CalculationService = mockService
      }

      val result = target.calculateTaxOwed(1, 0, 1, 0, 0, Some(1.0), 0, DateTime.parse("2017-10-10"), Some(DateTime.parse("2011-01-05")),
        0, PrivateResidenceReliefModel(false, None, None), "Individual", None, 1, 1, 0, 0, 0, 0, OtherReliefsModel(1, 1, 1))(fakeRequest)

      "should have a status of 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)
        val flatResultModel = TaxOwedModel(
          returnModel.taxOwed,
          returnModel.baseTaxGain,
          returnModel.baseTaxRate,
          returnModel.upperTaxGain,
          returnModel.upperTaxRate,
          15.0,
          2,
          Some(3.0),
          Some(3.0),
          None,
          Some(5.0),
          6.0,
          None
        )
        val rebasedResultModel = TaxOwedModel(
          returnModel.taxOwed,
          returnModel.baseTaxGain,
          returnModel.baseTaxRate,
          returnModel.upperTaxGain,
          returnModel.upperTaxRate,
          16.0,
          2,
          Some(3.0),
          Some(3.0),
          None,
          Some(5.0),
          6.0,
          None
        )
        val timeApportionedResultModel = TaxOwedModel(
          returnModel.taxOwed,
          returnModel.baseTaxGain,
          returnModel.baseTaxRate,
          returnModel.upperTaxGain,
          returnModel.upperTaxRate,
          17.0,
          2,
          Some(3.0),
          Some(3.0),
          None,
          Some(5.0),
          6.0,
          None
        )

        "should have a flat result" in {
          (json \ "flatResult").as[TaxOwedModel] shouldBe flatResultModel
        }

        "should have a rebased result" in {
          (json \ "rebasedResult").asOpt[TaxOwedModel] shouldBe Some(rebasedResultModel)
        }

        "should have a timeApportioned result" in {
          (json \ "timeApportionedResult").asOpt[TaxOwedModel] shouldBe Some(timeApportionedResultModel)
        }
      }
    }
  }
}
