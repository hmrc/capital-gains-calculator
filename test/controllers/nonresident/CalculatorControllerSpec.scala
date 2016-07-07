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

  //####### Flat Rate Tests #####################
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
      Option(Matchers.anyString),
      Option(Matchers.anyString),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble)
    )).thenReturn(CalculationResultModel(1800, 21100, 10000, 18, 0))

    val target: CalculatorController = new CalculatorController {
      override val calculationService = mockCalculationService
    }

    val fakeRequest = FakeRequest("GET", "/capital-gains-calculator/calculate-flat")
    val result = target.calculateFlat(
      customerType = "individual",
      priorDisposal = "No",
      annualExemptAmount = Some(0),
      otherPropertiesAmt = Some(0),
      isVulnerable = None,
      currentIncome = Some(7000),
      personalAllowanceAmt = Some(11000),
      disposalValue = 21100,
      disposalCosts = 0,
      acquisitionValueAmt = 0,
      acquisitionCostsAmt = 0,
      improvementsAmt = 0,
      reliefs = 0,
      allowableLossesAmt = 0,
      acquisitionDate = None,
      disposalDate = None,
      isClaimingPRR = None,
      daysClaimed = None)(fakeRequest)

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

  //####### Rebased Tests #####################
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
      Option(Matchers.anyString),
      Option(Matchers.anyString),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble)
    )).thenReturn(CalculationResultModel(1800, 21100, 10000, 18, 0))

    val target: CalculatorController = new CalculatorController {
      override val calculationService = mockCalculationService
    }

    val fakeRequest = FakeRequest("GET", "/capital-gains-calculator/calculate-rebased")
    val result = target.calculateRebased(
      customerType = "individual",
      priorDisposal = "No",
      annualExemptAmount = Some(0),
      otherPropertiesAmt = Some(0),
      isVulnerable = None,
      currentIncome = Some(7000),
      personalAllowanceAmt = Some(11000),
      disposalValue = 21100,
      disposalCosts = 0,
      rebasedValue = 0,
      revaluationCost = 0,
      improvementsAmt = 0,
      reliefs = 0,
      allowableLossesAmt = 0,
      acquisitionDate = None,
      disposalDate = None,
      isClaimingPRR = None,
      daysClaimedAfter = None)(fakeRequest)

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

  //####### Time Apportioned Tests ##################
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
      Option(Matchers.anyString),
      Option(Matchers.anyString),
      Option(Matchers.anyString),
      Option(Matchers.anyDouble),
      Option(Matchers.anyDouble)
    )).thenReturn(CalculationResultModel(1800, 21100, 10000, 18, 0))

    val target: CalculatorController = new CalculatorController {
      override val calculationService = mockCalculationService
    }

    val fakeRequest = FakeRequest("GET", "/capital-gains-calculator/calculate-time-apportioned")
    val result = target.calculateTA(
      customerType = "individual",
      priorDisposal = "No",
      annualExemptAmount = Some(0),
      otherPropertiesAmt = Some(0),
      isVulnerable = None,
      currentIncome = Some(7000),
      personalAllowanceAmt = Some(11000),
      disposalValue = 31100,
      disposalCosts = 0,
      acquisitionValueAmt = 0,
      acquisitionCostsAmt = 0,
      improvementsAmt = 0,
      reliefs = 0,
      allowableLossesAmt = 0,
      acquisitionDate = Some("2014-4-1"),
      disposalDate = Some("2016-1-1"),
      isClaimingPRR = None,
      daysClaimedAfter = None
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
}