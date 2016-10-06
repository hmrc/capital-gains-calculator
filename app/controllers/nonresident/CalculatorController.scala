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
import models.nonResident.CalculationRequestModel
import org.joda.time.DateTime
import play.api.libs.json.Json
import services.CalculationService
import uk.gov.hmrc.play.microservice.controller.BaseController
import play.api.mvc._

import scala.concurrent.Future


trait CalculatorController extends BaseController {

  val calculationService: CalculationService

  def calculateFlat(calculationRequestModel: CalculationRequestModel): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "flat",
      calculationRequestModel.customerType,
      calculationRequestModel.priorDisposal,
      calculationRequestModel.annualExemptAmount,
      calculationRequestModel.otherPropertiesAmount,
      calculationRequestModel.isVulnerable,
      calculationRequestModel.currentIncome,
      calculationRequestModel.personalAllowanceAmount,
      calculationRequestModel.disposalValue,
      calculationRequestModel.disposalCosts,
      calculationRequestModel.initialValue,
      calculationRequestModel.initialCosts,
      0,
      0,
      calculationRequestModel.improvementsAmount,
      calculationRequestModel.reliefsAmount,
      calculationRequestModel.allowableLosses,
      calculationRequestModel.acquisitionDate,
      calculationRequestModel.disposalDate,
      calculationRequestModel.isClaimingPRR,
      calculationRequestModel.daysClaimed,
      isProperty = true
    )

    Future.successful(Ok(Json.toJson(result)))
  }

  def calculateRebased(calculationRequestModel: CalculationRequestModel): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "rebased",
      calculationRequestModel.customerType,
      calculationRequestModel.priorDisposal,
      calculationRequestModel.annualExemptAmount,
      calculationRequestModel.otherPropertiesAmount,
      calculationRequestModel.isVulnerable,
      calculationRequestModel.currentIncome,
      calculationRequestModel.personalAllowanceAmount,
      calculationRequestModel.disposalValue,
      calculationRequestModel.disposalCosts,
      0,
      0,
      calculationRequestModel.initialValue,
      calculationRequestModel.initialCosts,
      calculationRequestModel.improvementsAmount,
      calculationRequestModel.reliefsAmount,
      calculationRequestModel.allowableLosses,
      calculationRequestModel.acquisitionDate,
      calculationRequestModel.disposalDate,
      calculationRequestModel.isClaimingPRR,
      None,
      calculationRequestModel.daysClaimed,
      isProperty = true
    )

    Future.successful(Ok(Json.toJson(result)))
  }

  //scalastyle: off
  def calculateTA(calculationRequestModel: CalculationRequestModel): Action[AnyContent] = Action.async { implicit request =>


    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "time",
      calculationRequestModel.customerType,
      calculationRequestModel.priorDisposal,
      calculationRequestModel.annualExemptAmount,
      calculationRequestModel.otherPropertiesAmount,
      calculationRequestModel.isVulnerable,
      calculationRequestModel.currentIncome,
      calculationRequestModel.personalAllowanceAmount,
      calculationRequestModel.disposalValue,
      calculationRequestModel.disposalCosts,
      calculationRequestModel.initialValue,
      calculationRequestModel.initialCosts,
      0,
      0,
      calculationRequestModel.improvementsAmount,
      calculationRequestModel.reliefsAmount,
      calculationRequestModel.allowableLosses,
      calculationRequestModel.acquisitionDate,
      calculationRequestModel.disposalDate,
      calculationRequestModel.isClaimingPRR,
      None,
      calculationRequestModel.daysClaimed,
      isProperty = true
    )

    Future.successful(Ok(Json.toJson(result)))
  }
}

object CalculatorController extends CalculatorController {
  // $COVERAGE-OFF$
  override val calculationService = CalculationService
  // $COVERAGE-ON$
}