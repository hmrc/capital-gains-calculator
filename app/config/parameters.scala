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

trait YearlyParameters {
  val taxYear: Int
  val maxAnnualExemptAmount: Int
  val notVulnerableMaxAnnualExemptAmount: Int
  val entrepreneursPercentage: Int
  val basicRatePercentage: Int
  val higherRatePercentage: Int
  val entrepreneursRate: Double
  val basicRate: Double
  val higherRate: Double
  val basicRateBand: Int
  val startOfTax = "2015-04-06"
  val startOfTaxDateTime = DateTime.parse("2015-04-06")
  val eighteenMonths = 18
}

object ParametersFor20162017 extends YearlyParameters {
  override val taxYear = 2016
  override val maxAnnualExemptAmount = 11100
  override val notVulnerableMaxAnnualExemptAmount = 5550
  override val entrepreneursPercentage = 10
  override val basicRatePercentage = 18
  override val higherRatePercentage = 28
  override val entrepreneursRate = entrepreneursPercentage / 100.toDouble
  override val basicRate = basicRatePercentage / 100.toDouble
  override val higherRate = higherRatePercentage / 100.toDouble
  override val basicRateBand = 32000
}

object YearlyParameters {
  val parameters = ParametersFor20162017 :: Nil

  def getParameters(year: Int): YearlyParameters = parameters.filter(_.taxYear == year) match {
    case params => params.head
    case _ => parameters.maxBy(_.taxYear)
  }
}