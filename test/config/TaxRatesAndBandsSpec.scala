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

package config

import org.scalacheck.Prop.propBoolean
import org.scalacheck.{Gen, Prop}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckPropertyChecks}

import java.time.LocalDate

class TaxRatesAndBandsSpec extends PlaySpec with ScalaCheckPropertyChecks with Checkers {

  "validating non-year specific parameters" must {

    "return date formatted start of tax" in {
      TaxRatesAndBands.getRates(0).startOfTax mustEqual "2015-04-06"
      TaxRatesAndBands.getRates(0).startOfTaxLocalDate mustEqual LocalDate.parse("2015-04-06")
    }
  }

  "tax year config for given tax year validation" must {
    "success for provided table driven tax year fixtures" in {
      val expected = Table(
        (
          "taxYear",
          "basicRate",
          "basicRatePercentage",
          "higherRate",
          "higherRatePercentage",
          "maxAnnualExemptAmount",
          "notVulnerableMaxAnnualExemptAmount",
          "basicRateBand",
          "maxPersonalAllowance"
        ),
        (2018, 0.18, 18, 0.28, 28, 11300, 5650, 33500, 11500),
        (2019, 0.18, 18, 0.28, 28, 11700, 5850, 34500, 11850),
        (2020, 0.18, 18, 0.28, 28, 12000, 6000, 37500, 12500),
        (2021, 0.18, 18, 0.28, 28, 12300, 6000, 37500, 12500),
        (2022, 0.18, 18, 0.28, 28, 12300, 6150, 37700, 12570),
        (2023, 0.18, 18, 0.28, 28, 12300, 6150, 37700, 12570),
        (2024, 0.18, 18, 0.28, 28, 6000, 3000, 37700, 12570),
        (2025, 0.18, 18, 0.24, 24, 3000, 3000, 37700, 12570)
      )

      forAll(expected) {
        (
          taxYear,
          basicRate,
          basicRatePercentage,
          higherRate,
          higherRatePercentage,
          maxAnnualExemptAmount,
          notVulnerableMaxAnnualExemptAmount,
          basicRateBand,
          maxPersonalAllowance
        ) =>
          val taxRateAndBand = TaxRatesAndBands.allRates.filter(_.taxYear == taxYear).head

          taxRateAndBand.taxYear                            should equal(taxYear)
          taxRateAndBand.basicRate                          should equal(basicRate)
          taxRateAndBand.basicRatePercentage                should equal(basicRatePercentage)
          taxRateAndBand.higherRate                         should equal(higherRate)
          taxRateAndBand.higherRatePercentage               should equal(higherRatePercentage)
          taxRateAndBand.maxAnnualExemptAmount              should equal(maxAnnualExemptAmount)
          taxRateAndBand.notVulnerableMaxAnnualExemptAmount should equal(notVulnerableMaxAnnualExemptAmount)
          taxRateAndBand.basicRateBand                      should equal(basicRateBand)
          taxRateAndBand.maxPersonalAllowance               should equal(maxPersonalAllowance)
      }
    }
  }

  "tax rates config" when {
    val acceptedMinTaxYear = TaxRatesAndBands.liveTaxRates.head.taxYear
    val acceptedMaxTaxYear = TaxRatesAndBands.liveTaxRates.last.taxYear

    "checked for closest tax year" must {
      "return correct closest tax year" in {
        check {
          Prop.forAll(Gen.choose(Integer.MIN_VALUE, Integer.MAX_VALUE)) { taxYear =>
            val closestTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
            if (taxYear >= acceptedMinTaxYear && taxYear <= acceptedMaxTaxYear) {
              closestTaxYear == taxYear
            } else if (taxYear > acceptedMaxTaxYear) {
              closestTaxYear == acceptedMaxTaxYear
            } else {
              closestTaxYear == acceptedMinTaxYear
            }
          }
        }
      }
    }
  }

  "Calling .getEarliestTaxYear" must {

    "return the earliest tax year in the list" in {
      TaxRatesAndBands.getEarliestTaxYear mustBe TaxRatesAndBands20152016
    }
  }

  "Mid year tax changes for year 2025" must {
    "return tax rates for after effective date" in {
      val taxYear =
        TaxRatesAndBands.getRates(2025, Some(LocalDate.parse("2024-10-31")), isMidYearChangeApplicable = true)
      taxYear.shareBasicRatePercentage mustBe 18
      taxYear.shareHigherRatePercentage mustBe 24
    }

    "return tax rates for before effective date" in {
      val taxYear =
        TaxRatesAndBands.getRates(2025, Some(LocalDate.parse("2024-10-30")), isMidYearChangeApplicable = true)
      taxYear.shareBasicRatePercentage mustBe 10
      taxYear.shareHigherRatePercentage mustBe 20
    }
    "return tax rates for before 2025" in {
      val taxYear =
        TaxRatesAndBands.getRates(2018, Some(LocalDate.parse("2017-10-30")), isMidYearChangeApplicable = true)
      taxYear.taxYear mustBe 2018
    }

    "throw Runtime exception when disposal date is empty" in {
      val exception =
        intercept[RuntimeException](TaxRatesAndBands.getRates(2025, Option.empty, isMidYearChangeApplicable = true))
      exception            shouldBe a[RuntimeException]
      exception.getMessage shouldBe "Disposal date can not be empty"
    }
  }
}
