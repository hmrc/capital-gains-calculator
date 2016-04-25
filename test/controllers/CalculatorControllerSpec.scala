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

package controllers

import models.CalculationResultModel
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CalculationService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CalculatorControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

//  "GET /capital-gains-calculator/calculate" should {
//
//    val mockCalculationService = mock[CalculationService]
//    when(mockCalculationService.add(Matchers.anyInt, Matchers.anyInt))
//      .thenReturn(CalculationResult(1, 2, 3))
//
//    val target: CalculatorController = new CalculatorController {
//      override val calculationService = mockCalculationService
//    }
//
//    val fakeRequest = FakeRequest("GET", "/capital-gains-calculator/calculate")
//    val result = target.calculate(1, 2)(fakeRequest)
//
//    "return 200" in {
//      status(result) shouldBe Status.OK
//    }
//
//    "return a JSON result" in {
//      contentType(result) shouldBe Some("application/json")
//      charset(result) shouldBe Some("utf-8")
//    }
//
//    "return a valid result" in {
//      val data = contentAsString(result)
//      val json = Json.parse(data)
//      (json \ "left").as[Int] shouldBe 1
//      (json \ "right").as[Int] shouldBe 2
//      (json \ "result").as[Int] shouldBe 3
//    }
//
//  }
}
