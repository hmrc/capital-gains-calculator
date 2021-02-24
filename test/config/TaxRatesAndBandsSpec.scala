/*
 * Copyright 2021 HM Revenue & Customs
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

package config

import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec

class TaxRatesAndBandsSpec extends PlaySpec {

  "validating non-year specific parameters" must {

    "return 2015-04-06 string for the start of the tax" in {
      TaxRatesAndBands.getRates(0).startOfTax mustEqual "2015-04-06"
    }

    "return a date of 2015-04-06 for the start of the tax date" in {
      TaxRatesAndBands.getRates(0).startOfTaxDateTime mustEqual DateTime.parse("2015-04-06")
    }

    "return 18 as the value of months" in {
      PrivateResidenceReliefDateUtils(DateTime.parse("2020-04-05")).pRRMonthDeductionApplicable().months mustEqual 18
    }

    "return 9 as the value of months" in {
      PrivateResidenceReliefDateUtils(DateTime.parse("2020-04-06")).pRRMonthDeductionApplicable().months mustEqual 9
    }

  }

  "validating 2020/2021 parameters" must {

    "return 2021 for the tax year" in {
      TaxRatesAndBands20202021.taxYear mustBe 2021
    }

    "return 18 as the basic percentage rate for the tax year" in {
      TaxRatesAndBands20202021.basicRate mustBe 0.18
      TaxRatesAndBands20202021.basicRatePercentage mustBe 18
    }

    "return 28 as the higher percentage rate for the tax year" in {
      TaxRatesAndBands20202021.higherRate mustBe 0.28
      TaxRatesAndBands20202021.higherRatePercentage mustBe 28
    }

    "return 12300 as the maximum Annual Excempt Amount" in {
      TaxRatesAndBands20202021.maxAnnualExemptAmount mustBe 12300
    }

    "return 6000 as the non-vulnerable trustee Annual Exempt Amount" in {
      TaxRatesAndBands20202021.notVulnerableMaxAnnualExemptAmount mustBe 6000
    }

    "return 37500 as the basic rate band" in {
      TaxRatesAndBands20202021.basicRateBand mustBe 37500
    }

    "return 12500 as the maximum Personal Allowance" in {
      TaxRatesAndBands20202021.maxPersonalAllowance mustBe 12500
    }

  }

  "validating 2019/2020 parameters" must {

    "return 2020 for the tax year" in {
      TaxRatesAndBands20192020.taxYear mustBe 2020
    }

    "return 18 as the basic percentage rate for the tax year" in {
      TaxRatesAndBands20192020.basicRate mustBe 0.18
      TaxRatesAndBands20192020.basicRatePercentage mustBe 18
    }

    "return 28 as the higher percentage rate for the tax year" in {
      TaxRatesAndBands20192020.higherRate mustBe 0.28
      TaxRatesAndBands20192020.higherRatePercentage mustBe 28
    }

    "return 12000 as the maximum Annual Excempt Amount" in {
      TaxRatesAndBands20192020.maxAnnualExemptAmount mustBe 12000
    }

    "return 6000 as the non-vulnerable trustee Annual Exempt Amount" in {
      TaxRatesAndBands20192020.notVulnerableMaxAnnualExemptAmount mustBe 6000
    }

    "return 37500 as the basic rate band" in {
      TaxRatesAndBands20192020.basicRateBand mustBe 37500
    }

    "return 12500 as the maximum Personal Allowance" in {
      TaxRatesAndBands20192020.maxPersonalAllowance mustBe 12500
    }

  }

  "validating 2018/2019 parameters" must {

    "return 2019 for the tax year" in {
      TaxRatesAndBands20182019.taxYear mustBe 2019
    }

    "return 18 as the basic percentage rate for the tax year" in {
      TaxRatesAndBands20182019.basicRate mustBe 0.18
      TaxRatesAndBands20182019.basicRatePercentage mustBe 18
    }

    "return 28 as the higher percentage rate for the tax year" in {
      TaxRatesAndBands20182019.higherRate mustBe 0.28
      TaxRatesAndBands20182019.higherRatePercentage mustBe 28
    }

    "return 11700 as the maximum Annual Excempt Amount" in {
      TaxRatesAndBands20182019.maxAnnualExemptAmount mustBe 11700
    }

    "return 5850 as the non-vulnerable trustee Annual Exempt Amount" in {
      TaxRatesAndBands20182019.notVulnerableMaxAnnualExemptAmount mustBe 5850
    }

    "return 33500 as the basic rate band" in {
      TaxRatesAndBands20182019.basicRateBand mustBe 34500
    }

    "return 11500 as the maximum Personal Allowance" in {
      TaxRatesAndBands20182019.maxPersonalAllowance mustBe 11850
    }

  }

  "validating 2016/2017 parameters" must {

    "return 2017 for the tax year" in {
      TaxRatesAndBands20162017.taxYear mustBe 2017
    }

    "return 18 as the basic percentage rate for the tax year" in {
      TaxRatesAndBands20162017.basicRate mustBe 0.18
      TaxRatesAndBands20162017.basicRatePercentage mustBe 18
    }

    "return 28 as the higher percentage rate for the tax year" in {
      TaxRatesAndBands20162017.higherRate mustBe 0.28
      TaxRatesAndBands20162017.higherRatePercentage mustBe 28
    }

    "return 11100 as the maximum Annual Excempt Amount" in {
      TaxRatesAndBands20162017.maxAnnualExemptAmount mustBe 11100
    }

    "return 5550 as the non-vulnerable trustee Annual Exempt Amount" in {
      TaxRatesAndBands20162017.notVulnerableMaxAnnualExemptAmount mustBe 5550
    }

    "return 32000 as the basic rate band" in {
      TaxRatesAndBands20162017.basicRateBand mustBe 32000
    }

    "return 11000 as the maximum Personal Allowance" in {
      TaxRatesAndBands20162017.maxPersonalAllowance mustBe 11000
    }

  }

  "Validating the getParameters method" when {


    "calling with the year 2018" must {

      "return 2018 for the tax year" in {
        TaxRatesAndBands.getRates(2018).taxYear mustBe 2018
      }

      "return 18 as the basic percentage rate for the tax year" in {
        TaxRatesAndBands.getRates(2018).basicRate mustBe 0.18
        TaxRatesAndBands.getRates(2018).basicRatePercentage mustBe 18
      }

      "return 28 as the higher percentage rate for the tax year" in {
        TaxRatesAndBands.getRates(2018).higherRate mustBe 0.28
        TaxRatesAndBands.getRates(2018).higherRatePercentage mustBe 28
      }

      "return 11100 as the maximum Annual Exempt Amount" in {
        TaxRatesAndBands.getRates(2018).maxAnnualExemptAmount mustBe 11300
      }

      "return 5550 as the non-vulnerable trustee Annual Exempt Amount" in {
        TaxRatesAndBands.getRates(2018).notVulnerableMaxAnnualExemptAmount mustBe 5650
      }

      "return 32000 as the basic rate band" in {
        TaxRatesAndBands.getRates(2018).basicRateBand mustBe 33500
      }

      "return 11000 as the maximum Personal Allowance" in {
        TaxRatesAndBands.getRates(2018).maxPersonalAllowance mustBe 11500
      }

    }

  }

  "calling the .filterRatesByTaxYear method" when {

    "passed a tax year of 2020" must {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2020)

      "return non-empty list" in {
        taxYearList.nonEmpty mustBe true
      }

      "return a list with a single value" in {
        taxYearList.size mustBe 1
      }

      "the listed item should be for the tax year 2020" in {
        taxYearList.head.taxYear mustBe 2020
      }
    }

    "passed a tax year of 2019" must {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2019)

      "return non-empty list" in {
        taxYearList.nonEmpty mustBe true
      }

      "return a list with a single value" in {
        taxYearList.size mustBe 1
      }

      "the listed item should be for the tax year 2019" in {
        taxYearList.head.taxYear mustBe 2019
      }
    }

    "passed a tax year of 2018" must {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2018)

      "return non-empty list" in {
        taxYearList.nonEmpty mustBe true
      }

      "return a list with a single value" in {
        taxYearList.size mustBe 1
      }

      "the listed item should be for the tax year 2018" in {
        taxYearList.head.taxYear mustBe 2018
      }
    }

    "passed a tax year of 2017" must {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2017)

      "return non-empty list" in {
        taxYearList.nonEmpty mustBe true
      }

      "return a list with a single value" in {
        taxYearList.size mustBe 1
      }

      "the listed item should be for the tax year 2017" in {
        taxYearList.head.taxYear mustBe 2017
      }
    }

    "passed a tax year of 2016" must {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2016)

      "return non-empty list" in {
        taxYearList.nonEmpty mustBe true
      }

      "return a list with a single value" in {
        taxYearList.size mustBe 1
      }

      "the listed item should be for the tax year 2016" in {
        taxYearList.head.taxYear mustBe 2016
      }
    }

    "passed a tax year of 2015" must {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2015)

      "return an empty list" in {
        taxYearList.isEmpty mustBe true
      }
    }
  }

  "calling .getClosestTaxYear" must {

    "return 2021 for a tax year input of 2021" in {
      TaxRatesAndBands.getClosestTaxYear(2021) mustBe 2021
    }

    "return 2020 for a tax year input of 2020" in {
      TaxRatesAndBands.getClosestTaxYear(2020) mustBe 2020
    }

    "return 2019 for a tax year input of 2019" in {
      TaxRatesAndBands.getClosestTaxYear(2019) mustBe 2019
    }

    "return 2018 for a tax year input of 2018" in {
      TaxRatesAndBands.getClosestTaxYear(2018) mustBe 2018
    }

    "return 2017 for a tax year input of 2017" in {
      TaxRatesAndBands.getClosestTaxYear(2017) mustBe 2017
    }

    "return the year 2016 for a tax year input of 2015" in {
      TaxRatesAndBands.getClosestTaxYear(2015) mustBe 2016
    }
  }

  "Calling .getEarliestTaxYear" must {

    "return the earliest tax year in the list" in {
      TaxRatesAndBands.getEarliestTaxYear mustBe TaxRatesAndBands20152016
    }
  }
}
