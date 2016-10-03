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
import org.scalatest.mock.MockitoSugar
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.play.test.UnitSpec
import org.mockito.Mockito._


class TotalGainModelSpec extends UnitSpec with MockitoSugar {

  def setupMockDoubleBinder(bindValue: Option[Either[String, Double]], unbindValue: String): QueryStringBindable[Double] = {

    val mockBinder = mock[QueryStringBindable[Double]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder
  }

  "Calling TotalGain binder" when {

    "given a valid binding value" should {
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(1000.0)), "1000")
      val binder = TotalGainModel.totalGainBinder

      "return a valid TotalGainModel on bind" in {
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Right(TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0)))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0))

        result shouldBe "disposalValue=1000&disposalCosts=1000&acquisitionValue=1000&acquisitionCosts=1000"
      }
    }

    "given an invalid binding value" should {
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Left("Error message")), "")
      val binder = TotalGainModel.totalGainBinder

      "return an error message on a failed bind" in {
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Left("Error message"))
      }
    }
  }
}
