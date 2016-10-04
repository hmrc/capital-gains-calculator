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

import models.resident.shares.TotalGainModel
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec


class ResidentSharesBindersSpec extends UnitSpec with MockitoSugar {

  "Calling totalGainBinder" when {
    val binder = new ResidentSharesBinders{}.totalGainBinder

    "calling .bind" should {

      "return a valid TotalGainModel with a valid map with the same values" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0")))

        result shouldBe Some(Right(TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0)))
      }

      "return a valid TotalGainModel on bind with a valid map with different values" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0")))

        result shouldBe Some(Right(TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)))
      }

      "return one error message on a failed bind on all inputs" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("a"),
          "disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("c"),
          "acquisitionCosts" -> Seq("d")))

        result shouldBe Some(Left("""Cannot parse parameter disposalValue as Double: For input string: "a""""))
      }

      "return an error message when one component fails" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0")))

        result shouldBe Some(Left("""Cannot parse parameter disposalCosts as Double: For input string: "b""""))
      }

      "return an error message when a component is missing" in {
        val result = binder.bind("Any", Map("disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0")))

        result shouldBe Some(Left("disposalValue is required."))
      }

    }

    "calling .unBind" should {

      "return a valid queryString on unbind with identical values" in {
        val result = binder.unbind("", TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0))

        result shouldBe "disposalValue=1000.0&disposalCosts=1000.0&acquisitionValue=1000.0&acquisitionCosts=1000.0"
      }

      "return a valid queryString on unbind with different values" in {
        val result = binder.unbind("", TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0))

        result shouldBe "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0"
      }
    }
  }
}
