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

import com.typesafe.config.ConfigFactory

import java.time.LocalDate

trait TaxRatesAndBands {
  val taxYear: Int
  val maxAnnualExemptAmount: Int
  val notVulnerableMaxAnnualExemptAmount: Int
  val basicRatePercentage: Int
  val higherRatePercentage: Int
  val shareBasicRatePercentage: Int
  val shareHigherRatePercentage: Int
  val maxPersonalAllowance: Int
  val basicRate: Double
  val higherRate: Double
  val shareBasicRate: Double
  val shareHigherRate: Double
  val basicRateBand: Int
  val blindPersonsAllowance: Int
  val marriageAllowance = 1260
  val maxLettingsRelief: Double
  val startOfTax = "2015-04-06"
  val startOfTaxLocalDate: LocalDate = LocalDate.parse(startOfTax)
  val effectiveDate: Option[LocalDate] = None
}

object TaxRatesAndBands {

  val latestTaxYearGoLiveDate: LocalDate = LocalDate.parse(ConfigFactory.load().getString("latest-tax-year-go-live-date"))

  val allRates: List[TaxRatesAndBands] = TaxRatesAndBands20152016 :: TaxRatesAndBands20162017 :: TaxRatesAndBands20172018 ::
    TaxRatesAndBands20182019 :: TaxRatesAndBands20192020 :: TaxRatesAndBands20202021 :: TaxRatesAndBands20212022 ::
    TaxRatesAndBands20222023 :: TaxRatesAndBands20232024 :: TaxRatesAndBands20242025 :: TaxRatesAndBands20242025MidYearChange :: Nil

  val liveTaxRates: List[TaxRatesAndBands] = if (LocalDate.now.isBefore(latestTaxYearGoLiveDate)) allRates.dropRight(1) else allRates

  def getRates(year: Int, disposalDate : Option[LocalDate] = None , isMidYearChangeApplicable : Boolean = false): TaxRatesAndBands = {
    liveTaxRates.filter(_.taxYear == year) match {
      case params if params.size > 1 && isMidYearChangeApplicable => getTaxRatesAndBandsForMidYearChange(params, disposalDate)
      case params if params.nonEmpty => params.head
      case _ => liveTaxRates.maxBy(_.taxYear)
    }
  }

  private def getTaxRatesAndBandsForMidYearChange(liveTaxRates : List[TaxRatesAndBands], disposalDate: Option[LocalDate]): TaxRatesAndBands = {
    liveTaxRates.filter(_.effectiveDate.nonEmpty) match {
      case effectiveTaxBands if effectiveTaxBands.size > 1 =>
        throw new RuntimeException("Invalid tax band configuration. No support for multiple effective tax bands in a tax year")
      case _ if disposalDate.isEmpty =>
        throw new RuntimeException("Disposal date can not be empty")
      case _ =>
        liveTaxRates.find(_.effectiveDate.nonEmpty) match {
          case Some(changedTaxRates) if changedTaxRates.effectiveDate.get.isBefore(disposalDate.get.plusDays(1)) => changedTaxRates
          case _ => liveTaxRates.head
        }
    }
  }

  def filterRatesByTaxYear (taxYear: Int): List[TaxRatesAndBands] = {
    liveTaxRates.filter(_.taxYear == taxYear)
  }

  def getClosestTaxYear (taxYear: Int): Int = {
    val validYears = liveTaxRates.map(_.taxYear)
    validYears.minBy(year => math.abs(year - taxYear))
  }

  def getEarliestTaxYear: TaxRatesAndBands = liveTaxRates.minBy(_.taxYear)

}

object TaxRatesAndBands20242025MidYearChange extends TaxRatesAndBands {
  override val taxYear = 2025
  override val maxAnnualExemptAmount = 3000
  override val notVulnerableMaxAnnualExemptAmount = 3000
  override val basicRatePercentage = 18
  override val higherRatePercentage = 24
  override val shareBasicRatePercentage = 18
  override val shareHigherRatePercentage = 24
  override val maxPersonalAllowance = 12570
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 37700
  override val blindPersonsAllowance = 2870
  override val maxLettingsRelief = 40000.0
  override val effectiveDate = Some(LocalDate.parse(ConfigFactory.load().getString("mid-year-tax-change-effective-date")))
}

