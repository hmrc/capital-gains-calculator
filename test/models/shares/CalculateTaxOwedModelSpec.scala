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

import models.resident.shares.{ChargeableGainModel, TotalGainModel, CalculateTaxOwedModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.play.test.UnitSpec


class CalculateTaxOwedModelSpec extends UnitSpec with MockitoSugar {

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

  def setupMockStringBinder(bindValue: Option[Either[String, String]],
                            unbindValue: String
                           ): QueryStringBindable[String] = {

    val mockBinder = mock[QueryStringBindable[String]]

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

  def setupMockChargeableGainBinder (bindValue: Option[Either[String, ChargeableGainModel]],
                                unbindValue: String
                               ): QueryStringBindable[ChargeableGainModel] = {

    val mockBinder = mock[QueryStringBindable[ChargeableGainModel]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder
  }

  "Calling Calculate Tax Owed binder" when {

    "given a valid binding value where all values are the same" should {
      val totalGainModel = TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0)
      val chargeableGainRequest = "disposalValue=1000.0&disposalCosts=1000.0&acquisitionValue=1000.0&acquisitionCosts=1000.0" +
        "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"
      implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(1000.0)), "1000.0")
      implicit val mockOptionDoubleBinder = setupMockOptionDoubleBinder(Some(Right(Some(1000.0))), "1000.0")
      implicit val mockStringBinder = setupMockStringBinder(Some(Right("2016-10-10")), "2016-10-10")
      implicit val mockChargeableGainBinder = setupMockChargeableGainBinder(Some(Right(chargeableGainModel)), chargeableGainRequest)
      val binder = CalculateTaxOwedModel.calculateTaxOwedBinder

      "return a valid CalculateTaxOwedModel on bind" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
        "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0"),
          "previousTaxableGain" -> Seq("1000.0"),
          "previousIncome" -> Seq("1000.0"),
          "personalAllowance" -> Seq("1000.0"),
          "disposalDate" -> Seq("2016-10-10")
        ))

        result shouldBe Some(Right(CalculateTaxOwedModel(chargeableGainModel, Some(1000.0), 1000.0, 1000.0, "2016-10-10")))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", CalculateTaxOwedModel(chargeableGainModel, Some(1000.0), 1000.0, 1000.0, "2016-10-10"))

        result shouldBe chargeableGainRequest + "&previousTaxableGain=1000.0&previousIncome=1000.0&personalAllowance=1000.0&disposalDate=2016-10-10"
      }
    }

    "given a valid binding value where all values are different" should {
      val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(4000.0), Some(4500.0), 5000.0)
      val chargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0" +
        "&allowableLosses=4000.0&broughtForwardLosses=4500.0&annualExemptAmount=5000.0"
      val binder = CalculateTaxOwedModel.calculateTaxOwedBinder

      "return a valid CalculateTaxOwedModel on bind" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0"),
          "allowableLosses" -> Seq("4000.0"),
          "broughtForwardLosses" -> Seq("4500.0"),
          "annualExemptAmount" -> Seq("5000.0"),
          "previousTaxableGain" -> Seq("4500.0"),
          "previousIncome" -> Seq("4000.0"),
          "personalAllowance" -> Seq("4000.0"),
          "disposalDate" -> Seq("2016-10-10")
        ))

        result shouldBe Some(Right(CalculateTaxOwedModel(chargeableGainModel, None, 4000.0, 4000.0, "2016-10-10")))
      }

      "return a valid queryString on unbind" in {
        val result = binder.unbind("key", CalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.0, 5500.0, "2016-10-10"))

        result shouldBe chargeableGainRequest + "&previousTaxableGain=4500.0&previousIncome=4000.0&personalAllowance=4000.0&disposalDate=2016-10-10"
      }
    }

    "given an invalid binding value" should {

      "return one error message on a failed bind on all inputs" in {
        val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
        val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0)
        val chargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0" +
          "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"
        implicit val mockChargeableGainBinder = setupMockChargeableGainBinder(Some(Left("Error message")), chargeableGainRequest)
        implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Left("Error message")), "6000.0")
        implicit val mockOptionDoubleBinder = setupMockOptionDoubleBinder(Some(Left("Error message")), "")
        val binder = CalculateTaxOwedModel.calculateTaxOwedBinder
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Left("Error message"))
      }

      "return an error message when one component fails" in {
        val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
        val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0)
        val chargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0" +
          "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"
        implicit val mockChargeableGainBinder = setupMockChargeableGainBinder(Some(Right(chargeableGainModel)), chargeableGainRequest)
        implicit val mockDoubleBinder = setupMockDoubleBinder(Some(Right(4000.0)), "4000.0")
        implicit val mockStringBinder = setupMockStringBinder(Some(Right("2016-10-10")), "2016-10-10")
        implicit val mockOptionDoubleBinder = setupMockOptionDoubleBinder(Some(Left("Invalid previous taxable gains")), "")
        val binder = CalculateTaxOwedModel.calculateTaxOwedBinder
        val result = binder.bind("Any", Map("key" -> Seq("data")))

        result shouldBe Some(Left("Invalid previous taxable gains"))
      }
    }
  }
}
