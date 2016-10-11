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

package common.validation

import models.nonResident.{CalculationRequestModel, TimeApportionmentCalculationRequestModel}
import org.joda.time.DateTime
import uk.gov.hmrc.play.test.UnitSpec

class NonResidentValidationSpec extends UnitSpec {

  val validModel = CalculationRequestModel(
    "individual", "No", None, None, None, None, None, 200000.0, 800.0, 100000.0,
    200.0, 12000.0, 0, 0, None, DateTime.parse("2016-12-12"), None, None, isProperty = true
  )

  val validTimeModel = TimeApportionmentCalculationRequestModel(
    "individual", "No", None, None, None, None, None, 200000.0, 800.0, 100000.0,
    200.0, 12000.0, 0, 0, DateTime.parse("2010-12-12"), DateTime.parse("2016-12-12"), None, None, isProperty = true
  )

  "Calling validateNonResidentProperty with a valid model" should {
    "return a Right with all validation passing" in {
      val result = NonResidentValidation.validateNonResidentProperty(validModel)
      result shouldBe Right(validModel)
    }
  }

  "Calling validateNonResidentProperty with an invalid model" should {
    "When the first error is the customer type" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "sdfdwq", "frew", Some(-1239.00), Some(1.00123), Some("adeuwif"), Some(9123182324127123.0), Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("customerType must be either individual, trustee or personalRep")
    }

    "When the first error is the priorDisposal" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "frew", Some(-1239.00), Some(1.00123), Some("adeuwif"), Some(9123182324127123.0), Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("priorDisposal must be either Yes or No")
    }

    "When the first error is the annualExemptAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(-1239.00), Some(1.00123), Some("adeuwif"), Some(9123182324127123.0), Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("annualExemptAmount cannot be negative.")
    }

    "When the first error is the otherPropertiesAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00123), Some("adeuwif"), Some(9123182324127123.0), Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("otherPropertiesAmt has too many decimal places.")
    }

    "When the first error is the isVulnerable" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("adeuwif"), Some(9123182324127123.0), Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("isVulnerable must be either Yes or No")
    }

    "When the first error is the currentIncome" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(9123182324127123.0), Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("currentIncome cannot be larger than 100,000,000.")
    }

    "When the first error is the personalAllowanceAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(-12314.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("personalAllowanceAmt cannot be negative.")
    }

    "When the first error is the disposalValue" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        -200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("disposalValue cannot be negative.")
    }

    "When the first error is the disposalCosts" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.01234123, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("disposalCosts has too many decimal places.")
    }

    "When the first error is the initialValue" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 1000000000000000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("initialValueAmt cannot be larger than 100,000,000.")
    }

    "When the first error is the initialCosts" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, -200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("initialCostsAmt cannot be negative.")
    }

    "When the first error is the improvementsAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0813, 0.2138924, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("improvementsAmt has too many decimal places.")
    }

    "When the first error is the reliefsAmount" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2134, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("reliefs has too many decimal places.")
    }

    "When the first error is the allowableLosses" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 0.123893, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("allowableLossesAmt has too many decimal places.")
    }

    "When the first error is the acquisitionDate" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, Some(DateTime.parse("2016-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("The acquisitionDate must be before the disposalDate")
    }

    "When the first error is the disposalDate" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, Some(DateTime.parse("2010-12-24")),
        DateTime.parse("2014-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("disposalDate cannot be before 2015-04-06")
    }

    "When the first error is the isClaimingPRR" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, Some(DateTime.parse("2010-12-24")),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("isClaimingPRR must be either Yes or No")
    }

    "When the first error is the daysClaimed" in {
      NonResidentValidation.validateNonResidentProperty(CalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, Some(DateTime.parse("2010-12-24")),
        DateTime.parse("2016-12-12"), Some("Yes"), Some(10231289.129), isProperty = true
      )) shouldBe Left("daysClaimed has too many decimal places.")
    }
  }

  "Calling validateNonResidentTimeApportioned with a valid model" should {
    "return a Right with all validation passing" in {
      val result = NonResidentValidation.validateNonResidentTimeApportioned(validTimeModel)
      result shouldBe Right(validTimeModel)
    }
  }

  "Calling validateNonResidentTimeApportioned with an invalid model" should {
    "When the first error is the acquisitionDate" in {
      NonResidentValidation.validateNonResidentTimeApportioned(TimeApportionmentCalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, DateTime.parse("2016-12-24"),
        DateTime.parse("2016-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("The acquisitionDate must be before the disposalDate")
    }

    "When the first error is the disposalDate" in {
      NonResidentValidation.validateNonResidentTimeApportioned(TimeApportionmentCalculationRequestModel(
        "individual", "Yes", Some(1239.00), Some(1.00), Some("No"), Some(5000.0), Some(1000.0),
        200000.0, 800.0, 100000.0, 200.0, 12000.0, 0.2, 1000.0, DateTime.parse("2010-12-24"),
        DateTime.parse("2014-12-12"), Some("suihur"), Some(10231289.129312), isProperty = true
      )) shouldBe Left("disposalDate cannot be before 2015-04-06")
    }
  }

}
