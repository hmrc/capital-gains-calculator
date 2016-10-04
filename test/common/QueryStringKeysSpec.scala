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

  "NonResidentCalculationKeys" should {

    s"have a customer type key with the value ${target.customerType}" in {
      target.customerType shouldBe "customerType"
    }

    s"have a prior disposal key with the value ${target.priorDisposal}" in {
      target.priorDisposal shouldBe "priorDisposal"
    }

    s"have a annual exempt amount key with the value ${target.annualExemptAmount}" in {
      target.annualExemptAmount shouldBe "annualExemptAmount"
    }

    s"have a other properties amount key with the value ${target.otherPropertiesAmount}" in {
      target.otherPropertiesAmount shouldBe "otherPropertiesAmt"
    }

    s"have a vulnerable key with the value ${target.vulnerable}" in {
      target.vulnerable shouldBe "isVulnerable"
    }

    s"have a current income key with the value ${target.currentIncome}" in {
      target.currentIncome shouldBe "currentIncome"
    }
  }
}
