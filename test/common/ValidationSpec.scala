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

import uk.gov.hmrc.play.test.UnitSpec

class ValidationSpec extends UnitSpec {

  "Calling validationErrorMessage" should {

    "return the string from the first left" when {

      "provided with a single Left input" in {
        val seq = Seq(Left("First error string"))
        val result = Validation.getFirstErrorMessage(seq)

        result shouldBe "First error string"
      }

      "provided with a single Left input after the first element" in {
        val seq = Seq(Right(BigDecimal(0)), Left("First error string"))
        val result = Validation.getFirstErrorMessage(seq)

        result shouldBe "First error string"
      }

      "provided with a single Left input after a Right element with a string" in {
        val seq = Seq(Right("First valid string"), Left("First error string"))
        val result = Validation.getFirstErrorMessage(seq)

        result shouldBe "First error string"
      }

      "provided with multiple Left inputs" in {
        val seq = Seq(Right("First valid string"), Left("First error string"), Left("Second error string"))
        val result = Validation.getFirstErrorMessage(seq)

        result shouldBe "First error string"
      }
    }
  }
}
