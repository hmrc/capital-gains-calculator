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

import models.nonResident.CalculationRequestModel
import org.scalatest.mock.MockitoSugar
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.play.test.UnitSpec
import common.QueryStringKeys.{NonResidentCalculationKeys => keys}
import org.joda.time.DateTime

class NonResidentCalculationBinderSpec extends UnitSpec with MockitoSugar {

  val target = new NonResidentCalculationRequestBinder {}.requestBinder
  implicit val mockStringBinder = mock[QueryStringBindable[String]]

  // the values to bind to a valid request
  val validRequest: Map[String, Seq[String]] = Map(
    keys.customerType -> Seq("individual"),
    keys.priorDisposal -> Seq("Yes"),
    keys.annualExemptAmount -> Seq("111.11"),
    keys.otherPropertiesAmount -> Seq("222.22"),
    keys.vulnerable -> Seq("Yes"),
    keys.currentIncome -> Seq("333.33"),
    keys.personalAllowanceAmount -> Seq("444.44"),
    keys.disposalValue -> Seq("555.55"),
    keys.disposalCosts  -> Seq("666.66"),
    keys.initialValue -> Seq("777.77"),
    keys.initialCosts -> Seq("888.88"),
    keys.improvementsAmount -> Seq("999.99"),
    keys.reliefsAmount -> Seq("11.11"),
    keys.allowableLosses -> Seq("22.22"),
    keys.acquisitionDate -> Seq("2016-12-20"),
    keys.disposalDate -> Seq("2018-09-13"),
    keys.isClaimingPRR -> Seq("Yes"),
    keys.daysClaimed -> Seq("200")
  )

  // the expected result of binding valid requests
  val expectedRequest = CalculationRequestModel(
    "individual",
    "Yes",
    Some(111.11),
    Some(222.22),
    Some("Yes"),
    Some(333.33),
    Some(444.44),
    555.55,
    666.66,
    777.77,
    888.88,
    999.99,
    11.11,
    22.22,
    Some(DateTime.parse("2016-12-20")),
    DateTime.parse("2018-09-13"),
    Some("Yes"),
    Some(200.0)
  )

  // the opposite of the expectedRequest
  val emptyCalculationRequest = CalculationRequestModel("", "", None, None, None, None, None, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, None, DateTime.parse("0000-01-01"), None, None)