object TaxRatesAndBands20242025 extends TaxRatesAndBands {
  override val taxYear = 2025
  override val maxAnnualExemptAmount = 3000
  override val notVulnerableMaxAnnualExemptAmount = 3000
  override val basicRatePercentage = 18
  override val higherRatePercentage = 24
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 12570
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 37700
  override val blindPersonsAllowance = 2870
  override val maxLettingsRelief = 40000.0
}

object TaxRatesAndBands20232024 extends TaxRatesAndBands {
  override val taxYear = 2024
  override val maxAnnualExemptAmount = 6000
  override val notVulnerableMaxAnnualExemptAmount = 3000
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 12570
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 37700
  override val blindPersonsAllowance = 2870
  override val maxLettingsRelief = 40000.0
}

object TaxRatesAndBands20222023 extends TaxRatesAndBands {
  override val taxYear = 2023
  override val maxAnnualExemptAmount = 12300
  override val notVulnerableMaxAnnualExemptAmount = 6150
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 12570
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 37700
  override val blindPersonsAllowance = 2600
  override val maxLettingsRelief = 40000.0
}

object TaxRatesAndBands20212022 extends TaxRatesAndBands {
  override val taxYear = 2022
  override val maxAnnualExemptAmount = 12300
  override val notVulnerableMaxAnnualExemptAmount = 6150
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 12570
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 37700
  override val blindPersonsAllowance = 2520
  override val maxLettingsRelief = 40000.0
}

object TaxRatesAndBands20202021 extends TaxRatesAndBands {
  override val taxYear = 2021
  override val maxAnnualExemptAmount = 12300
  override val notVulnerableMaxAnnualExemptAmount = 6000
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 12500
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 37500
  override val blindPersonsAllowance = 2390
  override val maxLettingsRelief = 40000.0
}

object TaxRatesAndBands20192020 extends TaxRatesAndBands {
  override val taxYear = 2020
  override val maxAnnualExemptAmount = 12000
  override val notVulnerableMaxAnnualExemptAmount = 6000
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 12500
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 37500
  override val blindPersonsAllowance = 2390
  override val maxLettingsRelief = 40000.0
}


object TaxRatesAndBands20182019 extends TaxRatesAndBands {
  override val taxYear = 2019
  override val maxAnnualExemptAmount = 11700
  override val notVulnerableMaxAnnualExemptAmount = 5850
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 11850
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 34500
  override val blindPersonsAllowance = 2390
  override val maxLettingsRelief = 40000.0
}

object TaxRatesAndBands20172018 extends TaxRatesAndBands {
  override val taxYear = 2018
  override val maxAnnualExemptAmount = 11300
  override val notVulnerableMaxAnnualExemptAmount = 5650
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 11500
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 33500
  override val blindPersonsAllowance = 2320
  override val maxLettingsRelief = 40000.0
}

object TaxRatesAndBands20162017 extends TaxRatesAndBands {
  override val taxYear = 2017
  override val maxAnnualExemptAmount = 11100
  override val notVulnerableMaxAnnualExemptAmount = 5550
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 10
  override val shareHigherRatePercentage = 20
  override val maxPersonalAllowance = 11000
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 32000
  override val blindPersonsAllowance = 2290
  override val maxLettingsRelief = 40000.0
}

object TaxRatesAndBands20152016 extends TaxRatesAndBands {
  override val taxYear = 2016
  override val maxAnnualExemptAmount = 11100
  override val notVulnerableMaxAnnualExemptAmount = 5550
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val shareBasicRatePercentage = 18
  override val shareHigherRatePercentage = 28
  override val maxPersonalAllowance = 10600
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val shareBasicRate = shareBasicRatePercentage / 100.toDouble
  override val shareHigherRate = shareHigherRatePercentage / 100.toDouble
  override val basicRateBand = 31785
  override val blindPersonsAllowance = 2290
  override val maxLettingsRelief = 40000.0
}
