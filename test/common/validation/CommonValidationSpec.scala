/*
 * Copyright 2022 HM Revenue & Customs
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

import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec

class CommonValidationSpec extends PlaySpec {

  "Calling validationErrorMessage" must {

    "return the string from the first left" when {

      "provided with a single Left input" in {
        val seq = Seq(Left("First error string"))
        val result = CommonValidation.getFirstErrorMessage(seq)

        result mustBe "First error string"
      }

      "provided with a single Left input after the first element" in {
        val seq = Seq(Right(BigDecimal(0)), Left("First error string"))
        val result = CommonValidation.getFirstErrorMessage(seq)

        result mustBe "First error string"
      }

      "provided with a single Left input after a Right element with a string" in {
        val seq = Seq(Right("First valid string"), Left("First error string"))
        val result = CommonValidation.getFirstErrorMessage(seq)

        result mustBe "First error string"
      }

      "provided with multiple Left inputs" in {
        val seq = Seq(Right("First valid string"), Left("First error string"), Left("Second error string"))
        val result = CommonValidation.getFirstErrorMessage(seq)

        result mustBe "First error string"
      }
    }
  }

  "Calling validateDecimalPlaces" must {

    "return a Right with no decimal places" in {
      val result = CommonValidation.validateDecimalPlaces(1, "disposalValue")

      result mustBe Right(1)
    }

    "return a Right with two decimal places" in {
      val result = CommonValidation.validateDecimalPlaces(1.01, "disposalValue")

      result mustBe Right(1.01)
    }

    "return a Right with a value of 10 million" in {
      val result = CommonValidation.validateDecimalPlaces(22222222.0, "disposalValue")

      result mustBe Right(22222222.0)
    }

    "return a Left with three decimal places" in {
      val result = CommonValidation.validateDecimalPlaces(1.011, "disposalValue")

      result mustBe Left("disposalValue has too many decimal places.")
    }
  }

  "Calling validateMaximum" must {

    "return a Right when the value is below the maximum" in {
      val result = CommonValidation.validateMaximum(100000000.0, "acquisitionValue")

      result mustBe Right(100000000.0)
    }

    "return a Right when the value is the maximum" in {
      val result = CommonValidation.validateMaximum(1000000000.0, "acquisitionValue")

      result mustBe Right(1000000000.0)
    }

    "return a Left when the value is above the maximum" in {
      val result = CommonValidation.validateMaximum(1000000000.1, "acquisitionValue")

      result mustBe Left("acquisitionValue cannot be larger than 1,000,000,000.")
    }
  }

  "Calling validateMinimum" must {

    "return a Left when the value is below the minimum" in {
      val result = CommonValidation.validateMinimum(-0.1, "disposalCosts")

      result mustBe Left("disposalCosts cannot be negative.")
    }

    "return a Right when the value is the minimum" in {
      val result = CommonValidation.validateMinimum(0.0, "disposalCosts")

      result mustBe Right(0.0)
    }

    "return a Right when the value is above the minimum" in {
      val result = CommonValidation.validateMinimum(0.1, "disposalCosts")

      result mustBe Right(0.1)
    }
  }

  "Calling validateDouble" must {

    "return a Right with all validation passing" in {
      val result = CommonValidation.validateDouble(60.0, "acquisitionCosts")

      result mustBe Right(60.0)
    }

    "return a Left when number is below minimum" in {
      val result = CommonValidation.validateDouble(-500.0, "acquisitionCosts")

      result mustBe Left("acquisitionCosts cannot be negative.")
    }

    "return a Left when number is above maximum" in {
      val result = CommonValidation.validateDouble(1100000000.0, "acquisitionCosts")

      result mustBe Left("acquisitionCosts cannot be larger than 1,000,000,000.")
    }

    "return a Left when number is has too many decimal places" in {
      val result = CommonValidation.validateDouble(450.456, "acquisitionCosts")

      result mustBe Left("acquisitionCosts has too many decimal places.")
    }

    "return only one error message when multiple failures occur" in {
      val result = CommonValidation.validateDouble(-450.456, "acquisitionCosts")

      result mustBe Left("acquisitionCosts cannot be negative.")
    }
  }

  "Calling validateOptionDouble" must {

    "return a Right with a None when a None is passed" in {
      val result = CommonValidation.validateOptionDouble(None, "allowableLosses")

      result mustBe Right(None)
    }

    "return a Right with Some valid data" in {
      val result = CommonValidation.validateOptionDouble(Some(1000.0), "allowableLosses")

      result mustBe Right(Some(1000.0))
    }

    "return a Left with Some invalid data" in {
      val result = CommonValidation.validateOptionDouble(Some(-1000.0), "allowableLosses")

      result mustBe Left("allowableLosses cannot be negative.")
    }
  }

  "Calling validatePersonalAllowance" must {

    "return a Right with a value of 13290.0 in 2016/17" in {
      val result = CommonValidation.validateResidentPersonalAllowance(13290.0, DateTime.parse("2016-08-08"))

      result mustBe Right(13290.0)
    }

    "return a Left with a value of 13890.0 in 2015/16" in {
      val result = CommonValidation.validateResidentPersonalAllowance(15290.0, DateTime.parse("2015-08-08"))

      result mustBe Left("personalAllowance cannot exceed 14150")
    }

    "return a Right with a value of 12890 in 2015/16" in {
      val result = CommonValidation.validateResidentPersonalAllowance(14150.0, DateTime.parse("2015-08-08"))

      result mustBe Right(14150.0)
    }

    "return a Left with a value of 13291 in 2016/17" in {
      val result = CommonValidation.validateResidentPersonalAllowance(14550.1, DateTime.parse("2016-08-08"))

      result mustBe Left("personalAllowance cannot exceed 14550")
    }

    "return a Left with an error message when the double fails validation" in {
      val result = CommonValidation.validateResidentPersonalAllowance(-1000.0, DateTime.parse("2016-08-08"))

      result mustBe Left("personalAllowance cannot be negative.")
    }
  }

  "Calling validateSharesDisposalDate" must {

    "return a Right when providing a date after the start of the 2015/16 tax year" in {
      val date = DateTime.parse("2015-08-10")
      val result = CommonValidation.validateDisposalDate(date)

      result mustBe Right(date)
    }

    "return a Right when providing a date on the start of the 2015/16 tax year" in {
      val date = DateTime.parse("2015-04-06")
      val result = CommonValidation.validateDisposalDate(date)

      result mustBe Right(date)
    }

    "return a Left when providing a date before the start of the 2015/16 tax year" in {
      val date = DateTime.parse("2015-04-05")
      val result = CommonValidation.validateDisposalDate(date)

      result mustBe Left("disposalDate cannot be before 2015-04-06")
    }
  }

  "Calling ValidateYesNo" must {
    "return a Right when given Yes" in {
      CommonValidation.validateYesNo("Yes", "option") mustBe Right("Yes")
    }

    "return a Right when given No" in {
      CommonValidation.validateYesNo("No", "option") mustBe Right("No")
    }

    "return a Left when given any other string" in {
      CommonValidation.validateYesNo("Fasd", "option") mustBe Left("option must be either Yes or No")
    }
  }

  "Calling ValidateOptionYesNo" must {
    "return a Right when given Yes" in {
      CommonValidation.validateOptionYesNo(Some("Yes"), "option") mustBe Right(Some("Yes"))
    }

    "return a Right when given No" in {
      CommonValidation.validateOptionYesNo(Some("No"), "option") mustBe Right(Some("No"))
    }

    "return a Left when given any other string" in {
      CommonValidation.validateOptionYesNo(Some("Fasd"), "option") mustBe Left("option must be either Yes or No")
    }
  }

  "Calling validateCustomerType" must {
    "return a Right when given individual" in {
      CommonValidation.validateCustomerType("individual", "option") mustBe Right("individual")
    }

    "return a Right when given trustee" in {
      CommonValidation.validateCustomerType("trustee", "option") mustBe Right("trustee")
    }

    "return a Right when given personalRep" in {
      CommonValidation.validateCustomerType("personalRep", "option") mustBe Right("personalRep")
    }

    "return a Left when given an invalid input" in {
      CommonValidation.validateCustomerType("frfsdaf", "option") mustBe Left("option must be either individual, trustee or personalRep")
    }
  }

  "Calling validateOptionalAcquisitionDate" must {

    val disposalDate = DateTime.parse("2016-12-12")

    "return a Left when given an AcquisitionDate after the disposal date" in {
      CommonValidation.validateOptionalAcquisitionDate(disposalDate, Some(DateTime.parse("2016-12-20"))) mustBe
        Left("The acquisitionDate must be before the disposalDate")
    }

    "return a Left when given an acquisition date that is equal to the disposal date" in {
      CommonValidation.validateOptionalAcquisitionDate(disposalDate, Some(disposalDate)) mustBe
        Left("The acquisitionDate must be before the disposalDate")
    }

    "return a Right when given an acquisition date that is before the disposal date" in {
      CommonValidation.validateOptionalAcquisitionDate(disposalDate, Some(DateTime.parse("2016-11-11"))) mustBe
        Right(Some(DateTime.parse("2016-11-11")))
    }
  }

  "Calling validateAcquisitionDate" must {

    val disposalDate = DateTime.parse("2016-12-12")

    "return a Left when given an AcquisitionDate after the disposal date" in {
      CommonValidation.validateAcquisitionDate(disposalDate, DateTime.parse("2016-12-20")) mustBe
        Left("The acquisitionDate must be before the disposalDate")
    }

    "return a Left when given an acquisition date that is equal to the disposal date" in {
      CommonValidation.validateAcquisitionDate(disposalDate, disposalDate) mustBe
        Left("The acquisitionDate must be before the disposalDate")
    }

    "return a Right when given an acquisition date that is before the disposal date" in {
      CommonValidation.validateAcquisitionDate(disposalDate, DateTime.parse("2016-11-11")) mustBe
        Right(Some(DateTime.parse("2016-11-11")))
    }
  }
}
