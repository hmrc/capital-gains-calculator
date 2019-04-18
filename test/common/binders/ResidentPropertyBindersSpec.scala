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
import models.resident.properties.{PropertyCalculateTaxOwedModel, PropertyChargeableGainModel, PropertyTotalGainModel}
import models.resident.shares.TotalGainModel
import org.joda.time.DateTime
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec

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

  "Calling Property Chargeable Gain binder " when {

    val binder = new ResidentPropertyBinders {}.propertyChargeableGainBinder

    "calling .bind" should {

      "return a valid PropertyTotalGainModel from a valid map with the same values" in {
        val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(2000, 2000, 2000, 2000), 2000)
        val result = binder.bind("", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2000.0"),
          "acquisitionValue" -> Seq("2000.0"),
          "acquisitionCosts" -> Seq("2000.0"),
          "improvements" -> Seq("2000.0"),
          "prrValue" -> Seq("2000.0"),
          "lettingReliefs" -> Seq("2000.0"),
          "allowableLosses" -> Seq("2000.0"),
          "broughtForwardLosses" -> Seq("2000.0"),
          "annualExemptAmount" -> Seq("2000.0"),
          "disposalDate" -> Seq("2016-10-10")))

        val date = DateTime.parse("2016-10-10")
        result shouldBe Some(Right(PropertyChargeableGainModel(propertyTotalGainModel, Some(2000.0), Some(2000.0), Some(2000.0),
                                  Some(2000.0), 2000, date)))
      }

      "return a valid PropertyTotalGainModel from a valid map with different values" in {
        val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(2000, 2500, 3000, 3500), 1500)
        val result = binder.bind("", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0"),
          "improvements" -> Seq("1500.0"),
          "prrValue" -> Seq("2050.0"),
          "lettingReliefs" -> Seq("2100.0"),
          "allowableLosses" -> Seq("2200.0"),
          "broughtForwardLosses" -> Seq("2300.0"),
          "annualExemptAmount" -> Seq("2400.0"),
          "disposalDate" -> Seq("2016-10-10")))

        val date = DateTime.parse("2016-10-10")
        result shouldBe Some(Right(PropertyChargeableGainModel(propertyTotalGainModel, Some(2050.0), Some(2100.0), Some(2200.0),
          Some(2300.0), 2400.0, date)))

      }

      "return an error message when one component fails" in {
        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("b"),
          "acquisitionCosts" -> Seq("1000.0"),
          "improvements" -> Seq("1000.0"),
          "prrValue" -> Seq("2000.0"),
          "lettingReliefs" -> Seq("2000.0"),
          "allowableLosses" -> Seq("2000.0"),
          "broughtForwardLosses" -> Seq("2000.0"),
          "annualExemptAmount" -> Seq("2000.0"),
          "disposalDate" -> Seq("2016-10-10")))

        result shouldBe Some(Left("""Cannot parse parameter acquisitionValue as Double: For input string: "b""""))
      }

      "return an error message when a component is missing" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "improvements" -> Seq("1000.0"),
          "prrValue" -> Seq("2000.0"),
          "lettingReliefs" -> Seq("2000.0"),
          "allowableLosses" -> Seq("2000.0"),
          "broughtForwardLosses" -> Seq("2000.0"),
          "annualExemptAmount" -> Seq("2000.0"),
          "disposalDate" -> Seq("2016-10-10")))

        result shouldBe Some(Left("acquisitionCosts is required."))
      }

      "return an error message when a value fails validation" in {
        val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(2000, 2500, 3000, 3500), 1500)
        val result = binder.bind("", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0"),
          "improvements" -> Seq("1500.0"),
          "prrValue" -> Seq("-2050.0"),
          "lettingReliefs" -> Seq("2100.0"),
          "allowableLosses" -> Seq("2200.0"),
          "broughtForwardLosses" -> Seq("2300.0"),
          "annualExemptAmount" -> Seq("2400.0"),
          "disposalDate" -> Seq("2016-10-10")))

        result shouldBe Some(Left("prrValue cannot be negative."))
      }
    }

    "calling .unBind" should {

      "return a valid queryString on unbind with identical values" in {
        val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(2000.0, 2000.0, 2000.0, 2000.0), 2000.0)
        val date = DateTime.parse("2016-10-10")
        val propertyChargeableGainModel = new PropertyChargeableGainModel(propertyTotalGainModel, Some(2000.0), Some(2000.0), Some(2000.0),
          Some(2000.0), 2000.0, date)

        val propertyTotalGainString = "disposalValue=2000.0&disposalCosts=2000.0&acquisitionValue=2000.0&acquisitionCosts=2000.0&improvements=2000.0"
        val result = binder.unbind("", propertyChargeableGainModel)

        result shouldBe propertyTotalGainString + "&prrValue=2000.0&lettingReliefs=2000.0&allowableLosses=2000.0&broughtForwardLosses=2000.0" +
          "&annualExemptAmount=2000.0&disposalDate=2016-10-10"
      }

      "return a valid queryString on unbind with different values" in {
        val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(2000.0, 1500.0, 1750.0, 3000.0), 2500.0)
        val date = DateTime.parse("2016-10-10")
        val propertyChargeableGainModel = new PropertyChargeableGainModel(propertyTotalGainModel, Some(2050.0), Some(2100.0), Some(2200.0),
          Some(2300.0), 2400.0, date)

        val propertyTotalGainString = "disposalValue=2000.0&disposalCosts=1500.0&acquisitionValue=1750.0&acquisitionCosts=3000.0&improvements=2500.0"
        val result = binder.unbind("", propertyChargeableGainModel)

        result shouldBe propertyTotalGainString + "&prrValue=2050.0&lettingReliefs=2100.0&allowableLosses=2200.0&broughtForwardLosses=2300.0" +
          "&annualExemptAmount=2400.0&disposalDate=2016-10-10"
      }
    }
  }

  "Calling Property Calculate Tax Owed binder" when {

    val binder = new ResidentPropertyBinders {}.propertyCalculateTaxOwedBinder

    "given a valid binding value where all values are the same" should {
      val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(1000.0, 1000.0, 1000.0, 1000.0), 1000.0)
      val date = DateTime.parse("2016-10-10")
      val propertyChargeableGainModel = PropertyChargeableGainModel(propertyTotalGainModel, Some(1000.0), Some(1000.0), Some(1000.0), Some(1000.0),
        1000.0, date)
      val propertyChargeableGainRequest = "disposalValue=1000.0&disposalCosts=1000.0&acquisitionValue=1000.0&acquisitionCosts=1000.0&improvements=1000.0" +
        "&prrValue=1000.0&lettingReliefs=1000.0&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0&disposalDate=2016-10-10"



      "return a valid PropertyCalculateTaxOwedModel on bind" in {
        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "improvements" -> Seq("1000.0"),
          "prrValue" -> Seq("1000.0"),
          "lettingReliefs" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0"),
          "disposalDate" -> Seq("2016-10-10"),
          "previousTaxableGain" -> Seq("1000.0"),
          "previousIncome" -> Seq("1000.0"),
          "personalAllowance" -> Seq("1000.0")
        ))
        val date = DateTime.parse("2016-10-10")

        result shouldBe Some(Right(PropertyCalculateTaxOwedModel(propertyChargeableGainModel, Some(1000.0), 1000.0, 1000.0)))
      }

      "return a valid queryString on unbind" in {
        val date = DateTime.parse("2016-10-10")
        val result = binder.unbind("key", PropertyCalculateTaxOwedModel(propertyChargeableGainModel, Some(1000.0), 1000.0, 1000.0))

        result shouldBe propertyChargeableGainRequest + "&previousTaxableGain=1000.0&previousIncome=1000.0&personalAllowance=1000.0"
      }
    }

    "given a valid binding value where all values are different" should {
      val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0),6000.0)
      val date = DateTime.parse("2016-10-10")
      val propertyChargeableGainModel = PropertyChargeableGainModel(propertyTotalGainModel, Some(4000.0), Some(4500.0), Some(5000.0),
        Some(5500.0), 6000.0, date)
      val propertyChargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0&improvements=6000.0" +
        "&prrValue=4000.0&lettingReliefs=4500.0&allowableLosses=5000.0&broughtForwardLosses=5500.0&annualExemptAmount=6000.0&disposalDate=2016-10-10"


      "return a valid CalculateTaxOwedModel on bind" in {
        val result = binder.bind("Any", Map("disposalValue" -> Seq("2000.0"),
          "disposalCosts" -> Seq("2500.0"),
          "acquisitionValue" -> Seq("3000.0"),
          "acquisitionCosts" -> Seq("3500.0"),
          "improvements" -> Seq("6000.0"),
          "prrValue" -> Seq("4000.0"),
          "lettingReliefs" -> Seq("4500.0"),
          "allowableLosses" -> Seq("5000.0"),
          "broughtForwardLosses" -> Seq("5500.0"),
          "annualExemptAmount" -> Seq("6000.0"),
          "disposalDate" -> Seq("2016-10-10"),
          "previousIncome" -> Seq("4000.0"),
          "personalAllowance" -> Seq("4000.0")
        ))

        val date = DateTime.parse("2016-10-10")
        result shouldBe Some(Right(PropertyCalculateTaxOwedModel(propertyChargeableGainModel, None, 4000.0, 4000.0)))
      }

      "return a valid queryString on unbind" in {
        val date = DateTime.parse("2016-10-10")
        val result = binder.unbind("key", PropertyCalculateTaxOwedModel(propertyChargeableGainModel, Some(4500.0), 5000.0, 5500.0))

        result shouldBe propertyChargeableGainRequest + "&previousTaxableGain=4500.0&previousIncome=5000.0&personalAllowance=5500.0"
      }
    }

    "given an invalid binding value" should {

      "return one error message on a failed bind on all inputs" in {
        val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0), 6000.0)
        val date = DateTime.parse("2016-10-10")
        val propertyChargeableGainModel = PropertyChargeableGainModel(propertyTotalGainModel, Some(1000.0), Some(1000.0), Some(1000.0),
          Some(1000.0), 1000.0, date)
        val propertyChargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0" +
          "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"

        val result = binder.bind("", Map("disposalValue" -> Seq("a"),
          "disposalCosts" -> Seq("b"),
          "acquisitionValue" -> Seq("c"),
          "acquisitionCosts" -> Seq("d"),
          "improvements" -> Seq("e"),
          "prrValue" -> Seq("f"),
          "lettingReliefs" -> Seq("g"),
          "allowableLosses" -> Seq("h"),
          "broughtForwardLosses" -> Seq("i"),
          "annualExemptAmount" -> Seq("j"),
          "disposalDate" -> Seq("k"),
          "previousTaxableGain" -> Seq("l"),
          "previousIncome" -> Seq("m"),
          "personalAllowance" -> Seq("n")
        ))

        result shouldBe Some(Left("""Cannot parse parameter disposalValue as Double: For input string: "a""""))
      }


      "return an error message when one component fails" in {
        val propertyTotalGainModel = new PropertyTotalGainModel(TotalGainModel(2000.0, 2500.0, 3000.0, 3500.0), 6000.0)
        val date = DateTime.parse("2016-10-10")
        val propertyChargeableGainModel = PropertyChargeableGainModel(propertyTotalGainModel, Some(1000.0), Some(1000.0), Some(1000.0),
          Some(1000.0), 1000.0, date)
        val propertyChargeableGainRequest = "disposalValue=2000.0&disposalCosts=2500.0&acquisitionValue=3000.0&acquisitionCosts=3500.0" +
          "&allowableLosses=1000.0&broughtForwardLosses=1000.0&annualExemptAmount=1000.0"

        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "improvements" -> Seq("1000.0"),
          "prrValue" -> Seq("1000.0"),
          "lettingReliefs" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0"),
          "disposalDate" -> Seq("2016-10-10"),
          "previousTaxableGain" -> Seq("1000.0"),
          "previousIncome" -> Seq("1000.0"),
          "personalAllowance" -> Seq("n")
        ))

        result shouldBe Some(Left("""Cannot parse parameter personalAllowance as Double: For input string: "n""""))
      }

      "return an error message when a component is missing" in {

        val result = binder.bind("Any", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "improvements" -> Seq("1000.0"),
          "prrValue" -> Seq("1000.0"),
          "lettingReliefs" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0"),
          "disposalDate" -> Seq("2016-10-10"),
          "previousTaxableGain" -> Seq("1000.0"),
          "personalAllowance" -> Seq("n")
        ))

        result shouldBe Some(Left("previousIncome is required."))
      }

      "return an error message when model fails validation" in {
        val result = binder.bind("", Map("disposalValue" -> Seq("1000.0"),
          "disposalCosts" -> Seq("1000.0"),
          "acquisitionValue" -> Seq("1000.0"),
          "acquisitionCosts" -> Seq("1000.0"),
          "improvements" -> Seq("1000.0"),
          "prrValue" -> Seq("1000.0"),
          "lettingReliefs" -> Seq("1000.0"),
          "allowableLosses" -> Seq("1000.0"),
          "broughtForwardLosses" -> Seq("1000.0"),
          "annualExemptAmount" -> Seq("1000.0"),
          "disposalDate" -> Seq("2016-10-10"),
          "previousTaxableGain" -> Seq("1000.0"),
          "previousIncome" -> Seq("1000.0"),
          "personalAllowance" -> Seq("-1000.0")
        ))

        result shouldBe Some(Left("personalAllowance cannot be negative."))
      }

    }
  }
}
