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

import uk.gov.hmrc.play.test.UnitSpec

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

  "Calling CalculationService.calculateGainFlat" should {

    "return the total Gain value of 4700 where Disposal Proceeds = 10000, Incidental Disposal Costs = 2000, Acquisition Cost = 1000, " +
      "Incidental Acquisition Costs = 300, Enhancement Costs = 2000" in {
      val result = CalculationService.calculateGainFlat(10000, 2000, 1000, 300, 2000)
      result shouldEqual 4700
    }
  }

  "Calling CalculationService.calculateGainTA" should {

    "return 2500 where Disposal Proceeds = 10000, Incidental Disposal Costs = 2000, Acquisition Cost = 1000," +
      "Incidental Acquisition Costs = 0, Enhancement Costs = 2000, Acquisition Date 05/04/2015, Disposal Date 06/04/2015" in {
      val result = CalculationService.calculateGainTA(10000, 2000, 1000, 0, 2000, "2015-04-05", "2015-04-06")
      result shouldEqual 2500
    }

    "return 3000 where Disposal Proceeds = 12,645.77, Incidental Disposal Costs = 1954.66, Acquisition Cost = 1000.04," +
      "Incidental Acquisition Costs = 0.99, Enhancement Costs = 2000.65, Acquisition Date 05/04/1967, Disposal Date 31/07/2016" in {
      val result = CalculationService.calculateGainTA(12645.77, 1954.66, 1000.04, 0.99, 2000.65, "1967-04-05", "2016-07-31")
      result shouldEqual 206.08
    }
  }

  "Calling CalculationService.calculateGainRebased" should {

    "return the total Gain value of 4700 where Disposal Proceeds = 10000, Incidental Disposal Costs = 2000, Rebased Value = 1000, " +
      "Revaluation Costs = 300, Enhancement Costs = 2000" in {
      val result = CalculationService.calculateGainRebased(10000, 2000, 1000, 300, 2000)
      result shouldEqual 4700
    }
  }

  "Calling CalculationService.calculateChargeableGain" should {

    "the total Chargeable Gain value of 4550 where total Gain = 5000, Relief = 200, In Year Losses = 150, AEA = 100" in {
      val result = CalculationService.calculateChargeableGain(5000, 200, 150, 100)
      result shouldEqual 4550
    }
  }

  "Calling CalculationService.brRemaining" should {

    "return a value of 32000 when Individual has Income of 8000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(8000, 11000, 0)
      result shouldEqual 32000
    }

    "return a value of 0 when Individual has Income of 50000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(50000, 11000, 0)
      result shouldEqual 0
    }

    "return a value of 3000 when Individual has Income of 50000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(40000, 11000, 0)
      result shouldEqual 3000
    }

    "return a value of 0 when Individual has Income of 33000 and a PA of 1000" in {
      val result = CalculationService.brRemaining(33000, 1000, 0)
      result shouldEqual 0
    }

    "return a value of 0 when Individual has Income of 33001 and a PA of 1000" in {
      val result = CalculationService.brRemaining(33001, 1000, 0)
      result shouldEqual 0
    }

    "return a value of 1 when Individual has Income of 32999 and a PA of 1000" in {
      val result = CalculationService.brRemaining(32999, 1000, 0)
      result shouldEqual 1
    }

    "return a value of 16000 when Individual has income of 8000, PA of 11000 and previous gain of 16000" in {
      val result = CalculationService.brRemaining(8000, 11000, 16000)
      result shouldEqual 16000
    }

    "return a value of 0 when Individual has Income of 50000, PA of 11000 and previous gain of 16000" in {
      val result = CalculationService.brRemaining(50000, 11000, 16000)
      result shouldEqual 0
    }

    "return a value of 0 when Individual has income of 8000, PA of 11000 and previous gain of 32000" in {
      val result = CalculationService.brRemaining(8000, 11000, 32000)
      result shouldEqual 0
    }
  }

  "calling CalculationService.calculateCapitalGainsTax" should {

    //########### Flat Rate Tests ##########################
    "when using the Flat Rate calculation method" should {

      "return £1,000 for an Individual, claiming ER, with a taxable gain of £10,000 charged at 10%" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(30000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 21100,
          disposalCosts = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(38000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(7000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(50000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "personalRep",
          priorDisposal = "No",
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "trustee",
          priorDisposal = "No",
          isVulnerable = Some("No"),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "trustee",
          priorDisposal = "No",
          isVulnerable = Some("Yes"),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(0),
          personalAllowanceAmt = Some(0),
          disposalValue = 1100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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

        "have the base tax gain of 0.0" in {
          result.baseTaxGain shouldEqual 0.0
        }

        "have the base tax rate of 0%" in {
          result.baseTaxRate shouldEqual 0
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "Yes",
          annualExemptAmount = Some(5000),
          currentIncome = Some(50000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 124000.68,
          disposalCosts = 1241.22,
          revaluedAmount = 0,
          revaluationCost = 0,
          acquisitionValueAmt = 65000.50,
          acquisitionCostsAmt = 1105.53,
          improvementsAmt = 12035.99,
          reliefs = 14000.11,
          allowableLossesAmt = 3000.01,
          entReliefClaimed = "No"
        )

        "have tax owed of £6,331.92" in {
          result.taxOwed shouldEqual 6331.92
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

        "have the upper tax gain of £22,614.0" in {
          result.upperTaxGain shouldEqual Some(22614.0)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }
      }
    }

    //########### Rebased Tests ##########################
    "when using the Rebased calculation method" should {

      "return £1,000 for an Individual, claiming ER, with a taxable gain of £10,000 charged at 10%" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "rebased",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(30000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 21100,
          disposalCosts = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(38000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(7000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(50000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "personalRep",
          priorDisposal = "No",
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "trustee",
          priorDisposal = "No",
          isVulnerable = Some("No"),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "trustee",
          priorDisposal = "No",
          isVulnerable = Some("Yes"),
          disposalValue = 21100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(0),
          personalAllowanceAmt = Some(0),
          disposalValue = 1100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          improvementsAmt = 0,
          reliefs = 0,
          allowableLossesAmt = 0,
          entReliefClaimed = "No"
        )

        val testService = new CalculationService {
          override def calculateGainFlat(disposalValue: Double, disposalCosts: Double, acquisitionValueAmt: Double,
                                         acquisitionCostsAmt: Double, improvementsAmt: Double) = 1100.00
        }

        "have tax owed of £0" in {
          result.taxOwed shouldEqual 0
        }

        "have the total gain £1,100" in {
          result.totalGain shouldEqual 1100
        }

        "have the base tax gain of 0.0" in {
          result.baseTaxGain shouldEqual 0.0
        }

        "have the base tax rate of 0%" in {
          result.baseTaxRate shouldEqual 0
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
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "Yes",
          annualExemptAmount = Some(5000),
          currentIncome = Some(50000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 124000.68,
          disposalCosts = 1241.22,
          revaluedAmount = 0,
          revaluationCost = 0,
          acquisitionValueAmt = 65000.50,
          acquisitionCostsAmt = 1105.53,
          improvementsAmt = 12035.99,
          reliefs = 14000.11,
          allowableLossesAmt = 3000.01,
          entReliefClaimed = "No"
        )

        "have tax owed of £6,331.92" in {
          result.taxOwed shouldEqual 6331.92
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

        "have the upper tax gain of £22,614.0" in {
          result.upperTaxGain shouldEqual Some(22614.0)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }
      }
    }

    //########### Time Apportioned Tests ##########################
    "when using the Time apportioned calculation method" should {

      "for an Individual disposing on 01/01/2016, acquired=01/04/2014, Income=30,000, PA=11,000 and net disposal proceeds of 31,100 claiming ER" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "time",
          customerType = "individual",
          priorDisposal = "No",
          currentIncome = Some(30000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 31100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          improvementsAmt = 0,
          reliefs = 0,
          allowableLossesAmt = 0,
          entReliefClaimed = "Yes",
          acquisitionDate = Some("2014-04-01"),
          disposalDate = Some("2016-01-01")
        )

        "have tax owed of £204.83" in {
          result.taxOwed shouldEqual 204.83
        }

        "have the total gain £13148.36" in {
          result.totalGain shouldEqual 13148.36
        }

        "have the base tax gain of £2048.36" in {
          result.baseTaxGain shouldEqual 2048.36
        }

        "have the base tax rate of 10%" in {
          result.baseTaxRate shouldEqual 10
        }

        "have no upper tax gain" in {
          result.upperTaxGain shouldEqual None
        }

        "have no upper tax rate" in {
          result.upperTaxRate shouldEqual None
        }
      }

      "for a Trust disposing on 01/01/2016, acquired=01/04/2014 and net disposal proceeds of 31,100 not claiming ER" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "time",
          customerType = "trustee",
          priorDisposal = "No",
          isVulnerable = Some("No"),
          disposalValue = 31100,
          disposalCosts = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          improvementsAmt = 0,
          reliefs = 0,
          allowableLossesAmt = 0,
          entReliefClaimed = "No",
          acquisitionDate = Some("2014-04-01"),
          disposalDate = Some("2016-01-01")
        )

        "have tax owed of £2127.54" in {
          result.taxOwed shouldEqual 2127.54
        }

        "have the total gain £13148.36" in {
          result.totalGain shouldEqual 13148.36
        }

        "have the base tax gain of £0.00" in {
          result.baseTaxGain shouldEqual 0.00
        }

        "have the base tax rate of 18%" in {
          result.baseTaxRate shouldEqual 18
        }

        "have the upper tax gain of £7598.36" in {
          result.upperTaxGain shouldEqual Some(7598.36)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }
      }
    }
  }
    //###################### Zero gain tests #############################
  "Calling the calculate your capital gains method, when the gain calculation results in a zero value it" should {

    "return a calculationResultModel with 0 taxable gain, 0 tax owed, 0 baseTaxGain and 0 tax rate." in {

      val testService = new CalculationService {
        override def calculateGainFlat(disposalValue: Double, disposalCosts: Double, acquisitionValueAmt: Double,
                                       acquisitionCostsAmt: Double, improvementsAmt: Double) = 0.00

        override def calculateGainRebased(disposalValue: Double, disposalCosts: Double, revaluedAmount: Double,
                                          revaluationCost: Double, improvementsAmt: Double) = 0.00

        override def calculateGainTA(disposalValue: Double, disposalCosts: Double, acquisitionValueAmt: Double,
                                     acquisitionCostsAmt: Double, improvementsAmt: Double,
                                     acquisitionDate: String, disposalDate: String) = 0.00
      }

      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 0, 0, "No")
      result.taxOwed shouldEqual 0.0
      result.totalGain shouldEqual 0.0
      result.baseTaxGain shouldEqual 0.0
      result.baseTaxRate shouldEqual 0
    }
  }

  "Calling the calculate your capital gains method, when the gain calculation results in a negative value it" should {

    val testService = new CalculationService {
      override def calculateGainFlat(disposalValue: Double, disposalCosts: Double, acquisitionValueAmt: Double,
                                     acquisitionCostsAmt: Double, improvementsAmt: Double) = -200.00
    }

    "return a negative calculation result" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), -200.0, 0, 0, 0, 0, 0, 0, 0, 0, "No")
      result.totalGain shouldEqual -200.0
      result.baseTaxGain shouldEqual 0.0
    }
  }

  "Calling the calculate your capital gains method, when the gain calculation results in a positive value it" should {

    val testService = new CalculationService {
      override def calculateGainFlat(disposalValue: Double, disposalCosts: Double, acquisitionValueAmt: Double,
                                     acquisitionCostsAmt: Double, improvementsAmt: Double) = 200.00
//      override def calculateAEA(customerType: String, priorDisposal: String,
//                                     annualExemptAmount: Option[Double] = None,
//                                     isVulnerable: Option[String] = None) = 10000.0
    }

    "return a calculation result model with 0 taxable gain if the reliefs reduce the gain to zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 200.00, 0, "No")
      result.totalGain shouldEqual 200.00
      result.baseTaxGain shouldEqual 0.0
    }

    "return a calculation result model with 0 taxable gain if the reliefs reduce the gain beyond zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 400.00, 0, "No")
      result.totalGain shouldEqual 200.00
      result.baseTaxGain shouldEqual 0.0
    }

    "return a calculation result model with 0 taxable gain if the allowable losses reduce the gain to zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 200.0, 0, "No")
      result.totalGain shouldEqual 200.00
      result.baseTaxGain shouldEqual 0.0
    }

    "return a calculation result model with -200.00 taxable gain if the allowable losses reduce the gain beyond zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 0, 400.0, "No")
      result.totalGain shouldEqual 200.0
      result.baseTaxGain shouldEqual -200.0
    }

    "return a calculation result model with 0 taxable gain if the AEA can reduce the gain too or beyond zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 0, 0, "No")
      result.totalGain shouldEqual 200.00
      result.baseTaxGain shouldEqual 0.0
    }
  }
}

