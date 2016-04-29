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
  override val calculationService = CalculationService
}

trait CalculatorController extends BaseController {

  val calculationService: CalculationService

  def calculateFlat(customerType: String,
                    priorDisposal: String,
                    annualExemptAmount: Option[Double],
                    isVulnerable: Option[String],
                    currentIncome: Double,
                    personalAllowanceAmt: Double,
                    disposalValue: Double,
                    disposalCosts: Double,
                    acquisitionValueAmt: Double,
                    acquisitionCostsAmt: Double,
                    improvementsAmt: Double,
                    reliefs: Double,
                    allowableLossesAmt: Double,
                    entReliefClaimed: String): Action[AnyContent] = Action.async { implicit request =>

    val result: CalculationResultModel = CalculationService.calculateCapitalGainsTax(
      "flat",
      customerType,
      priorDisposal,
      annualExemptAmount,
      isVulnerable,
      currentIncome,
      personalAllowanceAmt,
      disposalValue,
      disposalCosts,
      acquisitionValueAmt,
      acquisitionCostsAmt,
      improvementsAmt,
      reliefs,
      allowableLossesAmt,
      entReliefClaimed
    )

    Future.successful(Ok(Json.toJson(result)))
  }
}
