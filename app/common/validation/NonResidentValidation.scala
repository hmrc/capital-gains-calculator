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

package common.validation

import common.QueryStringKeys.{NonResidentCalculationKeys => keys}
import common.validation.CommonValidation._
import models.nonResident.{CalculationRequestModel, TimeApportionmentCalculationRequestModel}

object NonResidentValidation {

  def validateNonResidentProperty(model: CalculationRequestModel): Either[String, CalculationRequestModel] = {

    val priorDisposal = validateYesNo(model.priorDisposal, keys.priorDisposal)
    val annualExemptAmount = validateOptionDouble(model.annualExemptAmount, keys.annualExemptAmount)
    val otherPropertiesAmount = validateOptionDouble(model.otherPropertiesAmount, keys.otherPropertiesAmount)
    val currentIncome = validateDouble(model.currentIncome, keys.currentIncome)
    val personalAllowanceAmount = validateOptionDouble(model.personalAllowanceAmount, keys.personalAllowanceAmount)
    val disposalValue = validateDouble(model.disposalValue, keys.disposalValue)
    val disposalCosts = validateDouble(model.disposalCosts, keys.disposalCosts)
    val initialValue = validateDouble(model.initialValue, keys.initialValue)
    val initialCosts = validateDouble(model.initialCosts, keys.initialCosts)
    val improvementsAmount = validateDouble(model.improvementsAmount, keys.improvementsAmount)
    val reliefsAmount = validateDouble(model.reliefsAmount, keys.reliefsAmount)
    val allowableLosses = validateDouble(model.allowableLosses, keys.allowableLosses)
    val acquisitionDate = validateOptionalAcquisitionDate(model.disposalDate, model.acquisitionDate)
    val disposalDate = validateDisposalDate(model.disposalDate)
    val isClaimingPRR = validateOptionYesNo(model.isClaimingPRR, keys.isClaimingPRR)
    val daysClaimed = validateOptionDouble(model.daysClaimed, keys.daysClaimed)

    (priorDisposal, annualExemptAmount, otherPropertiesAmount, currentIncome,
      personalAllowanceAmount, disposalValue, disposalCosts, initialValue, initialCosts, improvementsAmount,
      reliefsAmount, allowableLosses, acquisitionDate, disposalDate, isClaimingPRR, daysClaimed) match {
      case (Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_),
      Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_)) =>
        Right(model)
      case _ => Left(getFirstErrorMessage(Seq(priorDisposal, annualExemptAmount, otherPropertiesAmount, currentIncome,
        personalAllowanceAmount, disposalValue, disposalCosts, initialValue, initialCosts, improvementsAmount,
        reliefsAmount, allowableLosses, acquisitionDate, disposalDate, isClaimingPRR, daysClaimed)))
    }
  }

  def validateNonResidentTimeApportioned(model: TimeApportionmentCalculationRequestModel): Either[String, TimeApportionmentCalculationRequestModel] = {

    val priorDisposal = validateYesNo(model.priorDisposal, keys.priorDisposal)
    val annualExemptAmount = validateOptionDouble(model.annualExemptAmount, keys.annualExemptAmount)
    val otherPropertiesAmount = validateOptionDouble(model.otherPropertiesAmount, keys.otherPropertiesAmount)
    val currentIncome = validateDouble(model.currentIncome, keys.currentIncome)
    val personalAllowanceAmount = validateOptionDouble(model.personalAllowanceAmount, keys.personalAllowanceAmount)
    val disposalValue = validateDouble(model.disposalValue, keys.disposalValue)
    val disposalCosts = validateDouble(model.disposalCosts, keys.disposalCosts)
    val initialValue = validateDouble(model.initialValue, keys.initialValue)
    val initialCosts = validateDouble(model.initialCosts, keys.initialCosts)
    val improvementsAmount = validateDouble(model.improvementsAmount, keys.improvementsAmount)
    val reliefsAmount = validateDouble(model.reliefsAmount, keys.reliefsAmount)
    val allowableLosses = validateDouble(model.allowableLosses, keys.allowableLosses)
    val acquisitionDate = validateAcquisitionDate(model.disposalDate, model.acquisitionDate)
    val disposalDate = validateDisposalDate(model.disposalDate)
    val isClaimingPRR = validateOptionYesNo(model.isClaimingPRR, keys.isClaimingPRR)
    val daysClaimed = validateOptionDouble(model.daysClaimed, keys.daysClaimed)

    (priorDisposal, annualExemptAmount, otherPropertiesAmount, currentIncome,
      personalAllowanceAmount, disposalValue, disposalCosts, initialValue, initialCosts, improvementsAmount,
      reliefsAmount, allowableLosses, acquisitionDate, disposalDate, isClaimingPRR, daysClaimed) match {
      case (Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_),
      Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_)) =>
        Right(model)
      case _ => Left(getFirstErrorMessage(Seq(priorDisposal, annualExemptAmount, otherPropertiesAmount, currentIncome,
        personalAllowanceAmount, disposalValue, disposalCosts, initialValue, initialCosts, improvementsAmount,
        reliefsAmount, allowableLosses, acquisitionDate, disposalDate, isClaimingPRR, daysClaimed)))
    }
  }
}
