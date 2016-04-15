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

package services

import models.CalculationResultModel
import uk.gov.hmrc.play.test.UnitSpec

class CalculationServiceSpec extends UnitSpec {

  "Calling CalculationService" should {
    "return the sum of two numbers" in {
      val result = CalculationService.add(20, 10)
      result shouldEqual CalculationResultModel(20, 10, 20 + 10)
    }
  }

}