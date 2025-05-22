/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec

import java.time.LocalDate

class CalculationServiceSpec extends PlaySpec with MockitoSugar {
  val calculationService = new CalculationService
  "Calling CalculationService.calculateGainFlat" must {

    "return the total Gain value of 4700 where Disposal Proceeds = 10000, Incidental Disposal Costs = 2000, Acquisition Cost = 1000, " +
      "Incidental Acquisition Costs = 300, Enhancement Costs = 2000" in {
        val result = calculationService.calculateGainFlat(10000, 2000, 1000, 300, 2000)
        result mustEqual 4700
      }
  }

  "Calling CalculationService.calculateGainTA" must {

    "return 2500 where Disposal Proceeds = 10000, Incidental Disposal Costs = 2000, Acquisition Cost = 1000," +
      "Incidental Acquisition Costs = 0, Enhancement Costs = 2000, Acquisition Date 05/04/2015, Disposal Date 06/04/2015" in {
        val result = calculationService.calculateGainTA(
          10000.0,
          2000.0,
          1000.0,
          0.0,
          2000.0,
          Some(LocalDate.parse("2015-04-05")),
          LocalDate.parse("2015-04-06")
        )
        result mustEqual 2500
      }

    "return 206 where Disposal Proceeds = 12,645.77, Incidental Disposal Costs = 1954.66, Acquisition Cost = 1000.04," +
      "Incidental Acquisition Costs = 0.99, Enhancement Costs = 2000.65, Acquisition Date 05/04/1967, Disposal Date 31/07/2016" in {
        val result = calculationService.calculateGainTA(
          12645.77,
          1954.66,
          1000.04,
          0.99,
          2000.65,
          Some(LocalDate.parse("1967-04-05")),
          LocalDate.parse("2016-07-31")
        )
        result mustEqual 206
      }
  }

  "Calling CalculationService.calculateGainRebased" must {

    "return the total Gain value of 4700 where Disposal Proceeds = 10000, Incidental Disposal Costs = 2000, Rebased Value = 1000, " +
      "Revaluation Costs = 300, Enhancement Costs = 2000" in {
        val result = calculationService.calculateGainRebased(10000, 2000, 1000, 300, 2000)
        result mustEqual 4700
      }
  }

  "Calling CalculationService.calculateChargeableGain" must {

    "the total Chargeable Gain value of 4550 where total Gain = 5000, Relief = 200, In Year Losses = 150, AEA = 100" in {
      val result = calculationService.calculateChargeableGain(5000, 200, 150, 100)
      result mustEqual 4550
    }

    "the total Chargeable Gain Value should be 270 where total gain = 500, Relief = 10, In Year Losses = 20, AEA = 30, Brought Forward Losses = 170" in {
      val result = calculationService.calculateChargeableGain(500, 10, 20, 30, 170)
      result mustEqual 270
    }

    "the total Chargeable Gain Value should be 0 where total gain = 230, Relief = 10, In Year Losses = 20, AEA = 30, Brought Forward Losses = 170" in {
      val result = calculationService.calculateChargeableGain(230, 10, 20, 30, 170)
      result mustEqual 0
    }

    "the total Chargeable Gain Value should be -5 where total gain = 225, Relief = 10, In Year Losses = 20, AEA = 30, Brought Forward Losses = 170" in {
      val result = calculationService.calculateChargeableGain(225, 10, 20, 30, 170)
      result mustEqual -5
    }

    "the total Chargeable Gain Value should be -50 where total gain = 200, Relief = 0, In Year Losses = 0, AEA = 150, Brought Forward Losses = 75" in {
      val result = calculationService.calculateChargeableGain(200, 0, 0, 150, 75)
      result mustEqual -25
    }

    "the total Chargeable Gain Value should be -50 where total gain = 200, Relief = 0, In Year Losses = 0, AEA = 150, Brought Forward Losses = 74.01" in {
      val result = calculationService.calculateChargeableGain(200, 0, 0, 150, 74.01)
      result mustEqual -25
    }
  }

