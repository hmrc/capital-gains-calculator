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

case class TaxRatesAndBands(
  taxYear: Int,
  maxAnnualExemptAmount: Int,
  notVulnerableMaxAnnualExemptAmount: Int,
  basicRatePercentage: Int,
  higherRatePercentage: Int,
  shareBasicRatePercentage: Int,
  shareHigherRatePercentage: Int,
  maxPersonalAllowance: Int,
  basicRateBand: Int,
  blindPersonsAllowance: Int,
  maxLettingsRelief: Double,
  marriageAllowance: Int = 1260,
  startOfTax: String = "2015-04-06",
  effectiveDate: Option[LocalDate] = None
) {
  val basicRate: Double              = basicRatePercentage / 100.toDouble
  val higherRate: Double             = higherRatePercentage / 100.toDouble
  val shareBasicRate: Double         = shareBasicRatePercentage / 100.toDouble
  val shareHigherRate: Double        = shareHigherRatePercentage / 100.toDouble
  val startOfTaxLocalDate: LocalDate = LocalDate.parse(startOfTax)
}

object TaxRatesAndBands {

  val latestTaxYearGoLiveDate: LocalDate =
    LocalDate.parse(ConfigFactory.load().getString("latest-tax-year-go-live-date"))

  val allRates: List[TaxRatesAndBands] =
    TaxRatesAndBands20152016 :: TaxRatesAndBands20162017 :: TaxRatesAndBands20172018 ::
      TaxRatesAndBands20182019 :: TaxRatesAndBands20192020 :: TaxRatesAndBands20202021 :: TaxRatesAndBands20212022 ::
      TaxRatesAndBands20222023 :: TaxRatesAndBands20232024 :: TaxRatesAndBands20242025 :: TaxRatesAndBands20242025MidYearChange ::
      TaxRatesAndBands20252026 :: Nil

  val liveTaxRates: List[TaxRatesAndBands] =
    if (LocalDate.now.isBefore(latestTaxYearGoLiveDate)) allRates.dropRight(1) else allRates

  def filterRatesByTaxYear(taxYear: Int): List[TaxRatesAndBands] =
    liveTaxRates.filter(_.taxYear == taxYear)

  def getRates(
    year: Int,
    disposalDate: Option[LocalDate] = None,
    isMidYearChangeApplicable: Boolean = false
  ): TaxRatesAndBands =
    getRates(year, liveTaxRates, disposalDate, isMidYearChangeApplicable)

  private[config] def getRates(
    year: Int,
    rates: List[TaxRatesAndBands],
    disposalDate: Option[LocalDate],
    isMidYearChangeApplicable: Boolean
  ): TaxRatesAndBands =
    rates.filter(_.taxYear == year) match {
      case params if params.size > 1 && isMidYearChangeApplicable =>
        getTaxRatesAndBandsForMidYearChange(params, disposalDate)
      case params if params.nonEmpty                              => params.head
      case _                                                      => latestRatesUpTo(rates, year).copy(taxYear = year, effectiveDate = None)
    }

  private[config] def latestRatesUpTo(rates: List[TaxRatesAndBands], year: Int): TaxRatesAndBands =
    rates.filter(_.taxYear <= year).lastOption.getOrElse(rates.last)

  private def getTaxRatesAndBandsForMidYearChange(
    liveTaxRates: List[TaxRatesAndBands],
    disposalDate: Option[LocalDate]
  ): TaxRatesAndBands =
    liveTaxRates.filter(_.effectiveDate.nonEmpty) match {
      case effectiveTaxBands if effectiveTaxBands.size > 1 =>
        throw new RuntimeException(
          "Invalid tax band configuration. No support for multiple effective tax bands in a tax year"
        )
      case _ if disposalDate.isEmpty                       =>
        throw new RuntimeException("Disposal date can not be empty")
      case _                                               =>
        liveTaxRates.find(_.effectiveDate.nonEmpty) match {
          case Some(changedTaxRates) if changedTaxRates.effectiveDate.get.isBefore(disposalDate.get.plusDays(1)) =>
            changedTaxRates
          case _                                                                                                 => liveTaxRates.head
        }
    }

