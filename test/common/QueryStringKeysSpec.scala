/*
 * Copyright 2024 HM Revenue & Customs
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

import org.scalatestplus.play.PlaySpec

class QueryStringKeysSpec extends PlaySpec {
  import common.QueryStringKeys.{NonResidentCalculationKeys => target, ResidentPropertiesCalculationKeys => propertyTarget}

  "NonResidentCalculationKeys" must {

    s"have a prior disposal key with the value ${target.priorDisposal}" in {
      target.priorDisposal mustBe "priorDisposal"
    }

    s"have an annual exempt amount key with the value ${target.annualExemptAmount}" in {
      target.annualExemptAmount mustBe "annualExemptAmount"
    }

    s"have an other properties amount key with the value ${target.otherPropertiesAmount}" in {
      target.otherPropertiesAmount mustBe "otherPropertiesAmt"
    }

    s"have a current income key with the value ${target.currentIncome}" in {
      target.currentIncome mustBe "currentIncome"
    }

    s"have a personal allowance key with the value ${target.personalAllowanceAmount}" in {
      target.personalAllowanceAmount mustBe "personalAllowanceAmt"
    }

    s"have a disposal value key with the value ${target.disposalValue}" in {
      target.disposalValue mustBe "disposalValue"
    }

    s"have a disposal costs key with the value ${target.disposalCosts}" in {
      target.disposalCosts mustBe "disposalCosts"
    }

    s"have an acquisition value key with the value ${target.initialValue}" in {
      target.initialValue mustBe "initialValueAmt"
    }

    s"have an acquisition costs key with the value ${target.initialCosts}" in {
      target.initialCosts mustBe "initialCostsAmt"
    }

    s"have an improvements amount key with the value ${target.improvementsAmount}" in {
      target.improvementsAmount mustBe "improvementsAmt"
    }

    s"have a reliefs amount key with the value ${target.reliefsAmount}" in {
      target.reliefsAmount mustBe "reliefs"
    }

    s"have an allowable losses amount key with the value ${target.allowableLosses}" in {
      target.allowableLosses mustBe "allowableLossesAmt"
    }

    s"have an acquisition date key with the value ${target.acquisitionDate}" in {
      target.acquisitionDate mustBe "acquisitionDate"
    }

    s"have an disposal date key with the value ${target.disposalDate}" in {
      target.disposalDate mustBe "disposalDate"
    }

    s"have an is claiming private residence relief key with the value ${target.isClaimingPRR}" in {
      target.isClaimingPRR mustBe "isClaimingPRR"
    }

    s"have an days claiming key with the value ${target.daysClaimed}" in {
      target.daysClaimed mustBe "daysClaimed"
    }
  }

  "ResidentPropertiesCalculationKeys" must {

    s"have a disposalValue key with the value ${propertyTarget.disposalValue}" in {
      propertyTarget.disposalValue mustBe "disposalValue"
    }

    s"have a disposalCosts key with the value ${propertyTarget.disposalCosts}" in {
      propertyTarget.disposalCosts mustBe "disposalCosts"
    }

    s"have a acquisitionValue key with the value ${propertyTarget.acquisitionValue}" in {
      propertyTarget.acquisitionValue mustBe "acquisitionValue"
    }

    s"have a acquisitionCosts key with the value ${propertyTarget.acquisitionCosts}" in {
      propertyTarget.acquisitionCosts mustBe "acquisitionCosts"
    }

    s"have a improvements key with the value ${propertyTarget.improvements}" in {
      propertyTarget.improvements mustBe "improvements"
    }

    s"have a prrValue key with the value ${propertyTarget.prrValue}" in {
      propertyTarget.prrValue mustBe "prrValue"
    }

    s"have a lettingReliefs key with the value ${propertyTarget.lettingReliefs}" in {
      propertyTarget.lettingReliefs mustBe "lettingReliefs"
    }

    s"have a allowableLosses key with the value ${propertyTarget.allowableLosses}" in {
      propertyTarget.allowableLosses mustBe "allowableLosses"
    }

    s"have a broughtForwardLosses key with the value ${propertyTarget.broughtForwardLosses}" in {
      propertyTarget.broughtForwardLosses mustBe "broughtForwardLosses"
    }

    s"have a annualExemptAmount key with the value ${propertyTarget.annualExemptAmount}" in {
      propertyTarget.annualExemptAmount mustBe "annualExemptAmount"
    }

    s"have a disposalDate key with the value ${propertyTarget.disposalDate}" in {
      propertyTarget.disposalDate mustBe "disposalDate"
    }
  }
}
