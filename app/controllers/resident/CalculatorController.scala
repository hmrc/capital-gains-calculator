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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.CalculationService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

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
}