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

package controllers.nonresident

import models.CalculationResultModel
import models.nonResident._
import org.joda.time.DateTime
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.scalatest.BeforeAndAfterEach
import org.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, ControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CalculationService
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class CalculatorControllerSpec extends PlaySpec with MockitoSugar with GuiceOneAppPerSuite with BeforeAndAfterEach {

  val mockService: CalculationService = mock[CalculationService]
  val injectedComponents: ControllerComponents = app.injector.instanceOf[ControllerComponents]

  val controller = new CalculatorController(mockService, injectedComponents)

  override def beforeEach(): Unit = {
    reset(mockService)
    super.beforeEach()
  }

  "Calling timeApportionedCalculationApplied" must {

    "return a true when provided with valid dates" in {
      val acquisitionDate = DateTime.parse("2013-03-10")
      val disposalDate = DateTime.parse("2016-01-14")
      val result = controller.timeApportionedCalculationApplicable(Some(disposalDate), Some(acquisitionDate))

      result mustBe true
    }

    "return a false when provided with an acquisition date after the tax start date" in {
      val acquisitionDate = DateTime.parse("2015-07-10")
      val disposalDate = DateTime.parse("2016-01-14")
      val result = controller.timeApportionedCalculationApplicable(Some(disposalDate), Some(acquisitionDate))

      result mustBe false
    }

    "return a false when provided a disposal date before the tax start date" in {
      val acquisitionDate = DateTime.parse("2013-07-10")
      val disposalDate = DateTime.parse("2013-01-14")
      val result = controller.timeApportionedCalculationApplicable(Some(disposalDate), Some(acquisitionDate))

      result mustBe false
    }

    "return a false when not provided with an acquisition date" in {
      val disposalDate = DateTime.parse("2016-01-14")
      val result = controller.timeApportionedCalculationApplicable(Some(disposalDate), None)

      result mustBe false
    }

    "return a false when not provided with a disposal date" in {
      val acquisitionDate = DateTime.parse("2013-07-10")
      val result = controller.timeApportionedCalculationApplicable(None, Some(acquisitionDate))

      result mustBe false
    }
  }

  "Calling .calculateTotalGain" when {
    def fakePostRequest(json: JsValue): FakeRequest[AnyContentAsJson] = {
      FakeRequest("POST", "").withJsonBody(json)
    }

    "only provided with mandatory values" must {
      when(mockService.calculateGainFlat(any(), any(), any(),
        any(), any())).thenReturn(1.0)

      val improvementsBefore = 2.0
      val improvementsAfter = improvementsBefore + 2

      val result = controller.calculateTotalGain()(fakePostRequest(
        Json.toJson(NonResidentTotalGainRequestModel(
          disposalValue = 1,
          disposalCosts = 1,
          acquisitionValue = 1,
          acquisitionCosts = 1,
          improvementsBefore,
          rebasedValue = None,
          rebasedCosts = 0,
          disposalDate = None,
          acquisitionDate = None,
          improvementsAfter
        ))
      ))

      "return a status of 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flatGain of 1.0" in {
          (json \ "flatGain").as[Double] mustBe 1.0
        }

        "should have no value for rebasedGain" in {
          (json \ "rebasedGain").asOpt[Double] mustBe None
        }

        "should have no value for timeApportionedGain" in {
          (json \ "timeApportionedGain").asOpt[Double] mustBe None
        }
      }
    }

    "provided with the values for the rebased calculation" must {
      when(mockService.calculateGainFlat(any(), any(), any(),
        any(), any())).thenReturn(1.0)

      when(mockService.calculateGainRebased(any(), any(), any(),
        any(), any())).thenReturn(2.0)


      val improvementsBefore = 2.0
      val improvementsAfter = improvementsBefore + 2

      val result = controller.calculateTotalGain()(fakePostRequest(
        Json.toJson(NonResidentTotalGainRequestModel(
          disposalValue = 1,
          disposalCosts = 1,
          acquisitionValue = 1,
          acquisitionCosts = 1,
          improvementsBefore,
          rebasedValue = Some(1),
          rebasedCosts = 1,
          disposalDate = None,
          acquisitionDate = None,
          improvementsAfter
        ))
      ))

      verify(mockService, times(0)).calculateGainTA(any(), any(), any(), any(), any(), any(), any())

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flatGain of 1.0" in {
          (json \ "flatGain").as[Double] mustBe 1.0
        }

        "should have no value for rebasedGain" in {
          (json \ "rebasedGain").asOpt[Double] mustBe None
        }

        "should have no value for timeApportionedGain" in {
          (json \ "timeApportionedGain").asOpt[Double] mustBe None
        }
      }

      "call the flat gain function on the calculator service once" in {
      }
    }

    "provided with the values for the time apportioned calculation" must {
      val disposalDate = DateTime.parse("2016-05-08")
      val acquisitionDate = DateTime.parse("2012-04-09")

      when(mockService.calculateGainFlat(any(), any(), any(),
        any(), any())).thenReturn(1.0)

      when(mockService.calculateGainTA(any(), any(), any(),
        any(), any(), any(), any()))
        .thenReturn(3.0)

      val improvementsBefore = 2.0
      val improvementsAfter = improvementsBefore + 2
      val totalImprovements = improvementsBefore + improvementsAfter
      val result = controller.calculateTotalGain()(fakePostRequest(
        Json.toJson(NonResidentTotalGainRequestModel(
          disposalValue = 1,
          disposalCosts = 1,
          acquisitionValue = 1,
          acquisitionCosts = 1,
          improvementsBefore,
          rebasedValue = None,
          rebasedCosts = 0,
          disposalDate = Some(disposalDate),
          acquisitionDate = Some(acquisitionDate),
          improvementsAfter
        ))
      ))

      verify(mockService, times(3)).calculateGainFlat(any(), any(), any(), any(), any())
      verify(mockService, times(0)).calculateGainRebased(any(), any(), any(), any(), any())
      verify(mockService, times(1)).calculateGainTA(any(), any(), any(), any(), any(), any(), any())
      verify(mockService).calculateGainTA(any(), any(), any(), any(), ArgumentMatchers.eq(totalImprovements), any(), any())

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flatGain of 1.0" in {
          (json \ "flatGain").as[Double] mustBe 1.0
        }

        "should have no value for rebasedGain" in {
          (json \ "rebasedGain").asOpt[Double] mustBe None
        }

        "should have a timeApportionedGain of 3.0" in {
          (json \ "timeApportionedGain").asOpt[Double] mustBe Some(3.0)
        }
      }
    }
  }

  "Calling .calculateTaxableGainAfterPRR" when {

    "provided with a flat calculation with no acquisition date" must {
      val fakeRequest = FakeRequest("GET", "")
      val disposalDate = DateTime.parse("2016-05-08")

      when(mockService.calculateGainFlat(any(), any(), any(), any(), any())).thenReturn(6.0)
      when(mockService.calculateChargeableGain(any(), any(), any(), any(), any()))
        .thenReturn(6.0)

      val claimingPRR = false
      val daysClaimed = 0
      val daysClaimedAfter = 0

      val result = controller.calculateTaxableGainAfterPRR(10.0, 1, 1, 1, 1, None, 0, Some(disposalDate), None,
        0, claimingPRR, daysClaimed, daysClaimedAfter)(fakeRequest)

      "return a status of 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flat result" which {

          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain mustEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain mustEqual 6.0
          }

          "should have a prrUsed of 0.0" in {
            flatResultJson.prrUsed mustEqual 0.0
          }
        }

        "should have a rebasedGain of None" in {
          json.toString must not include "rebasedGain"
        }

        "should have a timeApportionedGain of None" in {
          json.toString must not include "timeApportionedGain"
        }
      }
    }

    "only provided with values for the flat calculation with an acquisition date" must {
      val fakeRequest = FakeRequest("GET", "")

      when(mockService.calculateGainFlat(any(), any(), any(), any(), any())).thenReturn(6.0)
      when(mockService.calculateChargeableGain(any(), any(), any(), any(), any()))
        .thenReturn(6.0)
      when(mockService.calculateFlatPRR(any(), any(), any(), any()))
        .thenReturn(5.0)
      when(mockService.determineReliefsUsed(any(), any()))
        .thenReturn(100.0)

      val claimingPRR = false
      val daysClaimed = 0
      val daysClaimedAfter = 1
      val disposalDate = DateTime.parse("2016-05-08")
      val acquisitionDate = DateTime.parse("2012-04-09")

      val result = controller.calculateTaxableGainAfterPRR(1, 1, 1, 1, 2.0, None, 0, Some(disposalDate), Some(acquisitionDate),
        4.0, claimingPRR, daysClaimed, daysClaimedAfter)(fakeRequest)

      "return a status of 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flat result" which {

          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain mustEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain mustEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            flatResultJson.prrUsed mustEqual 100.0
          }
        }

        "should have a rebasedGain of None" in {
          json.toString must not include "rebasedGain"
        }

        "should have a timeApportionedGain of None" in {
          json.toString must not include "timeApportionedGain"
        }
      }
    }

    "only provided with values for the flat and rebased calculation without an acquisition date" must {
      val fakeRequest = FakeRequest("GET", "")

      when(mockService.calculateGainFlat(any(), any(), any(), any(), any())).thenReturn(6.0)
      when(mockService.calculateGainRebased(any(), any(), any(), any(), any())).thenReturn(100.0)
      when(mockService.calculateChargeableGain(any(), any(), any(), any(), any()))
        .thenReturn(6.0)
      when(mockService.calculateFlatPRR(any(), any(), any(), any()))
        .thenReturn(5.0)
      when(mockService.calculateRebasedPRR(any(), any(), any()))
        .thenReturn(87.0)
      when(mockService.determineReliefsUsed(any(), any()))
        .thenReturn(100.0)

      val claimingPRR = true
      val daysClaimed = 2847
      val daysClaimedAfter = 1
      val disposalDate = DateTime.parse("2017-01-02")

      val result = controller.calculateTaxableGainAfterPRR(1000.0, 55.0, 750.0, 50.0, 2.0, Some(150.0), 5.0, Some(disposalDate), None,
        4.0, claimingPRR, daysClaimed, daysClaimedAfter)(fakeRequest)

      "return a status of 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flat result" which {

          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain mustEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain mustEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            flatResultJson.prrUsed mustEqual 100.0
          }
        }

        "should have a rebaseGain of None" in {
          json.toString must not include "rebasedResult"
        }

        "should have a timeApportionedGain of None" in {
          json.toString must not include "timeApportionedGain"
        }
      }
    }

    "provided with values for the flat, rebased and time apportioned calculations" must {
      val fakeRequest = FakeRequest("GET", "")

      when(mockService.calculateGainFlat(any(), any(), any(), any(), any())).thenReturn(6.0)
      when(mockService.calculateGainRebased(any(), any(), any(), any(), any())).thenReturn(100.0)
      when(mockService.calculateGainTA(any(), any(), any(), any(),
        any(), any(), any())).thenReturn(50.0)
      when(mockService.calculateChargeableGain(any(), any(), any(), any(), any()))
        .thenReturn(6.0)
      when(mockService.calculateFlatPRR(any(), any(), any(), any()))
        .thenReturn(5.0)
      when(mockService.calculateRebasedPRR(any(), any(), any()))
        .thenReturn(87.0)
      when(mockService.calculateTimeApportionmentPRR(any(), any(), any()))
        .thenReturn(44.0)
      when(mockService.determineReliefsUsed(any(), any()))
        .thenReturn(100.0)

      val claimingPRR = true
      val daysClaimed = 2847
      val daysClaimedAfter = 100
      val disposalDate = DateTime.parse("2017-01-02")
      val acquisitionDate = DateTime.parse("2005-10-16")

      val result = controller.calculateTaxableGainAfterPRR(1000.0, 55.0, 750.0, 50.0, 2.0, Some(150.0), 5.0, Some(disposalDate), Some(acquisitionDate),
        4.0, claimingPRR, daysClaimed, daysClaimedAfter)(fakeRequest)

      "return a status of 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flat result" which {

          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain mustEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain mustEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            flatResultJson.prrUsed mustEqual 100.0
          }
        }

        "should have a rebased result" which {

          val rebasedResultJson = (json \ "rebasedResult").as[GainsAfterPRRModel]

          "should have a totalGain of 100" in {
            rebasedResultJson.totalGain mustEqual 100.0
          }

          "should have a taxableGain of 6.0" in {
            rebasedResultJson.taxableGain mustEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            rebasedResultJson.prrUsed mustEqual 100.0
          }
        }

        "should have a timeApportionedGain result" which {

          val timeApportionedResultJson = (json \ "timeApportionedResult").as[GainsAfterPRRModel]

          "should have a totalGain of 50" in {
            timeApportionedResultJson.totalGain mustEqual 50.0
          }

          "should have a taxableGain of 6.0" in {
            timeApportionedResultJson.taxableGain mustEqual 6.0
          }

          "should have a prrUsed of 100.0" in {
            timeApportionedResultJson.prrUsed mustEqual 100.0
          }
        }
      }
    }

    "provided with values for the flat, rebased and time apportioned calculations but no disposal date" must {
      val fakeRequest = FakeRequest("GET", "")

      when(mockService.calculateGainFlat(any(), any(), any(), any(), any())).thenReturn(6.0)
      when(mockService.calculateGainRebased(any(), any(), any(), any(), any())).thenReturn(100.0)
      when(mockService.calculateGainTA(any(), any(), any(), any(), any(), any(), any())).thenReturn(50.0)
      when(mockService.calculateChargeableGain(any(), any(), any(), any(), any())).thenReturn(6.0)
      when(mockService.determineReliefsUsed(any(), any())).thenReturn(0.0)

      val claimingPRR = true
      val daysClaimed = 2847
      val daysClaimedAfter = 1
      val acquisitionDate = DateTime.parse("2005-10-16")
      val disposalDate = DateTime.parse("2017-10-16")

      val result = controller.calculateTaxableGainAfterPRR(1000.0, 55.0, 750.0, 50.0, 2.0, Some(150.0), 5.0, Some(disposalDate), Some(acquisitionDate),
        4.0, claimingPRR, daysClaimed, daysClaimedAfter)(fakeRequest)

      "return a status of 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
      }

      "return a valid result" which {
        val data = contentAsString(result)
        val json = Json.parse(data)

        "should have a flat result" which {
          val flatResultJson = (json \ "flatResult").as[GainsAfterPRRModel]
          println(flatResultJson.toString)

          "should have a totalGain of 6.0" in {
            flatResultJson.totalGain mustEqual 6.0
          }

          "should have a taxableGain of 6.0" in {
            flatResultJson.taxableGain mustEqual 6.0
          }

          "should have a prrUsed of 0.0" in {
            flatResultJson.prrUsed mustEqual 0.0
          }
        }

        "should have a rebasedGain of None" in {
          json.toString must not include "rebasedGain"
        }

        "should have a timeApportionedGain of None" in {
          json.toString must not include "timeApportionedGain"
        }
      }
    }
  }

  "Calling calculateTaxOwed" when {

    "only a flat calculation result is available" should {
      val fakeRequest = FakeRequest("GET", "")
      val returnModel = CalculationResultModel(8.0, 9.0, 10.0, 20, 0.0, 0.0)

      when(mockService.calculateGainFlat(any(), any(), any(), any(), any())).thenReturn(15.0)
      when(mockService.brRemaining(any(), any(), any(), any())).thenReturn(1.0)
      when(mockService.calculateChargeableGain(any(), any(), any(), any(), any())).thenReturn(2.0)
      when(mockService.determineReliefsUsed(any(), any())).thenReturn(3.0)
      when(mockService.determineLossLeft(any(), any())).thenReturn(4.0)
      when(mockService.annualExemptAmountUsed(any(), any(), any(), any())).thenReturn(5.0)
      when(mockService.annualExemptAmountLeft(any(), any())).thenReturn(6.0)
      when(mockService.determineLossLeft(any(), any())).thenReturn(7.0)
      when(mockService.calculationResult(any(), any(), any(), any(), any(), any(),
        any(), any(), any(), any()))
        .thenReturn(returnModel)

      val result = controller.calculateTaxOwed(1, 0, 1, 0, 0, None, 0, DateTime.parse("2017-10-10"), None, 0,
        PrivateResidenceReliefModel(claimingPRR = false, None, None), 1, 1, 0, 0, 0, 0, OtherReliefsModel(1, 1, 1))(fakeRequest)

      "should have a status of 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
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
          None,
          None,
          Some(7.0),
          Some(7.0),
          None,
          Some(0.0),
          Some(0.0)
        )

        "should have a flat result" in {
          (json \ "flatResult").as[TaxOwedModel] mustBe flatResultModel
        }

        "must not have a rebased result" in {
          (json \ "rebasedResult").asOpt[TaxOwedModel] mustBe None
        }

        "must not have a timeApportioned result" in {
          (json \ "timeApportionedResult").asOpt[TaxOwedModel] mustBe None
        }
      }
    }

    "all calculation methods are available" should {
      val fakeRequest = FakeRequest("GET", "")
      val returnModel = CalculationResultModel(8.0, 9.0, 10.0, 20, 0.0, 0.0)

      when(mockService.calculateGainFlat(any(), any(), any(), any(), any())).thenReturn(15.0)
      when(mockService.calculateGainRebased(any(), any(), any(), any(), any())).thenReturn(16.0)
      when(mockService.calculateGainTA(any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(17.0)
      when(mockService.brRemaining(any(), any(), any(), any())).thenReturn(1.0)
      when(mockService.calculateChargeableGain(any(), any(), any(), any(), any())).thenReturn(2.0)
      when(mockService.determineReliefsUsed(any(), any())).thenReturn(3.0)
      when(mockService.determineLossLeft(any(), any())).thenReturn(4.0)
      when(mockService.annualExemptAmountUsed(any(), any(), any(), any())).thenReturn(5.0)
      when(mockService.annualExemptAmountLeft(any(), any())).thenReturn(6.0)
      when(mockService.determineLossLeft(any(), any())).thenReturn(7.0)
      when(mockService.calculationResult(any(), any(), any(), any(), any(), any(),
        any(), any(), any(), any()))
        .thenReturn(returnModel)

      val result = controller.calculateTaxOwed(1, 0, 1, 0, 0, Some(1.0), 0, DateTime.parse("2017-10-10"), Some(DateTime.parse("2011-01-05")),
        0, PrivateResidenceReliefModel(claimingPRR = false, None, None), 1, 1, 0, 0, 0, 0, OtherReliefsModel(1, 1, 1))(fakeRequest)

      "should have a status of 200" in {
        status(result) mustBe 200
      }

      "return a JSON result" in {
        contentType(result) mustBe Some("application/json")
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
          None,
          None,
          Some(7.0),
          Some(7.0),
          None,
          Some(0.0),
          Some(0.0)
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
          None,
          None,
          Some(7.0),
          Some(7.0),
          None,
          Some(0.0),
          Some(0.0)
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
          None,
          None,
          Some(7.0),
          Some(7.0),
          None,
          Some(0.0),
          Some(0.0)
        )

        "should have a flat result" in {
          (json \ "flatResult").as[TaxOwedModel] mustBe flatResultModel
        }

        "should have a rebased result" in {
          (json \ "rebasedResult").asOpt[TaxOwedModel] mustBe Some(rebasedResultModel)
        }

        "should have a timeApportioned result" in {
          (json \ "timeApportionedResult").asOpt[TaxOwedModel] mustBe Some(timeApportionedResultModel)
        }
      }
    }
  }

  "Calling calculateTotalCosts" when {

    when(mockService.calculateTotalCosts(
      anyDouble,
      anyDouble,
      anyDouble
    )).thenReturn(5000.00)

    val fakeRequest = FakeRequest("GET", "/capital-gains-calculator/calculate-total-costs")
    val result = controller.calculateTotalCosts(
      disposalCosts = 3000.00,
      improvements = 1000.00,
      acquisitionCosts = 1000.00)(fakeRequest)

    "return 200" in {
      status(result) mustBe Status.OK
    }

    "return a JSON result" in {
      contentType(result) mustBe Some("application/json")
    }

    "return a valid result" in {
      val data = contentAsString(result)
      val json = Json.parse(data)

      json.as[Double] mustEqual 5000.0
    }
  }
}
