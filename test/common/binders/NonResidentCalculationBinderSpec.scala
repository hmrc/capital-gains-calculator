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

package common.binders

import models.nonResident.CalculationRequest
import org.scalatest.mock.MockitoSugar
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.play.test.UnitSpec
import common.QueryStringKeys.{NonResidentCalculationKeys => keys}

class NonResidentCalculationBinderSpec extends UnitSpec with MockitoSugar {

  val target = new NonResidentCalculationRequestBinder {}.requestBinder
  implicit val mockStringBinder = mock[QueryStringBindable[String]]

  // the values to bind to a valid request
  val validRequest: Map[String, Seq[String]] = Map(
    keys.customerType -> Seq("individual"),
    keys.priorDisposal -> Seq("yes"),
    keys.annualExemptAmount -> Seq("111.11"),
    keys.otherPropertiesAmount -> Seq("222.22"),
    keys.vulnerable -> Seq("yes"),
    keys.currentIncome -> Seq("333.33"),
    keys.personalAllowanceAmount -> Seq("444.44")
  )

  "Binding a valid non resident calculation request" when {

    // the expected result of binding valid requests
    val expectedRequest = CalculationRequest(
      "individual",
      "yes",
      Some(111.11),
      Some(222.22),
      Some("yes"),
      Some(333.33),
      Some(444.44)
    )

    // the opposite of the expectedRequest
    val invalidCalculationRequest = CalculationRequest("", "", None, None, None, None, None)

    val result = target.bind("", validRequest) match {
      case Some(Right(data)) => data
      case _ => invalidCalculationRequest
    }

    "a customer type is defined" should {
      "return a CalculationRequest with the customer type populated" in {
        result.customerType shouldBe expectedRequest.customerType
      }
    }

    "a prior disposal is defined" should {
      "return a CalculationRequest with the prior disposal populated" in {
        result.priorDisposal shouldBe expectedRequest.priorDisposal
      }
    }

    "an annual exempt amount is defined" should {
      "return a CalculationRequest with the annual exempt amount populated" in {
        result.annualExemptAmount shouldBe expectedRequest.annualExemptAmount
      }
    }

    "an annual exempt amount is not defined" should {
      "return a CalculationRequest with the annual exempt amount not populated" in {
        val request = validRequest.filterKeys(key => key != keys.annualExemptAmount)
        val result = target.bind("", request) match {
          case Some(Right(data)) => data
          case _ => invalidCalculationRequest
        }

        result should not be invalidCalculationRequest
        result.annualExemptAmount shouldBe None
      }
    }

    "an other properties amount is defined" should {
      "return a CalculationRequest with the other properties amount populated" in {
        result.otherPropertiesAmount shouldBe expectedRequest.otherPropertiesAmount
      }
    }

    "an other properties amount is not defined" should {
      "return a CalculationRequest with the other properties amount not populated" in {
        val request = validRequest.filterKeys(key => key != keys.otherPropertiesAmount)
        val result = target.bind("", request) match {
          case Some(Right(data)) => data
          case _ => invalidCalculationRequest
        }

        result should not be invalidCalculationRequest
        result.otherPropertiesAmount shouldBe None
      }

      "a vulnerable flag is defined" should {
        "return a CalculationRequest with the vulnerable flag populated" in {
          result.isVulnerable shouldBe expectedRequest.isVulnerable
        }
      }

      "a current income is defined" should {
        "return a CalculationRequest with the current income populated" in {
          result.currentIncome shouldBe expectedRequest.currentIncome
        }
      }

      "a current income is not defined" should {
        "return a CalculationRequest with the current income not populated" in {
          val request = validRequest.filterKeys(key => key != keys.currentIncome)
          val result = target.bind("", request) match {
            case Some(Right(data)) => data
            case _ => invalidCalculationRequest
          }

          result should not be invalidCalculationRequest
          result.currentIncome shouldBe None
        }
      }

      "a personal allowance is defined" should {
        "return a CalculationRequest with the personal allowance populated" in {
          result.personalAllowanceAmt shouldBe expectedRequest.personalAllowanceAmt
        }
      }

      "a personal allowance is not defined" should {
        "return a CalculationRequest with the personal allowance not populated" in {
          val request = validRequest.filterKeys(key => key != keys.personalAllowanceAmount)
          val result = target.bind("", request) match {
            case Some(Right(data)) => data
            case _ => invalidCalculationRequest
          }

          result should not be invalidCalculationRequest
          result.personalAllowanceAmt shouldBe None
        }
      }
    }
  }

