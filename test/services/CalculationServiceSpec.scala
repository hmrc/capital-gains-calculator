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

package services

import java.util.Date

import models.{DateModel, CalculationResult}
import uk.gov.hmrc.play.test.UnitSpec

class CalculationServiceSpec extends UnitSpec {

  "Calling CalculationService .add()" should {
    "return the sum of two numbers" in {
      val result = CalculationService.add(20, 10)
      result shouldEqual CalculationResult(20, 10, 20 + 10)
    }
  }

  "Calling CalculationService .annualExemptYear(date: SimpleDateFormat)" should {
    "return a value of 11000 when a date between 05/04/2015 and 06/04/2016 is entered" in {
      val value = CalculationService.annualExemptYear("01062015")
      value shouldEqual 11000
    }

    "return a value of 10500 when a date before or after these dates are entered" in {
      CalculationService.annualExemptYear("01032017") shouldEqual 10500
      val value2 = CalculationService.annualExemptYear("01032015")
      value2 shouldEqual 10500
    }
  }

  "Calling CalculationService .isVulnerableTrustee" should {
    "return a value of 5500" in {
      CalculationService.isVulnerableTrustee("07072015", "No") shouldEqual 5500
    }
  }
}