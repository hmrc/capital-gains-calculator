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

object QueryStringKeys {

  object NonResidentCalculationKeys {
    val customerType = "customerType"
    val priorDisposal = "priorDisposal"
    val annualExemptAmount = "annualExemptAmount"
    val otherPropertiesAmount = "otherPropertiesAmt"
    val vulnerable = "isVulnerable"
    val currentIncome = "currentIncome"
    val personalAllowanceAmount = "personalAllowanceAmt"
    val disposalValue = "disposalValue"
    val disposalCosts = "disposalCosts"
    val acquisitionValue = "acquisitionValueAmt"
    val acquisitionCosts = "acquisitionCostsAmt"
    val improvementsAmount = "improvementsAmt"
  }

  object ResidentSharesCalculationKeys {
    val disposalValue = "disposalValue"
    val disposalCosts = "disposalCosts"
    val acquisitionValue = "acquisitionValue"
    val acquisitionCosts = "acquisitionCosts"
    val allowableLosses = "allowableLosses"
    val broughtForwardLosses = "broughtForwardLosses"
    val annualExemptAmount = "annualExemptAmount"
  }
}