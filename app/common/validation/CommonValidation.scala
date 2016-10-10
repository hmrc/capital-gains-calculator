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

package common.validation

import common.Date
import config.{TaxRatesAndBands, TaxRatesAndBands20152016}
import org.joda.time.DateTime
import common.QueryStringKeys.{ResidentSharesCalculationKeys => sharesKeys}

object CommonValidation {

  val maxNumeric = 1000000000.0
  val minNumeric = 0.0

  def getFirstErrorMessage(inputs: Seq[Either[String, Any]]): String = {
    inputs.find(_.isLeft).fold("")(_.left.get)
  }

  def validateDouble(input: Double, key: String): Either[String, Double] = {
    Seq(validateMinimum(input, key), validateMaximum(input, key), validateDecimalPlaces(input, key)) match {
      case Seq(Right(_), Right(_), Right(_)) => Right(input)
      case failed => Left(getFirstErrorMessage(failed))
    }
  }

  def validateOptionDouble(input: Option[Double], key: String): Either[String, Option[Double]] = {
    input match {
      case Some(data) => validateDouble(data, key) match {
        case Right(value) => Right(Some(value))
        case Left(message) => Left(message)
      }
      case _ => Right(None)
    }
  }

  def validateMinimum(input: Double, key: String): Either[String, Double] = {
    if (input >= minNumeric) Right(input)
    else Left(s"$key cannot be negative.")
  }

  def validateMaximum(input: Double, key: String): Either[String, Double] = {
    if (input <= maxNumeric) Right(input)
    else Left(s"$key cannot be larger than 100,000,000.")
  }

  def validateDecimalPlaces(input: Double, key: String): Either[String, Double] = {
    val value = input.toString.split("\\.")
    if (value.size > 1) {
      if (value.apply(1).length <= 2) Right(input)
      else Left(s"$key has too many decimal places.")
    } else Right(input)
  }

  def validateResidentPersonalAllowance(input: Double, disposalDate: DateTime): Either[String, Double] = {
    val closestTaxYear = TaxRatesAndBands.getClosestTaxYear(Date.getTaxYear(disposalDate))
    val taxBands = TaxRatesAndBands.getRates(closestTaxYear)
    val maxPersonalAllowance = taxBands.maxPersonalAllowance + taxBands.blindPersonsAllowance

    validateDouble(input, sharesKeys.personalAllowance) match {
      case Right(data) if data <= maxPersonalAllowance => Right(input)
      case Right(test) =>
        Left(s"${sharesKeys.personalAllowance} cannot exceed $maxPersonalAllowance")
      case Left(message) => Left(message)
    }
  }

  def validateDisposalDate(disposalDate: DateTime): Either[String, DateTime] = {
    if (!disposalDate.isBefore(TaxRatesAndBands20152016.startOfTaxDateTime)) Right(disposalDate)
    else Left("disposalDate cannot be before 2015-04-06")
  }

  //When the refactor the the non-resident calculator happens this validation should become
  //redundant.
  def validateYesNo(input: String, key: String): Either[String, String] = {
    if (input == "Yes" || input == "No") Right(input)
    else Left(s"$key must be either Yes or No")
  }

  def validateOptionYesNo(input: Option[String], key: String): Either[String, Option[String]] = {
    input match {
      case Some(data) => validateYesNo(data, key) match {
        case Right(value) => Right(Some(value))
        case Left(message) => Left(message)
      }
      case _ => Right(None)
    }
  }

  def validateCustomerType(input: String, key: String): Either[String, String] = {
    if (input == "individual" || input == "trustee" || input == "personalRep") Right(input)
    else Left(s"$key must be either individual, trustee or personalRep")
  }

  def validateOptionalAcquisitionDate(disposalDate: DateTime, acquisitionDate: Option[DateTime]): Either[String, Option[DateTime]] = {
    acquisitionDate match {
      case Some(data) if data.isBefore(disposalDate) => Right(Some(acquisitionDate.get))
      case None => Right(None)
      case _ => Left(s"The acquisitionDate must be before the disposalDate")
    }
  }

  def validateAcquisitionDate(disposalDate: DateTime, acquisitionDate: DateTime): Either[String, Option[DateTime]] = {
    acquisitionDate match {
      case data if data.isBefore(disposalDate) => Right(Some(acquisitionDate))
      case _ => Left(s"The acquisitionDate must be before the disposalDate")
    }
  }
}
