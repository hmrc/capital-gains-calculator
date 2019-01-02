/*
 * Copyright 2019 HM Revenue & Customs
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

import org.joda.time.{DateTime, Days}

object Date {

  val taxYearEnd = "04-05"
  val taxYearStart = "04-06"
  val taxStartDate = DateTime.parse("2015-04-05")

  def daysBetween(start: String, end: String): Double = {
    Days.daysBetween(DateTime.parse(start), DateTime.parse(end)).getDays + 1
  }

  def daysBetween(start: DateTime, end: DateTime): Double = {
    Days.daysBetween(start, end).getDays + 1
  }

  def getTaxYear(date: DateTime): Int = {
    val year = date.getYear
    if (date.isAfter(DateTime.parse(s"${year.toString}-$taxYearEnd"))) {
      year + 1
    }
    else {
      year
    }
  }

  def taxYearToString(input: Int): String = {
    val startYear = input - 1
    val endYear = input.toString.takeRight(2)
    s"$startYear/$endYear"
  }

  def afterTaxStarted(date: DateTime): Boolean = {
    date.isAfter(taxStartDate)
  }

  def taxYearEndDate(input: Int): DateTime = DateTime.parse(
    s"${input -1}-$taxYearEnd"
  )
}
