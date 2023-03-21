/*
 * Copyright 2023 HM Revenue & Customs
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

import common.Math._
import org.scalatestplus.play.PlaySpec

class MathSpec extends PlaySpec {

  "Calling common.Math.round" must {

    "return rounded down whole number of 1000 for value of 1000.01 for UK Taxable Income" in {
      val result = round("down", 1000.01)
      result mustEqual 1000
    }

    "return rounded down whole number of 1000 for value of 1000.99 for Disposable Proceeds" in {
      val result = round("down", 1000.99)
      result mustEqual 1000
    }

    "return rounded up whole number of 101 for value of 100.01 for AEA remaining" in {
      val result = round("up", 100.01)
      result mustEqual 101
    }

    "return rounded up whole number of 100 for value of 99.01 for Acquisition cost" in {
      val result = round("up", 99.01)
      result mustEqual 100
    }

    "return rounded up whole number of 90 for value of 89.49 for Incidental Acquisition cost" in {
      val result = round("up", 89.49)
      result mustEqual 90
    }

    "return rounded up whole number of 90 for value of 41 for Enhancement costs" in {
      val result = round("up", 40.01)
      result mustEqual 41
    }

    "return rounded up whole number of 91 for value of 90.01 for Incidental Disposal cost" in {
      val result = round("up", 90.01)
      result mustEqual 91
    }

    "return rounded up whole number of 55 for value of 54.49 for Allowable Losses" in {
      val result = round("up", 54.49)
      result mustEqual 55
    }

    "return rounded up whole number of 31 for value of 30.01 for Reliefs" in {
      val result = round("up", 30.01)
      result mustEqual 31
    }

    "return rounded down decimal of 5000.87 for value of 5000.87666 for Flat Rate output" in {
      val result = round("result", 5000.87666)
      result mustEqual 5000.87
    }

    "return rounded down decimal of 5000.99 for value of 5000.9999 for Flat Rate output" in {
      val result = round("result", 5000.9999)
      result mustEqual 5000.99
    }

    "return rounded down decimal of 500 for value of 500.99 rounding a gain" in {
      val result = round("gain", 500.99)
      result mustEqual 500
    }

    "return rounded down decimal of 500 for value of 500.01 rounding a gain" in {
      val result = round("gain", 500.01)
      result mustEqual 500
    }

    "return rounded down decimal of -500 for value of -499.01 when rounding a gain" in {
      val result = round("gain", -499.01)
      result mustEqual -500
    }

    "return rounded down decimal of -500 for value of -499.99 when rounding a gain" in {
      val result = round("gain", -499.99)
      result mustEqual -500
    }
  }

  "calling common.Math.negativeToZero" must {

    "return 0 when -0.01 is supplied" in {
      negativeToZero(-0.01) mustEqual 0
    }
    "return 0 when 0.00 is supplied" in {
      negativeToZero(0.00) mustEqual 0
    }
    "return 0.01 when 0.01 is supplied" in {
      negativeToZero(0.01) mustEqual 0.01
    }
  }

  "calling common.Math.negativeToNone" must {

    "return None when -0.01 is supplied" in {
      negativeToNone(-0.01) mustEqual None
    }
    "return 0 when 0.00 is supplied" in {
      negativeToNone(0.00) mustEqual Some(0)
    }
    "return 0.01 when 0.01 is supplied" in {
      negativeToNone(0.01) mustEqual Some(0.01)
    }
  }

  "calling common.Math.min" must {

    "return -1 when -1 and 0 are supplied" in {
      min(-1,0) mustEqual -1
    }

    "return 0 when 0 and 0 are supplied" in {
      min(0,0) mustEqual 0
    }

    "return 0 when 1 and 0 are supplied" in {
      min(1,0) mustEqual 0
    }

    "return 1 when 1 and 2 are supplied" in {
      min(1,2) mustEqual 1
    }
  }

}