  "Binding a valid non resident calculation request" when {

    val result = target.bind("", validRequest) match {
      case Some(Right(data)) => data
      case _ => emptyCalculationRequest
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
      val request = validRequest.filterKeys(key => key != keys.annualExemptAmount)
      val result = target.bind("", request) match {
        case Some(Right(data)) => data
        case _ => emptyCalculationRequest
      }
      "return a CalculationRequest with the annual exempt amount not populated" in {
        result.annualExemptAmount shouldBe None
      }
      "not match the empty test model as defined above" in {
        result should not be emptyCalculationRequest
      }
    }

    "an other properties amount is defined" should {
      "return a CalculationRequest with the other properties amount populated" in {
        result.otherPropertiesAmount shouldBe expectedRequest.otherPropertiesAmount
      }
    }

    "an other properties amount is not defined" should {
      val request = validRequest.filterKeys(key => key != keys.otherPropertiesAmount)
      val result = target.bind("", request) match {
        case Some(Right(data)) => data
        case _ => emptyCalculationRequest
      }
      "return a CalculationRequest with the other properties amount not populated" in {
        result.otherPropertiesAmount shouldBe None
      }
      "not match the empty test model as defined above" in {
        result should not be emptyCalculationRequest
      }
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
      val request = validRequest.filterKeys(key => key != keys.currentIncome)
      val result = target.bind("", request) match {
        case Some(Right(data)) => data
        case _ => emptyCalculationRequest
      }
      "return a CalculationRequest with the current income not populated" in {
        result.currentIncome shouldBe None
      }
      "not match the empty test model as defined above" in {
        result should not be emptyCalculationRequest
      }
    }

    "a personal allowance is defined" should {
      "return a CalculationRequest with the personal allowance populated" in {
        result.personalAllowanceAmount shouldBe expectedRequest.personalAllowanceAmount
      }
    }

    "a personal allowance is not defined" should {
      val request = validRequest.filterKeys(key => key != keys.personalAllowanceAmount)
      val result = target.bind("", request) match {
        case Some(Right(data)) => data
        case _ => emptyCalculationRequest
      }
      "return a CalculationRequest with the personal allowance not populated" in {
        result.personalAllowanceAmount shouldBe None
      }
      "not match the empty test model as defined above" in {
        result should not be emptyCalculationRequest
      }
    }

    "a disposal value is defined" should {
      "return a CalculationRequest with the disposal value populated" in {
        result.disposalValue shouldBe expectedRequest.disposalValue
      }
    }

    "a disposal costs is defined" should {
      "return a CalculationRequest with the disposal costs populated" in {
        result.disposalCosts shouldBe expectedRequest.disposalCosts
      }
    }

    "an acquisition value is defined" should {
      "return a CalculationRequest with the acquisition value populated" in {
        result.initialValue shouldBe expectedRequest.initialValue
      }
    }

    "an acquisition cost is defined" should {
      "return a CalculationRequest with the acquisition costs populated" in {
        result.initialCosts shouldBe expectedRequest.initialCosts
      }
    }

    "an improvements amount is defined" should {
      "return a CalculationRequest with the improvements amount populated" in {
        result.improvementsAmount shouldBe expectedRequest.improvementsAmount
      }
    }

    "a reliefs amount is defined" should {
      "return a CalculationRequest with the reliefs amount populated" in {
        result.reliefsAmount shouldBe expectedRequest.reliefsAmount
      }
    }

    "an allowable losses amount is defined" should {
      "return a CalculationRequest with the allowable losses amount populated" in {
        result.allowableLosses shouldBe expectedRequest.allowableLosses
      }
    }

    "an acquisition date is defined" should {
      "return a CalculationRequest with the acquisition date populated" in {
        result.acquisitionDate shouldBe expectedRequest.acquisitionDate
      }
    }

    "an acquisition date is not defined" should {
      val request = validRequest.filterKeys(key => key != keys.acquisitionDate)
      val result = target.bind("", request) match {
        case Some(Right(data)) => data
        case _ => emptyCalculationRequest
      }
      "return a CalculationRequest with the acquisition date not populated" in {
        result.acquisitionDate shouldBe None
      }
      "not match the empty test model as defined above" in {
        result should not be emptyCalculationRequest
      }
    }

    "a disposal date is defined" should {
      "return a CalculationRequest with the disposal date populated" in {
        result.disposalDate shouldBe expectedRequest.disposalDate
      }
    }

    "an is claiming prr is defined" should {
      "return a CalculationRequest with the is claiming prr populated" in {
        result.isClaimingPRR shouldBe expectedRequest.isClaimingPRR
      }
    }

    "is claiming prr is not defined" should {
      val request = validRequest.filterKeys(key => key != keys.isClaimingPRR)
      val result = target.bind("", request) match {
        case Some(Right(data)) => data
        case _ => emptyCalculationRequest
      }
      "return a CalculationRequest with the is claiming prr not populated" in {
        result.isClaimingPRR shouldBe None
      }
      "not match the empty test model as defined above" in {
        result should not be emptyCalculationRequest
      }
    }

    "a days claimed is defined" should {
      "return a CalculationRequest with the days claimed populated" in {
        result.daysClaimed shouldBe expectedRequest.daysClaimed
      }
    }

    "a days claimed value is not defined" should {
      val request = validRequest.filterKeys(key => key != keys.daysClaimed)
      val result = target.bind("", request) match {
        case Some(Right(data)) => data
        case _ => emptyCalculationRequest
      }
      "return a CalculationRequest with the annual exempt amount not populated" in {
        result.daysClaimed shouldBe None
      }
      "not match the empty test model as defined above" in {
        result should not be emptyCalculationRequest
      }
    }
  }

