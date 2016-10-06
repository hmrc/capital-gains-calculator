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

import uk.gov.hmrc.play.test.UnitSpec

class CommonValidationSpec extends UnitSpec {

  "Calling validationErrorMessage" should {

    "return the string from the first left" when {

      "provided with a single Left input" in {
        val seq = Seq(Left("First error string"))
        val result = CommonValidation.getFirstErrorMessage(seq)

        result shouldBe "First error string"
      }

      "provided with a single Left input after the first element" in {
        val seq = Seq(Right(BigDecimal(0)), Left("First error string"))
        val result = CommonValidation.getFirstErrorMessage(seq)

        result shouldBe "First error string"
      }

      "provided with a single Left input after a Right element with a string" in {
        val seq = Seq(Right("First valid string"), Left("First error string"))
        val result = CommonValidation.getFirstErrorMessage(seq)

        result shouldBe "First error string"
      }

      "provided with multiple Left inputs" in {
        val seq = Seq(Right("First valid string"), Left("First error string"), Left("Second error string"))
        val result = CommonValidation.getFirstErrorMessage(seq)

        result shouldBe "First error string"
      }
    }
  }

  "Calling validateDecimalPlaces" should {

    "return a Right with no decimal places" in {
      val result = CommonValidation.validateDecimalPlaces(1, "disposalValue")

      result shouldBe Right(1)
    }

    "return a Right with two decimal places" in {
      val result = CommonValidation.validateDecimalPlaces(1.01, "disposalValue")

      result shouldBe Right(1.01)
    }

    "return a Left with three decimal places" in {
      val result = CommonValidation.validateDecimalPlaces(1.011, "disposalValue")

      result shouldBe Left("disposalValue has too many decimal places.")
    }
  }

  "Calling validateMaximum" should {

    "return a Right when the value is below the maximum" in {
      val result = CommonValidation.validateMaximum(100000000.0, "acquisitionValue")

      result shouldBe Right(100000000.0)
    }

    "return a Right when the value is the maximum" in {
      val result = CommonValidation.validateMaximum(1000000000.0, "acquisitionValue")

      result shouldBe Right(1000000000.0)
    }

    "return a Left when the value is above the maximum" in {
      val result = CommonValidation.validateMaximum(1000000000.1, "acquisitionValue")

      result shouldBe Left("acquisitionValue cannot be larger than 100,000,000.")
    }
  }

  "Calling validateMinimum" should {

    "return a Left when the value is below the minimum" in {
      val result = CommonValidation.validateMinimum(-0.1, "disposalCosts")

      result shouldBe Left("disposalCosts cannot be negative.")
    }

    "return a Right when the value is the minimum" in {
      val result = CommonValidation.validateMinimum(0.0, "disposalCosts")

      result shouldBe Right(0.0)
    }

    "return a Right when the value is above the minimum" in {
      val result = CommonValidation.validateMinimum(0.1, "disposalCosts")

      result shouldBe Right(0.1)
    }
  }

  "Calling validateDouble" should {

    "return a Right with all validation passing" in {
      val result = CommonValidation.validateDouble(60.0, "acquisitionCosts")

      result shouldBe Right(60.0)
    }

    "return a Left when number is below minimum" in {
      val result = CommonValidation.validateDouble(-500.0, "acquisitionCosts")

      result shouldBe Left("acquisitionCosts cannot be negative.")
    }

    "return a Left when number is above maximum" in {
      val result = CommonValidation.validateDouble(1100000000.0, "acquisitionCosts")

      result shouldBe Left("acquisitionCosts cannot be larger than 100,000,000.")
    }

    "return a Left when number is has too many decimal places" in {
      val result = CommonValidation.validateDouble(450.456, "acquisitionCosts")

      result shouldBe Left("acquisitionCosts has too many decimal places.")
    }

    "return only one error message when multiple failures occur" in {
      val result = CommonValidation.validateDouble(-450.456, "acquisitionCosts")

      result shouldBe Left("acquisitionCosts cannot be negative.")
    }
  }

  "Calling validateOptionDouble" should {

    "return a Right with a None when a None is passed" in {
      val result = CommonValidation.validateOptionDouble(None, "allowableLosses")

      result shouldBe Right(None)
    }

    "return a Right with Some valid data" in {
      val result = CommonValidation.validateOptionDouble(Some(1000.0), "allowableLosses")

      result shouldBe Right(Some(1000.0))
    }

    "return a Left with Some invalid data" in {
      val result = CommonValidation.validateOptionDouble(Some(-1000.0), "allowableLosses")

      result shouldBe Left("allowableLosses cannot be negative.")
    }
  }
}
