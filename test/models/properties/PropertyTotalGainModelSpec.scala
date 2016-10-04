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

package models.properties

import models.resident.properties.PropertyTotalGainModel
import models.resident.shares.TotalGainModel
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.play.test.UnitSpec

class PropertyTotalGainModelSpec extends UnitSpec with MockitoSugar {

  def setupMockDoubleBinder(bindValue: Option[Either[String, Double]],
                            unbindValue: String)
                            : QueryStringBindable[Double] = {

    val mockBinder = mock[QueryStringBindable[Double]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder
  }

  def setupMockTotalGainBinder(bindValue: Option[Either[String, TotalGainModel]],
                               unbindValue: String)
                              : QueryStringBindable[TotalGainModel] = {

    val mockBinder = mock[QueryStringBindable[TotalGainModel]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder
  }

  "Calling Property Total Gain binder " when {
    "given a valid binding value where all values are the same" should {
      val totalGain = new TotalGainModel(2000, 2000, 2000, 2000)
      val improvements = 2000
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(improvements)), "2000.0")
      implicit val mockTotalGainBinder = setupMockTotalGainBinder(Some(Right(totalGain)), "disposalValue=2000.0&disposalCosts=2000.0&acquisitionValue=2000.0&acquisitionCosts=2000.0")

      val binder = PropertyTotalGainModel.propertyTotalGainBinder

      "return a valid Property Total Gain Model on bind" in {
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Right(PropertyTotalGainModel(TotalGainModel(2000.0, 2000.0, 2000.0, 2000.0), 2000.0)))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", PropertyTotalGainModel(TotalGainModel(2000.0, 2000.0, 2000.0, 2000.0), 2000.0))

        result shouldBe "totalGainModel=disposalValue=2000.0&disposalCosts=2000.0&acquisitionValue=2000.0&acquisitionCosts=2000.0&improvements=2000.0"
      }
    }

    "given a invalid binding value where all values are the same" should {
      val totalGain = new TotalGainModel(2000, 2500, 3000, 3500)
      val improvements = 4000
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(improvements)), "4000.0")
      implicit val mockTotalGainBinder = setupMockTotalGainBinder(Some(Right(totalGain)), "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0")

      val binder = PropertyTotalGainModel.propertyTotalGainBinder

      "return a valid Property Total Gain Model on bind" in {
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Right(PropertyTotalGainModel(TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0), 4000.0)))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", PropertyTotalGainModel(TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0), 4000.0))

        result shouldBe "totalGainModel=disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0&improvements=4000.0"
      }
    }

    "given an invalid binding value" should {

      "return one error message on a failed bind on all inputs" in {
        implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Left("Error message")), "")
        val binder = PropertyTotalGainModel.propertyTotalGainBinder
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Left("Error message"))
      }

      "return an error message when one component fails" in {

        val binder = PropertyTotalGainModel.propertyTotalGainBinder

        val result = binder.bind("Any",
          Map(
            "disposalValue" -> Seq("1000.0"),
            "disposalCosts" -> Seq("1500.0"),
            "acquisitionValue" -> Seq("2000.0"),
            "acquisitionCosts" -> Seq("2500.0"),
            "improvements" -> Seq("")
          )
        )

        result shouldBe Some(Left("Cannot parse parameter improvements as Double: empty String"))
      }
    }
  }
}
