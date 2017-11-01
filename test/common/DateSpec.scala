/*
 * Copyright 2017 HM Revenue & Customs
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
import uk.gov.hmrc.play.test.UnitSpec
import org.joda.time.{DateTime, Days}

class DateSpec extends UnitSpec {

  "calling common.Date.daysBetween" should {

    "return 10 days when passing in String dates 1/1/2015 and 10/1/2015" in {
      val result = daysBetween("2015-1-1", "2015-1-10")
      result shouldEqual 10
    }

    "return 1 day when passing in String dates 1/1/2015 and 1/1/2015" in {
      val result = daysBetween("2015-1-1", "2015-1-1")
      result shouldEqual 1
    }

    "return 365 days when passing in String dates 1/1/2015 and 31/12/2015" in {
      val result = daysBetween("2015-1-1","2015-12-31")
      result shouldEqual 365
    }

    "return 366 days when passing in String dates 1/1/2016 and 31/12/2016 (leap year test)" in {
      val result = daysBetween("2016-1-1", "2016-12-31")
      result shouldEqual 366
    }

    "return 10 days when passing in DateTime dates 1/1/2015 and 10/1/2015" in {
      val result = daysBetween(DateTime.parse("2015-1-1"), DateTime.parse("2015-1-10"))
      result shouldEqual 10
    }

    "return 1 day when passing in DateTime dates 1/1/2015 and 1/1/2015" in {
      val result = daysBetween(DateTime.parse("2015-1-1"), DateTime.parse("2015-1-1"))
      result shouldEqual 1
    }

    "return 365 days when passing in DateTime dates 1/1/2015 and 31/12/2015" in {
      val result = daysBetween(DateTime.parse("2015-1-1"), DateTime.parse("2015-12-31"))
      result shouldEqual 365
    }

    "return 366 days when passing in DateTime dates 1/1/2016 and 31/12/2016 (leap year test)" in {
      val result = daysBetween(DateTime.parse("2016-1-1"), DateTime.parse("2016-12-31"))
      result shouldEqual 366
    }
  }

  "calling getTaxYear" should {

    "return 2017 from a date after 5th April in 2016" in {
      val result = getTaxYear(DateTime.parse("2016-10-10"))
      result shouldBe 2017
    }

    "return 2017 from a date before 5th April in 2017" in {
      val result = getTaxYear(DateTime.parse("2017-01-01"))
      result shouldBe 2017
    }

    "return 2016 from a date after 5th April in 2015" in {
      val result = getTaxYear(DateTime.parse("2015-10-10"))
      result shouldBe 2016
    }

    "return 2016 from a date before 5th April in 2016" in {
      val result = getTaxYear(DateTime.parse("2016-01-01"))
      result shouldBe 2016
    }
  }

  "calling taxYearToString" should {

    "return a date of '2016/17' from 2017" in {
      val result = taxYearToString(2017)
      result shouldBe "2016/17"
    }

    "return a date of '2015/16' from 2016" in {
      val result = taxYearToString(2016)
      result shouldBe "2015/16"
    }
  }

  "Calling afterTaxStarted" should {

    "return a false with a date before the start date" in {
      val date = DateTime.parse("2015-04-04")
      val result = Date.afterTaxStarted(date)

      result shouldBe false
    }

    "return a false with a date on the start date" in {
      val date = DateTime.parse("2015-04-05")
      val result = Date.afterTaxStarted(date)

      result shouldBe false
    }

    "return a true with a date after the start date" in {
      val date = DateTime.parse("2015-04-06")
      val result = Date.afterTaxStarted(date)

      result shouldBe true
    }
  }

  "Calling .taxYearStartDate" should {

    "return a value of 2015-04-06 from 2016" in {
      val result = Date.taxYearStartDate(2016)

      result shouldBe DateTime.parse("2015-04-06")
    }

    "return a value of 2016-04-06 from 2017" in {
      val result = Date.taxYearStartDate(2017)

      result shouldBe DateTime.parse("2016-04-06")
    }

    "return a value of 2014-04-06 from 2015" in {
      val result = Date.taxYearStartDate(2015)

      result shouldBe DateTime.parse("2014-04-06")
    }
  }
}