  "Calling CalculationService.brRemaining" must {

    "return a value of 1 when Individual has income of 49999, PA of 12300" in {
      val result = calculationService.brRemaining(49999, 12500, 0, 2020)
      result mustEqual 1
    }

    "return a value of 0 when Individual has income of 50000, PA of 12300" in {
      val result = calculationService.brRemaining(50000, 12500, 0, 2020)
      result mustEqual 0
    }

    "return a value of 37500 when Individual has income of 50000, PA of 12300" in {
      val result = calculationService.brRemaining(0, 12500, 0, 2020)
      result mustEqual 37500
    }

    "return a value of 32000 when Individual has Income of 8000 and a PA of 11000" in {
      val result = calculationService.brRemaining(8000, 11000, 0, 2017)
      result mustEqual 32000
    }

    "return a value of 0 when Individual has Income of 50000 and a PA of 11000" in {
      val result = calculationService.brRemaining(50000, 11000, 0, 2017)
      result mustEqual 0
    }

    "return a value of 3000 when Individual has Income of 40000 and a PA of 11000" in {
      val result = calculationService.brRemaining(40000, 11000, 0, 2017)
      result mustEqual 3000
    }

    "return a value of 0 when Individual has Income of 33000 and a PA of 1000" in {
      val result = calculationService.brRemaining(33000, 1000, 0, 2017)
      result mustEqual 0
    }

    "return a value of 0 when Individual has Income of 33001 and a PA of 1000" in {
      val result = calculationService.brRemaining(33001, 1000, 0, 2017)
      result mustEqual 0
    }

    "return a value of 1 when Individual has Income of 32999 and a PA of 1000" in {
      val result = calculationService.brRemaining(32999, 1000, 0, 2017)
      result mustEqual 1
    }

    "return a value of 16000 when Individual has income of 8000, PA of 11000 and previous gain of 16000" in {
      val result = calculationService.brRemaining(8000, 11000, 16000, 2017)
      result mustEqual 16000
    }

    "return a value of 0 when Individual has Income of 50000, PA of 11000 and previous gain of 16000" in {
      val result = calculationService.brRemaining(50000, 11000, 16000, 2017)
      result mustEqual 0
    }

    "return a value of 0 when Individual has income of 8000, PA of 11000 and previous gain of 32000" in {
      val result = calculationService.brRemaining(8000, 11000, 32000, 2017)
      result mustEqual 0
    }

    "return a value of 2000 when Individual has income of 31000.99, PA of 10999.99 and previous gain of 10000.99" in {
      val result = calculationService.brRemaining(31000.99, 10999.99, 10000.99, 2017)
      result mustEqual 2000
    }

    "return a value of 2000 when Individual has income of 31000.01, PA of 10999.01 and previous gain of 10000.01" in {
      val result = calculationService.brRemaining(31000.01, 10999.01, 10000.01, 2017)
      result mustEqual 2000
    }

  }

  "Calling the partialAEAUsed method" must {

    "return an AEA of £6000 used with no losses or reliefs" in {
      val result = calculationService.partialAEAUsed(6000, 0, 0)
      result mustEqual 6000
    }

    "return an AEA of £2000 used with losses and reliefs used" in {
      val result = calculationService.partialAEAUsed(6000, 2000, 2000)
      result mustEqual 2000
    }

    "return an AEA of £0 used when losses and reliefs eliminate the gain" in {
      val result = calculationService.partialAEAUsed(6000, 3000, 3000)
      result mustEqual 0
    }

    "return an AEA of £0 when losses and reliefs eliminate the gain through rounding" in {
      val result = calculationService.partialAEAUsed(6000, 2999.01, 2999.01)
      result mustEqual 0
    }
  }

