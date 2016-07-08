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

import play.api.libs.json.Json
import uk.gov.hmrc.play.microservice.controller.BaseController
import scala.concurrent.Future
import play.api.mvc.{Action, AnyContent}
import config.TaxRatesAndBands._
import models._

object TaxRatesAndBandsController extends TaxRatesAndBandsController {
}

trait TaxRatesAndBandsController extends BaseController {

  def getMaxAEA(year: Int): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(Json.toJson(AnnualExemptAmountModel(getRates(year).maxAnnualExemptAmount))))
  }
  def getMaxNonVulnerableAEA(year: Int): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(Json.toJson(AnnualExemptAmountModel(getRates(year).notVulnerableMaxAnnualExemptAmount))))
  }
  def getMaxPersonalAllowance(year: Int, isEligibleBlindPersonsAllowance: Option[Boolean]): Action[AnyContent] = Action.async { implicit request =>
    isEligibleBlindPersonsAllowance match {
      case Some(true) => Future.successful(Ok(Json.toJson(PersonalAllowanceModel(getRates(year).maxPersonalAllowance + getRates(year).blindPersonsAllowance))))
      case _ =>     Future.successful(Ok(Json.toJson(PersonalAllowanceModel(getRates(year).maxPersonalAllowance))))
    }
  }

}
