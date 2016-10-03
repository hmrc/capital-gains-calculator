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

package models.shares

import models.resident.shares.TotalGainModel
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.play.test.UnitSpec


class TotalGainModelSpec extends UnitSpec with MockitoSugar {

  def setupMockDoubleBinder(bindValue: Option[Either[String, Double]],
                            unbindValue: String,
                            bindValues: (String, Option[Either[String, Double]])*): QueryStringBindable[Double] = {

    val mockBinder = mock[QueryStringBindable[Double]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    bindValues.foreach(input =>
      when(mockBinder.bind(Matchers.eq(input._1), Matchers.any()))
        .thenReturn(input._2)
    )

    bindValues.filter(_._2.get.isRight).foreach(input =>
      when(mockBinder.unbind(Matchers.eq(input._1), Matchers.any()))
        .thenReturn(input._2.get.right.get.toString)
    )

    mockBinder
  }

  "Calling TotalGain binder" when {

    "given a valid binding value where all values are the same" should {
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(1000.0)), "1000.0")
      val binder = TotalGainModel.totalGainBinder

      "return a valid TotalGainModel on bind" in {
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Right(TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0)))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0))

        result shouldBe "disposalValue=1000.0&disposalCosts=1000.0&acquisitionValue=1000.0&acquisitionCosts=1000.0"
      }
    }

    "given a valid binding value where all values are different" should {
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(2000.0)), "2000.0", ("disposalCosts", Some(Right(2500.0))),
        ("acquisitionValue", Some(Right(3000.0))), ("acquisitionCosts", Some(Right(3500.0))))
      val binder = TotalGainModel.totalGainBinder

      "return a valid TotalGainModel on bind" in {
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Right(TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0))

        result shouldBe "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0"
      }
    }

    "given an invalid binding value" should {

      "return one error message on a failed bind on all inputs" in {
        implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Left("Error message")), "")
        val binder = TotalGainModel.totalGainBinder
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Left("Error message"))
      }

      "return an error message when one component fails" in {
        implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(1000.0)), "", ("disposalValue", Some(Left("Invalid disposal value"))))
        val binder = TotalGainModel.totalGainBinder
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Left("Invalid disposal value"))
      }
    }
  }
}
