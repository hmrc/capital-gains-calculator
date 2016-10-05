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

    s"have an annual exempt amount key with the value ${target.annualExemptAmount}" in {
      target.annualExemptAmount shouldBe "annualExemptAmount"
    }

    s"have an other properties amount key with the value ${target.otherPropertiesAmount}" in {
      target.otherPropertiesAmount shouldBe "otherPropertiesAmt"
    }

    s"have a vulnerable key with the value ${target.vulnerable}" in {
      target.vulnerable shouldBe "isVulnerable"
    }

    s"have a current income key with the value ${target.currentIncome}" in {
      target.currentIncome shouldBe "currentIncome"
    }

    s"have a personal allowance key with the value ${target.personalAllowanceAmount}" in {
      target.personalAllowanceAmount shouldBe "personalAllowanceAmt"
    }

    s"have a disposal value key with the value ${target.disposalValue}" in {
      target.disposalValue shouldBe "disposalValue"
    }

    s"have a disposal costs key with the value ${target.disposalCosts}" in {
      target.disposalCosts shouldBe "disposalCosts"
    }

    s"have an acquisition value key with the value ${target.acquisitionValue}" in {
      target.acquisitionValue shouldBe "acquisitionValueAmt"
    }

    s"have an acquisition costs key with the value ${target.acquisitionCosts}" in {
      target.acquisitionCosts shouldBe "acquisitionCostsAmt"
    }

    s"have an improvements amount key with the value ${target.improvementsAmount}" in {
      target.improvementsAmount shouldBe "improvementsAmt"
    }

    s"have a reliefs amount key with the value ${target.reliefsAmount}" in {
      target.reliefsAmount shouldBe "reliefs"
    }

    s"have an allowable losses amount key with the value ${target.allowableLosses}" in {
      target.allowableLosses shouldBe "allowableLossesAmt"
    }

    s"have an acquisition date key with the value ${target.acquisitionDate}" in {
      target.acquisitionDate shouldBe "acquisitionDate"
    }

    s"have an is claiming private residence relief key with the value ${target.isClaimingPRR}" in {
      target.isClaimingPRR shouldBe "isClaimingPRR"
    }

    s"have an days claiming key with the value ${target.daysClaimed}" in {
      target.daysClaimed shouldBe "daysClaimed"
    }
  }
}
