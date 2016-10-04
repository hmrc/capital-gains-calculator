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

package models.resident.shares

import common.Validation
import common.binders.CommonBinders
import org.joda.time.DateTime
import play.api.mvc.QueryStringBindable

case class CalculateTaxOwedModel (chargeableGainModel: ChargeableGainModel,
                                  previousTaxableGain: Option[Double],
                                  previousIncome: Double,
                                  personalAllowance: Double,
                                  disposalDate: DateTime)

object CalculateTaxOwedModel extends CommonBinders {

  implicit def calculateTaxOwedBinder(implicit doubleBinder: QueryStringBindable[Double],
                                    optionDoubleBinder: QueryStringBindable[Option[Double]],
                                      chargeableGainBinder: QueryStringBindable[ChargeableGainModel],
                                      localDateBinder: QueryStringBindable[DateTime]): QueryStringBindable[CalculateTaxOwedModel] =
    new QueryStringBindable[CalculateTaxOwedModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CalculateTaxOwedModel]] = {
        for {
          chargeableGainModelEither <- chargeableGainBinder.bind("chargeableGain", params)
          previousTaxableGainEither <- optionDoubleBinder.bind("previousTaxableGain", params)
          previousIncomeEither <- doubleBinder.bind("previousIncome", params)
          personalAllowanceEither <- doubleBinder.bind("personalAllowance", params)
          disposalDateEither <- localDateBinder.bind("disposalDate", params)
        } yield {
          val inputs = (chargeableGainModelEither, previousTaxableGainEither, previousIncomeEither, personalAllowanceEither,
            disposalDateEither)
          inputs match {
            case (Right(chargeableGain), Right(previousTaxableGain), Right(previousIncome), Right(personalAllowance), Right(disposalDate)) =>
              Right(CalculateTaxOwedModel(chargeableGain, previousTaxableGain, previousIncome, personalAllowance, disposalDate))
            case _ =>
              val inputs = Seq(chargeableGainModelEither, previousTaxableGainEither, previousIncomeEither, personalAllowanceEither,
                disposalDateEither)
              Left(Validation.getFirstErrorMessage(inputs))
          }
        }
      }

      override def unbind(key: String, calculateTaxOwedModel: CalculateTaxOwedModel): String =
          s"${chargeableGainBinder.unbind("chargeableGain", calculateTaxOwedModel.chargeableGainModel)}&" +
          s"previousTaxableGain=${optionDoubleBinder.unbind("previousTaxableGain", calculateTaxOwedModel.previousTaxableGain)}&" +
          s"previousIncome=${doubleBinder.unbind("previousIncome", calculateTaxOwedModel.previousIncome)}&" +
          s"personalAllowance=${doubleBinder.unbind("personalAllowance", calculateTaxOwedModel.personalAllowance)}&" +
          s"disposalDate=${localDateBinder.unbind("disposalDate", calculateTaxOwedModel.disposalDate)}"

    }
}