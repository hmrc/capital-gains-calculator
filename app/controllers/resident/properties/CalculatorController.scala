/*
 * Copyright 2020 HM Revenue & Customs
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

package controllers.resident.properties

import common.Date
import common.Date._
import common.Math._
import config.TaxRatesAndBands
import javax.inject.{Inject, Singleton}
import models.CalculationResultModel
import models.resident.properties.{PropertyCalculateTaxOwedModel, PropertyChargeableGainModel, PropertyTotalGainModel}
import models.resident.{ChargeableGainResultModel, TaxOwedResultModel}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.CalculationService
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.Future

@Singleton
class CalculatorController @Inject()(
                                          val calculationService: CalculationService,
                                          val cc: ControllerComponents
                                        ) extends BackendController(cc) {

  def calculateTotalGain(propertyGainModel: PropertyTotalGainModel): Action[AnyContent] = Action.async { implicit request =>

    val result = calculationService.calculateGainFlat(propertyGainModel.totalGainModel.disposalValue,
      propertyGainModel.totalGainModel.disposalCosts,
      propertyGainModel.totalGainModel.acquisitionValue,
      propertyGainModel.totalGainModel.acquisitionCosts,
      propertyGainModel.improvements)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateChargeableGain(propertyChargeableGainModel: PropertyChargeableGainModel): Action[AnyContent] = Action.async { implicit request =>

    val taxYear = getTaxYear(propertyChargeableGainModel.disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)

    val gain = calculationService.calculateGainFlat(propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.disposalValue,
      propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.disposalCosts,
      propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.acquisitionValue,
      propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.acquisitionCosts,
      propertyChargeableGainModel.propertyTotalGainModel.improvements)

    val prrUsed = calculationService.determineReliefsUsed(gain, propertyChargeableGainModel.prrValue)
    val lettingReliefsUsed = calculationService.determineLettingsReliefsUsed(gain, prrUsed,
      propertyChargeableGainModel.lettingReliefs, calcTaxYear)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, lettingReliefsUsed + prrUsed, propertyChargeableGainModel.allowableLosses.getOrElse(0),
      propertyChargeableGainModel.annualExemptAmount, propertyChargeableGainModel.broughtForwardLosses.getOrElse(0)
    )
    val aeaUsed = calculationService.annualExemptAmountUsed(
      propertyChargeableGainModel.annualExemptAmount,
      gain,
      lettingReliefsUsed + prrUsed,
      propertyChargeableGainModel.allowableLosses.getOrElse(0)
    )
    val aeaRemaining = calculationService.annualExemptAmountLeft(propertyChargeableGainModel.annualExemptAmount, aeaUsed)
    val allowableLossesRemaining = calculationService.determineLossLeft(gain - (lettingReliefsUsed + prrUsed), round("up",
      propertyChargeableGainModel.allowableLosses.getOrElse(0)))
    val broughtForwardLossesRemaining = calculationService.determineLossLeft(gain - (lettingReliefsUsed + prrUsed +
      round("up", propertyChargeableGainModel.allowableLosses.getOrElse(0.0)) + aeaUsed),
      propertyChargeableGainModel.broughtForwardLosses.getOrElse(0))
    val broughtForwardLossesUsed = calculationService.calculateAmountUsed(round("up",
      propertyChargeableGainModel.broughtForwardLosses.getOrElse(0)), broughtForwardLossesRemaining)
    val allowableLossesUsed = calculationService.calculateAmountUsed(round("up",
      propertyChargeableGainModel.allowableLosses.getOrElse(0)), allowableLossesRemaining)

    val deductions = round("up", prrUsed + lettingReliefsUsed + round("up", allowableLossesUsed) + aeaUsed + round("up", broughtForwardLossesUsed))

    val result = ChargeableGainResultModel(gain, chargeableGain, aeaUsed, aeaRemaining, deductions, allowableLossesRemaining, broughtForwardLossesRemaining,
      Some(lettingReliefsUsed), Some(prrUsed), Some(broughtForwardLossesUsed), allowableLossesUsed)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTaxOwed(propertyCalculateTaxOwedModel: PropertyCalculateTaxOwedModel): Action[AnyContent] = Action.async { implicit request =>

    val taxYear = getTaxYear(propertyCalculateTaxOwedModel.propertyChargeableGainModel.disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)

    val gain = calculationService.calculateGainFlat(propertyCalculateTaxOwedModel.propertyChargeableGainModel.propertyTotalGainModel.
      totalGainModel.disposalValue,
      propertyCalculateTaxOwedModel.propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.disposalCosts,
      propertyCalculateTaxOwedModel.propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.acquisitionValue,
      propertyCalculateTaxOwedModel.propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.acquisitionCosts,
      propertyCalculateTaxOwedModel.propertyChargeableGainModel.propertyTotalGainModel.improvements)
    val prrUsed = calculationService.determineReliefsUsed(gain, propertyCalculateTaxOwedModel.propertyChargeableGainModel.prrValue)
    val lettingReliefsUsed = calculationService.determineLettingsReliefsUsed(gain, prrUsed, propertyCalculateTaxOwedModel.
      propertyChargeableGainModel.lettingReliefs, calcTaxYear)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, lettingReliefsUsed + prrUsed, propertyCalculateTaxOwedModel.propertyChargeableGainModel.allowableLosses.getOrElse(0.0),
      propertyCalculateTaxOwedModel.propertyChargeableGainModel.annualExemptAmount,
      propertyCalculateTaxOwedModel.propertyChargeableGainModel.broughtForwardLosses.getOrElse(0.0)
    )
    val aeaUsed: Double = calculationService.annualExemptAmountUsed(
      propertyCalculateTaxOwedModel.propertyChargeableGainModel.annualExemptAmount, gain,
      lettingReliefsUsed + prrUsed, propertyCalculateTaxOwedModel.propertyChargeableGainModel.allowableLosses.getOrElse(0.0)
    )
    val deductions = prrUsed + lettingReliefsUsed + propertyCalculateTaxOwedModel.propertyChargeableGainModel.allowableLosses.getOrElse(0.0) +
      aeaUsed + propertyCalculateTaxOwedModel.propertyChargeableGainModel.broughtForwardLosses.getOrElse(0.0)
    val calculationResult: CalculationResultModel = calculationService.calculationResult(gain, chargeableGain,
      negativeToZero(chargeableGain), calculationService.brRemaining(propertyCalculateTaxOwedModel.previousIncome,
        propertyCalculateTaxOwedModel.personalAllowance, propertyCalculateTaxOwedModel.previousTaxableGain.getOrElse(0.0),
        Date.getTaxYear(propertyCalculateTaxOwedModel.propertyChargeableGainModel.disposalDate)),
      0.0, "No", aeaUsed, 0.0, calcTaxYear, isProperty = true)
    val result: TaxOwedResultModel = TaxOwedResultModel(
      gain,
      chargeableGain,
      aeaUsed,
      deductions,
      calculationResult.taxOwed,
      calculationResult.baseTaxGain,
      calculationResult.baseTaxRate,
      calculationResult.upperTaxGain,
      calculationResult.upperTaxRate,
      Some(lettingReliefsUsed),
      Some(prrUsed),
      //Logic here is that there has been a total gain made.  Therefore any brought forward losses gained have been used entirely.
      //As such it returns either a 0 if no losses were supplied or the value of the losses supplied.
      Some(propertyCalculateTaxOwedModel.propertyChargeableGainModel.broughtForwardLosses.getOrElse(0)),
      propertyCalculateTaxOwedModel.propertyChargeableGainModel.allowableLosses.getOrElse(0),
      calculationResult.baseRateTotal,
      calculationResult.upperRateTotal
    )
    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTotalCosts(propertyTotalGainModel: PropertyTotalGainModel): Action[AnyContent] = Action.async { implicit request =>

    val result = calculationService.calculateTotalCosts(propertyTotalGainModel.totalGainModel.disposalCosts,
      propertyTotalGainModel.totalGainModel.acquisitionCosts,
      propertyTotalGainModel.improvements)

    Future.successful(Ok(Json.toJson(result)))
  }
}