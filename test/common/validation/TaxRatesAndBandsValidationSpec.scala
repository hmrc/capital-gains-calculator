/*
 * Copyright 2024 HM Revenue & Customs
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

package common.validation

import common.validation.TaxRatesAndBandsValidation._
import org.scalatestplus.play.PlaySpec

import java.time.LocalDate

class TaxRatesAndBandsValidationSpec extends PlaySpec{

  "calling checkValidTaxYear" must {

    "return true with a year after 2020" in {
      val result = checkValidTaxYear(2020)
      result mustBe true
    }

    "return true with a year after 2015" in {
      val result = checkValidTaxYear(2016)

      result mustBe true
    }

    "return false with a year 2015" in {
      val result = checkValidTaxYear(2015)

      result mustBe false
    }

    "return false with a year less than 2015" in {
      val result = checkValidTaxYear(2014)

      result mustBe false
    }

    "return false with a year greater than the current tax year band" in {
      val result = checkValidTaxYear(LocalDate.now().getYear + 2)

      result mustBe false
    }
  }
}