  def getClosestTaxYear(taxYear: Int): Int = {
    val validYears = liveTaxRates.map(_.taxYear)
    validYears.minBy(year => math.abs(year - taxYear))
  }

  def getEarliestTaxYear: TaxRatesAndBands = liveTaxRates.minBy(_.taxYear)

}

def TaxRatesAndBands20252026: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2026,
    maxAnnualExemptAmount = 3000,
    notVulnerableMaxAnnualExemptAmount = 1500,
    basicRatePercentage = 18,
    higherRatePercentage = 24,
    shareBasicRatePercentage = 18,
    shareHigherRatePercentage = 24,
    maxPersonalAllowance = 12570,
    basicRateBand = 37700,
    blindPersonsAllowance = 2870,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20242025MidYearChange: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2025,
    maxAnnualExemptAmount = 3000,
    notVulnerableMaxAnnualExemptAmount = 1500,
    basicRatePercentage = 18,
    higherRatePercentage = 24,
    shareBasicRatePercentage = 18,
    shareHigherRatePercentage = 24,
    maxPersonalAllowance = 12570,
    basicRateBand = 37700,
    blindPersonsAllowance = 2870,
    maxLettingsRelief = 40000.0,
    effectiveDate = Some(LocalDate.parse(ConfigFactory.load().getString("mid-year-tax-change-effective-date")))
  )

def TaxRatesAndBands20242025: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2025,
    maxAnnualExemptAmount = 3000,
    notVulnerableMaxAnnualExemptAmount = 3000,
    basicRatePercentage = 18,
    higherRatePercentage = 24,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 12570,
    basicRateBand = 37700,
    blindPersonsAllowance = 2870,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20232024: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2024,
    maxAnnualExemptAmount = 6000,
    notVulnerableMaxAnnualExemptAmount = 3000,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 12570,
    basicRateBand = 37700,
    blindPersonsAllowance = 2870,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20222023: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2023,
    maxAnnualExemptAmount = 12300,
    notVulnerableMaxAnnualExemptAmount = 6150,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 12570,
    basicRateBand = 37700,
    blindPersonsAllowance = 2600,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20212022: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2022,
    maxAnnualExemptAmount = 12300,
    notVulnerableMaxAnnualExemptAmount = 6150,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 12570,
    basicRateBand = 37700,
    blindPersonsAllowance = 2520,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20202021: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2021,
    maxAnnualExemptAmount = 12300,
    notVulnerableMaxAnnualExemptAmount = 6000,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 12500,
    basicRateBand = 37500,
    blindPersonsAllowance = 2390,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20192020: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2020,
    maxAnnualExemptAmount = 12000,
    notVulnerableMaxAnnualExemptAmount = 6000,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 12500,
    basicRateBand = 37500,
    blindPersonsAllowance = 2390,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20182019: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2019,
    maxAnnualExemptAmount = 11700,
    notVulnerableMaxAnnualExemptAmount = 5850,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 11850,
    basicRateBand = 34500,
    blindPersonsAllowance = 2390,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20172018: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2018,
    maxAnnualExemptAmount = 11300,
    notVulnerableMaxAnnualExemptAmount = 5650,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 11500,
    basicRateBand = 33500,
    blindPersonsAllowance = 2320,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20162017: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2017,
    maxAnnualExemptAmount = 11100,
    notVulnerableMaxAnnualExemptAmount = 5550,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 10,
    shareHigherRatePercentage = 20,
    maxPersonalAllowance = 11000,
    basicRateBand = 32000,
    blindPersonsAllowance = 2290,
    maxLettingsRelief = 40000.0
  )

def TaxRatesAndBands20152016: TaxRatesAndBands =
  TaxRatesAndBands(
    taxYear = 2016,
    maxAnnualExemptAmount = 11100,
    notVulnerableMaxAnnualExemptAmount = 5550,
    basicRatePercentage = 18,
    higherRatePercentage = 28,
    shareBasicRatePercentage = 18,
    shareHigherRatePercentage = 28,
    maxPersonalAllowance = 10600,
    basicRateBand = 31785,
    blindPersonsAllowance = 2290,
    maxLettingsRelief = 40000.0
  )
