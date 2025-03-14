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

package controllers

import com.typesafe.config.ConfigFactory
import common._
import common.validation.TaxRatesAndBandsValidation
import config.TaxRatesAndBands
import config.TaxRatesAndBands._
import models._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

@Singleton
class TaxRatesAndBandsController @Inject()(val cc: ControllerComponents
                                          )(implicit ec: ExecutionContext) extends BackendController(cc) {

  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d")

  def getMaxAEA(year: Int): Action[AnyContent] = Action.async {
    if (TaxRatesAndBandsValidation.checkValidTaxYear(year)) Future.successful(Ok(Json.toJson(getRates(year).maxAnnualExemptAmount)))
    else Future.successful(BadRequest(Json.toJson("This tax year is not valid")))
  }

  def getMaxPersonalAllowance(year: Int, isEligibleBlindPersonsAllowance: Option[Boolean],
                              isEligibleMarriageAllowance: Option[Boolean]): Action[AnyContent] = Action.async {
    if (TaxRatesAndBandsValidation.checkValidTaxYear(year)) {
      val rates = getRates(year)
      val blindPersonalAllowance = if (isEligibleBlindPersonsAllowance.contains(true)) rates.blindPersonsAllowance else 0
      val marriageAllowance = if (isEligibleMarriageAllowance.contains(true)) rates.marriageAllowance else 0
      Future.successful(Ok(Json.toJson(rates.maxPersonalAllowance + blindPersonalAllowance + marriageAllowance)))
    }
    else Future.successful(BadRequest(Json.toJson("This tax year is not valid")))
  }

  def getTaxYear(dateString: String): Action[AnyContent] = Action.async {

    def tryParsing(): Either[String, LocalDate] = {
      Try {
        LocalDate.parse(dateString, dateFormatter)
      } match {
        case Success(date) => Right(date)
        case _ => Left(ValidationErrorMessages.invalidDateFormat(dateString))
      }
    }

    tryParsing() match {
      case Right(parsedDate) =>
        val taxYear = Date.getTaxYear(parsedDate)
        val isValidTaxYear:Boolean = (Date.taxYearToString(taxYear)<= Date.taxYearToString(LocalDate.now().getYear)) && (Date.taxYearToString(taxYear)>= Date.taxYearToString(TaxRatesAndBands.allRates.head.taxYear))
        val result = TaxYearModel(Date.taxYearToString(taxYear), isValidTaxYear,
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
