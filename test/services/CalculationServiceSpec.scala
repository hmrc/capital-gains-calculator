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

package services

import java.util.Date

import models.{DateModel, CalculationResultModel}
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatest._

class CalculationServiceSpec extends UnitSpec {

  "Calling CalculationService.annualExemptAmount" should {

    "return a value of 11100 when Person is Individual and has no prior disposal for year" in {
      val result = CalculationService.calculateAEA("individual", "No")
      result shouldEqual 11100
    }

    "return a value of 2000 (remaining AEA input) when Person is Individual and has a prior disposal for year" in {
      val result = CalculationService.calculateAEA("individual", "Yes", Some(2000))
      result shouldEqual 2000
    }

    "return a value of 11100 when Person is PR and has no prior disposal for year" in {
      val result = CalculationService.calculateAEA("personalRep", "No")
      result shouldEqual 11100
    }

    "return a value of 1500 (remaining AEA input) when Person is PR and has a prior disposal for year" in {
      val result = CalculationService.calculateAEA("personalRep", "Yes", Some(1500))
      result shouldEqual 1500
    }

    "return a value of 11100 when Person is Trustee for Vulnerable Person and has no prior disposal for year" in {
      val result = CalculationService.calculateAEA("trustee", "No", None, Some("Yes"))
      result shouldEqual 11100
    }

    "return a value of 5550 when Person is other Trustee and has no prior disposal for year" in {
      val result = CalculationService.calculateAEA("trustee", "No")
      result shouldEqual 5550
    }

    "return a value of 1000 (remaining AEA input) when Person is Trustee for Vulnerable Person and has a prior disposal for year" in {
      val result = CalculationService.calculateAEA("trustee", "Yes", Some(1000), Some("Yes"))
      result shouldEqual 1000
    }

    "return a value of 500 (remaining AEA input) when Person is other Trustee and has a prior disposal for year" in {
      val result = CalculationService.calculateAEA("trustee", "Yes", Some(500), Some("No"))
      result shouldEqual 500
    }
  }


  "Calling CalculationService.round" should {

    "return rounded down whole number of 1000 for value of 1000.01 for UK Taxable Income" in {
      val result = CalculationService.round("down", 1000.01)
      result shouldEqual 1000
    }

    "return rounded down whole number of 1000 for value of 1000.99 for Disposable Proceeds" in {
      val result = CalculationService.round("down", 1000.99)
      result shouldEqual 1000
    }

    "return rounded up whole number of 101 for value of 100.01 for AEA remaining" in {
      val result = CalculationService.round("up", 100.01)
      result shouldEqual 101
    }

    "return rounded up whole number of 100 for value of 99.01 for Acquisition cost" in {
      val result = CalculationService.round("up", 99.01)
      result shouldEqual 100
    }

    "return rounded up whole number of 90 for value of 89.49 for Incidental Acquisition cost" in {
      val result = CalculationService.round("up", 89.49)
      result shouldEqual 90
    }

    "return rounded up whole number of 90 for value of 41 for Enhancement costs" in {
      val result = CalculationService.round("up", 40.01)
      result shouldEqual 41
    }

    "return rounded up whole number of 91 for value of 90.01 for Incidental Disposal cost" in {
      val result = CalculationService.round("up", 90.01)
      result shouldEqual 91
    }

    "return rounded up whole number of 55 for value of 54.49 for Allowable Losses" in {
      val result = CalculationService.round("up", 54.49)
      result shouldEqual 55
    }

    "return rounded up whole number of 31 for value of 30.01 for Reliefs" in {
      val result = CalculationService.round("up", 30.01)
      result shouldEqual 31
    }

    "return rounded down decimal of 5000.87 for value of 5000.87666 for Flat Rate output" in {
      val result = CalculationService.round("result", 5000.87666)
      result shouldEqual 5000.87
    }

    "return rounded down decimal of 5000.99 for value of 5000.9999 for Flat Rate output" in {
      val result = CalculationService.round("result", 5000.9999)
      result shouldEqual 5000.99
    }
  }


