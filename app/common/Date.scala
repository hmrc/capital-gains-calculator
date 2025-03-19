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

package common

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit._

object Date {

  val taxYearEnd                 = "04-05"
  val taxYearStart               = "04-06"
  val taxStartDate               = LocalDate.parse("2015-04-05")
  val PRRDeductionApplicableDate = LocalDate.parse("2020-04-05")

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d")

  def daysBetween(start: String, end: String): Double =
    (DAYS.between(LocalDate.parse(start, dateFormatter), LocalDate.parse(end, dateFormatter)) + 1).toDouble

  def daysBetween(start: LocalDate, end: LocalDate): Double =
    (DAYS.between(start, end) + 1).toDouble

  def getTaxYear(date: LocalDate): Int = {
    val year = date.getYear
    if (date.isAfter(LocalDate.parse(s"${year.toString}-$taxYearEnd"))) {
      year + 1
    } else {
      year
    }
  }

  def taxYearToString(input: Int): String = {
    val startYear = input - 1
    val endYear   = input.toString.takeRight(2)
    s"$startYear/$endYear"
  }

  def afterTaxStarted(date: LocalDate): Boolean =
    date.isAfter(taxStartDate)

  def taxYearEndDate(input: Int): LocalDate = LocalDate.parse(
    s"${input - 1}-$taxYearEnd"
  )
}
