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

object QueryStringKeys {

  object NonResidentCalculationKeys {
    val priorDisposal = "priorDisposal"
    val annualExemptAmount = "annualExemptAmount"
    val otherPropertiesAmount = "otherPropertiesAmt"
    val currentIncome = "currentIncome"
    val personalAllowanceAmount = "personalAllowanceAmt"
    val disposalValue = "disposalValue"
    val disposalCosts = "disposalCosts"
    val initialValue = "initialValueAmt"
    val initialCosts = "initialCostsAmt"
    val improvementsAmount = "improvementsAmt"
    val reliefsAmount = "reliefs"
    val allowableLosses = "allowableLossesAmt"
    val acquisitionDate = "acquisitionDate"
    val disposalDate = "disposalDate"
    val isClaimingPRR = "isClaimingPRR"
    val daysClaimed = "daysClaimed"
  }

  object ResidentSharesCalculationKeys {
    val disposalValue = "disposalValue"
    val disposalCosts = "disposalCosts"
    val acquisitionValue = "acquisitionValue"
    val acquisitionCosts = "acquisitionCosts"
    val allowableLosses = "allowableLosses"
    val broughtForwardLosses = "broughtForwardLosses"
    val annualExemptAmount = "annualExemptAmount"
    val previousTaxableGain = "previousTaxableGain"
    val previousIncome = "previousIncome"
    val personalAllowance = "personalAllowance"
    val disposalDate = "disposalDate"
  }

  object ResidentPropertiesCalculationKeys {
    val disposalValue = "disposalValue"
    val disposalCosts = "disposalCosts"
    val acquisitionValue = "acquisitionValue"
    val acquisitionCosts = "acquisitionCosts"
    val improvements = "improvements"
    val prrValue = "prrValue"
    val lettingReliefs = "lettingReliefs"
    val allowableLosses = "allowableLosses"
    val broughtForwardLosses = "broughtForwardLosses"
    val annualExemptAmount = "annualExemptAmount"
    val previousTaxableGain ="previousTaxableGain"
    val previousIncome = "previousIncome"
    val personalAllowance = "personalAllowance"
    val disposalDate = "disposalDate"
  }
}
