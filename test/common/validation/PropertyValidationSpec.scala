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

/**
  * Copyright 2016 HM Revenue & Customs
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package common.validation

import models.resident.properties.PropertyTotalGainModel
import models.resident.shares.TotalGainModel
import uk.gov.hmrc.play.test.UnitSpec

class PropertyValidationSpec extends UnitSpec {

  "Calling validatePropertyTotalGain" should {

    val validTotalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
    val inValidTotalGainModel = TotalGainModel(-1000.0, 1500.0, 2000.0, 2500.0)

    "return a Right with all validation passing" in {
      val model = PropertyTotalGainModel(validTotalGainModel, 1000.0)
      val result = PropertyValidation.validatePropertyTotalGain(model)

      result shouldBe Right(model)
    }

    "return a Left with an invalid TotalGainModel" in {
      val model = PropertyTotalGainModel(inValidTotalGainModel, 1000.0)
      val result = PropertyValidation.validatePropertyTotalGain(model)

      result shouldBe Left("disposalValue cannot be negative.")
    }

    "return a Left with improvements validation failing" in {
      val model = PropertyTotalGainModel(validTotalGainModel, -1000.0)
      val result = PropertyValidation.validatePropertyTotalGain(model)

      result shouldBe Left("improvements cannot be negative.")
    }

    "return a Left with a the TotalGainsModel and improvements both failing validation" in {
      val model = PropertyTotalGainModel(inValidTotalGainModel, -1000.0)
      val result = PropertyValidation.validatePropertyTotalGain(model)

      result shouldBe Left("disposalValue cannot be negative.")
    }
  }
}
