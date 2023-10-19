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

package common.validation

import models.nonResident.{CalculationRequestModel, TimeApportionmentCalculationRequestModel}
import java.time.LocalDate
import org.scalatestplus.play.PlaySpec

class NonResidentValidationSpec extends PlaySpec {

  val validModel = CalculationRequestModel("No", None, None, 0, None, 200000.0, 800.0, 100000.0,
    200.0, 12000.0, 0, 0, None, LocalDate.parse("2016-12-12"), None, None)

  val validTimeModel = TimeApportionmentCalculationRequestModel(
    "No", None, None, 0, None, 200000.0, 800.0, 100000.0,
    200.0, 12000.0, 0, 0, LocalDate.parse("2010-12-12"), LocalDate.parse("2016-12-12"), None, None)

  "Calling validateNonResidentProperty with a valid model" must {
    "return a Right with all validation passing" in {
      val result = NonResidentValidation.validateNonResidentProperty(validModel)
      result mustBe Right(validModel)
    }
  }

  "Calling validateNonResidentProperty with an invalid model" must {

    "When the first error is the priorDisposal" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "frew", Some(-1239.00), Some(1.00123), 9123182324127123.0, Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("priorDisposal must be either Yes or No")
    }

    "When the first error is the annualExemptAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(-1239.00), Some(1.00123), 9123182324127123.0, Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("annualExemptAmount cannot be negative.")
    }

    "When the first error is the otherPropertiesAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00123), 9123182324127123.0, Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("otherPropertiesAmt has too many decimal places.")
    }

    "When the first error is the currentIncome" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 91231823241123123.0, Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("currentIncome cannot be larger than 1,000,000,000.")
    }

    "When the first error is the personalAllowanceAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("personalAllowanceAmt cannot be negative.")
    }

    "When the first error is the disposalValue" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("disposalValue cannot be negative.")
    }

    "When the first error is the disposalCosts" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("disposalCosts has too many decimal places.")
    }

    "When the first error is the initialValue" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("initialValueAmt cannot be larger than 1,000,000,000.")
    }

    "When the first error is the initialCosts" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("initialCostsAmt cannot be negative.")
    }

    "When the first error is the improvementsAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0813, 0.2138924, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("improvementsAmt has too many decimal places.")
    }

    "When the first error is the reliefsAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2134, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("reliefs has too many decimal places.")
    }

    "When the first error is the allowableLosses" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 0.123893, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("allowableLossesAmt has too many decimal places.")
    }

    "When the first error is the acquisitionDate" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, Some(LocalDate.parse("2016-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("The acquisitionDate must be before the disposalDate")
    }

    "When the first error is the disposalDate" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, Some(LocalDate.parse("2010-12-24")),
        LocalDate.parse("2014-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("disposalDate cannot be before 2015-04-06")
    }

    "When the first error is the isClaimingPRR" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, Some(LocalDate.parse("2010-12-24")),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("isClaimingPRR must be either Yes or No")
    }

    "When the first error is the daysClaimed" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, Some(LocalDate.parse("2010-12-24")),
        LocalDate.parse("2016-12-12"), Some("Yes"), Some(10231289.129))) mustBe Left("daysClaimed has too many decimal places.")
    }
  }

  "Calling validateNonResidentTimeApportioned with a valid model" must {
    "return a Right with all validation passing" in {
      val result = NonResidentValidation.validateNonResidentTimeApportioned(validTimeModel)
      result mustBe Right(validTimeModel)
    }
  }

  "Calling validateNonResidentTimeApportioned with an invalid model" must {
    "When the first error is the acquisitionDate" in {
      NonResidentValidation.validateNonResidentTimeApportioned(TimeApportionmentCalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, LocalDate.parse("2016-12-24"),
        LocalDate.parse("2016-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("The acquisitionDate must be before the disposalDate")
    }

    "When the first error is the disposalDate" in {
      NonResidentValidation.validateNonResidentTimeApportioned(TimeApportionmentCalculationRequestModel(
        "Yes", Some(1239.00), Some(1.00), 5000.0, Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, LocalDate.parse("2010-12-24"),
        LocalDate.parse("2014-12-12"), Some("suihur"), Some(10231289.129312))) mustBe Left("disposalDate cannot be before 2015-04-06")
    }
  }

}
