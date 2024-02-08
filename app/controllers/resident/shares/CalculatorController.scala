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

package controllers.resident.shares

import common.Date
import common.Date._
import common.Math._
import config.TaxRatesAndBands
import javax.inject.{Inject, Singleton}
import models.CalculationResultModel
import models.resident.shares.{CalculateTaxOwedModel, ChargeableGainModel, TotalGainModel}
import models.resident.{ChargeableGainResultModel, TaxOwedResultModel}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.CalculationService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.Future

@Singleton
class CalculatorController @Inject()(
                                          val calculationService: CalculationService,
                                          val cc: ControllerComponents
                                        ) extends BackendController(cc) {

  def calculateTotalGain(totalGainModel: TotalGainModel): Action[AnyContent] =
    Action.async {

      val result = calculationService.calculateGainFlat(
        totalGainModel.disposalValue,
        totalGainModel.disposalCosts,
        totalGainModel.acquisitionValue,
        totalGainModel.acquisitionCosts,
        0)

      Future.successful(Ok(Json.toJson(result)))
    }

  def calculateChargeableGain
  (
    chargeableGainModel: ChargeableGainModel
  ): Action[AnyContent] = Action.async {
    val totalGainModel = chargeableGainModel.totalGainModel

    val gain = calculationService.calculateGainFlat(totalGainModel.disposalValue, totalGainModel.disposalCosts,
      totalGainModel.acquisitionValue, totalGainModel.acquisitionCosts, 0)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, 0, chargeableGainModel.allowableLosses.getOrElse(0), chargeableGainModel.annualExemptAmount, chargeableGainModel.broughtForwardLosses.getOrElse(0)
    )
    val aeaUsed = calculationService.annualExemptAmountUsed(
      chargeableGainModel.annualExemptAmount,
      gain,
      0,
      chargeableGainModel.allowableLosses.getOrElse(0)
    )
    val aeaRemaining = calculationService.annualExemptAmountLeft(chargeableGainModel.annualExemptAmount, aeaUsed)
    val allowableLossesRemaining = calculationService.determineLossLeft(gain, chargeableGainModel.allowableLosses.getOrElse(0))
    val broughtForwardLossesRemaining = calculationService.determineLossLeft(chargeableGain + chargeableGainModel.broughtForwardLosses.getOrElse(0.0),
      chargeableGainModel.broughtForwardLosses.getOrElse(0))
    val broughtForwardLossesUsed = calculationService.calculateAmountUsed(round("up", chargeableGainModel.broughtForwardLosses.getOrElse(0)),
      broughtForwardLossesRemaining)
    val allowableLossesUsed = calculationService.calculateAmountUsed(round("up", chargeableGainModel.allowableLosses.getOrElse(0)), allowableLossesRemaining)

    val deductions = round("up", allowableLossesUsed) + aeaUsed + round("up", broughtForwardLossesUsed)

    val result = ChargeableGainResultModel(gain, chargeableGain, aeaUsed, aeaRemaining, deductions, allowableLossesRemaining,
      broughtForwardLossesRemaining, None, None, Some(broughtForwardLossesUsed), allowableLossesUsed)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTaxOwed
  (
    calculateTaxOwedModel: CalculateTaxOwedModel
  ): Action[AnyContent] = Action.async {

    val chargeableGainModel = calculateTaxOwedModel.chargeableGainModel

    val taxYear = getTaxYear(calculateTaxOwedModel.disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
    val gain = calculationService.calculateGainFlat(chargeableGainModel.totalGainModel.disposalValue,
      chargeableGainModel.totalGainModel.disposalCosts, chargeableGainModel.totalGainModel.acquisitionValue,
      chargeableGainModel.totalGainModel.acquisitionCosts, 0)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, 0, chargeableGainModel.allowableLosses.getOrElse(0.0), chargeableGainModel.annualExemptAmount,
      chargeableGainModel.broughtForwardLosses.getOrElse(0.0)
    )
    val aeaUsed: Double = calculationService.annualExemptAmountUsed(
      chargeableGainModel.annualExemptAmount,
      gain,
      0,
      chargeableGainModel.allowableLosses.getOrElse(0.0)
    )
    val deductions = 0 + chargeableGainModel.allowableLosses.getOrElse(0.0) + aeaUsed + chargeableGainModel.broughtForwardLosses.getOrElse(0.0)
    val calculationResult: CalculationResultModel = calculationService.calculationResult(gain, chargeableGain, negativeToZero(chargeableGain),
      calculationService.brRemaining(calculateTaxOwedModel.previousIncome,
        calculateTaxOwedModel.personalAllowance, calculateTaxOwedModel.previousTaxableGain.getOrElse(0.0),
        Date.getTaxYear(calculateTaxOwedModel.disposalDate)),
      0.0, "No", aeaUsed, 0.0, calcTaxYear, isProperty = false
    )
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
      None,
      None,
      //Logic here is that there has been a total gain made.  Therefore any brought forward losses claimed have been used entirely.
      //As such it returns either a 0 if no losses were supplied or the value of the losses supplied.
      Some(chargeableGainModel.broughtForwardLosses.getOrElse(0)),
      chargeableGainModel.allowableLosses.getOrElse(0),
      calculationResult.baseRateTotal,
      calculationResult.upperRateTotal
    )
    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTotalCosts(totalGainModel: TotalGainModel): Action[AnyContent] = Action.async {

    val result = calculationService.calculateTotalCosts(totalGainModel.disposalCosts,
      totalGainModel.acquisitionCosts)

    Future.successful(Ok(Json.toJson(result)))
  }
}