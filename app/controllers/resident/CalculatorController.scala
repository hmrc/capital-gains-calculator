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

package controllers.resident

import common.Date
import common.Math._
import models.CalculationResultModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.CalculationService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import models.resident._
import org.joda.time.DateTime
object CalculatorController extends CalculatorController {

  override val calculationService = CalculationService

}

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
    reliefs: Option[Double],
    allowableLosses: Option[Double],
    broughtForwardLosses: Option[Double],
    annualExemptAmount: Double
  ): Action[AnyContent] = Action.async { implicit request =>

    val gain = calculationService.calculateGainFlat(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts, improvements)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, reliefs.getOrElse(0), allowableLosses.getOrElse(0), annualExemptAmount, broughtForwardLosses.getOrElse(0)
    )
    val aeaUsed = calculationService.annualExemptAmountUsed(
      annualExemptAmount,
      gain,
      calculationService.calculateChargeableGain(gain, reliefs.getOrElse(0), allowableLosses.getOrElse(0), annualExemptAmount, 0),
      reliefs.getOrElse(0),
      allowableLosses.getOrElse(0)
    )
    val deductions = reliefs.getOrElse(0.0) + allowableLosses.getOrElse(0.0) + aeaUsed + broughtForwardLosses.getOrElse(0.0)

    val result = ChargeableGainResultModel(gain, chargeableGain, aeaUsed, deductions)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTaxOwed
  (
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValue: Double,
    acquisitionCosts: Double,
    improvements: Double,
    reliefs: Option[Double],
    allowableLosses: Option[Double],
    broughtForwardLosses: Option[Double],
    annualExemptAmount: Double,
    previousTaxableGain: Option[Double],
    previousIncome: Double,
    personalAllowance: Double,
    taxYear: DateTime = DateTime.parse("20151010")
  ): Action[AnyContent] = Action.async { implicit request =>

    val gain = calculationService.calculateGainFlat(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts, improvements)
    val chargeableGain = calculationService.calculateChargeableGain(
      gain, reliefs.getOrElse(0.0), allowableLosses.getOrElse(0.0), annualExemptAmount, broughtForwardLosses.getOrElse(0.0)
    )
    val aeaUsed: Double = calculationService.annualExemptAmountUsed(
      annualExemptAmount,
      gain,
      calculationService.calculateChargeableGain(gain, reliefs.getOrElse(0.0), allowableLosses.getOrElse(0.0), annualExemptAmount, 0.0),
      reliefs.getOrElse(0.0),
      allowableLosses.getOrElse(0.0)
    )
    val deductions = reliefs.getOrElse(0.0) + allowableLosses.getOrElse(0.0) + aeaUsed + broughtForwardLosses.getOrElse(0.0)
    val calculationResult: CalculationResultModel  = calculationService.calculationResult (
      "individual",
      gain,
      chargeableGain,
      negativeToZero(chargeableGain),
      calculationService.brRemaining(previousIncome, personalAllowance, previousTaxableGain.getOrElse(0.0), Date.getTaxYear(taxYear)),
      0.0,
      "No",
      aeaUsed
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
      calculationResult.upperTaxRate
    )
    Future.successful(Ok(Json.toJson(result)))
  }
}