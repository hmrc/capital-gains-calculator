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

import models.CalculationResultModel
import models.nonResident._
import org.joda.time.DateTime
import play.api.libs.json.Json
import services.CalculationService
import uk.gov.hmrc.play.microservice.controller.BaseController
import play.api.mvc._
import common.Date

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
                              improvementsAfterTaxStarted: Double):TotalGainModel = {

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

    print(Json.toJson(TotalGainModel(flatGain, rebasedGain, timeApportionedGain)))
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
        case (Some(_), Some(_)) => CalculationService.calculateFlatPRR(disposalDate.get, acquisitionDate.get, daysClaimed, totalGainModel.flatGain)
        case _ => 0.0
      }

      val flatPRR = flatPRRValue
      val flatChargeableGain = CalculationService.calculateChargeableGain(totalGainModel.flatGain, flatPRR, 0, 0, 0)
      val flatPRRUsed = CalculationService.determinePRRUsed(flatChargeableGain, Some(flatPRR))
      GainsAfterPRRModel(totalGainModel.flatGain, flatChargeableGain, flatPRRUsed)
    }

    def rebasedModel(): Option[GainsAfterPRRModel] = {

      def rebasedPRRValue(rebasedGain: Double) = disposalDate match {
        case Some(_) => CalculationService.calculateRebasedPRR(disposalDate.get, daysClaimedAfter, rebasedGain)
        case _ => 0.0
      }

      totalGainModel.rebasedGain match {
        case Some(model) =>
          val rebasedPRR = rebasedPRRValue(model)
          val taxableGain = CalculationService.calculateChargeableGain(model, rebasedPRR, 0, 0, 0)
          val prrUsed = CalculationService.determinePRRUsed(model, Some(rebasedPRR))
          Some(GainsAfterPRRModel(model, taxableGain, prrUsed))
        case None => None
      }
    }

    def timeApportionedGain(): Option[GainsAfterPRRModel] = totalGainModel.timeApportionedGain match {
        case Some(model) =>
          val timeApportionedPRR = CalculationService.calculateRebasedPRR(disposalDate.get, daysClaimedAfter, model)
          val taxableGain = CalculationService.calculateChargeableGain(model, timeApportionedPRR, 0, 0, 0)
          val prrUsed = CalculationService.determinePRRUsed(model, Some(timeApportionedPRR))
          Some(GainsAfterPRRModel(model, taxableGain, prrUsed))
        case None => None
    }

    val result = CalculationResultsWithPRRModel(flatModel(), rebasedModel(), timeApportionedGain())
    Future.successful(Ok(Json.toJson(result)))
  }
}

object CalculatorController extends CalculatorController {
  override val calculationService = CalculationService
}