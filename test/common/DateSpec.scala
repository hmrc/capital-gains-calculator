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

import common.Date._
import org.scalatestplus.play.PlaySpec

import java.time.LocalDate

class DateSpec extends PlaySpec {

  "calling common.Date.daysBetween" must {

    "return 10 days when passing in String dates 1/1/2015 and 10/1/2015" in {
      val result = daysBetween("2015-1-1", "2015-1-10")
      result mustEqual 10
    }

    "return 1 day when passing in String dates 1/1/2015 and 1/1/2015" in {
      val result = daysBetween("2015-1-1", "2015-1-1")
      result mustEqual 1
    }

    "return 365 days when passing in String dates 1/1/2015 and 31/12/2015" in {
      val result = daysBetween("2015-1-1", "2015-12-31")
      result mustEqual 365
    }

    "return 366 days when passing in String dates 1/1/2016 and 31/12/2016 (leap year test)" in {
      val result = daysBetween("2016-1-1", "2016-12-31")
      result mustEqual 366
    }

    "return 10 days when passing in LocalDate dates 1/1/2015 and 10/1/2015" in {
      val result = daysBetween(LocalDate.parse("2015-01-01"), LocalDate.parse("2015-01-10"))
      result mustEqual 10
    }

    "return 1 day when passing in LocalDate dates 1/1/2015 and 1/1/2015" in {
      val result = daysBetween(LocalDate.parse("2015-01-01"), LocalDate.parse("2015-01-01"))
      result mustEqual 1
    }

    "return 365 days when passing in LocalDate dates 1/1/2015 and 31/12/2015" in {
      val result = daysBetween(LocalDate.parse("2015-01-01"), LocalDate.parse("2015-12-31"))
      result mustEqual 365
    }

    "return 366 days when passing in LocalDate dates 1/1/2016 and 31/12/2016 (leap year test)" in {
      val result = daysBetween(LocalDate.parse("2016-01-01"), LocalDate.parse("2016-12-31"))
      result mustEqual 366
    }
  }

  "calling getTaxYear" must {

    "return 2017 from a date after 5th April in 2016" in {
      val result = getTaxYear(LocalDate.parse("2016-10-10"))
      result mustBe 2017
    }

    "return 2017 from a date before 5th April in 2017" in {
      val result = getTaxYear(LocalDate.parse("2017-01-01"))
      result mustBe 2017
    }

    "return 2016 from a date after 5th April in 2015" in {
      val result = getTaxYear(LocalDate.parse("2015-10-10"))
      result mustBe 2016
    }

    "return 2016 from a date before 5th April in 2016" in {
      val result = getTaxYear(LocalDate.parse("2016-01-01"))
      result mustBe 2016
    }
  }

  "calling taxYearToString" must {

    "return a date of '2016/17' from 2017" in {
      val result = taxYearToString(2017)
      result mustBe "2016/17"
    }

    "return a date of '2015/16' from 2016" in {
      val result = taxYearToString(2016)
      result mustBe "2015/16"
    }
  }

  "Calling afterTaxStarted" must {

    "return a false with a date before the start date" in {
      val date   = LocalDate.parse("2015-04-04")
      val result = Date.afterTaxStarted(date)

      result mustBe false
    }

    "return a false with a date on the start date" in {
      val date   = LocalDate.parse("2015-04-05")
      val result = Date.afterTaxStarted(date)

      result mustBe false
    }

    "return a true with a date after the start date" in {
      val date   = LocalDate.parse("2015-04-06")
      val result = Date.afterTaxStarted(date)

      result mustBe true
    }
  }

  "Calling .taxYearStartDate" must {

    "return a value of 2015-04-05 from 2016" in {
      val result = Date.taxYearEndDate(2016)

      result mustBe LocalDate.parse("2015-04-05")
    }

    "return a value of 2016-04-05 from 2017" in {
      val result = Date.taxYearEndDate(2017)

      result mustBe LocalDate.parse("2016-04-05")
    }

    "return a value of 2014-04-05 from 2015" in {
      val result = Date.taxYearEndDate(2015)

      result mustBe LocalDate.parse("2014-04-05")
    }
  }
}
