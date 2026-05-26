/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.nonresident

import models.nonResident.TaxOwedModel
import services.CalculationService

import common.Math._
import javax.inject.{Inject, Singleton}

@Singleton
class NonResidentTaxCalculationHelper @Inject() (
  calculationService: CalculationService
) {

  def buildTaxOwedModel(
    gain: Double,
    reliefs: Double,
    currentIncome: Double,
    personalAllowanceAmt: Double,
    previousGain: Double,
    prrValue: Double,
    allowableLoss: Double,
    annualExemptAmount: Double,
    broughtForwardLoss: Double,
    prrClaimed: Option[Double],
    calcTaxYear: Int,
    useClaimedPrrForReliefsRemaining: Boolean = false
  ): TaxOwedModel = {

    val brRemaining = calculationService.brRemaining(
      currentIncome,
      personalAllowanceAmt,
      previousGain,
      calcTaxYear
    )

    val chargeableGain = calculationService.calculateChargeableGain(
      gain,
      prrValue + reliefs,
      allowableLoss,
      annualExemptAmount,
      broughtForwardLoss
    )

    val prrUsed =
      calculationService.determineReliefsUsed(gain, Some(prrValue))

    val otherReliefsUsed =
      calculationService.determineReliefsUsed(
        gain - prrUsed,
        Some(reliefs)
      )

    val allowableLossesLeft =
      calculationService.determineLossLeft(
        gain - (prrUsed + otherReliefsUsed),
        allowableLoss
      )

    val allowableLossesUsed =
      allowableLoss - allowableLossesLeft

    val aeaUsed =
      calculationService.annualExemptAmountUsed(
        annualExemptAmount,
        gain,
        prrValue + reliefs,
        allowableLoss
      )

    val aeaRemaining =
      calculationService.annualExemptAmountLeft(
        annualExemptAmount,
        aeaUsed
      )

    val broughtForwardLossRemaining =
      calculationService.determineLossLeft(
        gain - (
          prrUsed +
            round("up", allowableLoss) +
            aeaUsed +
            otherReliefsUsed
        ),
        broughtForwardLoss
      )

    val broughtForwardLossUsed =
      broughtForwardLoss - broughtForwardLossRemaining

    val prrValueForRemaining =
      if (useClaimedPrrForReliefsRemaining) prrValue
      else prrUsed

    val reliefsRemaining =
      (prrValue + round("up", reliefs)) -
        (prrValueForRemaining + otherReliefsUsed)

    val taxOwed =
      calculationService.calculationResult(
        gain,
        negativeToZero(chargeableGain),
        chargeableGain,
        brRemaining,
        prrClaimed,
        aeaUsed,
        aeaRemaining,
        calcTaxYear,
        isProperty = true
      )

    val totalDeductions =
      prrUsed +
        otherReliefsUsed +
        allowableLossesUsed +
        aeaUsed +
        broughtForwardLossUsed

    TaxOwedModel(
      taxOwed.taxOwed,
      taxOwed.baseTaxGain,
      taxOwed.baseTaxRate,
      taxOwed.upperTaxGain,
      taxOwed.upperTaxRate,
      gain,
      chargeableGain,
      if (prrUsed > 0) Some(prrUsed) else None,
      if (otherReliefsUsed > 0) Some(otherReliefsUsed) else None,
      if (allowableLossesUsed > 0) Some(allowableLossesUsed) else None,
      if (aeaUsed > 0) Some(aeaUsed) else None,
      aeaRemaining,
      if (broughtForwardLossUsed > 0) Some(broughtForwardLossUsed) else None,
      if (reliefsRemaining > 0) Some(reliefsRemaining) else None,
      if (allowableLossesLeft > 0) Some(allowableLossesLeft) else None,
      if (broughtForwardLossRemaining > 0) Some(broughtForwardLossRemaining) else None,
      if (totalDeductions > 0) Some(totalDeductions) else None,
      Some(taxOwed.baseRateTotal),
      Some(taxOwed.upperRateTotal)
    )
  }
}
