/*
 * Copyright 2022 HM Revenue & Customs
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
import config.TaxRatesAndBands._
import javax.inject.{Inject, Singleton}
import models._
import org.joda.time.DateTime
import play.api.libs.json.JodaWrites._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Success, Try}

@Singleton
class TaxRatesAndBandsController @Inject()(
                                            val cc: ControllerComponents
                                          ) extends BackendController(cc) {

  def getMaxAEA(year: Int): Action[AnyContent] = Action.async {
    if(TaxRatesAndBandsValidation.checkValidTaxYear(year)) Future.successful(Ok(Json.toJson(getRates(year).maxAnnualExemptAmount)))
    else Future.successful(BadRequest(Json.toJson("This tax year is not valid")))
  }

  def getMaxPersonalAllowance(year: Int, isEligibleBlindPersonsAllowance: Option[Boolean]): Action[AnyContent] = Action.async {
    if(TaxRatesAndBandsValidation.checkValidTaxYear(year)){
      isEligibleBlindPersonsAllowance match {
        case Some(true) => Future.successful(Ok(Json.toJson(getRates(year).maxPersonalAllowance + getRates(year).blindPersonsAllowance)))
        case _ =>     Future.successful(Ok(Json.toJson(getRates(year).maxPersonalAllowance)))
      }
    }
    else Future.successful(BadRequest(Json.toJson("This tax year is not valid")))
  }

  def getTaxYear(dateString: String): Action[AnyContent] = Action.async {

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

  val getMinimumDate: Action[AnyContent] = Action.async {
    Future {
      val date = Date.taxYearEndDate(TaxRatesAndBands.getEarliestTaxYear.taxYear)
      Ok(Json.toJson(date))
    }
  }
}
