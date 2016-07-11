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

import config.TaxRatesAndBands
import org.joda.time.DateTime
import uk.gov.hmrc.play.test.UnitSpec
import services.CalculationService._

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

    "return 206 where Disposal Proceeds = 12,645.77, Incidental Disposal Costs = 1954.66, Acquisition Cost = 1000.04," +
      "Incidental Acquisition Costs = 0.99, Enhancement Costs = 2000.65, Acquisition Date 05/04/1967, Disposal Date 31/07/2016" in {
      val result = CalculationService.calculateGainTA(12645.77, 1954.66, 1000.04, 0.99, 2000.65, "1967-04-05", "2016-07-31")
      result shouldEqual 206
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

    "the total Chargeable Gain Value should be 270 where total gain = 500, Relief = 10, In Year Losses = 20, AEA = 30, Brought Forward Losses = 170" in {
      val result = calculateChargeableGain(500, 10, 20, 30, 170)
      result shouldEqual 270
    }

    "the total Chargeable Gain Value should be 0 where total gain = 230, Relief = 10, In Year Losses = 20, AEA = 30, Brought Forward Losses = 170" in {
      val result = calculateChargeableGain(230, 10, 20, 30, 170)
      result shouldEqual 0
    }

    "the total Chargeable Gain Value should be -5 where total gain = 225, Relief = 10, In Year Losses = 20, AEA = 30, Brought Forward Losses = 170" in {
      val result = calculateChargeableGain(225, 10, 20, 30, 170)
      result shouldEqual -5
    }

    "the total Chargeable Gain Value should be -50 where total gain = 200, Relief = 0, In Year Losses = 0, AEA = 150, Brought Forward Losses = 75" in {
      val result = calculateChargeableGain(200, 0, 0, 150, 75)
      result shouldEqual -25
    }

    "the total Chargeable Gain Value should be -50 where total gain = 200, Relief = 0, In Year Losses = 0, AEA = 150, Brought Forward Losses = 74.01" in {
      val result = calculateChargeableGain(200, 0, 0, 150, 74.01)
      result shouldEqual -25
    }
  }

  "Calling CalculationService.brRemaining" should {

    "return a value of 32000 when Individual has Income of 8000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(8000, 11000, 0, 2017)
      result shouldEqual 32000
    }

    "return a value of 0 when Individual has Income of 50000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(50000, 11000, 0, 2017)
      result shouldEqual 0
    }

    "return a value of 3000 when Individual has Income of 50000 and a PA of 11000" in {
      val result = CalculationService.brRemaining(40000, 11000, 0, 2017)
      result shouldEqual 3000
    }

    "return a value of 0 when Individual has Income of 33000 and a PA of 1000" in {
      val result = CalculationService.brRemaining(33000, 1000, 0, 2017)
      result shouldEqual 0
    }

    "return a value of 0 when Individual has Income of 33001 and a PA of 1000" in {
      val result = CalculationService.brRemaining(33001, 1000, 0, 2017)
      result shouldEqual 0
    }

    "return a value of 1 when Individual has Income of 32999 and a PA of 1000" in {
      val result = CalculationService.brRemaining(32999, 1000, 0, 2017)
      result shouldEqual 1
    }

    "return a value of 16000 when Individual has income of 8000, PA of 11000 and previous gain of 16000" in {
      val result = CalculationService.brRemaining(8000, 11000, 16000, 2017)
      result shouldEqual 16000
    }

    "return a value of 0 when Individual has Income of 50000, PA of 11000 and previous gain of 16000" in {
      val result = CalculationService.brRemaining(50000, 11000, 16000, 2017)
      result shouldEqual 0
    }

    "return a value of 0 when Individual has income of 8000, PA of 11000 and previous gain of 32000" in {
      val result = CalculationService.brRemaining(8000, 11000, 32000, 2017)
      result shouldEqual 0
    }

    "return a value of 2000 when Individual has income of 31000.99, PA of 10999.99 and previous gain of 10000.99" in {
      val result = CalculationService.brRemaining(31000.99, 10999.99, 10000.99, 2017)
      result shouldEqual 2000
    }

    "return a value of 2000 when Individual has income of 31000.01, PA of 10999.01 and previous gain of 10000.01" in {
      val result = CalculationService.brRemaining(31000.01, 10999.01, 10000.01, 2017)
      result shouldEqual 2000
    }
  }

  "calling CalculationService.calculateCapitalGainsTax" should {

    //########### Flat Rate Tests ##########################
    "when using the Flat Rate calculation method" should {

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
          allowableLossesAmt = 3000.01
        )

        "have tax owed of £6,331.92" in {
          result.taxOwed shouldEqual 6331.64
        }

        "have the total gain £44,615" in {
          result.totalGain shouldEqual 44615
        }

        "have the base tax gain of £0" in {
          result.baseTaxGain shouldEqual 0
        }

        "have the base tax rate of 0%" in {
          result.baseTaxRate shouldEqual 0
        }

        "have the upper tax gain of £22,614.0" in {
          result.upperTaxGain shouldEqual Some(22613.0)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }
      }

      //############ Flat Rate PRR Tests ########################
      "return £0 tax owed for a Disposal Date of 06-10-2016, Acquisition Date of 05-04-2015, Days Eligible of 5, Gain of £2000 and " +
        "PRR of £2015" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          annualExemptAmount = Some(0),
          otherPropertiesAmt = Some(0),
          isVulnerable = Some("No"),
          currentIncome = Some(0),
          personalAllowanceAmt = Some(0),
          disposalValue = 2000,
          disposalCosts = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
          improvementsAmt = 0,
          reliefs = 0,
          allowableLossesAmt = 0,
          acquisitionDate = Some("2015-04-05"),
          disposalDate = Some("2016-10-06"),
          isClaimingPRR = Some("Yes"),
          daysClaimed = Some(5)
        )

        "have total gain of £2,000" in {
          result.totalGain shouldEqual 2000
        }

        "have total tax owed of £0" in {
          result.taxOwed shouldEqual 0
        }

        "have total PRR of £2,000" in {
          result.simplePRR shouldEqual Some(2000)
        }
      }

      "return £9498.56 tax owed for a Disposal Date of 03-10-2016, Acquisition Date of 20-04-2013, Days Eligible of 0, Gain of £100,000 and " +
        "PRR of £43,548" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "No",
          annualExemptAmount = Some(0),
          otherPropertiesAmt = Some(0),
          isVulnerable = Some("No"),
          currentIncome = Some(0),
          personalAllowanceAmt = Some(0),
          disposalValue = 100000,
          disposalCosts = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
          improvementsAmt = 0,
          reliefs = 0,
          allowableLossesAmt = 0,
          acquisitionDate = Some("2013-04-20"),
          disposalDate = Some("2016-10-03"),
          isClaimingPRR = Some("Yes"),
          daysClaimed = Some(0)
        )

        "have total gain of £100,000" in {
          result.totalGain shouldEqual 100000
        }

        "have base tax gain of £32000" in {
          result.baseTaxGain shouldEqual 32000
        }

        "have base tax rate of 18%" in {
          result.baseTaxRate shouldEqual 18
        }

        "have an upper tax gain of £13352" in {
          result.upperTaxGain shouldEqual Some(13352)
        }

        "have an upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }

        "have total taxed owed of £9498.56" in {
          result.taxOwed shouldEqual 9498.56
        }

        "have total PRR of £43,548" in {
          result.simplePRR shouldEqual Some(43548)
        }
      }

      "return £861.84 tax owed for an Individual claiming PRR, with a taxable gain of £44,615, chargeable gain of £3078 " +
        "and a PRR total of £19535" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "flat",
          customerType = "individual",
          priorDisposal = "Yes",
          annualExemptAmount = Some(4999.23),
          currentIncome = Some(49999.34),
          personalAllowanceAmt = Some(10999.45),
          disposalValue = 124000.68,
          disposalCosts = 1241.22,
          revaluedAmount = 0,
          revaluationCost = 0,
          acquisitionValueAmt = 65000.50,
          acquisitionCostsAmt = 1105.53,
          improvementsAmt = 12035.99,
          reliefs = 14000.11,
          allowableLossesAmt = 3001,
          isClaimingPRR = Some("Yes"),
          acquisitionDate = Some("2013-04-20"),
          disposalDate = Some("2016-10-03"),
          daysClaimed = Some(3)
        )

        "have tax owed of £861.84" in {
          result.taxOwed shouldEqual 861.84
        }

        "have the total gain £44,615" in {
          result.totalGain shouldEqual 44615
        }

        "have the base tax gain of £0" in {
          result.baseTaxGain shouldEqual 0
        }

        "have the base tax rate of 0%" in {
          result.baseTaxRate shouldEqual 0
        }

        "have the upper tax gain of £3078" in {
          result.upperTaxGain shouldEqual Some(3078)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }
      }
    }

    //########### Rebased Tests ##########################
    "when using the Rebased calculation method" should {

      "return £6,331.64 for an Individual not claiming ER, with a higher rate income and taxable gain of £44,615 charged " +
        "at 28%" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "rebased",
          customerType = "individual",
          priorDisposal = "Yes",
          annualExemptAmount = Some(5000),
          currentIncome = Some(50000),
          personalAllowanceAmt = Some(11000),
          disposalValue = 124000.68,
          disposalCosts = 1241.22,
          revaluedAmount = 65000.50,
          revaluationCost = 1105.53,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          improvementsAmt = 12035.99,
          reliefs = 14000.11,
          allowableLossesAmt = 3000.01
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

        "have the base tax rate of 0%" in {
          result.baseTaxRate shouldEqual 0
        }

        "have the upper tax gain of £22,613.0" in {
          result.upperTaxGain shouldEqual Some(22613.0)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }
      }

      //############### Rebased PRR Tests #####################

      "return £0 tax owed for an Individual claiming PRR, with a total gain of £100,000 and PRR of £100,000" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "rebased",
          customerType = "individual",
          priorDisposal = "No",
          annualExemptAmount = Some(0),
          otherPropertiesAmt = Some(0),
          isVulnerable = Some("No"),
          currentIncome = Some(0),
          personalAllowanceAmt = Some(0),
          disposalValue = 110000,
          disposalCosts = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          revaluedAmount = 10000,
          revaluationCost = 0,
          improvementsAmt = 0,
          reliefs = 0,
          allowableLossesAmt = 0,
          acquisitionDate = None,
          disposalDate = Some("2016-10-06"),
          isClaimingPRR = Some("Yes"),
          daysClaimedAfter = Some(0)
        )

        "have tax owed of £0" in {
          result.taxOwed shouldEqual 0
        }

        "have the total gain £100,000" in {
          result.totalGain shouldEqual 100000
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

        "have total PRR of £100,000" in {
          result.simplePRR shouldEqual Some(100000)
        }
      }

      "return £17206.28 tax owed for a Trustee not for Vulnerable person claiming PRR, with a total gain of £288400 and " +
        "PRR of £220,707" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "rebased",
          customerType = "trustee",
          priorDisposal = "No",
          annualExemptAmount = Some(0),
          otherPropertiesAmt = Some(0),
          isVulnerable = Some("No"),
          currentIncome = Some(0),
          personalAllowanceAmt = Some(0),
          disposalValue = 323456.78,
          disposalCosts = 1001.34,
          acquisitionValueAmt = 455.67,
          acquisitionCostsAmt = 100.45,
          revaluedAmount = 34000.78,
          revaluationCost = 12.99,
          improvementsAmt = 39.99,
          reliefs = 300.99,
          allowableLossesAmt = 390.45,
          acquisitionDate = Some("2015-04-06"),
          disposalDate = Some("2017-03-25"),
          isClaimingPRR = Some("Yes"),
          daysClaimedAfter = Some(3)
        )

        "have tax owed of £17,206.28" in {
          result.taxOwed shouldEqual 17206.28
        }

        "have the total gain £288,400" in {
          result.totalGain shouldEqual 288400
        }

        "have the base tax gain of 0.0" in {
          result.baseTaxGain shouldEqual 0
        }

        "have the base tax rate of 0%" in {
          result.baseTaxRate shouldEqual 0
        }

        "have the upper tax gain of £61,451" in {
          result.upperTaxGain shouldEqual Some(61451)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }

        "have total PRR of £220,707" in {
          result.simplePRR shouldEqual Some(220707)
        }
      }
    }

    //########### Time Apportioned Tests ##########################
    "when using the Time apportioned calculation method" should {

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
          acquisitionDate = Some("2014-04-01"),
          disposalDate = Some("2016-01-01")
        )

        "have tax owed of £2127.44" in {
          result.taxOwed shouldEqual 2127.44
        }

        "have the total gain £13148" in {
          result.totalGain shouldEqual 13148
        }

        "have the base tax gain of £0.00" in {
          result.baseTaxGain shouldEqual 0.00
        }

        "have the base tax rate of 0%" in {
          result.baseTaxRate shouldEqual 0
        }

        "have the upper tax gain of £7598" in {
          result.upperTaxGain shouldEqual Some(7598)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }
      }

      //############### Time Apportioned PRR ######################

      "return £0 tax owed for an Individual claiming PRR, with a total gain of £109,800, PRR of £109,800, acquisition date of 05-04-2015, " +
        "disposal date of 06-10-2016 and days claimed of 3" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "time",
          customerType = "individual",
          priorDisposal = "No",
          annualExemptAmount = Some(0),
          otherPropertiesAmt = Some(0),
          isVulnerable = Some("No"),
          currentIncome = Some(0),
          personalAllowanceAmt = Some(0),
          disposalValue = 110000,
          disposalCosts = 0,
          acquisitionValueAmt = 0,
          acquisitionCostsAmt = 0,
          revaluedAmount = 0,
          revaluationCost = 0,
          improvementsAmt = 0,
          reliefs = 0,
          allowableLossesAmt = 0,
          acquisitionDate = Some("2015-04-05"),
          disposalDate = Some("2016-10-06"),
          isClaimingPRR = Some("Yes"),
          daysClaimedAfter = Some(3)
        )

        "have tax owed of £0" in {
          result.taxOwed shouldEqual 0
        }

        "have the total gain £109,800" in {
          result.totalGain shouldEqual 109800
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

        "have total PRR of £109,800" in {
          result.simplePRR shouldEqual Some(109800)
        }
      }

      "return £19,405.12 tax owed for a Trustee not for Vulnerable person claiming PRR, with a total gain of £321,857 and " +
        "PRR of £246,311" should {
        val result = CalculationService.calculateCapitalGainsTax(
          calculationType = "time",
          customerType = "trustee",
          priorDisposal = "No",
          annualExemptAmount = Some(0),
          otherPropertiesAmt = Some(0),
          isVulnerable = Some("No"),
          currentIncome = Some(0),
          personalAllowanceAmt = Some(0),
          disposalValue = 323456.78,
          disposalCosts = 1001.34,
          acquisitionValueAmt = 455.67,
          acquisitionCostsAmt = 100.45,
          revaluedAmount = 34000.78,
          revaluationCost = 12.99,
          improvementsAmt = 39.99,
          reliefs = 300.99,
          allowableLossesAmt = 390.45,
          acquisitionDate = Some("2015-04-06"),
          disposalDate = Some("2017-03-25"),
          isClaimingPRR = Some("Yes"),
          daysClaimedAfter = Some(3)
        )

        "have tax owed of £19,405.12" in {
          result.taxOwed shouldEqual 19405.12
        }

        "have the total gain £321,857" in {
          result.totalGain shouldEqual 321857
        }

        "have the base tax gain of 0.0" in {
          result.baseTaxGain shouldEqual 0
        }

        "have the base tax rate of 0%" in {
          result.baseTaxRate shouldEqual 0
        }

        "have the upper tax gain of £61,451" in {
          result.upperTaxGain shouldEqual Some(69304)
        }

        "have the upper tax rate of 28%" in {
          result.upperTaxRate shouldEqual Some(28)
        }

        "have total PRR of £246,311" in {
          result.simplePRR shouldEqual Some(246311)
        }
      }
    }
  }

  "Calling CalculationService.calculateFlatPRR" should {

    "return £2000 (capped at the Gain) for a Disposal Date of 05-10-2016, Acquisition Date of 05-04-2015, Days Eligible of " +
      "5 and Gain of £2000 " in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-10-05"), DateTime.parse("2015-04-05"), 5, 2000)
      result shouldEqual 2000
    }

    "return £4501 (capped at the Gain) for a Disposal Date of 20-01-2016, Acquisition Date of 01-04-2015, Days Eligible of " +
      "20 and Gain of £4501" in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-01-20"), DateTime.parse("2015-04-01"), 20, 4501)
      result shouldEqual 4501
    }

    "return £2001 (capped at the Gain) for a Disposal Date of 05-10-2016, Acquisition Date of 06-04-2015, Days Eligible of " +
      "5 and Gain of £2000" in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-10-05"), DateTime.parse("2015-04-06"), 5, 2001)
      result shouldEqual 2001
    }

    "return £4502 (capped at the Gain) for a Disposal Date of 05-10-2016, Acquisition Date of 01-04-2016, Days Eligible of " +
      "0 and Gain of £4502" in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-10-05"), DateTime.parse("2016-04-01"), 0, 4502)
      result shouldEqual 4502
    }

    "return £2002 (capped at the Gain) for a Disposal Date of 06-10-2016, Acquisition Date of 05-04-2015, Days Eligible of " +
      "5 and Gain of £2000" in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-10-06"), DateTime.parse("2015-04-05"), 5, 2002)
      result shouldEqual 2002
    }

    "return £4503 (capped at the Gain) for a Disposal Date of 20-10-2016, Acquisition Date of 05-04-2015, Days Eligible of " +
      "20 and Gain of £4503" in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-10-20"), DateTime.parse("2015-04-05"), 20, 4503)
      result shouldEqual 4503
    }

    "return £2003 (capped at the Gain) for a Disposal Date of 06-10-2016, Acquisition Date of 06-04-2015, Days Eligible of " +
      "5 and Gain of £2003" in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-10-06"), DateTime.parse("2015-04-06"), 5, 2003)
      result shouldEqual 2003
    }

    "return £4504 (capped at the Gain) for a Disposal Date of 20-10-2016, Acquisition Date of 20-04-2015, Days Eligible of " +
      "20 and Gain of £4504" in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-10-20"), DateTime.parse("2015-04-20"), 20, 4504)
      result shouldEqual 4504
    }

    "return a rounded up amount of £43,548 for a Disposal Date of 03-10-2016, Acquisition Date of 20-04-2013, " +
      "Days Eligible of 0 and Gain of £100,000 which results in a PRR of £43,547.11005[...]" in {
      val result = CalculationService.calculateFlatPRR(DateTime.parse("2016-10-03"), DateTime.parse("2013-04-20"), 0, 100000)
      result shouldEqual 43548
    }
  }

  "Calling CalculationService.calculateRebasedPRR" should {

    "return £100,000 for a Disposal date of 06-10-2016, days claimed after of 0 and gain of £100,000" in {
      val result = CalculationService.calculateRebasedPRR(DateTime.parse("2016-10-06"), 0, 100000)
      result shouldEqual 100000
    }

    "return £100,000 for a Disposal date of 06-10-2016, days claimed after of 20 and gain of £100,000 resulting PRR of " +
      "£103,637 that is capped" in {
      val result = CalculationService.calculateRebasedPRR(DateTime.parse("2016-10-06"), 20, 100000)
      result shouldEqual 100000
    }

    "return £75,000 for a Disposal date of 06-10-2016, days claimed after of 0 and gain of £75,000" in {
      val result = CalculationService.calculateRebasedPRR(DateTime.parse("2016-10-06"), 0, 75000)
      result shouldEqual 75000
    }

    "return a rounded up amount of £30,899 for a Disposal date of 25-12-2016, days claimed after of 0 and gain of £56,000 " +
      "which results in a PRR of £30898.492[...]" in {
      val result = CalculationService.calculateRebasedPRR(DateTime.parse("2017-12-25"), 0, 56000)
      result shouldEqual 30899
    }

    "return £45,000 for a Disposal date of 05-10-2016, days claimed after of 0 (the question was not asked) and gain of £45,000 " +
      "resulting in a PRR of £45082 that is capped" in {
      val result = CalculationService.calculateRebasedPRR(DateTime.parse("2016-10-05"), 0, 45000)
      result shouldEqual 45000
    }

    "return £35000 for a Disposal date of 25-12-2015, days claimed after of 0 (the question was not asked) and gain of £35,000 " +
      "resulting in a PRR of £72785 that is capped" in {
      val result = CalculationService.calculateRebasedPRR(DateTime.parse("2015-12-25"), 0, 35000)
      result shouldEqual 35000
    }

  }

  "Calling CalculationService.calculateTimeApportionmentPRR" should {

    "return £100,000 for a Disposal date of 06-10-2016, days claimed after of 0 and gain of £100,000" in {
      val result = CalculationService.calculateTimeApportionmentPRR(DateTime.parse("2016-10-06"), 0, 100000)
      result shouldEqual 100000
    }

    "return £100,000 for a Disposal date of 06-10-2016, days claimed after of 20 and gain of £100,000 resulting PRR of " +
      "£103,637 that is capped" in {
      val result = CalculationService.calculateTimeApportionmentPRR(DateTime.parse("2016-10-06"), 20, 100000)
      result shouldEqual 100000
    }

    "return £75,000 for a Disposal date of 06-10-2016, days claimed after of 0 and gain of £75,000" in {
      val result = CalculationService.calculateTimeApportionmentPRR(DateTime.parse("2016-10-06"), 0, 75000)
      result shouldEqual 75000
    }

    "return a rounded up amount of £30,899 for a Disposal date of 25-12-2016, days claimed after of 0 and gain of £56,000 " +
      "which results in a PRR of £30898.492[...]" in {
      val result = CalculationService.calculateTimeApportionmentPRR(DateTime.parse("2017-12-25"), 0, 56000)
      result shouldEqual 30899
    }

    "return £45,000 for a Disposal date of 05-10-2016, days claimed after of 0 (the question was not asked) and gain of £45,000 " +
      "resulting in a PRR of £45082 that is capped" in {
      val result = CalculationService.calculateTimeApportionmentPRR(DateTime.parse("2016-10-05"), 0, 45000)
      result shouldEqual 45000
    }

    "return £35000 for a Disposal date of 25-12-2015, days claimed after of 0 (the question was not asked) and gain of £35,000 " +
      "resulting in a PRR of £72785 that is capped" in {
      val result = CalculationService.calculateTimeApportionmentPRR(DateTime.parse("2015-12-25"), 0, 35000)
      result shouldEqual 35000
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

        override val taxRatesAndBands = TaxRatesAndBands.getRates(2017)
      }

      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 0, 0)
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

      override val taxRatesAndBands = TaxRatesAndBands.getRates(2017)
    }

    "for an individual performing flat who is not claiming ER resulting in a £200 loss" should {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), -200.0, 0, 0, 0, 0, 0, 0, 0, 0)

      "return -200 for total gain" in {
        result.totalGain shouldEqual -200.0
      }

      "return 0 for basic tax gain" in {
        result.baseTaxGain shouldEqual 0.0
      }
    }

    "for an individual performing flat who is claiming ER resulting in a £200 loss" should {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), -200.0, 0, 0, 0, 0, 0, 0, 0, 0)

      "return -200 for total gain" in {
        result.totalGain shouldEqual -200.0
      }

      "return 0 for basic tax gain" in {
        result.baseTaxGain shouldEqual 0.0
      }
    }
  }

  "Calling the calculate your capital gains method, when the gain calculation results in a positive value it" should {

    val testService = new CalculationService {
      override def calculateGainFlat(disposalValue: Double, disposalCosts: Double, acquisitionValueAmt: Double,
                                     acquisitionCostsAmt: Double, improvementsAmt: Double) = 200.00

      override val taxRatesAndBands = TaxRatesAndBands.getRates(2017)
    }

    "return a calculation result model with 0 taxable gain if the reliefs reduce the gain to zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 200.00, 0)
      result.totalGain shouldEqual 200.00
      result.baseTaxGain shouldEqual 0.0
    }

    "return a calculation result model with 0 taxable gain if the reliefs reduce the gain beyond zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 400.00, 0)
      result.totalGain shouldEqual 200.00
      result.baseTaxGain shouldEqual 0.0
    }

    "return a calculation result model with 0 taxable gain if the allowable losses reduce the gain to zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 200.0, 0)
      result.totalGain shouldEqual 200.00
      result.baseTaxGain shouldEqual 0.0
    }

    "return a calculation result model with -200.00 taxable gain if the allowable losses reduce the gain beyond zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 0, 400.0)
      result.totalGain shouldEqual 200.0
      result.baseTaxGain shouldEqual -200.0
    }

    "return a calculation result model with 0 taxable gain if the AEA can reduce the gain too or beyond zero" in {
      val result = testService.calculateCapitalGainsTax("flat", "individual", "No", Some(0), Some(0), Some("No"), Some(0), Some(0), 0, 0, 0, 0, 0, 0, 0, 0, 0)
      result.totalGain shouldEqual 200.00
      result.baseTaxGain shouldEqual 0.0

    }
  }

  "Calling the partialAEAUsed method" should {

    "return an AEA of £6000 used with no losses or reliefs" in {
      val result = CalculationService.partialAEAUsed(6000, 0, 0)
      result shouldEqual 6000
    }

    "return an AEA of £2000 used with losses and reliefs used" in {
      val result = CalculationService.partialAEAUsed(6000, 2000, 2000)
      result shouldEqual 2000
    }

    "return an AEA of £0 used when losses and reliefs eliminate the gain" in {
      val result = CalculationService.partialAEAUsed(6000, 3000, 3000)
      result shouldEqual 0
    }

    "return an AEA of £0 when losses and reliefs eliminate the gain through rounding" in {
      val result = CalculationService.partialAEAUsed(6000, 2999.01, 2999.01)
    }
  }

  "Calling the annualExemptAmountUsed method" should {

    "return an AEA used equal to the max remaining when the chargeable gain is positive" in {
      val result = CalculationService.annualExemptAmountUsed(11100, 50000, 38900, 5000, 5000)
      result shouldEqual 11100
    }

    "return an AEA used equal to the max remaining rounded down when the chargeable gain is positive" in {
      val result = CalculationService.annualExemptAmountUsed(10000.99, 50000, 38900, 5000, 5000)
      result shouldEqual 10000
    }

    "return an AEA used of 0 when the chargeable gain is negative" in {
      val result = CalculationService.annualExemptAmountUsed(11100, 20000, -10000, 15000, 15000)
      result shouldEqual 0
    }

    "return an AEA from the partialAEAUsed method when chargeable gain is zero" in {
      val result = CalculationService.annualExemptAmountUsed(11100, 20000, 0, 7000, 7000)
      result shouldEqual 6000
    }
  }
}
