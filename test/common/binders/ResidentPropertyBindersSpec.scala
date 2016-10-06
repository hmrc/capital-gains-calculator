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
import models.resident.properties.PropertyTotalGainModel
import models.resident.shares.TotalGainModel
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatest.mock.MockitoSugar

class ResidentPropertyBindersSpec extends UnitSpec with MockitoSugar {

  "Calling Property Total Gain binder " when {

    val binder = new ResidentPropertyBinders {}.propertyTotalGainBinder

    "calling .bind" should {

      "return a valid PropertyTotalGainModel from a valid map with the same values" in {
        val totalGainModel = new TotalGainModel(2000, 2000, 2000, 2000)
        val result = binder.bind("", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2000.0"),
          "acquisitionValue" -> Seq("2000.0"),
          "acquisitionCosts" -> Seq("2000.0"),
          "improvements" -> Seq("2000.0")))

        result shouldBe Some(Right(PropertyTotalGainModel(totalGainModel, 2000.0)))
      }

      "return a valid PropertyTotalGainModel from a valid map with different values" in {
        val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
        val result = binder.bind("", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0"),
          "improvements" -> Seq("1500.0")))

        result shouldBe Some(Right(PropertyTotalGainModel(totalGainModel, 1500.0)))

      }

      "return an error message when one component fails" in {
        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("b"),
          "acquisitionCosts" -> Seq("1000.0"),
          "improvements" -> Seq("1000.0")))

        result shouldBe Some(Left("""Cannot parse parameter acquisitionValue as Double: For input string: "b""""))
      }

      "return an error message when a component is missing" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "improvements" -> Seq("1000.0")))

        result shouldBe Some(Left("acquisitionCosts is required."))
      }

      "return an error message when a value fails validation" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1500.0"),
          "acquisitionValue" -> Seq("2000.0"),
          "acquisitionCosts" -> Seq("2500.0"),
          "improvements" -> Seq("-3000.0")))

        result shouldBe Some(Left("improvements cannot be negative."))
      }
    }

    "calling .unBind" should {

      "return a valid queryString on unbind with identical values" in {
        val totalGainString = "disposalValue=1000.0&disposalCosts=1000.0&acquisitionValue=1000.0&acquisitionCosts=1000.0"
        val result = binder.unbind("", PropertyTotalGainModel(TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0), 1000.0))

        result shouldBe totalGainString + "&improvements=1000.0"
      }

      "return a valid queryString on unbind with different values" in {
        val totalGainString = "disposalValue=2000.0&disposalCosts=1500.0&acquisitionValue=1750.0&acquisitionCosts=3000.0"
        val result = binder.unbind("", PropertyTotalGainModel(TotalGainModel(2000.0, 1500.0, 1750.0, 3000.0), 2500.0))

        result shouldBe totalGainString + "&improvements=2500.0"
      }
    }
  }
}
