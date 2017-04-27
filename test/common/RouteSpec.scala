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

package common

import models.nonResident.{OtherReliefsModel, PrivateResidenceReliefModel}
import models.resident.properties.PropertyTotalGainModel
import models.resident.shares.TotalGainModel
import org.joda.time.DateTime
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class RouteSpec extends UnitSpec with WithFakeApplication {

  "The route for calculating total gain for non-resident properties" should {

    val testDate = Some(new DateTime("2001-01-01"))
    lazy val url = controllers.nonresident.routes.CalculatorController.calculateTotalGain(1, 1, 1, 1, 1, Some(1), 1, testDate, testDate, 1).url
    lazy val queryStringParameters = url.substring(url.indexOf('?'))

    "have the path /non-resident/calculate-total-gain" in {
      url should startWith("/capital-gains-calculator/non-resident/calculate-total-gain")
    }

    "have query string parameters" in {
      queryStringParameters should not be empty
    }

    "include the disposal value query string parameter" in {
      url should include("disposalValue=1.0")
    }

    "include the disposal costs query string parameter" in {
      queryStringParameters should include("disposalCosts=1.0")
    }

    "include the acquisition value query string parameter" in {
      queryStringParameters should include("acquisitionValue=1.0")
    }

    "include the acquisition costs query string parameter" in {
      queryStringParameters should include("acquisitionCosts=1.0")
    }

    "include the improvements query string parameter" in {
      queryStringParameters should include("improvements=1.0")
    }

    "include the rebased value query string parameter" in {
      queryStringParameters should include("rebasedValue=1.0")
    }

    "include the disposal date query string parameter" in {
      queryStringParameters should include("disposalDate=2001-1-1")
    }

    "include the acquisition date query string parameter" in {
      queryStringParameters should include("acquisitionDate=2001-1-1")
    }

    "include the improvements after the tax started query string parameter" in {
      queryStringParameters should include("improvementsAfterTaxStarted=1.0")
    }
  }

  "The route for calculate gain after prr for non-resident properties" should {
    val testDate = Some(new DateTime("2001-01-01"))
    lazy val url = controllers.nonresident.routes.CalculatorController.
      calculateTaxableGainAfterPRR(1, 1, 1, 1, 1, Some(1), 1, testDate, testDate, 1, true, 1, 1).url
    lazy val queryStringParameters = url.substring(url.indexOf('?'))

    "have the path /non-resident/calculate-gain-after-prr" in {
      url should startWith("/capital-gains-calculator/non-resident/calculate-gain-after-prr")
    }

    "have query string parameters" in {
      queryStringParameters should not be empty
    }

    "include the disposal value query string parameter" in {
      url should include("disposalValue=1.0")
    }

    "include the disposal costs query string parameter" in {
      queryStringParameters should include("disposalCosts=1.0")
    }

    "include the acquisition value query string parameter" in {
      queryStringParameters should include("acquisitionValue=1.0")
    }

    "include the acquisition costs query string parameter" in {
      queryStringParameters should include("acquisitionCosts=1.0")
    }

    "include the improvements query string parameter" in {
      queryStringParameters should include("improvements=1.0")
    }

    "include the rebased value query string parameter" in {
      queryStringParameters should include("rebasedValue=1.0")
    }

    "include the disposal date query string parameter" in {
      queryStringParameters should include("disposalDate=2001-1-1")
    }

    "include the acquisition date query string parameter" in {
      queryStringParameters should include("acquisitionDate=2001-1-1")
    }

    "include the improvements after the tax started query string parameter" in {
      queryStringParameters should include("improvementsAfterTaxStarted=1.0")
    }

    "include the claiming PRR query string parameter" in {
      queryStringParameters should include("claimingPRR=true")
    }

    "include the days claimed query string parameter" in {
      queryStringParameters should include("daysClaimed=1.0")
    }

    "include the days claimed after the tax started query string parameter" in {
      queryStringParameters should include("daysClaimedAfter=1.0")
    }
  }

  "The route for calculate tax owed for non-resident properties" should {
    val testDate = Some(new DateTime("2001-01-01"))
    lazy val url = controllers.nonresident.routes.CalculatorController.calculateTaxOwed(1,1,1,1,1,Some(1),1,testDate.get, testDate,
      1, PrivateResidenceReliefModel(true, Some(1), Some(1)),"test",Some("test"),1,1,1,1,1,1, OtherReliefsModel(1, 1, 1)).url
    lazy val queryStringParameters = url.substring(url.indexOf('?'))

    "have the path /non-resident/calculate-gain-after-prr" in {
      url should startWith("/capital-gains-calculator/non-resident/calculate-tax-owed")
    }

    "have query string parameters" in {
      queryStringParameters should not be empty
    }

    "include the disposal value query string parameter" in {
      url should include("disposalValue=1.0")
    }

    "include the disposal costs query string parameter" in {
      queryStringParameters should include("disposalCosts=1.0")
    }

    "include the acquisition value query string parameter" in {
      queryStringParameters should include("acquisitionValue=1.0")
    }

    "include the acquisition costs query string parameter" in {
      queryStringParameters should include("acquisitionCosts=1.0")
    }

    "include the improvements query string parameter" in {
      queryStringParameters should include("improvements=1.0")
    }

    "include the rebased value query string parameter" in {
      queryStringParameters should include("rebasedValue=1.0")
    }

    "include the disposal date query string parameter" in {
      queryStringParameters should include("disposalDate=2001-1-1")
    }

    "include the acquisition date query string parameter" in {
      queryStringParameters should include("acquisitionDate=2001-1-1")
    }

    "include the improvements after the tax started query string parameter" in {
      queryStringParameters should include("improvementsAfterTaxStarted=1.0")
    }

    "include the claiming PRR query string parameter" in {
      queryStringParameters should include("claimingPRR=true")
    }

    "include the days claimed query string parameter" in {
      queryStringParameters should include("daysClaimed=1.0")
    }

    "include the days claimed after the tax started query string parameter" in {
      queryStringParameters should include("daysClaimedAfter=1.0")
    }

    "include the customer type query string parameter" in {
      queryStringParameters should include("customerType=test")
    }

    "include the is vulnerable query string parameter" in {
      queryStringParameters should include("isVulnerable=test")
    }

    "include the current income query string parameter" in {
      queryStringParameters should include("currentIncome=1.0")
    }

    "include the personal allowance query string parameter" in {
      queryStringParameters should include("personalAllowanceAmt=1.0")
    }

    "include the allowable loss query string parameter" in {
      queryStringParameters should include("allowableLoss=1.0")
    }

    "include the previous gain query string parameter" in {
      queryStringParameters should include("previousGain=1.0")
    }

    "include the annual exempt amount query string parameter" in {
      queryStringParameters should include("annualExemptAmount=1.0")
    }

    "include the brought forward loss query string parameter" in {
      queryStringParameters should include("broughtForwardLoss=1.0")
    }

    "include the other reliefs flat query string parameter" in {
      queryStringParameters should include("otherReliefsFlat=1.0")
    }

    "include the other reliefs rebased query string parameter" in {
      queryStringParameters should include("otherReliefsRebased=1.0")
    }

    "include the other reliefs time apportioned query string parameter" in {
      queryStringParameters should include("otherReliefsTimeApportioned=1.0")
    }
  }

  "The route for calculate total costs for resident properties" should {

    val totalGainModel = TotalGainModel(0,0,0,0)
    val propertyTotalGainModel = PropertyTotalGainModel(totalGainModel, 0)

    lazy val url = controllers.resident.properties.routes.CalculatorController.calculateTotalCosts(propertyTotalGainModel).url

    "be equal to '/capital-gains-calculator/calculate-total-costs'" in {
      url should startWith("/capital-gains-calculator/calculate-total-costs")
    }

    "include the disposal value" in {
      url should include("disposalValue=0")
    }

    "include the disposal costs" in {
      url should include("disposalCosts=0")
    }

    "include the acquisition value" in {
      url should include("acquisitionValue=0")
    }

    "include the acquisition costs" in {
      url should include("acquisitionCosts=0")
    }

    "include the improvements" in {
      url should include("improvements=0")
    }
  }
}
