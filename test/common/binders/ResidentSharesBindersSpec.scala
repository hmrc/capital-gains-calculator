/*
 * Copyright 2019 HM Revenue & Customs
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

import models.resident.shares.{CalculateTaxOwedModel, ChargeableGainModel, TotalGainModel}
import org.joda.time.DateTime
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec


class ResidentSharesBindersSpec extends UnitSpec with MockitoSugar {

  "Calling totalGainBinder" when {
    val binder = new ResidentSharesBinders{}.totalGainBinder

    "calling .bind" should {

      "return a valid TotalGainModel from a valid map with the same values" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0")))

        result shouldBe Some(Right(TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0)))
      }

      "return a valid TotalGainModel from a valid map with different values" in {
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

      "return an error message when a value fails validation" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("-3000.0"),
          "acquisitionCosts" -> Seq("3500.0")))

        result shouldBe Some(Left("acquisitionValue cannot be negative."))
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

  "Calling chargeableGain Binder" when {
    val binder = new ResidentSharesBinders{}.chargeableGainBinder

    "calling .bind" should {

      "return a valid ChargeableGainModel from a valid map with the same values" in {
        val totalGainModel = TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0)
        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0")))

        result shouldBe Some(Right(ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0)))
      }

      "return a valid ChargeableGainModel from a valid map with different values" in {
        val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
        val result = binder.bind("", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0"),
          "annualExemptAmount" -> Seq("4000.0")))

        result shouldBe Some(Right(ChargeableGainModel(totalGainModel, None, None, 4000.0)))
      }

      "return one error message on a failed bind on all inputs" in {
        val result = binder.bind("", Map("disposalValue" -> Seq("a"),
          "disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("c"),
          "acquisitionCosts" -> Seq("d"),
          "allowableLosses" -> Seq("e"),
          "broughtForwardLosses" -> Seq("f"),
          "annualExemptAmount" -> Seq("g")))

        result shouldBe Some(Left("""Cannot parse parameter disposalValue as Double: For input string: "a""""))
      }

      "return an error message when one component fails" in {
        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0")))

        result shouldBe Some(Left("""Cannot parse parameter disposalCosts as Double: For input string: "b""""))
      }

      "return an error message when a component is missing" in {
        val result = binder.bind("Any", Map("disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("3000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0")))

        result shouldBe Some(Left("disposalValue is required."))
      }

      "return an error message if validation fails" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("-1000.0"),
          "annualExemptAmount" -> Seq("1000.0")))

        result shouldBe Some(Left("broughtForwardLosses cannot be negative."))
      }
    }

    "calling .unBind" should {

      "return a valid queryString on unbind with identical values" in {
        val totalGainString = "disposalValue=1000.0&disposalCosts=1000.0&acquisitionValue=1000.0&acquisitionCosts=1000.0"
        val result = binder.unbind("", ChargeableGainModel(TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0), Some(1000.0), Some(1000.0), 1000.0))

        result shouldBe totalGainString + "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"
      }

      "return a valid queryString on unbind with different values" in {
        val totalGainString = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0"
        val result = binder.unbind("", ChargeableGainModel(TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0), None, None, 4000.0))

        result shouldBe totalGainString + "&annualExemptAmount=4000.0"
      }
    }
  }

  "Calling Calculate Tax Owed binder" when {
    
    val binder = CalculateTaxOwedModel.calculateTaxOwedBinder

    "given a valid binding value where all values are the same" should {
      val totalGainModel = TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0)
      val chargeableGainRequest = "disposalValue=1000.0&disposalCosts=1000.0&acquisitionValue=1000.0&acquisitionCosts=1000.0" +
        "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"



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
        val date = DateTime.parse("2016-10-10")

        result shouldBe Some(Right(CalculateTaxOwedModel(chargeableGainModel, Some(1000.0), 1000.0, 1000.0, date)))
      }

      "return a valid queryString on unbind" in {
        val date = DateTime.parse("2016-10-10")
        val result = binder.unbind("key", CalculateTaxOwedModel(chargeableGainModel, Some(1000.0), 1000.0, 1000.0, date))

        result shouldBe chargeableGainRequest + "&previousTaxableGain=1000.0&previousIncome=1000.0&personalAllowance=1000.0&disposalDate=2016-10-10"
      }
    }

    "given a valid binding value where all values are different" should {
      val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(4000.0), Some(4500.0), 5000.0)
      val chargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0" +
        "&allowableLosses=4000.0&broughtForwardLosses=4500.0&annualExemptAmount=5000.0"


      "return a valid CalculateTaxOwedModel on bind" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0"),
          "allowableLosses" -> Seq("4000.0"),
          "broughtForwardLosses" -> Seq("4500.0"),
          "annualExemptAmount" -> Seq("5000.0"),
          "previousIncome" -> Seq("4000.0"),
          "personalAllowance" -> Seq("4000.0"),
          "disposalDate" -> Seq("2016-10-10")
        ))

        val date = DateTime.parse("2016-10-10")
        result shouldBe Some(Right(CalculateTaxOwedModel(chargeableGainModel, None, 4000.0, 4000.0, date)))
      }

      "return a valid queryString on unbind" in {
        val date = DateTime.parse("2016-10-10")
        val result = binder.unbind("key", CalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.0, 5500.0, date))

        result shouldBe chargeableGainRequest + "&previousTaxableGain=4500.0&previousIncome=5000.0&personalAllowance=5500.0&disposalDate=2016-10-10"
      }
    }

    "given an invalid binding value" should {

      "return one error message on a failed bind on all inputs" in {
        val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
        val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0)
        val chargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0" +
          "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"

        val result = binder.bind("", Map("disposalValue" -> Seq("a"),
          "disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("c"),
          "acquisitionCosts" -> Seq("d"),
          "allowableLosses" -> Seq("e"),
          "broughtForwardLosses" -> Seq("f"),
          "annualExemptAmount" -> Seq("g"),
          "previousTaxableGain" -> Seq("h"),
          "previousIncome" -> Seq("i"),
          "personalAllowance" -> Seq("j"),
          "disposalDate" -> Seq("k")
        ))

        result shouldBe Some(Left("""Cannot parse parameter disposalValue as Double: For input string: "a""""))
      }


      "return an error message when one component fails" in {
        val totalGainModel = TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0)
        val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(1000.0), Some(1000.0), 1000.0)
        val chargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0" +
          "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"

        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0"),
          "previousTaxableGain" -> Seq("1000.0"),
          "previousIncome" -> Seq("1000.0"),
          "personalAllowance" -> Seq("1000.0"),
          "disposalDate" -> Seq("k")
        ))

        result shouldBe Some(Left("""Cannot parse parameter disposalDate as DateTime: For input string: "k""""))
      }

      "return an error message when a component is missing" in {

        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("3000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0"),
          "previousTaxableGain" -> Seq("1000.0"),
          "previousIncome" -> Seq("1000.0"),
          "disposalDate" -> Seq("k")))

        result shouldBe Some(Left("personalAllowance is required."))
      }

      "return an error message when model fails validation" in {
        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("-1000.0"),
          "annualExemptAmount" -> Seq("1000.0"),
          "previousTaxableGain" -> Seq("1000.0"),
          "previousIncome" -> Seq("1000.0"),
          "personalAllowance" -> Seq("1000.0"),
          "disposalDate" -> Seq("1000.0")
        ))

        result shouldBe Some(Left("broughtForwardLosses cannot be negative."))
      }
    }
  }
}
