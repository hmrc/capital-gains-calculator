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
import org.joda.time.DateTime

import scala.concurrent.Future

trait CalculatorController extends BaseController {

  val calculationService: CalculationService

  def calculateTotalGain (disposalValue: Double,
                         disposalCosts: Double,
                         acquisitionValue: Double,
                         acquisitionCosts: Double,
                        improvements: Double): Action[AnyContent] = Action.async { implicit request =>

    val result = calculationService.calculateGainFlat(disposalValue,
      disposalCosts,
      acquisitionValue,
      acquisitionCosts,
      improvements)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateChargeableGain
  (
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValue: Double,
    acquisitionCosts: Double,
    improvements: Double,
    prrType: Option[String],
    prrValue: Option[Double],
    reliefs: Option[Double],
    allowableLosses: Option[Double],
    broughtForwardLosses: Option[Double],
    annualExemptAmount: Double
  ): Action[AnyContent] = Action.async { implicit request =>

    val gain = calculationService.calculateGainFlat(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts, improvements)
    val prrUsed = CalculationService.determinePRRUsed(gain, prrValue, prrType)
    val reliefsUsed = CalculationService.determineReliefsUsed(gain - prrUsed, reliefs)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, reliefsUsed + prrUsed, allowableLosses.getOrElse(0), annualExemptAmount, broughtForwardLosses.getOrElse(0)
    )
    val aeaUsed = calculationService.annualExemptAmountUsed(
      annualExemptAmount,
      gain,
      calculationService.calculateChargeableGain(gain, reliefsUsed + prrUsed, allowableLosses.getOrElse(0), annualExemptAmount, 0),
      reliefsUsed + prrUsed,
      allowableLosses.getOrElse(0)
    )
    val aeaRemaining = calculationService.annualExemptAmountLeft(annualExemptAmount, aeaUsed)
    val allowableLossesRemaining = CalculationService.determineLossLeft(gain - (reliefsUsed + prrUsed), round("up", allowableLosses.getOrElse(0)))
    val broughtForwardLossesRemaining = CalculationService.determineLossLeft(gain - (reliefsUsed + prrUsed + round("up", allowableLosses.getOrElse(0.0)) +
      aeaUsed), broughtForwardLosses.getOrElse(0))
    val broughtForwardLossesUsed = CalculationService.calculateAmountUsed(round("up", broughtForwardLosses.getOrElse(0)), broughtForwardLossesRemaining)
    val allowableLossesUsed = CalculationService.calculateAmountUsed(round("up", allowableLosses.getOrElse(0)), allowableLossesRemaining)

    val deductions = round("up", prrUsed + reliefsUsed + round("up", allowableLossesUsed) + aeaUsed + round("up", broughtForwardLossesUsed))

    val result = ChargeableGainResultModel(gain, chargeableGain, aeaUsed, aeaRemaining, deductions, allowableLossesRemaining, broughtForwardLossesRemaining,
      Some(reliefsUsed), Some(prrUsed), Some(broughtForwardLossesUsed), allowableLossesUsed)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTaxOwed(disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double,
    improvements: Double, prrType: Option[String], prrValue: Option[Double], reliefs: Option[Double], allowableLosses: Option[Double],
    broughtForwardLosses: Option[Double], annualExemptAmount: Double, previousTaxableGain: Option[Double],
    previousIncome: Double, personalAllowance: Double, disposalDate: String = "2015-10-10"
  ): Action[AnyContent] = Action.async { implicit request =>

    val taxYear = getTaxYear(DateTime.parse(disposalDate))
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)

    val gain = calculationService.calculateGainFlat(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts, improvements)
    val prrUsed = CalculationService.determinePRRUsed(gain, prrValue, prrType)
    val reliefsUsed = CalculationService.determineReliefsUsed(gain - prrUsed, reliefs)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, reliefsUsed + prrUsed, allowableLosses.getOrElse(0.0), annualExemptAmount, broughtForwardLosses.getOrElse(0.0)
    )
    val aeaUsed: Double = calculationService.annualExemptAmountUsed(
      annualExemptAmount, gain,
      calculationService.calculateChargeableGain(gain, reliefsUsed + prrUsed, allowableLosses.getOrElse(0.0), annualExemptAmount, 0.0),
      reliefsUsed + prrUsed, allowableLosses.getOrElse(0.0)
    )
    val deductions = prrUsed + reliefsUsed + allowableLosses.getOrElse(0.0) + aeaUsed + broughtForwardLosses.getOrElse(0.0)
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
      Some(reliefsUsed),
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