  "Calling CalculationService.gain" should {

    "return the total Gain value of 4700 where Disposal Proceeds = 10000, Incidental Disposal Costs = 2000, Acquisition Cost = 1000, " +
    "Incidental Acquisition Costs = 300, Enhancement Costs = 2000" in {
      val result = CalculationService.calculateGain(10000, 2000, 1000, 300, 2000)
      result shouldEqual 4700
    }
  }


  "Calling CalculationService.chargeableGain" should {

    "the total Chargeable Gain value of 4550 where total Gain = 5000, Relief = 200, In Year Losses = 150, AEA = 100" in {
      val result = CalculationService.calculateChargeableGain(5000, 200, 150, 100)
      result shouldEqual 4550
    }
  }


  "Calling CalculationService.brRemaining" should {

    "return a value of 32000 when Individual has Income of 8000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(8000, 11000)
      result shouldEqual 32000
    }

    "return a value of 0 when Individual has Income of 50000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(50000, 11000)
      result shouldEqual 0
    }

    "return a value of 3000 when Individual has Income of 50000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(40000, 11000)
      result shouldEqual 3000
    }

    "return a value of 0 when Individual has Income of 33000 and a PA of 1000" in {
      val result = CalculationService.brRemaining(33000, 1000)
      result shouldEqual 0
    }

    "return a value of 0 when Individual has Income of 33001 and a PA of 1000" in {
      val result = CalculationService.brRemaining(33001, 1000)
      result shouldEqual 0
    }

    "return a value of 1 when Individual has Income of 32999 and a PA of 1000" in {
      val result = CalculationService.brRemaining(32999, 1000)
      result shouldEqual 1
    }
  }

  "calling CalculationService.negativeToZero" should {

    "return 0 when -0.01 is supplied" in {
      CalculationService.negativeToZero(-0.01) shouldEqual 0
    }
    "return 0 when 0.00 is supplied" in {
      CalculationService.negativeToZero(0.00) shouldEqual 0
    }
    "return 0.01 when 0.01 is supplied" in {
      CalculationService.negativeToZero(0.01) shouldEqual 0.01
    }
  }

  "calling CalculationService.calculateCapitalGainsTax" should {

    "return £1,000 for an Individual, claiming ER, with a taxable gain of £10,000 charged at 10%" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "individual",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        isVulnerable = None,
        currentIncome = Some(30000),
        personalAllowanceAmt = Some(11000),
        disposalValue = 21100,
        disposalCosts = 0,
        acquisitionValueAmt = 0,
        acquisitionCostsAmt = 0,
        improvementsAmt = 0,
        reliefs = 0,
        allowableLossesAmt = 0,
        entReliefClaimed = "Yes"
      )

      "have tax owed of £1,000" in {
        result.taxOwed shouldEqual 1000
      }

      "have the total gain £21,100" in {
        result.totalGain shouldEqual 21100
      }

      "have the base tax gain of £10,000" in {
        result.baseTaxGain shouldEqual 10000
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 10
      }

      "have the upper tax gain of £5000" in {
        result.upperTaxGain shouldEqual None
      }

      "have the upper tax rate of 28%" in {
        result.upperTaxRate shouldEqual None
      }
    }

    "return £2,300 for an Individual, not claiming ER, with a lower and higher rate income and a taxable gain of £10,000, " +
      "£5,000 of which charged at 18% and the remaining at 28%" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "individual",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        isVulnerable = None,
        currentIncome = Some(38000),
        personalAllowanceAmt = Some(11000),
        disposalValue = 21100,
        disposalCosts = 0,
        acquisitionValueAmt = 0,
        acquisitionCostsAmt = 0,
        improvementsAmt = 0,
        reliefs = 0,
        allowableLossesAmt = 0,
        entReliefClaimed = "No"
      )

      "have tax owed of £2,300" in {
        result.taxOwed shouldEqual 2300
      }

      "have the total gain £21,100" in {
        result.totalGain shouldEqual 21100
      }

      "have the base tax gain of £5,000" in {
        result.baseTaxGain shouldEqual 5000
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 18
      }

      "have the upper tax gain of £5,000" in {
        result.upperTaxGain shouldEqual Some(5000)
      }

      "have the upper tax rate of 28%" in {
        result.upperTaxRate shouldEqual Some(28)
      }
    }

    "return £1,800 for an Individual, not claiming ER, with a lower rate income and a taxable gain of £10,000 " +
      "charged at 18%" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "individual",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        isVulnerable = None,
        currentIncome = Some(7000),
        personalAllowanceAmt = Some(11000),
        disposalValue = 21100,
        disposalCosts = 0,
        acquisitionValueAmt = 0,
        acquisitionCostsAmt = 0,
        improvementsAmt = 0,
        reliefs = 0,
        allowableLossesAmt = 0,
        entReliefClaimed = "No"
      )

      "have tax owed of £1,800" in {
        result.taxOwed shouldEqual 1800
      }

      "have the total gain £21,100" in {
        result.totalGain shouldEqual 21100
      }

      "have the base tax gain of £10,000" in {
        result.baseTaxGain shouldEqual 10000
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 18
      }

      "have the upper tax gain of None" in {
        result.upperTaxGain shouldEqual None
      }

      "have the upper tax rate of None" in {
        result.upperTaxRate shouldEqual None
      }
    }

    "return £2,800 for an Individual, not claiming ER, with a higher rate income and a taxable gain of £10,000 " +
      "charged at 28%" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "individual",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        isVulnerable = None,
        currentIncome = Some(50000),
        personalAllowanceAmt = Some(11000),
        disposalValue = 21100,
        disposalCosts = 0,
        acquisitionValueAmt = 0,
        acquisitionCostsAmt = 0,
        improvementsAmt = 0,
        reliefs = 0,
        allowableLossesAmt = 0,
        entReliefClaimed = "No"
      )

      "have tax owed of £2,800" in {
        result.taxOwed shouldEqual 2800
      }

      "have the total gain £21,100" in {
        result.totalGain shouldEqual 21100
      }

      "have the base tax gain of 0" in {
        result.baseTaxGain shouldEqual 0
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 18
      }

      "have the upper tax gain of £10,000" in {
        result.upperTaxGain shouldEqual Some(10000)
      }

      "have the upper tax rate of 28%" in {
        result.upperTaxRate shouldEqual Some(28)
      }
    }

    "return £2,800 for a PR, not claiming ER, with taxable gain of £10,000 charged at 28%" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "personalRep",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        isVulnerable = None,
        currentIncome = None,
        personalAllowanceAmt = None,
        disposalValue = 21100,
        disposalCosts = 0,
        acquisitionValueAmt = 0,
        acquisitionCostsAmt = 0,
        improvementsAmt = 0,
        reliefs = 0,
        allowableLossesAmt = 0,
        entReliefClaimed = "No"
      )

      "have tax owed of £2,800" in {
        result.taxOwed shouldEqual 2800
      }

      "have the total gain £21,100" in {
        result.totalGain shouldEqual 21100
      }

      "have the base tax gain of 0" in {
        result.baseTaxGain shouldEqual 0
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 18
      }

      "have the upper tax gain of £10,000" in {
        result.upperTaxGain shouldEqual Some(10000)
      }

      "have the upper tax rate of 28%" in {
        result.upperTaxRate shouldEqual Some(28)
      }
    }

    "return £4,354 for a Trustee for non-vulnerable person, not claiming ER, with taxable gain of £15,550 " +
      "charged at 28%" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "trustee",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        isVulnerable = Some("No"),
        currentIncome = None,
        personalAllowanceAmt = None,
        disposalValue = 21100,
        disposalCosts = 0,
        acquisitionValueAmt = 0,
        acquisitionCostsAmt = 0,
        improvementsAmt = 0,
        reliefs = 0,
        allowableLossesAmt = 0,
        entReliefClaimed = "No"
      )

      "have tax owed of £4,354" in {
        result.taxOwed shouldEqual 4354
      }

      "have the total gain £21,100" in {
        result.totalGain shouldEqual 21100
      }

      "have the base tax gain of 0" in {
        result.baseTaxGain shouldEqual 0
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 18
      }

      "have the upper tax gain of £15,550" in {
        result.upperTaxGain shouldEqual Some(15550)
      }

      "have the upper tax rate of 28%" in {
        result.upperTaxRate shouldEqual Some(28)
      }
    }

    "return £2,800 for a Trustee for vulnerable person, not claiming ER, with a taxable gain of £10,000 " +
      "charged at 28%" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "trustee",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        isVulnerable = Some("Yes"),
        currentIncome = None,
        personalAllowanceAmt = None,
        disposalValue = 21100,
        disposalCosts = 0,
        acquisitionValueAmt = 0,
        acquisitionCostsAmt = 0,
        improvementsAmt = 0,
        reliefs = 0,
        allowableLossesAmt = 0,
        entReliefClaimed = "No"
      )

      "have tax owed of £2,800" in {
        result.taxOwed shouldEqual 2800
      }

      "have the total gain £21,100" in {
        result.totalGain shouldEqual 21100
      }

      "have the base tax gain of 0" in {
        result.baseTaxGain shouldEqual 0
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 18
      }

      "have the upper tax gain of £10,000" in {
        result.upperTaxGain shouldEqual Some(10000)
      }

      "have the upper tax rate of 28%" in {
        result.upperTaxRate shouldEqual Some(28)
      }
    }

    "return £0 for an Individual not claiming ER, with a lower rate income and a taxable gain of -£10,000" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "individual",
        priorDisposal = "No",
        annualExemptAmount = Some(0),
        isVulnerable = None,
        currentIncome = None,
        personalAllowanceAmt = None,
        disposalValue = 1100,
        disposalCosts = 0,
        acquisitionValueAmt = 0,
        acquisitionCostsAmt = 0,
        improvementsAmt = 0,
        reliefs = 0,
        allowableLossesAmt = 0,
        entReliefClaimed = "No"
      )

      "have tax owed of £0" in {
        result.taxOwed shouldEqual 0
      }

      "have the total gain £1,100" in {
        result.totalGain shouldEqual 1100
      }

      "have the base tax gain of -£10,000" in {
        result.baseTaxGain shouldEqual -10000
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 18
      }

      "have the upper tax gain of None" in {
        result.upperTaxGain shouldEqual None
      }

      "have the upper tax rate of None" in {
        result.upperTaxRate shouldEqual None
      }
    }

    "return £6,331.64 for an Individual not claiming ER, with a higher rate income and taxable gain of £44,615 charged " +
      "at 28%" should {
      val result = CalculationService.calculateCapitalGainsTax(
        customerType = "individual",
        priorDisposal = "Yes",
        annualExemptAmount = Some(5000),
        isVulnerable = None,
        currentIncome = Some(50000),
        personalAllowanceAmt = Some(11000),
        disposalValue = 124000.68,
        disposalCosts = 1241.22,
        acquisitionValueAmt = 65000.50,
        acquisitionCostsAmt = 1105.53,
        improvementsAmt = 12035.99,
        reliefs = 14000.11,
        allowableLossesAmt = 3000.01,
        entReliefClaimed = "No"
      )

      "have tax owed of £6,331.64" in {
        result.taxOwed shouldEqual 6331.64
      }

      "have the total gain £44,615" in {
        result.totalGain shouldEqual 44615
      }

      "have the base tax gain of £0" in {
        result.baseTaxGain shouldEqual 0
      }

      "have the base tax rate of 18%" in {
        result.baseTaxRate shouldEqual 18
      }

      "have the upper tax gain of £22,613" in {
        result.upperTaxGain shouldEqual Some(22613)
      }

      "have the upper tax rate of 28%" in {
        result.upperTaxRate shouldEqual Some(28)
      }
    }
  }
}