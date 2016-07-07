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

package config

import org.joda.time.DateTime

trait TaxRatesAndBands {
  val taxYear: Int
  val maxAnnualExemptAmount: Int
  val notVulnerableMaxAnnualExemptAmount: Int
  val basicRatePercentage: Int
  val higherRatePercentage: Int
  val maxPersonalAllowance: Int
  val basicRate: Double
  val higherRate: Double
  val basicRateBand: Int
  val blindPersonsAllowance: Int
  val startOfTax = "2015-04-06"
  val startOfTaxDateTime = DateTime.parse(startOfTax)
  val eighteenMonths = 18
}

object TaxRatesAndBands {
  val rates = TaxRatesAndBands20162017 :: TaxRatesAndBands20152016 :: Nil

  def getRates(year: Int): TaxRatesAndBands = rates.filter(_.taxYear == year) match {
    case params if params.nonEmpty => params.head
    case _ => rates.maxBy(_.taxYear)
  }
}

object TaxRatesAndBands20162017 extends TaxRatesAndBands {
  override val taxYear = 2017
  override val maxAnnualExemptAmount = 11100
  override val notVulnerableMaxAnnualExemptAmount = 5550
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val maxPersonalAllowance = 11000
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val basicRateBand = 32000
  override val blindPersonsAllowance = 2290
}

object TaxRatesAndBands20152016 extends TaxRatesAndBands {
  override val taxYear = 2016
  override val maxAnnualExemptAmount = 11100
  override val notVulnerableMaxAnnualExemptAmount = 5550
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val maxPersonalAllowance = 10600
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val basicRateBand = 31785
  override val blindPersonsAllowance = 2290
}