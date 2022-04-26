/*
 * Copyright 2022 HM Revenue & Customs
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

import common.QueryStringKeys.{ResidentSharesCalculationKeys => residentShareKeys}
import common.validation.CommonValidation._
import models.resident.shares.{CalculateTaxOwedModel, ChargeableGainModel, TotalGainModel}

object SharesValidation {

  def validateSharesTotalGain(totalGainModel: TotalGainModel): Either[String, TotalGainModel] = {
    val disposalValue = validateDouble(totalGainModel.disposalValue, residentShareKeys.disposalValue)
    val disposalCosts = validateDouble(totalGainModel.disposalCosts, residentShareKeys.disposalCosts)
    val acquisitionValue = validateDouble(totalGainModel.acquisitionValue, residentShareKeys.acquisitionValue)
    val acquisitionCosts = validateDouble(totalGainModel.acquisitionCosts, residentShareKeys.acquisitionCosts)

    Seq(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts) match {
      case Seq(Right(_), Right(_), Right(_), Right(_)) => Right(totalGainModel)
      case failed => Left(getFirstErrorMessage(failed))
    }
  }

  def validateSharesChargeableGain(chargeableGainModel: ChargeableGainModel): Either[String, ChargeableGainModel] = {
    val totalGainModel = validateSharesTotalGain(chargeableGainModel.totalGainModel)
    val allowableLosses = validateOptionDouble(chargeableGainModel.allowableLosses, residentShareKeys.allowableLosses)
    val broughtForwardLosses = validateOptionDouble(chargeableGainModel.broughtForwardLosses, residentShareKeys.broughtForwardLosses)
    val annualExemptAmount = validateDouble(chargeableGainModel.annualExemptAmount, residentShareKeys.annualExemptAmount)

    (totalGainModel, allowableLosses, broughtForwardLosses, annualExemptAmount) match {
      case (Right(_), Right(_), Right(_), Right(_)) => Right(chargeableGainModel)
      case _ => Left(getFirstErrorMessage(Seq(totalGainModel, allowableLosses, broughtForwardLosses, annualExemptAmount)))
    }
  }

  def validateSharesTaxOwed(taxOwedModel: CalculateTaxOwedModel): Either[String, CalculateTaxOwedModel] = {
    val chargeableGainModel = validateSharesChargeableGain(taxOwedModel.chargeableGainModel)
    val previousTaxableGain = validateOptionDouble(taxOwedModel.previousTaxableGain, residentShareKeys.previousTaxableGain)
    val previousIncome = validateDouble(taxOwedModel.previousIncome, residentShareKeys.previousIncome)
    val disposalDate = validateDisposalDate(taxOwedModel.disposalDate)
    val personalAllowance = disposalDate match {
      case Right(date) => validateResidentPersonalAllowance (taxOwedModel.personalAllowance, date)
      case Left(_) => Right(taxOwedModel.personalAllowance)
    }

    (chargeableGainModel, previousTaxableGain, previousIncome, personalAllowance, disposalDate) match {
      case (Right(_), Right(_), Right(_), Right(_), Right(_)) => Right(taxOwedModel)
      case _ => Left(getFirstErrorMessage(Seq(chargeableGainModel, previousTaxableGain, previousIncome, personalAllowance, disposalDate)))
    }
  }

}