  "Calling the determinePRRUsed method" must {

    "return a value of PRR claimed when less than total gain" in {
      val result = calculationService.determineReliefsUsed(1000, Some(500))
      result mustEqual 500
    }

    "return a value of PRR claimed when equal to total gain" in {
      val result = calculationService.determineReliefsUsed(1000, Some(1000))
      result mustEqual 1000
    }

    "return a value equal to the total gain when PRR claimed is greater" in {
      val result = calculationService.determineReliefsUsed(1000, Some(1200))
      result mustEqual 1000
    }

    "return a value of PRR claimed with correct rounding when less than total gain" in {
      val result = calculationService.determineReliefsUsed(1000, Some(500.01))
      result mustEqual 501
    }

    "return a value equal to the total gain when PRR claimed is greater with correct rounding" in {
      val result = calculationService.determineReliefsUsed(1000, Some(1000.01))
      result mustEqual 1000
    }

    "return a value of 0 when PRR is not claimed" in {
      val result = calculationService.determineReliefsUsed(1000, None)
      result mustEqual 0
    }
  }

  "Calling the annualExemptAmountUsed method" must {

    "return an AEA used of 0 when reliefs and in year losses equal the gain" in {
      calculationService.annualExemptAmountUsed(10000, 100, 50, 50) mustBe 0
    }

    "return an AEA used of 0 when reliefs and in year losses are greater than the gain" in {
      calculationService.annualExemptAmountUsed(10000, 100, 100, 100) mustBe 0
    }

    "return an AEA used equal to the available amount when the gain minus losses and reliefs is greater than the available amount" in {
      calculationService.annualExemptAmountUsed(10000, 150000, 5000, 5000) mustBe 10000
    }

    "return an AEA used equal to the available amount when the gain minus losses and reliefs is equal to the available amount" in {
      calculationService.annualExemptAmountUsed(5000, 10000, 2500, 2500) mustBe 5000
    }

    "return the result of the partial AEA used when the available AEA is greater than the remaining gain" in {
      calculationService.annualExemptAmountUsed(5000, 7500, 2500, 2500) mustBe 2500
    }
  }

  "Calling the annualExemptAmountLeft method" must {

    "return an AEA remaining equal to £5,100 when calculated AEA is £11,100 and £6,000 is used" in {
      val result = calculationService.annualExemptAmountLeft(11100, 6000)
      result mustEqual 5100
    }

    "return an AEA remaining equal to £5,101 when calculated AEA is £11,100.99 rounded up and £6,000 is used" in {
      val result = calculationService.annualExemptAmountLeft(11100.99, 6000)
      result mustEqual 5101
    }

    "return an AEA remaining equal to £0 when calculated AEA is £11,100 and £11,100 is used" in {
      val result = calculationService.annualExemptAmountLeft(11100, 11100)
      result mustEqual 0
    }

    "return an AEA remaining equal to £0 when calculated AEA is £0 and £0 is used" in {
      val result = calculationService.annualExemptAmountLeft(0, 0)
      result mustEqual 0
    }
  }

  "Calling the determineLossLeft method" must {

    "return loss remaining equal to £90 when Gain is £10 and £100 loss" in {
      val result = calculationService.determineLossLeft(10, 100)
      result mustEqual 90
    }

    "return loss remaining equal to £1 when Gain is £0 and £0.01 loss" in {
      val result = calculationService.determineLossLeft(0, 0.01)
      result mustEqual 1
    }

    "return loss remaining equal to £0 when Gain is £50 and £0.00 loss " in {
      val result = calculationService.determineLossLeft(50, 0)
      result mustEqual 0
    }

    "return loss remaining equal to £0 when Gain is £0 and £0 loss " in {
      val result = calculationService.determineLossLeft(0, 0)
      result mustEqual 0
    }

  }

