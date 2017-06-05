/*
 * Copyright 2017 HM Revenue & Customs
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

import common._
import common.validation.TaxRatesAndBandsValidation
import config.TaxRatesAndBands
import play.api.libs.json.Json
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import play.api.mvc.{Action, AnyContent}
import config.TaxRatesAndBands._
import models._
import org.joda.time.DateTime

import scala.util.{Success, Try}

object TaxRatesAndBandsController extends TaxRatesAndBandsController {
}

trait TaxRatesAndBandsController extends BaseController {

  def getMaxAEA(year: Int): Action[AnyContent] = Action.async { implicit request =>
   if(TaxRatesAndBandsValidation.checkValidTaxYear(year)) Future.successful(Ok(Json.toJson(getRates(year).maxAnnualExemptAmount)))
   else Future.successful(BadRequest(Json.toJson("This tax year is not valid")))
  }

  def getMaxPersonalAllowance(year: Int, isEligibleBlindPersonsAllowance: Option[Boolean]): Action[AnyContent] = Action.async { implicit request =>
    if(TaxRatesAndBandsValidation.checkValidTaxYear(year)){
      isEligibleBlindPersonsAllowance match {
        case Some(true) => Future.successful(Ok(Json.toJson(getRates(year).maxPersonalAllowance + getRates(year).blindPersonsAllowance)))
        case _ =>     Future.successful(Ok(Json.toJson(getRates(year).maxPersonalAllowance)))
      }
    }
    else Future.successful(BadRequest(Json.toJson("This tax year is not valid")))
  }

  def getTaxYear(dateString: String): Action[AnyContent] = Action.async { implicit request =>

    def tryParsing(): Either[String, DateTime] = {
      Try {
        DateTime.parse(dateString)
      } match {
        case Success(date) => Right(date)
        case _ => Left(ValidationErrorMessages.invalidDateFormat(dateString))
      }
    }

    tryParsing() match {
      case Right(parsedDate) =>
        val taxYear = Date.getTaxYear(parsedDate)
        val result = TaxYearModel(Date.taxYearToString(taxYear), TaxRatesAndBands.filterRatesByTaxYear(taxYear).nonEmpty,
          Date.taxYearToString(TaxRatesAndBands.getClosestTaxYear(taxYear)))
        Future.successful(Ok(Json.toJson(result)))

      case Left(errorMessage) =>
        Future.successful(BadRequest(Json.toJson(errorMessage)))

    }
  }
}
