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

class QueryStringKeysSpec extends UnitSpec {
  import common.QueryStringKeys.{NonResidentCalculationKeys => target}
  import common.QueryStringKeys.{ResidentPropertiesCalculationKeys => propertyTarget}

  "NonResidentCalculationKeys" should {

    s"have a customer type key with the value ${target.customerType}" in {
      target.customerType shouldBe "customerType"
    }

    s"have a prior disposal key with the value ${target.priorDisposal}" in {
      target.priorDisposal shouldBe "priorDisposal"
    }

    s"have a annual exempt amount with the value ${target.annualExemptAmount}" in {
      target.annualExemptAmount shouldBe "annualExemptAmount"
    }

    s"have a other properties amount with the value ${target.otherPropertiesAmount}" in {
      target.otherPropertiesAmount shouldBe "otherPropertiesAmt"
    }
  }

  "ResidentPropertiesCalculationKeys" should {

    s"have a disposalValue key with the value ${propertyTarget.disposalValue}" in {
      propertyTarget.disposalValue shouldBe "disposalValue"
    }

    s"have a disposalCosts key with the value ${propertyTarget.disposalCosts}" in {
      propertyTarget.disposalCosts shouldBe "disposalCosts"
    }

    s"have a acquisitionValue key with the value ${propertyTarget.acquisitionValue}" in {
      propertyTarget.acquisitionValue shouldBe "acquisitionValue"
    }

    s"have a acquisitionCosts key with the value ${propertyTarget.acquisitionCosts}" in {
      propertyTarget.acquisitionCosts shouldBe "acquisitionCosts"
    }

    s"have a improvements key with the value ${propertyTarget.improvements}" in {
      propertyTarget.improvements shouldBe "improvements"
    }
  }
}
