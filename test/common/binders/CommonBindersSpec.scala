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

package common.binders

import java.time.LocalDate

import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec

class CommonBindersSpec extends CommonBinders with UnitSpec with MockitoSugar {

  val binder = localDateBinder

  "Binding to a localDateBinder" should {

    "return a LocalDate with a valid map" in {
      val map = Map("disposalDate" -> Seq("2016-05-04"))
      val result = binder.bind("disposalDate", map)

      result shouldBe Some(Right(LocalDate.parse("2016-05-04")))
    }

    "return an error message with a valid map but invalid date" in {
      val map = Map("disposalDate" -> Seq("not-a-date"))
      val result = binder.bind("disposalDate", map)

      result shouldBe Some(Left("""Cannot parse parameter disposalDate as LocalDate: For input string: "not-a-date""""))
    }

    "return an error message with an invalid map with empty value" in {
      val map = Map("disposalDate" -> Seq())
      val result = binder.bind("disposalDate", map)

      result shouldBe None
    }

    "return an error message with an invalid map with no value" in {
      val map = Map("test" -> Seq())
      val result = binder.bind("disposalDate", map)

      result shouldBe None
    }
  }

  "Unbinding using the localDateBinder" should {

    "return a valid String from a LocalDate" in {
      val date = LocalDate.parse("2015-06-04")
      val result = binder.unbind("disposalDate", date)

      result shouldBe "disposalDate=2015-6-4"
    }
  }
}