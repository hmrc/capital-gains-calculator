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

import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class RouteSpec extends UnitSpec with WithFakeApplication {

  "The route for calculating total gain for non-resident properties" should {
    "be /non-resident/calculate-total-gain" in {
      controllers.nonresident.routes.CalculatorController.calculateTotalGain(0, 0, 0, 0, 0, None, None, None).url shouldBe
        "/capital-gains-calculator/non-resident/calculate-total-gain" +
          "?disposalValue=0.0&disposalCosts=0.0&acquisitionValue=0.0&acquisitionCosts=0.0&improvements=0.0"
    }
  }
}
