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

package common

import common.Date._
import uk.gov.hmrc.play.test.UnitSpec

class DateSpec extends UnitSpec {

  "calling common.Date.monthsBetween" should {

    "return 10 days when passing in dates 1/1/2015 and 10/1/2015" in {
      val result = daysBetween("2015-1-1", "2015-1-10")
      result shouldEqual 10
    }

    "return 1 day when passing in the dates 1/1/2015 and 1/1/2015" in {
      val result = daysBetween("2015-1-1", "2015-1-1")
      result shouldEqual 1
    }

    "return 365 days when passing in the dates 1/1/2015 and 31/12/2015" in {
      val result = daysBetween("2015-1-1","2015-12-31")
      result shouldEqual 365
    }

    "return 366 days when passing in the dates 1/1/2016 and 31/12/2016 (leap year test)" in {
      val result = daysBetween("2016-1-1", "2016-12-31")
      result shouldEqual 366
    }
  }
}
