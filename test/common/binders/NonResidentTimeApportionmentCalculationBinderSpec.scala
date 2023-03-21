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

package common.binders

import models.nonResident.TimeApportionmentCalculationRequestModel
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.QueryStringBindable
import org.scalatestplus.play.PlaySpec
import common.QueryStringKeys.{NonResidentCalculationKeys => keys}
import org.joda.time.DateTime

class NonResidentTimeApportionmentCalculationBinderSpec extends PlaySpec with MockitoSugar {

  val target = new NonResidentTimeApportionmentCalculationRequestBinder {}.requestBinder
  implicit val mockStringBinder = mock[QueryStringBindable[String]]

  // the values to bind to a valid request
  val validRequest: Map[String, Seq[String]] = Map(
    keys.priorDisposal -> Seq("Yes"),
    keys.annualExemptAmount -> Seq("111.11"),
    keys.otherPropertiesAmount -> Seq("222.22"),
    keys.currentIncome -> Seq("333.33"),
    keys.personalAllowanceAmount -> Seq("444.44"),
    keys.disposalValue -> Seq("555.55"),
    keys.disposalCosts -> Seq("666.66"),
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
  val expectedRequest = TimeApportionmentCalculationRequestModel(
    "Yes",
    Some(111.11),
    Some(222.22),
    333.33,
    Some(444.44),
    555.55,
    666.66,
    777.77,
    888.88,
    999.99,
    11.11,
    22.22,
    DateTime.parse("2016-12-20"),
    DateTime.parse("2018-09-13"),
    Some("Yes"),
    Some(200.0)
  )

  // the opposite of the expectedRequest
  val emptyCalculationRequest = TimeApportionmentCalculationRequestModel("", None, None, 0.00, None, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, DateTime.parse("0000-01-01"), DateTime.parse("0000-01-01"), None, None)

  "Binding a invalid non resident calculation request" when {

    def badRequest(badKey: String, value: Option[String]): Map[String, Seq[String]] = value match {
      case Some(data) => validRequest.filterKeys(key => key != badKey) ++ Map(badKey -> Seq(data))
      case None => validRequest.filterKeys(key => key != badKey)
    }

//    def dateParseError(param: String, value: String): String = s"""Cannot parse parameter $param as DateTime: For input string: "$value""""

    "an acquisition date is not supplied" must {
      "return an error message" in {
        val request = badRequest(keys.acquisitionDate, None)
        val result = target.bind("", request)
        result mustBe Some(Left(s"${keys.acquisitionDate} is required."))
      }
    }
  }
}
