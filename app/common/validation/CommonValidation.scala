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
}
