/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class TaxRatesAndBandsSpec extends UnitSpec with WithFakeApplication {

  "validating non-year specific parameters" should {

    "return 2015-04-06 string for the start of the tax" in {
      TaxRatesAndBands.getRates(0).startOfTax shouldEqual "2015-04-06"
    }

    "return a date of 2015-04-06 for the start of the tax date" in {
      TaxRatesAndBands.getRates(0).startOfTaxDateTime shouldEqual DateTime.parse("2015-04-06")
    }

    "return 18 as the value of eighteenMonths" in {
      TaxRatesAndBands.getRates(0).eighteenMonths shouldEqual 18
    }

  }

  "validating 2016/2017 parameters" should {

    "return 2017 for the tax year" in {
      TaxRatesAndBands20162017.taxYear shouldBe 2017
    }

    "return 18 as the basic percentage rate for the tax year" in {
      TaxRatesAndBands20162017.basicRate shouldBe 0.18
      TaxRatesAndBands20162017.basicRatePercentage shouldBe 18
    }

    "return 28 as the higher percentage rate for the tax year" in {
      TaxRatesAndBands20162017.higherRate shouldBe 0.28
      TaxRatesAndBands20162017.higherRatePercentage shouldBe 28
    }

    "return 11100 as the maximum Annual Excempt Amount" in {
      TaxRatesAndBands20162017.maxAnnualExemptAmount shouldBe 11100
    }

    "return 5550 as the non-vulnerable trustee Annual Exempt Amount" in {
      TaxRatesAndBands20162017.notVulnerableMaxAnnualExemptAmount shouldBe 5550
    }

    "return 32000 as the basic rate band" in {
      TaxRatesAndBands20162017.basicRateBand shouldBe 32000
    }

    "return 11000 as the maximum Personal Allowance" in {
      TaxRatesAndBands20162017.maxPersonalAllowance shouldBe 11000
    }

  }

  "Validating the getParameters method" when {

    "calling with the year 2017" should {

      "return 2017 for the tax year" in {
        TaxRatesAndBands.getRates(2017).taxYear shouldBe 2017
      }

      "return 18 as the basic percentage rate for the tax year" in {
        TaxRatesAndBands.getRates(2017).basicRate shouldBe 0.18
        TaxRatesAndBands.getRates(2017).basicRatePercentage shouldBe 18
      }

      "return 28 as the higher percentage rate for the tax year" in {
        TaxRatesAndBands.getRates(2017).higherRate shouldBe 0.28
        TaxRatesAndBands.getRates(2017).higherRatePercentage shouldBe 28
      }

      "return 11100 as the maximum Annual Exempt Amount" in {
        TaxRatesAndBands.getRates(2017).maxAnnualExemptAmount shouldBe 11100
      }

      "return 5550 as the non-vulnerable trustee Annual Exempt Amount" in {
        TaxRatesAndBands.getRates(2017).notVulnerableMaxAnnualExemptAmount shouldBe 5550
      }

      "return 32000 as the basic rate band" in {
        TaxRatesAndBands.getRates(2017).basicRateBand shouldBe 32000
      }

      "return 11000 as the maximum Personal Allowance" in {
        TaxRatesAndBands.getRates(2017).maxPersonalAllowance shouldBe 11000
      }

    }

    "calling with the year 2018" should {

      "return 2018 for the tax year" in {
        TaxRatesAndBands.getRates(2018).taxYear shouldBe 2018
      }

      "return 18 as the basic percentage rate for the tax year" in {
        TaxRatesAndBands.getRates(2018).basicRate shouldBe 0.18
        TaxRatesAndBands.getRates(2018).basicRatePercentage shouldBe 18
      }

      "return 28 as the higher percentage rate for the tax year" in {
        TaxRatesAndBands.getRates(2018).higherRate shouldBe 0.28
        TaxRatesAndBands.getRates(2018).higherRatePercentage shouldBe 28
      }

      "return 11100 as the maximum Annual Exempt Amount" in {
        TaxRatesAndBands.getRates(2018).maxAnnualExemptAmount shouldBe 11300
      }

      "return 5550 as the non-vulnerable trustee Annual Exempt Amount" in {
        TaxRatesAndBands.getRates(2018).notVulnerableMaxAnnualExemptAmount shouldBe 5650
      }

      "return 32000 as the basic rate band" in {
        TaxRatesAndBands.getRates(2018).basicRateBand shouldBe 33500
      }

      "return 11000 as the maximum Personal Allowance" in {
        TaxRatesAndBands.getRates(2018).maxPersonalAllowance shouldBe 11500
      }

    }

  }

  "calling the .filterRatesByTaxYear method" when {

    "passed a tax year of 2018" should {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2018)

      "return non-empty list" in {
        taxYearList.nonEmpty shouldBe true
      }

      "return a list with a single value" in {
        taxYearList.size shouldBe 1
      }

      "the listed item should be for the tax year 2018" in {
        taxYearList.head.taxYear shouldBe 2018
      }
    }

    "passed a tax year of 2017" should {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2017)

      "return non-empty list" in {
        taxYearList.nonEmpty shouldBe true
      }

      "return a list with a single value" in {
        taxYearList.size shouldBe 1
      }

      "the listed item should be for the tax year 2017" in {
        taxYearList.head.taxYear shouldBe 2017
      }
    }

    "passed a tax year of 2016" should {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2016)

      "return non-empty list" in {
        taxYearList.nonEmpty shouldBe true
      }

      "return a list with a single value" in {
        taxYearList.size shouldBe 1
      }

      "the listed item should be for the tax year 2016" in {
        taxYearList.head.taxYear shouldBe 2016
      }
    }

    "passed a tax year of 2015" should {
      val taxYearList = TaxRatesAndBands.filterRatesByTaxYear(2015)

      "return an empty list" in {
        taxYearList.isEmpty shouldBe true
      }
    }
  }

  "calling .getClosestTaxYear" should {

    "return 2018 for a tax year input of 2019" in {
      TaxRatesAndBands.getClosestTaxYear(2019) shouldBe 2018
    }

    "return 2018 for a tax year input of 2018" in {
      TaxRatesAndBands.getClosestTaxYear(2018) shouldBe 2018
    }

    "return 2017 for a tax year input of 2017" in {
      TaxRatesAndBands.getClosestTaxYear(2017) shouldBe 2017
    }

    "return the year 2016 for a tax year input of 2015" in {
      TaxRatesAndBands.getClosestTaxYear(2015) shouldBe 2016
    }
  }

  "Calling .getEarliestTaxYear" should {

    "return the earliest tax year in the list" in {
      TaxRatesAndBands.getEarliestTaxYear shouldBe TaxRatesAndBands20152016
    }
  }
}
