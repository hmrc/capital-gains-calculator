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

package controllers

import models.CalculationResultModel
import play.api.libs.json.Json
import services.CalculationService
import uk.gov.hmrc.play.microservice.controller.BaseController
import play.api.mvc._
import services.CalculationService._

import scala.concurrent.Future

object CalculatorController extends CalculatorController {
  // $COVERAGE-OFF$
  override val calculationService = CalculationService
  // $COVERAGE-ON$
}

trait CalculatorController extends BaseController {

  val calculationService: CalculationService

  def calculateFlat
  (
    customerType: String,
    priorDisposal: String,
    annualExemptAmount: Option[Double],
    otherPropertiesAmt: Option[Double],
    isVulnerable: Option[String],
    currentIncome: Option[Double],
    personalAllowanceAmt: Option[Double],
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValueAmt: Double,
    acquisitionCostsAmt: Double,
    improvementsAmt: Double,
    reliefs: Double,
    allowableLossesAmt: Double,
    entReliefClaimed: String,
    acquisitionDate: Option[String] = None,
    disposalDate: Option[String] = None,
    isClaimingPRR: Option[String],
    daysClaimed: Option[Double]
  ): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "flat",
      customerType,
      priorDisposal,
      annualExemptAmount,
      otherPropertiesAmt,
      isVulnerable,
      currentIncome,
      personalAllowanceAmt,
      disposalValue,
      disposalCosts,
      acquisitionValueAmt,
      acquisitionCostsAmt,
      0,
      0,
      improvementsAmt,
      reliefs,
      allowableLossesAmt,
      entReliefClaimed,
      None,
      None,
      isClaimingPRR,
      daysClaimed
    )

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateRebased
  (
    customerType: String,
    priorDisposal: String,
    annualExemptAmount: Option[Double],
    otherPropertiesAmt: Option[Double],
    isVulnerable: Option[String],
    currentIncome: Option[Double],
    personalAllowanceAmt: Option[Double],
    disposalValue: Double,
    disposalCosts: Double,
    rebasedValue: Double,
    revaluationCost: Double,
    improvementsAmt: Double,
    reliefs: Double,
    allowableLossesAmt: Double,
    entReliefClaimed: String,
    acquisitionDate: Option[String] = None,
    disposalDate: Option[String] = None,
    isClaimingPRR: Option[String],
    daysClaimedAfter: Option[Double]

  ): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "rebased",
      customerType,
      priorDisposal,
      annualExemptAmount,
      otherPropertiesAmt,
      isVulnerable,
      currentIncome,
      personalAllowanceAmt,
      disposalValue,
      disposalCosts,
      0,
      0,
      rebasedValue,
      revaluationCost,
      improvementsAmt,
      reliefs,
      allowableLossesAmt,
      entReliefClaimed,
      acquisitionDate,
      disposalDate,
      isClaimingPRR,
      daysClaimedAfter
    )

    Future.successful(Ok(Json.toJson(result)))
  }

//scalastyle: off
  def calculateTA
  (
    customerType: String,
    priorDisposal: String,
    annualExemptAmount: Option[Double],
    otherPropertiesAmt: Option[Double],
    isVulnerable: Option[String],
    currentIncome: Option[Double],
    personalAllowanceAmt: Option[Double],
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValueAmt: Double,
    acquisitionCostsAmt: Double,
    improvementsAmt: Double,
    reliefs: Double,
    allowableLossesAmt: Double,
    entReliefClaimed: String,
    acquisitionDate: Option[String],
    disposalDate: Option[String],
    isClaimingPRR: Option[String] = None,
    daysClaimedAfter: Option[Double] = None
  ): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "time",
      customerType,
      priorDisposal,
      annualExemptAmount,
      otherPropertiesAmt,
      isVulnerable,
      currentIncome,
      personalAllowanceAmt,
      disposalValue,
      disposalCosts,
      acquisitionValueAmt,
      acquisitionCostsAmt,
      0,
      0,
      improvementsAmt,
      reliefs,
      allowableLossesAmt,
      entReliefClaimed,
      acquisitionDate,
      disposalDate,
      isClaimingPRR,
      daysClaimedAfter
    )

    Future.successful(Ok(Json.toJson(result)))
  }
}