  "Calling the determineReliefsUsed method" must {

    "return the a value limited to the prr of letting relief submitted when less than the gain - prr" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 20000, Some(30000), 2015)
      result mustEqual 20000
    }

    "return a value of 40000 letting relief submitted when prr and lettings greater than 40000" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 50000, Some(50000), 2015)
      result mustEqual 40000
    }

    "return a value of 40000 letting relief submitted when prr and lettings greater than 40000 again" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 50000, Some(45000), 2015)
      result mustEqual 40000
    }

    "return the value of the gain when reliefs submitted greater than the gain" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 50000, Some(30000), 2015)
      result mustEqual 30000
    }

    "return a value of gain-prr when the letting relief submitted exceeds this" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 70000, Some(40000), 2015)
      result mustEqual 30000
    }

    "return a value of letting relief when the letting relief submitted is less than 40000 and gain-prr" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 80000, Some(18000), 2015)
      result mustEqual 18000
    }

    "return the value of 0 if there is no prr claimed" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 0, Some(30000), 2015)
      result mustEqual 0
    }

    "return a result of 0 if no reliefs are submitted" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 50000, None, 2015)
      result mustEqual 0
    }

    "return a result rounded up if reliefs is a fractional value" in {
      val result = calculationService.determineLettingsReliefsUsed(2000, 1000, Some(499.01), 2013)
      result mustEqual 500
    }

    "return a result equal to the gain when reliefs is fractionally above the gain" in {
      val result = calculationService.determineLettingsReliefsUsed(800, 400, Some(800.01), 2014)
      result mustEqual 400
    }

    "return a result equal to the gain when reliefs is fractionally below the gain" in {
      val result = calculationService.determineLettingsReliefsUsed(800, 400, Some(799.01), 2015)
      result mustEqual 400
    }

    "return a result equal to the max reliefs for the latest year when provided with a date in the future" in {
      val result = calculationService.determineLettingsReliefsUsed(100000, 50000, Some(45000), 2020)
      result mustEqual 40000
    }
  }

  "Calling the calculateAmountUsed method" must {
    "return 0 when supplied with 10 and 10" in {
      calculationService.calculateAmountUsed(10, 10) mustEqual 0
    }

    "return 1 when supplied with 10 and 9" in {
      calculationService.calculateAmountUsed(10, 9) mustEqual 1
    }

    "return 0 when supplied with 9 and 10" in {
      calculationService.calculateAmountUsed(9, 10) mustEqual 0
    }
  }

  "Calling .calculateTotalCosts" when {

    "disposal costs are £999.99, acquisition costs are £299.50 and improvements are £5,000.01" must {

      val result = calculationService.calculateTotalCosts(999.99, 299.50, 5000.01)

      "return 6301" in {
        result mustEqual 6301.00
      }
    }

    "disposal costs are £999, acquisition costs are £299 and improvements are £5,000" must {

      val result = calculationService.calculateTotalCosts(999.00, 299.00, 5000.00)

      "return 6298" in {
        result mustEqual 6298.00
      }
    }
  }

  "Calling .calculationResult" when {

    "customer type is individual and is property" must {

      "when taxable gain is lower than basic rate remaining" must {

        val result = calculationService.calculationResult(
          gain = 10000,
          taxableGain = 10000,
          chargeableGain = 10000,
          basicRateRemaining = 10001,
          prrClaimed = None,
          usedAEA = 0,
          aeaLeft = 11000,
          taxYear = 2016,
          isProperty = true
        )

        "return basic rate total of 1800" in {
          result.baseRateTotal mustEqual 1800
        }

        "return an upper rate total of 0" in {
          result.upperRateTotal mustEqual 0
        }
      }

      "when taxable gain is higher than basic rate remaining" must {

        val result = calculationService.calculationResult(
          gain = 10000,
          taxableGain = 20000,
          chargeableGain = 20000,
          basicRateRemaining = 10000,
          prrClaimed = None,
          usedAEA = 0,
          aeaLeft = 11000,
          taxYear = 2016,
          isProperty = true
        )

        "return basic rate total of 1800" in {
          result.baseRateTotal mustEqual 1800
        }

        "return an upper rate total of 2800" in {
          result.upperRateTotal mustEqual 2800
        }
      }

      "when a decimal result is produced" must {

        val result = calculationService.calculationResult(
          gain = 10000,
          taxableGain = 56789,
          chargeableGain = 56789,
          basicRateRemaining = 34567,
          prrClaimed = None,
          usedAEA = 0,
          aeaLeft = 11000,
          taxYear = 2016,
          isProperty = true
        )

        "return a rounded number for base rate total" in {
          result.baseRateTotal mustEqual 6222.05
        }

        "return a rounded number for upper rate total" in {
          result.upperRateTotal mustEqual 6222.16
        }
      }
    }
  }
}
