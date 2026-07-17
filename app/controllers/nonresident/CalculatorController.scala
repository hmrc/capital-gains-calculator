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
import models.nonResident._
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import services.CalculationService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import controllers.nonresident.NonResidentTaxCalculationHelper

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class CalculatorController @Inject() (
  val calculationService: CalculationService,
  val cc: ControllerComponents,
  val nonResidentTaxCalculationHelper: NonResidentTaxCalculationHelper
) extends BackendController(cc) {

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
    val prrValue: Double = prrClaimed.getOrElse(0)

    val flatModel =
      nonResidentTaxCalculationHelper.buildTaxOwedModel(
        gain = totalGainModel.flatGain,
        reliefs = otherReliefsModel.flatReliefs,
        currentIncome = currentIncome,
        personalAllowanceAmt = personalAllowanceAmt,
        previousGain = previousGain,
        prrValue = prrValue,
        allowableLoss = allowableLoss,
        annualExemptAmount = annualExemptAmount,
        broughtForwardLoss = broughtForwardLoss,
        prrClaimed = prrClaimed,
        calcTaxYear = taxYear
      )

    val rebasedModel =
      totalGainModel.rebasedGain.map { gain =>
        nonResidentTaxCalculationHelper.buildTaxOwedModel(
          gain = gain,
          reliefs = otherReliefsModel.rebasedReliefs,
          currentIncome = currentIncome,
          personalAllowanceAmt = personalAllowanceAmt,
          previousGain = previousGain,
          prrValue = prrValue,
          allowableLoss = allowableLoss,
          annualExemptAmount = annualExemptAmount,
          broughtForwardLoss = broughtForwardLoss,
          prrClaimed = prrClaimed,
          calcTaxYear = taxYear,
          useClaimedPrrForReliefsRemaining = true
        )
      }

    val timeApportionedModel =
      totalGainModel.timeApportionedGain.map { gain =>
        nonResidentTaxCalculationHelper.buildTaxOwedModel(
          gain = gain,
          reliefs = otherReliefsModel.timeApportionedReliefs,
          currentIncome = currentIncome,
          personalAllowanceAmt = personalAllowanceAmt,
          previousGain = previousGain,
          prrValue = prrValue,
          allowableLoss = allowableLoss,
          annualExemptAmount = annualExemptAmount,
          broughtForwardLoss = broughtForwardLoss,
          prrClaimed = prrClaimed,
          calcTaxYear = taxYear,
          useClaimedPrrForReliefsRemaining = true
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
