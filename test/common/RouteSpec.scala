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

package common

import org.joda.time.DateTime
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class RouteSpec extends UnitSpec with WithFakeApplication {

  "The route for calculating total gain for non-resident properties" should {

    val testDate = Some(new DateTime("2001-01-01"))
    lazy val url = controllers.nonresident.routes.CalculatorController.calculateTotalGain(1, 1, 1, 1, 1, Some(1), 1, testDate, testDate, 1).url
    lazy val querystringParameters = url.substring(url.indexOf('?'))

    "have the path /non-resident/calculate-total-gain" in {
      url should startWith("/capital-gains-calculator/non-resident/calculate-total-gain")
    }

    "have querystring parameters" in {
      querystringParameters should not be empty
    }

    "include the disposal value querystring parameter" in {
      url should include("disposalValue=1.0")
    }

    "include the disposal costs querystring parameter" in {
      querystringParameters should include("disposalCosts=1.0")
    }

    "include the acquisition value querystring parameter" in {
      querystringParameters should include("acquisitionValue=1.0")
    }

    "include the acquisition costs querystring parameter" in {
      querystringParameters should include("acquisitionCosts=1.0")
    }

    "include the improvements querystring parameter" in {
      querystringParameters should include("improvements=1.0")
    }

    "include the rebased value querystring parameter" in {
      querystringParameters should include("rebasedValue=1.0")
    }

    "include the disposal date querystring parameter" in {
      querystringParameters should include("disposalDate=2001-1-1")
    }

    "include the acquisition date querystring parameter" in {
      querystringParameters should include("acquisitionDate=2001-1-1")
    }

    "include the improvements after the tax started querystring parameter" in {
      querystringParameters should include("improvementsAfterTaxStarted=1.0")
    }
  }
}
