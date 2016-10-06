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
import models.nonResident.{CalculationRequestModel, TimeApportionmentCalculationRequestModel}
import org.joda.time.DateTime
import play.api.libs.json.Json
import services.CalculationService
import uk.gov.hmrc.play.microservice.controller.BaseController
import play.api.mvc._

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
      model.acquisitionDate,
      model.disposalDate,
      model.isClaimingPRR,
      None,
      model.daysClaimed,
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