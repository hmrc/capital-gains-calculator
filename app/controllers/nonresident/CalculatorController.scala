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

package controllers.nonresident

import common.Date
import common.Date._
import common.Math._
import config.TaxRatesAndBands
import models.CalculationResultModel
import models.nonResident._
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import services.CalculationService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

trait CalculatorController extends BaseController {

  val calculationService: CalculationService

  def calculateFlat(model: CalculationRequestModel): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "flat",
      model.customerType,
      model.priorDisposal,
      model.annualExemptAmount,
      model.otherPropertiesAmount,
      model.isVulnerable,
      model.currentIncome,
      model.personalAllowanceAmount,
      model.disposalValue,
      model.disposalCosts,
      model.initialValue,
      model.initialCosts,
      0,
      0,
      model.improvementsAmount,
      model.reliefsAmount,
      model.allowableLosses,
      model.acquisitionDate,
      model.disposalDate,
      model.isClaimingPRR,
      model.daysClaimed,
      isProperty = true
    )

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateRebased(model: CalculationRequestModel): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "rebased",
      model.customerType,
      model.priorDisposal,
      model.annualExemptAmount,
      model.otherPropertiesAmount,
      model.isVulnerable,
      model.currentIncome,
      model.personalAllowanceAmount,
      model.disposalValue,
      model.disposalCosts,
      0,
      0,
      model.initialValue,
      model.initialCosts,
      model.improvementsAmount,
      model.reliefsAmount,
      model.allowableLosses,
      model.acquisitionDate,
      model.disposalDate,
      model.isClaimingPRR,
      None,
      model.daysClaimed,
      isProperty = true
    )

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTA(model: TimeApportionmentCalculationRequestModel): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "time",
      model.customerType,
      model.priorDisposal,
      model.annualExemptAmount,
      model.otherPropertiesAmount,
      model.isVulnerable,
      model.currentIncome,
      model.personalAllowanceAmount,
      model.disposalValue,
      model.disposalCosts,
      model.initialValue,
      model.initialCosts,
      0,
      0,
      model.improvementsAmount,
      model.reliefsAmount,
      model.allowableLosses,
      Some(model.acquisitionDate),
      model.disposalDate,
      model.isClaimingPRR,
      None,
      model.daysClaimed,
      isProperty = true
    )

    Future.successful(Ok(Json.toJson(result)))
  }

  def timeApportionedCalculationApplicable(disposalDate: Option[DateTime], acquisitionDate: Option[DateTime]): Boolean = {
    (disposalDate, acquisitionDate) match {
      case (Some(soldDate), Some(boughtDate)) => !Date.afterTaxStarted(boughtDate) && Date.afterTaxStarted(soldDate)
      case _ => false
    }
  }

  def buildTotalGainsModel(disposalValue: Double,
                           disposalCosts: Double,
                           acquisitionValue: Double,
                           acquisitionCosts: Double,
                           improvements: Double,
                           rebasedValue: Option[Double],
                           rebasedCosts: Double,
                           disposalDate: Option[DateTime],
                           acquisitionDate: Option[DateTime],
                           improvementsAfterTaxStarted: Double): TotalGainModel = {

    val totalImprovements = improvements + improvementsAfterTaxStarted

    val flatGain = calculationService.calculateGainFlat(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts, totalImprovements)

    val rebasedGain = rebasedValue collect { case value =>
      calculationService.calculateGainRebased(disposalValue, disposalCosts, value, rebasedCosts, improvementsAfterTaxStarted)
    }

    val timeApportionedGain = {
      if (timeApportionedCalculationApplicable(disposalDate, acquisitionDate))
        Some(calculationService.calculateGainTA(
          disposalValue,
          disposalCosts,
          acquisitionValue,
          acquisitionCosts,
          totalImprovements,
          acquisitionDate,
          disposalDate.get))
      else None
    }

    TotalGainModel(flatGain, rebasedGain, timeApportionedGain)
  }

  def calculateTotalGain(disposalValue: Double,
                         disposalCosts: Double,
                         acquisitionValue: Double,
                         acquisitionCosts: Double,
                         improvements: Double,
                         rebasedValue: Option[Double],
                         rebasedCosts: Double,
                         disposalDate: Option[DateTime],
                         acquisitionDate: Option[DateTime],
                         improvementsAfterTaxStarted: Double): Action[AnyContent] = Action.async { implicit request =>

    val result = buildTotalGainsModel(disposalValue,
      disposalCosts,
      acquisitionValue,
      acquisitionCosts,
      improvements,
      rebasedValue,
      rebasedCosts,
      disposalDate,
      acquisitionDate,
      improvementsAfterTaxStarted)

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTaxableGainAfterPRR(disposalValue: Double,
                                   disposalCosts: Double,
                                   acquisitionValue: Double,
                                   acquisitionCosts: Double,
                                   improvements: Double,
                                   rebasedValue: Option[Double],
                                   rebasedCosts: Double,
                                   disposalDate: Option[DateTime],
                                   acquisitionDate: Option[DateTime],
                                   improvementsAfterTaxStarted: Double,
                                   claimingPRR: Boolean,
                                   daysClaimed: Double,
                                   daysClaimedAfter: Double): Action[AnyContent] = Action.async { implicit request =>

    val totalGainModel = buildTotalGainsModel(disposalValue,
      disposalCosts,
      acquisitionValue,
      acquisitionCosts,
      improvements,
      rebasedValue,
      rebasedCosts,
      disposalDate,
      acquisitionDate,
      improvementsAfterTaxStarted)

    def flatModel(): GainsAfterPRRModel = {

      def flatPRRValue = (acquisitionDate, disposalDate) match {
        case (Some(_), Some(_)) => calculationService.calculateFlatPRR(disposalDate.get, acquisitionDate.get, daysClaimed, totalGainModel.flatGain)
        case _ => 0.0
      }

      val flatPRR = flatPRRValue
      val flatChargeableGain = calculationService.calculateChargeableGain(totalGainModel.flatGain, flatPRR, 0, 0, 0)
      val flatPRRUsed = calculationService.determinePRRUsed(totalGainModel.flatGain, Some(flatPRR))
      GainsAfterPRRModel(totalGainModel.flatGain, flatChargeableGain, flatPRRUsed)
    }

    def rebasedModel(): Option[GainsAfterPRRModel] = {

      val rebasedPRRValue = (disposalDate, totalGainModel.rebasedGain) match {
        case (Some(_), Some(value)) => calculationService.calculateRebasedPRR(disposalDate.get, daysClaimedAfter, value)
        case _ => 0.0
      }

      totalGainModel.rebasedGain match {
        case Some(model) =>
          val rebasedPRR = rebasedPRRValue
          val taxableGain = calculationService.calculateChargeableGain(model, rebasedPRR, 0, 0, 0)
          val prrUsed = calculationService.determinePRRUsed(model, Some(rebasedPRR))
          Some(GainsAfterPRRModel(model, taxableGain, prrUsed))
        case None => None
      }
    }

    def timeApportionedGain(): Option[GainsAfterPRRModel] = totalGainModel.timeApportionedGain match {
      case Some(model) =>
        val timeApportionedPRR = calculationService.calculateRebasedPRR(disposalDate.get, daysClaimedAfter, model)
        val taxableGain = calculationService.calculateChargeableGain(model, timeApportionedPRR, 0, 0, 0)
        val prrUsed = calculationService.determinePRRUsed(model, Some(timeApportionedPRR))
        Some(GainsAfterPRRModel(model, taxableGain, prrUsed))
      case None => None
    }

    val result = CalculationResultsWithPRRModel(flatModel(), rebasedModel(), timeApportionedGain())
    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTaxOwed(disposalValue: Double,
                       disposalCosts: Double,
                       acquisitionValue: Double,
                       acquisitionCosts: Double,
                       improvements: Double,
                       rebasedValue: Option[Double],
                       rebasedCosts: Double,
                       disposalDate: DateTime,
                       acquisitionDate: Option[DateTime],
                       improvementsAfterTaxStarted: Double,
                       claimingPRR: Boolean,
                       daysClaimed: Option[Double],
                       daysClaimedAfter: Option[Double],
                       customerType: String,
                       isVulnerable: Option[String],
                       currentIncome: Double,
                       personalAllowanceAmt: Double,
                       allowableLoss: Double,
                       previousGain: Double,
                       annualExemptAmount: Double,
                       broughtForwardLoss: Double): Action[AnyContent] = Action.async { implicit request =>

    val totalGainModel = buildTotalGainsModel(disposalValue,
      disposalCosts,
      acquisitionValue,
      acquisitionCosts,
      improvements,
      rebasedValue,
      rebasedCosts,
      Some(disposalDate),
      acquisitionDate,
      improvementsAfterTaxStarted)

    val taxYear = getTaxYear(disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)

    def flatModel() = {
      val flatPRR = (claimingPRR, acquisitionDate) match {
        case (true, Some(_)) => calculationService.calculateFlatPRR(disposalDate, acquisitionDate.get, daysClaimed.getOrElse(0.0), totalGainModel.flatGain)
        case _ => 0.0
      }
      val brRemaining = calculationService.brRemaining(currentIncome, personalAllowanceAmt, previousGain, calcTaxYear)
      val flatChargeableGain = calculationService.calculateChargeableGain(totalGainModel.flatGain, flatPRR, allowableLoss, annualExemptAmount, broughtForwardLoss)
      val flatPRRUsed = calculationService.determinePRRUsed(totalGainModel.flatGain, Some(flatPRR))
      val allowableLossesUsed = calculationService.determineLossLeft(totalGainModel.flatGain, allowableLoss)
      val aeaUsed = calculationService.annualExemptAmountUsed(annualExemptAmount, totalGainModel.flatGain, flatChargeableGain, flatPRR, allowableLoss)
      val aeaRemaining = calculationService.annualExemptAmountLeft(annualExemptAmount, aeaUsed)
      val broughtForwardLossRemaining = calculationService.determineLossLeft(totalGainModel.flatGain - (flatPRRUsed +
        round("up", allowableLoss) + aeaUsed),
        broughtForwardLoss)

      val taxOwed = calculationService.calculationResult(customerType, totalGainModel.flatGain, negativeToZero(flatChargeableGain), flatChargeableGain,
        brRemaining, flatPRR, if (claimingPRR) "Yes" else "No", aeaUsed, aeaRemaining, calcTaxYear, true)

      TaxOwedModel(taxOwed.taxOwed, taxOwed.baseTaxGain, taxOwed.baseTaxRate, taxOwed.upperTaxGain, taxOwed.upperTaxRate, totalGainModel.flatGain, flatChargeableGain,
        if (flatPRRUsed > 0) Some(flatPRRUsed) else None, if (allowableLossesUsed > 0) Some(allowableLossesUsed) else None, if (aeaUsed > 0) Some(aeaUsed) else None,
        aeaRemaining, if (broughtForwardLossRemaining > 0) Some(broughtForwardLossRemaining) else None)
    }

    def rebasedModel() = {
      totalGainModel.rebasedGain match {
        case Some(data) =>
          val rebasedPRR = {
            if (claimingPRR) calculationService.calculateRebasedPRR(disposalDate, daysClaimedAfter.getOrElse(0.0), data)
            else 0.0
          }
          val brRemaining = calculationService.brRemaining(currentIncome, personalAllowanceAmt, previousGain, calcTaxYear)
          val rebasedChargeableGain = calculationService.calculateChargeableGain(data, rebasedPRR, allowableLoss, annualExemptAmount, broughtForwardLoss)
          val rebasedPRRUsed = calculationService.determinePRRUsed(data, Some(rebasedPRR))
          val allowableLossesUsed = calculationService.determineLossLeft(data, allowableLoss)
          val aeaUsed = calculationService.annualExemptAmountUsed(annualExemptAmount, data, rebasedChargeableGain, rebasedPRR, allowableLoss)
          val aeaRemaining = calculationService.annualExemptAmountLeft(annualExemptAmount, aeaUsed)
          val broughtForwardLossRemaining = calculationService.determineLossLeft(data - (rebasedPRRUsed +
            round("up", allowableLoss) + aeaUsed),
            broughtForwardLoss)

          val taxOwed = calculationService.calculationResult(customerType, data, negativeToZero(rebasedChargeableGain), rebasedChargeableGain,
            brRemaining, rebasedPRR, if (claimingPRR) "Yes" else "No", aeaUsed, aeaRemaining, calcTaxYear, true)

          Some(TaxOwedModel(taxOwed.taxOwed, taxOwed.baseTaxGain, taxOwed.baseTaxRate, taxOwed.upperTaxGain, taxOwed.upperTaxRate, data, rebasedChargeableGain,
            if (rebasedPRRUsed > 0) Some(rebasedPRRUsed) else None, if (allowableLossesUsed > 0) Some(allowableLossesUsed) else None,
            if (aeaUsed > 0) Some(aeaUsed) else None, aeaRemaining, if (broughtForwardLossRemaining > 0) Some(broughtForwardLossRemaining) else None))
        case _ => None
      }
    }

    def timeApportionedModel() = {
      totalGainModel.timeApportionedGain match {
        case Some(data) =>
          val timeApportionedPRR = {
            if (claimingPRR) calculationService.calculateTimeApportionmentPRR(disposalDate, daysClaimedAfter.getOrElse(0.0), data)
            else 0.0
          }
          val brRemaining = calculationService.brRemaining(currentIncome, personalAllowanceAmt, previousGain, calcTaxYear)
          val timeApportionedChargeableGain = calculationService.calculateChargeableGain(data, timeApportionedPRR, allowableLoss, annualExemptAmount, broughtForwardLoss)
          val timeApportionedPRRUsed = calculationService.determinePRRUsed(data, Some(timeApportionedPRR))
          val allowableLossesUsed = calculationService.determineLossLeft(data, allowableLoss)
          val aeaUsed = calculationService.annualExemptAmountUsed(annualExemptAmount, data, timeApportionedChargeableGain, timeApportionedPRR, allowableLoss)
          val aeaRemaining = calculationService.annualExemptAmountLeft(annualExemptAmount, aeaUsed)
          val broughtForwardLossRemaining = calculationService.determineLossLeft(data - (timeApportionedPRRUsed +
            round("up", allowableLoss) + aeaUsed),
            broughtForwardLoss)
          val taxOwed = calculationService.calculationResult(customerType, data, negativeToZero(timeApportionedChargeableGain), timeApportionedChargeableGain,
            brRemaining, timeApportionedPRR, if (claimingPRR) "Yes" else "No", aeaUsed, aeaRemaining, calcTaxYear, true)

          Some(TaxOwedModel(taxOwed.taxOwed, taxOwed.baseTaxGain, taxOwed.baseTaxRate, taxOwed.upperTaxGain, taxOwed.upperTaxRate, data, timeApportionedChargeableGain,
            if (timeApportionedPRRUsed > 0) Some(timeApportionedPRRUsed) else None, if (allowableLossesUsed > 0) Some(allowableLossesUsed) else None,
            if (aeaUsed > 0) Some(aeaUsed) else None, aeaRemaining, if (broughtForwardLossRemaining > 0) Some(broughtForwardLossRemaining) else None))
        case _ => None
      }
    }

    val result = CalculationResultsWithTaxOwed(flatModel(), rebasedModel(), timeApportionedModel())
    Future.successful(Ok(Json.toJson(result)))
  }
}

object CalculatorController extends CalculatorController {
  override val calculationService = CalculationService
}