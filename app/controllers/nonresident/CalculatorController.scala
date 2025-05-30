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

package controllers.nonresident

import common.Date
import common.Date._
import common.Math._
import config.TaxRatesAndBands
import models.nonResident._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import services.CalculationService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class CalculatorController @Inject() (val calculationService: CalculationService, val cc: ControllerComponents)
    extends BackendController(cc) {

  def timeApportionedCalculationApplicable(
    disposalDate: Option[LocalDate],
    acquisitionDate: Option[LocalDate]
  ): Boolean =
    (disposalDate, acquisitionDate) match {
      case (Some(soldDate), Some(boughtDate)) => !Date.afterTaxStarted(boughtDate) && Date.afterTaxStarted(soldDate)
      case _                                  => false
    }

  def buildTotalGainsModel(
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValue: Double,
    acquisitionCosts: Double,
    improvements: Double,
    rebasedValue: Option[Double],
    rebasedCosts: Double,
    disposalDate: Option[LocalDate],
    acquisitionDate: Option[LocalDate],
    improvementsAfterTaxStarted: Double
  ): TotalGainModel = {

    val totalImprovements = improvements + improvementsAfterTaxStarted

    val flatGain = calculationService.calculateGainFlat(
      disposalValue,
      disposalCosts,
      acquisitionValue,
      acquisitionCosts,
      totalImprovements
    )

    val rebasedGain = if (timeApportionedCalculationApplicable(disposalDate, acquisitionDate)) {
      rebasedValue collect { case value =>
        calculationService.calculateGainRebased(
          disposalValue,
          disposalCosts,
          value,
          rebasedCosts,
          improvementsAfterTaxStarted
        )
      }
    } else None

    val timeApportionedGain =
      if (timeApportionedCalculationApplicable(disposalDate, acquisitionDate))
        Some(
          calculationService.calculateGainTA(
            disposalValue,
            disposalCosts,
            acquisitionValue,
            acquisitionCosts,
            totalImprovements,
            acquisitionDate,
            disposalDate.get
          )
        )
      else None

    TotalGainModel(flatGain, rebasedGain, timeApportionedGain)
  }

  def calculateTotalGain: Action[AnyContent] = Action { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        json.validate[NonResidentTotalGainRequestModel] match {
          case JsSuccess(gainModel, _) =>
            val result = buildTotalGainsModel(
              gainModel.disposalValue,
              gainModel.disposalCosts,
              gainModel.acquisitionValue,
              gainModel.acquisitionCosts,
              gainModel.improvements,
              gainModel.rebasedValue,
              gainModel.rebasedCosts,
              gainModel.disposalDate,
              gainModel.acquisitionDate,
              gainModel.improvementsAfterTaxStarted
            )
            Ok(Json.toJson(result))

          case JsError(error) => BadRequest(s"Validation failed with errors: $error")
        }
      case None       => BadRequest("No Json provided")
    }
  }

  def calculateTaxableGainAfterPRR(
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValue: Double,
    acquisitionCosts: Double,
    improvements: Double,
    rebasedValue: Option[Double],
    rebasedCosts: Double,
    disposalDate: Option[LocalDate],
    acquisitionDate: Option[LocalDate],
    improvementsAfterTaxStarted: Double,
    prrClaimed: Option[Double]
  ): Action[AnyContent] = Action.async {

    val totalGainModel = buildTotalGainsModel(
      disposalValue,
      disposalCosts,
      acquisitionValue,
      acquisitionCosts,
      improvements,
      rebasedValue,
      rebasedCosts,
      disposalDate,
      acquisitionDate,
      improvementsAfterTaxStarted
    )

    def gainsAfterPRR(model: Double) = {
      val taxableGain = calculationService.calculateChargeableGain(model, prrClaimed.getOrElse(0), 0, 0)
      val prrUsed     = calculationService.determineReliefsUsed(model, prrClaimed)
      GainsAfterPRRModel(model, taxableGain, prrUsed)
    }
    val result                       = CalculationResultsWithPRRModel(
      flatResult = gainsAfterPRR(totalGainModel.flatGain),
      rebasedResult = totalGainModel.rebasedGain.map(gainsAfterPRR),
      timeApportionedResult = totalGainModel.timeApportionedGain.map(gainsAfterPRR)
    )
    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTaxOwed(
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValue: Double,
    acquisitionCosts: Double,
    improvements: Double,
    rebasedValue: Option[Double],
    rebasedCosts: Double,
    disposalDate: LocalDate,
    acquisitionDate: Option[LocalDate],
    improvementsAfterTaxStarted: Double,
    prrClaimed: Option[Double],
    currentIncome: Double,
    personalAllowanceAmt: Double,
    allowableLoss: Double,
    previousGain: Double,
    annualExemptAmount: Double,
    broughtForwardLoss: Double,
    otherReliefsModel: OtherReliefsModel
  ): Action[AnyContent] = Action.async {

    val totalGainModel = buildTotalGainsModel(
      disposalValue,
      disposalCosts,
      acquisitionValue,
      acquisitionCosts,
      improvements,
      rebasedValue,
      rebasedCosts,
      Some(disposalDate),
      acquisitionDate,
      improvementsAfterTaxStarted
    )

    val taxYear          = getTaxYear(disposalDate)
    val calcTaxYear      = TaxRatesAndBands.getClosestTaxYear(taxYear)
    val prrValue: Double = prrClaimed.getOrElse(0)

    val flatModel = {
      val brRemaining                 = calculationService.brRemaining(currentIncome, personalAllowanceAmt, previousGain, calcTaxYear)
      val flatChargeableGain          = calculationService.calculateChargeableGain(
        totalGainModel.flatGain,
        prrValue + otherReliefsModel.flatReliefs,
        allowableLoss,
        annualExemptAmount,
        broughtForwardLoss
      )
      val flatPRRUsed                 = calculationService.determineReliefsUsed(totalGainModel.flatGain, Some(prrValue))
      val otherReliefsUsed            = calculationService.determineReliefsUsed(
        totalGainModel.flatGain - flatPRRUsed,
        Some(otherReliefsModel.flatReliefs)
      )
      val allowableLossesLeft         =
        calculationService.determineLossLeft(totalGainModel.flatGain - (flatPRRUsed + otherReliefsUsed), allowableLoss)
      val allowableLossesUsed         = allowableLoss - allowableLossesLeft
      val aeaUsed                     = calculationService.annualExemptAmountUsed(
        annualExemptAmount,
        totalGainModel.flatGain,
        prrValue + otherReliefsModel.flatReliefs,
        allowableLoss
      )
      val aeaRemaining                = calculationService.annualExemptAmountLeft(annualExemptAmount, aeaUsed)
      val broughtForwardLossRemaining = calculationService.determineLossLeft(
        totalGainModel.flatGain - (flatPRRUsed +
          round("up", allowableLoss) + aeaUsed + otherReliefsUsed),
        broughtForwardLoss
      )
      val broughtForwardLossUsed      = broughtForwardLoss - broughtForwardLossRemaining

      val reliefsRemaining = (prrValue + round("up", otherReliefsModel.flatReliefs)) - (flatPRRUsed + otherReliefsUsed)

      val taxOwed = calculationService.calculationResult(
        totalGainModel.flatGain,
        negativeToZero(flatChargeableGain),
        flatChargeableGain,
        brRemaining,
        prrClaimed,
        aeaUsed,
        aeaRemaining,
        calcTaxYear,
        isProperty = true
      )

      val totalDeductions = flatPRRUsed + otherReliefsUsed + allowableLossesUsed + aeaUsed + broughtForwardLossUsed

      TaxOwedModel(
        taxOwed.taxOwed,
        taxOwed.baseTaxGain,
        taxOwed.baseTaxRate,
        taxOwed.upperTaxGain,
        taxOwed.upperTaxRate,
        totalGainModel.flatGain,
        flatChargeableGain,
        if (flatPRRUsed > 0) Some(flatPRRUsed) else None,
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

    val rebasedModel = totalGainModel.rebasedGain.map { data =>
      val brRemaining                 = calculationService.brRemaining(currentIncome, personalAllowanceAmt, previousGain, calcTaxYear)
      val rebasedChargeableGain       = calculationService.calculateChargeableGain(
        data,
        prrValue + otherReliefsModel.rebasedReliefs,
        allowableLoss,
        annualExemptAmount,
        broughtForwardLoss
      )
      val rebasedPRRUsed              = calculationService.determineReliefsUsed(data, Some(prrValue))
      val otherReliefsUsed            = calculationService.determineReliefsUsed(
        totalGainModel.rebasedGain.get - rebasedPRRUsed,
        Some(otherReliefsModel.rebasedReliefs)
      )
      val allowableLossesLeft         =
        calculationService.determineLossLeft(data - (otherReliefsUsed + rebasedPRRUsed), allowableLoss)
      val allowableLossesUsed         = allowableLoss - allowableLossesLeft
      val aeaUsed                     = calculationService.annualExemptAmountUsed(
        annualExemptAmount,
        data,
        prrValue + otherReliefsModel.rebasedReliefs,
        allowableLoss
      )
      val aeaRemaining                = calculationService.annualExemptAmountLeft(annualExemptAmount, aeaUsed)
      val broughtForwardLossRemaining = calculationService.determineLossLeft(
        data - (rebasedPRRUsed +
          round("up", allowableLoss) + aeaUsed + otherReliefsUsed),
        broughtForwardLoss
      )
      val broughtForwardLossUsed      = broughtForwardLoss - broughtForwardLossRemaining

      val reliefsRemaining = (prrValue + round("up", otherReliefsModel.rebasedReliefs)) - (prrValue + otherReliefsUsed)

      val taxOwed = calculationService.calculationResult(
        data,
        negativeToZero(rebasedChargeableGain),
        rebasedChargeableGain,
        brRemaining,
        prrClaimed,
        aeaUsed,
        aeaRemaining,
        calcTaxYear,
        isProperty = true
      )

      val totalDeductions = rebasedPRRUsed + otherReliefsUsed + allowableLossesUsed + aeaUsed + broughtForwardLossUsed

      TaxOwedModel(
        taxOwed.taxOwed,
        taxOwed.baseTaxGain,
        taxOwed.baseTaxRate,
        taxOwed.upperTaxGain,
        taxOwed.upperTaxRate,
        data,
        rebasedChargeableGain,
        if (rebasedPRRUsed > 0) Some(rebasedPRRUsed) else None,
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

    val timeApportionedModel = totalGainModel.timeApportionedGain.map { data =>
      val brRemaining                   = calculationService.brRemaining(currentIncome, personalAllowanceAmt, previousGain, calcTaxYear)
      val timeApportionedChargeableGain = calculationService.calculateChargeableGain(
        data,
        prrValue + otherReliefsModel.timeApportionedReliefs,
        allowableLoss,
        annualExemptAmount,
        broughtForwardLoss
      )
      val timeApportionedPRRUsed        = calculationService.determineReliefsUsed(data, Some(prrValue))
      val otherReliefsUsed              = calculationService.determineReliefsUsed(
        totalGainModel.timeApportionedGain.get - timeApportionedPRRUsed,
        Some(otherReliefsModel.timeApportionedReliefs)
      )
      val allowableLossesLeft           =
        calculationService.determineLossLeft(data - (timeApportionedPRRUsed + otherReliefsUsed), allowableLoss)
      val allowableLossesUsed           = allowableLoss - allowableLossesLeft
      val aeaUsed                       = calculationService.annualExemptAmountUsed(
        annualExemptAmount,
        data,
        prrValue + otherReliefsModel.timeApportionedReliefs,
        allowableLoss
      )
      val aeaRemaining                  = calculationService.annualExemptAmountLeft(annualExemptAmount, aeaUsed)
      val broughtForwardLossRemaining   = calculationService.determineLossLeft(
        data - (timeApportionedPRRUsed +
          round("up", allowableLoss) + aeaUsed + otherReliefsUsed),
        broughtForwardLoss
      )
      val broughtForwardLossUsed        = broughtForwardLoss - broughtForwardLossRemaining
      val taxOwed                       = calculationService.calculationResult(
        data,
        negativeToZero(timeApportionedChargeableGain),
        timeApportionedChargeableGain,
        brRemaining,
        prrClaimed,
        aeaUsed,
        aeaRemaining,
        calcTaxYear,
        isProperty = true
      )

      val reliefsRemaining =
        (prrValue + round("up", otherReliefsModel.timeApportionedReliefs)) - (prrValue + otherReliefsUsed)

      val totalDeductions =
        timeApportionedPRRUsed + otherReliefsUsed + allowableLossesUsed + aeaUsed + broughtForwardLossUsed

      TaxOwedModel(
        taxOwed.taxOwed,
        taxOwed.baseTaxGain,
        taxOwed.baseTaxRate,
        taxOwed.upperTaxGain,
        taxOwed.upperTaxRate,
        data,
        timeApportionedChargeableGain,
        if (timeApportionedPRRUsed > 0) Some(timeApportionedPRRUsed) else None,
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

    val result = CalculationResultsWithTaxOwed(flatModel, rebasedModel, timeApportionedModel)
    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateTotalCosts(
    disposalCosts: Double,
    acquisitionCosts: Double,
    improvements: Double = 0
  ): Action[AnyContent] = Action.async {
    val result = calculationService.calculateTotalCosts(disposalCosts, acquisitionCosts, improvements)
    Future.successful(Ok(Json.toJson(result)))
  }
}