  "Binding a invalid non resident calculation request" when {

    def badRequest(badKey: String, value: Option[String]): Map[String, Seq[String]] = value match {
      case Some(data) => validRequest.filterKeys(key => key != badKey) ++ Map(badKey -> Seq(data))
      case None => validRequest.filterKeys(key => key != badKey)
    }

    def doubleParseError(param: String, value: String): String = s"""Cannot parse parameter $param as Double: For input string: "$value""""

    "a customer type is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.customerType, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.customerType} is required."))
      }
    }

    "a prior disposal is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.priorDisposal, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.priorDisposal} is required."))
      }
    }

    "an annual exempt amount is not defined" should {
      "return a CalculationRequest with the annual exempt amount not populated" in {
        val badData = "bad data"
        val request = badRequest(keys.annualExemptAmount, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.annualExemptAmount, badData)))
      }
    }

    "an other properties amount is not defined" should {
      "return a CalculationRequest with the other properties amount not populated" in {
        val badData = "bad data"
        val request = badRequest(keys.otherPropertiesAmount, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.otherPropertiesAmount, badData)))
      }
    }

    "a current income is not defined" should {
      "return a CalculationRequest with the current income not populated" in {
        val badData = "bad data"
        val request = badRequest(keys.currentIncome, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.currentIncome, badData)))
      }
    }

    "a personal allowance is not defined" should {
      "return a CalculationRequest with the personal allowance not populated" in {
        val badData = "bad data"
        val request = badRequest(keys.personalAllowanceAmount, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.personalAllowanceAmount, badData)))
      }
    }
  }

  "Unbinding a non resident calculation request" when {

    "all properties are populated" should {

      val request = CalculationRequest("ind", "yes", Some(111.11), Some(222.22), Some("yes"), Some(333.33), Some(444.44))
      val result = target.unbind("", request)

      "output the customer type key and value" in {
        result should include(s"${keys.customerType}=ind")
      }

      "output the prior disposal key and value" in {
        result should include(s"&${keys.priorDisposal}=yes")
      }

      "output the annual exempt amount key and value" in {
        result should include(s"&${keys.annualExemptAmount}=111.11")
      }

      "output the other properties amount key and value" in {
        result should include(s"&${keys.otherPropertiesAmount}=222.22")
      }

      "output the vulnerable flag key and value" in {
        result should include(s"&${keys.vulnerable}=yes")
      }

      "output the current income key and value" in {
        result should include(s"&${keys.currentIncome}=333.33")
      }

      "output the personal allowance key and value" in {
        result should include(s"&${keys.personalAllowanceAmount}=444.44")
      }
    }

    "optional properties are missing" should {

      val request = CalculationRequest("ind", "yes", None, None, None, None, None)
      val result = target.unbind("", request)

      "not output the annual exempt amount key and value" in {
        result should not include s"&${keys.annualExemptAmount}"
      }

      "not output the other properties amount key and value" in {
        result should not include s"&${keys.otherPropertiesAmount}"
      }

      "not output the vulnerable flag key and value" in {
        result should not include s"&${keys.vulnerable}"
      }

      "not output the current income key and value" in {
        result should not include s"&${keys.currentIncome}"
      }

      "not output the personal allowance key and value" in {
        result should not include s"&${keys.personalAllowanceAmount}"
      }
    }
  }
}