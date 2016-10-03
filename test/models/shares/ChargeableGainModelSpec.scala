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

import models.resident.shares.{ChargeableGainModel, TotalGainModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.play.test.UnitSpec


class ChargeableGainModelSpec extends UnitSpec with MockitoSugar {

  def setupMockDoubleBinder(bindValue: Option[Either[String, Double]],
                            unbindValue: String
                            ): QueryStringBindable[Double] = {

    val mockBinder = mock[QueryStringBindable[Double]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder

  }

  def setupMockOptionDoubleBinder (bindValue: Option[Either[String, Option[Double]]],
  unbindValue: String
  ): QueryStringBindable[Option[Double]] = {

    val mockBinder = mock[QueryStringBindable[Option[Double]]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder
  }

  def setupMockTotalGainBinder (bindValue: Option[Either[String, TotalGainModel]],
                                   unbindValue: String
                                  ): QueryStringBindable[TotalGainModel] = {

    val mockBinder = mock[QueryStringBindable[TotalGainModel]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder
  }

  "Calling Chargeable Gain binder" when {

    "given a valid binding value where all values are the same" should {
      val totalGainModel = TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0)
      val totalGainRequest = "disposalValue=1000.0&disposalCosts=1000.0&acquisitionValue=1000.0&acquisitionCosts=1000.0"
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(1000.0)), "1000.0")
      implicit val mockOptionDoubleBinder = setupMockOptionDoubleBinder(Some(Right(Some(1000.0))), "1000.0")
      implicit val mockTotalGainBinder = setupMockTotalGainBinder(Some(Right(totalGainModel)), totalGainRequest)
      val binder = ChargeableGainModel.chargeableGainBinder

      "return a valid ChargeableGainModel on bind" in {
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Right(ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0)))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0))

        result shouldBe totalGainRequest + "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"
      }
    }

    "given a valid binding value where all values are different" should {
      val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
      val totalGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0"
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(4000.0)), "4000.0")
      implicit val mockOptionDoubleBinder = setupMockOptionDoubleBinder(Some(Right(None)), "4500.0")
      implicit val mockTotalGainBinder = setupMockTotalGainBinder(Some(Right(totalGainModel)), totalGainRequest)
      val binder = ChargeableGainModel.chargeableGainBinder

      "return a valid ChargeableGainModel on bind" in {
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Right(ChargeableGainModel(totalGainModel, None, None, 4000.0)))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", ChargeableGainModel(totalGainModel, Some(4500.0), Some(4500.0), 4000.0))

        result shouldBe totalGainRequest + "&allowableLosses=4500.0&broughtForwardLosses=4500.0&annualExemptAmount=4000.0"
      }
    }

    "given an invalid binding value" should {

      "return one error message on a failed bind on all inputs" in {
        val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
        val totalGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0"
        implicit val mockTotalGainBinder = setupMockTotalGainBinder(Some(Left("Error message")), totalGainRequest)
        implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Left("Error message")), "4000.0")
        implicit val mockOptionDoubleBinder = setupMockOptionDoubleBinder(Some(Left("Error message")), "")
        val binder = ChargeableGainModel.chargeableGainBinder
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Left("Error message"))
      }

      "return an error message when one component fails" in {
        val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
        val totalGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0"
        implicit val mockTotalGainBinder = setupMockTotalGainBinder(Some(Right(totalGainModel)), totalGainRequest)
        implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(4000.0)), "4000.0")
        implicit val mockOptionDoubleBinder = setupMockOptionDoubleBinder(Some(Left("Invalid allowable losses")), "")
        val binder = ChargeableGainModel.chargeableGainBinder
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Left("Invalid allowable losses"))
      }
    }
  }
}