  "Binding a invalid non resident calculation request" when {

    def badRequest(badKey: String, value: Option[String]): Map[String, Seq[String]] = value match {
      case Some(data) => validRequest.filterKeys(key => key != badKey) ++ Map(badKey -> Seq(data))
      case None => validRequest.filterKeys(key => key != badKey)
    }

    def doubleParseError(param: String, value: String): String = s"""Cannot parse parameter $param as Double: For input string: "$value""""

    def dateParseError(param: String, value: String): String = s"""Cannot parse parameter $param as DateTime: For input string: "$value""""

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

    "an annual exempt amount with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.annualExemptAmount, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.annualExemptAmount, badData)))
      }
    }

    "an other properties amount with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.otherPropertiesAmount, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.otherPropertiesAmount, badData)))
      }
    }

    "a current income with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.currentIncome, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.currentIncome, badData)))
      }
    }

    "a personal allowance with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.personalAllowanceAmount, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.personalAllowanceAmount, badData)))
      }
    }

    "a disposal value is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.disposalValue, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.disposalValue} is required."))
      }
    }

    "a disposal value with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.disposalValue, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.disposalValue, badData)))
      }
    }

    "a disposal costs is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.disposalCosts, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.disposalCosts} is required."))
      }
    }

    "a disposal costs with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.disposalCosts, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.disposalCosts, badData)))
      }
    }

    "an initial value is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.initialValue, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.initialValue} is required."))
      }
    }

    "an initial value with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.initialValue, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.initialValue, badData)))
      }
    }

    "an initial costs is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.initialCosts, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.initialCosts} is required."))
      }
    }

    "an initial costs with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.initialCosts, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.initialCosts, badData)))
      }
    }

    "an improvements amount is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.improvementsAmount, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.improvementsAmount} is required."))
      }
    }

    "an improvements amount with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.improvementsAmount, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.improvementsAmount, badData)))
      }
    }

    "an reliefs amount is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.reliefsAmount, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.reliefsAmount} is required."))
      }
    }

    "an reliefs amount with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.reliefsAmount, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.reliefsAmount, badData)))
      }
    }

    "an allowable losses amount is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.allowableLosses, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.allowableLosses} is required."))
      }
    }

    "an allowable losses amount with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.allowableLosses, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.allowableLosses, badData)))
      }
    }

    "a acquisition date with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.acquisitionDate, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(dateParseError(keys.acquisitionDate, badData)))
      }
    }

    "a disposal date is not supplied" should {
      "return an error message" in {
        val request = badRequest(keys.disposalDate, None)
        val result = target.bind("", request)
        result shouldBe Some(Left(s"${keys.disposalDate} is required."))
      }
    }

    "a disposal date amount with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.disposalDate, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(dateParseError(keys.disposalDate, badData)))
      }
    }

    "a days claimed with an invalid value" should {
      "return an error message" in {
        val badData = "bad data"
        val request = badRequest(keys.daysClaimed, Some(badData))
        val result = target.bind("", request)
        result shouldBe Some(Left(doubleParseError(keys.daysClaimed, badData)))
      }
    }
  }

  "Unbinding a non resident calculation request" when {

    "all properties are populated" should {

      val request = expectedRequest
      val result = target.unbind("", request)

      "output the customer type key and value" in {
        result should include(s"${keys.customerType}=ind")
      }

      "output the prior disposal key and value" in {
        result should include(s"&${keys.priorDisposal}=Yes")
      }

      "output the annual exempt amount key and value" in {
        result should include(s"&${keys.annualExemptAmount}=111.11")
      }

      "output the other properties amount key and value" in {
        result should include(s"&${keys.otherPropertiesAmount}=222.22")
      }

      "output the vulnerable flag key and value" in {
        result should include(s"&${keys.vulnerable}=Yes")
      }

      "output the current income key and value" in {
        result should include(s"&${keys.currentIncome}=333.33")
      }

      "output the personal allowance key and value" in {
        result should include(s"&${keys.personalAllowanceAmount}=444.44")
      }

      "output the disposal value key and value" in {
        result should include(s"&${keys.disposalValue}=555.55")
      }

      "output the disposal costs key and value" in {
        result should include(s"&${keys.disposalCosts}=666.66")
      }

      "output the acquisition value key and value" in {
        result should include(s"&${keys.initialValue}=777.77")
      }

      "output the acquisition costs key and value" in {
        result should include(s"&${keys.initialCosts}=888.88")
      }

      "output the improvements amount key and value" in {
        result should include(s"&${keys.improvementsAmount}=999.99")
      }

      "output the reliefs amount key and value" in {
        result should include(s"&${keys.reliefsAmount}=11.11")
      }

      "output the allowable losses amount key and value" in {
        result should include(s"&${keys.allowableLosses}=22.22")
      }

      "output the acquisition date key and value" in {
        result should include(s"&${keys.acquisitionDate}=2016-12-20")
      }

      "output the disposal date key and value" in {
        result should include(s"&${keys.disposalDate}=2018-9-13")
      }

      "output the is claiming prr key and value" in {
        result should include(s"&${keys.isClaimingPRR}=Yes")
      }

      "output the days claiming key and value" in {
        result should include(s"&${keys.daysClaimed}=200")
      }
    }

    "optional properties are missing" should {

      val request = emptyCalculationRequest
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

      "not output the acquisition date key and value" in {
        result should not include s"&${keys.acquisitionDate}"
      }

      "not output the is claiming prr key and value" in {
        result should not include s"&${keys.isClaimingPRR}"
      }

      "not output the days claiming key and value" in {
        result should not include s"&${keys.daysClaimed}"
      }
    }
  }
}