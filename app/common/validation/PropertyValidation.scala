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

package common.validation

import common.QueryStringKeys.{ResidentPropertiesCalculationKeys => residentPropertyKeys}
import common.validation.CommonValidation._
import common.validation.SharesValidation.validateSharesTotalGain
import models.resident.properties.{PropertyCalculateTaxOwedModel, PropertyChargeableGainModel, PropertyTotalGainModel}

object PropertyValidation {

  def validatePropertyTotalGain(propertyTotalGainModel: PropertyTotalGainModel): Either[String, PropertyTotalGainModel] = {
    val totalGainModel = validateSharesTotalGain(propertyTotalGainModel.totalGainModel)
    val improvements = validateDouble(propertyTotalGainModel.improvements, residentPropertyKeys.improvements)

    (totalGainModel, improvements) match {
      case (Right(_), Right(_)) => Right(propertyTotalGainModel)
      case _ => Left(getFirstErrorMessage(Seq(totalGainModel, improvements)))
    }
  }

  def validatePropertyChargeableGain(propertyChargeableGainModel: PropertyChargeableGainModel): Either[String, PropertyChargeableGainModel] = {
    val propertyGainModel = validatePropertyTotalGain(propertyChargeableGainModel.propertyTotalGainModel)
    val prrValue = validateOptionDouble(propertyChargeableGainModel.prrValue, residentPropertyKeys.prrValue)
    val lettingReliefs = validateOptionDouble(propertyChargeableGainModel.lettingReliefs, residentPropertyKeys.lettingReliefs)
    val allowableLosses = validateOptionDouble(propertyChargeableGainModel.allowableLosses, residentPropertyKeys.allowableLosses)
    val broughtForwardLosses = validateOptionDouble(propertyChargeableGainModel.broughtForwardLosses, residentPropertyKeys.broughtForwardLosses)
    val annualExemptAmount = validateDouble(propertyChargeableGainModel.annualExemptAmount, residentPropertyKeys.annualExemptAmount)
    val disposalDate = validateDisposalDate(propertyChargeableGainModel.disposalDate)

    (propertyGainModel, prrValue, lettingReliefs, allowableLosses, broughtForwardLosses, annualExemptAmount, disposalDate) match {
      case (Right(_), Right(_), Right(_), Right(_), Right(_), Right(_), Right(_)) => Right(propertyChargeableGainModel)
      case _ => Left(getFirstErrorMessage(Seq(propertyGainModel, prrValue, lettingReliefs, allowableLosses,
        broughtForwardLosses, annualExemptAmount, disposalDate)))
    }
  }

  def validatePropertyTaxOwed(propertyCalculateTaxOwedModel: PropertyCalculateTaxOwedModel): Either[String, PropertyCalculateTaxOwedModel] = {
    val propertyChargeableGainModel = validatePropertyChargeableGain(propertyCalculateTaxOwedModel.propertyChargeableGainModel)
    val previousTaxableGain = validateOptionDouble(propertyCalculateTaxOwedModel.previousTaxableGain, residentPropertyKeys.previousTaxableGain)
    val previousIncome = validateDouble(propertyCalculateTaxOwedModel.previousIncome, residentPropertyKeys.previousIncome)
    val personalAllowance = propertyChargeableGainModel match {
      case Right(data) => validateResidentPersonalAllowance (propertyCalculateTaxOwedModel.personalAllowance, data.disposalDate)
      case Left(_) => Left("Validation failed on Chargeable Gain inputs.")
    }

    (propertyChargeableGainModel, previousTaxableGain, previousIncome, personalAllowance) match {
      case (Right(_), Right(_), Right(_), Right(_)) => Right(propertyCalculateTaxOwedModel)
      case _ => Left(getFirstErrorMessage(Seq(propertyChargeableGainModel, previousTaxableGain, previousIncome, personalAllowance)))
    }
  }
}
