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

package controllers.resident.properties

import common.Date
import common.Date._
import models.resident.{ChargeableGainResultModel, TaxOwedResultModel}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.CalculationService
import uk.gov.hmrc.play.microservice.controller.BaseController
import common.Math._
import config.TaxRatesAndBands
import models.CalculationResultModel
import models.resident.properties.{PropertyChargeableGainModel, PropertyTotalGainModel}
import org.joda.time.DateTime

import scala.concurrent.Future

trait CalculatorController extends BaseController {

  val calculationService: CalculationService

  def calculateTotalGain (propertyGainModel: PropertyTotalGainModel): Action[AnyContent] = Action.async { implicit request =>

    val result = calculationService.calculateGainFlat(propertyGainModel.totalGainModel.disposalValue,
      propertyGainModel.totalGainModel.disposalCosts,
      propertyGainModel.totalGainModel.acquisitionValue,
      propertyGainModel.totalGainModel.acquisitionCosts,
      propertyGainModel.improvements)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateChargeableGain (propertyChargeableGainModel: PropertyChargeableGainModel): Action[AnyContent] = Action.async { implicit request =>

    val taxYear = getTaxYear(propertyChargeableGainModel.disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)

    val gain = calculationService.calculateGainFlat(propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.disposalValue,
      propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.disposalCosts,
      propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.acquisitionValue,
      propertyChargeableGainModel.propertyTotalGainModel.totalGainModel.acquisitionCosts,
      propertyChargeableGainModel.propertyTotalGainModel.improvements)

    val prrUsed = CalculationService.determinePRRUsed(gain, propertyChargeableGainModel.prrValue)
    val lettingReliefsUsed = CalculationService.determineLettingsReliefsUsed(gain, prrUsed,
      propertyChargeableGainModel.lettingReliefs,calcTaxYear)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, lettingReliefsUsed + prrUsed, propertyChargeableGainModel.allowableLosses.getOrElse(0),
      propertyChargeableGainModel.annualExemptAmount, propertyChargeableGainModel.broughtForwardLosses.getOrElse(0)
    )
    val aeaUsed = calculationService.annualExemptAmountUsed(
      propertyChargeableGainModel.annualExemptAmount,
      gain,
      calculationService.calculateChargeableGain(gain, lettingReliefsUsed + prrUsed,
        propertyChargeableGainModel.allowableLosses.getOrElse(0), propertyChargeableGainModel.annualExemptAmount, 0),
      lettingReliefsUsed + prrUsed,
      propertyChargeableGainModel.allowableLosses.getOrElse(0)
    )
    val aeaRemaining = calculationService.annualExemptAmountLeft(propertyChargeableGainModel.annualExemptAmount, aeaUsed)
    val allowableLossesRemaining = CalculationService.determineLossLeft(gain - (lettingReliefsUsed + prrUsed), round("up",
      propertyChargeableGainModel.allowableLosses.getOrElse(0)))
    val broughtForwardLossesRemaining = CalculationService.determineLossLeft(gain - (lettingReliefsUsed + prrUsed +
      round("up", propertyChargeableGainModel.allowableLosses.getOrElse(0.0)) + aeaUsed),
      propertyChargeableGainModel.broughtForwardLosses.getOrElse(0))
    val broughtForwardLossesUsed = CalculationService.calculateAmountUsed(round("up",
      propertyChargeableGainModel.broughtForwardLosses.getOrElse(0)), broughtForwardLossesRemaining)
    val allowableLossesUsed = CalculationService.calculateAmountUsed(round("up",
      propertyChargeableGainModel.allowableLosses.getOrElse(0)), allowableLossesRemaining)

    val deductions = round("up", prrUsed + lettingReliefsUsed + round("up", allowableLossesUsed) + aeaUsed + round("up", broughtForwardLossesUsed))

    val result = ChargeableGainResultModel(gain, chargeableGain, aeaUsed, aeaRemaining, deductions, allowableLossesRemaining, broughtForwardLossesRemaining,
      Some(lettingReliefsUsed), Some(prrUsed), Some(broughtForwardLossesUsed), allowableLossesUsed)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTaxOwed(disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double,
    improvements: Double, prrValue: Option[Double], lettingReliefs: Option[Double], allowableLosses: Option[Double],
    broughtForwardLosses: Option[Double], annualExemptAmount: Double, previousTaxableGain: Option[Double],
    previousIncome: Double, personalAllowance: Double, disposalDate: String = "2015-10-10"
  ): Action[AnyContent] = Action.async { implicit request =>

    val taxYear = getTaxYear(DateTime.parse(disposalDate))
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)

    val gain = calculationService.calculateGainFlat(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts, improvements)
    val prrUsed = CalculationService.determinePRRUsed(gain, prrValue)
    val lettingReliefsUsed = CalculationService.determineLettingsReliefsUsed(gain, prrUsed, lettingReliefs, calcTaxYear)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, lettingReliefsUsed + prrUsed, allowableLosses.getOrElse(0.0), annualExemptAmount, broughtForwardLosses.getOrElse(0.0)
    )
    val aeaUsed: Double = calculationService.annualExemptAmountUsed(
      annualExemptAmount, gain,
      calculationService.calculateChargeableGain(gain, lettingReliefsUsed + prrUsed, allowableLosses.getOrElse(0.0), annualExemptAmount, 0.0),
      lettingReliefsUsed + prrUsed, allowableLosses.getOrElse(0.0)
    )
    val deductions = prrUsed + lettingReliefsUsed + allowableLosses.getOrElse(0.0) + aeaUsed + broughtForwardLosses.getOrElse(0.0)
    val calculationResult: CalculationResultModel  = calculationService.calculationResult ("individual", gain, chargeableGain, negativeToZero(chargeableGain),
      calculationService.brRemaining(previousIncome, personalAllowance, previousTaxableGain.getOrElse(0.0), Date.getTaxYear(DateTime.parse(disposalDate))),
      0.0, "No", aeaUsed, 0.0, calcTaxYear, true)
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
      Some(broughtForwardLosses.getOrElse(0)),
      allowableLosses.getOrElse(0)
    )
    Future.successful(Ok(Json.toJson(result)))
  }
}

object CalculatorController extends CalculatorController {

  override val calculationService = CalculationService

}
