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

package common.binders

import common.QueryStringKeys.{ResidentSharesCalculationKeys => queryKeys}
import common.Validation
import models.resident.shares.{ChargeableGainModel, TotalGainModel}
import play.api.mvc.QueryStringBindable

trait ResidentSharesBinders {

  val totalGainParameters = Seq(queryKeys.disposalValue, queryKeys.disposalCosts, queryKeys.acquisitionValue, queryKeys.acquisitionCosts)
  val chargeableGainParameters = totalGainParameters ++ Seq(queryKeys.annualExemptAmount)

  implicit def totalGainBinder(implicit doubleBinder: QueryStringBindable[Double]): QueryStringBindable[TotalGainModel] =
    new QueryStringBindable[TotalGainModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, TotalGainModel]] = {

        val missingParameter = totalGainParameters.find(element => params.get(element).isEmpty)

        if(missingParameter.isEmpty) {
          for {
            disposalValueEither <- doubleBinder.bind(queryKeys.disposalValue, params)
            disposalCostsEither <- doubleBinder.bind(queryKeys.disposalCosts, params)
            acquisitionValueEither <- doubleBinder.bind(queryKeys.acquisitionValue, params)
            acquisitionCostsEither <- doubleBinder.bind(queryKeys.acquisitionCosts, params)
          } yield {
            val inputs = Seq(disposalValueEither, disposalCostsEither, acquisitionValueEither, acquisitionCostsEither)
            inputs match {
              case Seq(Right(disposalValue), Right(disposalCosts), Right(acquisitionValue), Right(acquisitionCosts)) =>
                Right(TotalGainModel(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts))
              case fail => Left(Validation.getFirstErrorMessage(fail))
            }
          }
        }
        else Some(Left(s"${missingParameter.get} is required."))
      }

      override def unbind(key: String, request: TotalGainModel): String =
        Seq(
          doubleBinder.unbind(queryKeys.disposalValue, request.disposalValue),
          doubleBinder.unbind(queryKeys.disposalCosts, request.disposalCosts),
          doubleBinder.unbind(queryKeys.acquisitionValue, request.acquisitionValue),
          doubleBinder.unbind(queryKeys.acquisitionCosts, request.acquisitionCosts)
        ).mkString("&")
    }

  implicit def chargeableGainBinder(implicit doubleBinder: QueryStringBindable[Double],
                                    totalGainBinder: QueryStringBindable[TotalGainModel],
                                    optionDoubleBinder: QueryStringBindable[Option[Double]]): QueryStringBindable[ChargeableGainModel] =
    new QueryStringBindable[ChargeableGainModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ChargeableGainModel]] = {

        val missingParameter = chargeableGainParameters.find(element => params.get(element).isEmpty)

        if(missingParameter.isEmpty) {
          for {
            totalGainModelEither <- totalGainBinder.bind("", params)
            annualExemptAmountEither <- doubleBinder.bind(queryKeys.annualExemptAmount, params)
            allowableLossesEither <- optionDoubleBinder.bind(queryKeys.allowableLosses, params)
            broughtForwardLossesEither <- optionDoubleBinder.bind(queryKeys.broughtForwardLosses, params)
          } yield {
            val inputs = (totalGainModelEither, annualExemptAmountEither, allowableLossesEither, broughtForwardLossesEither)
            inputs match {
              case (Right(totalGain), Right(annualExemptAmount), Right(allowableLosses), Right(broughtForwardLosses)) =>
                Right(ChargeableGainModel(totalGain, allowableLosses, broughtForwardLosses, annualExemptAmount))
              case _ =>
                val inputs = Seq(totalGainModelEither, annualExemptAmountEither, allowableLossesEither, broughtForwardLossesEither)
                Left(Validation.getFirstErrorMessage(inputs))
            }
          }
        }
          else Some(Left(s"${missingParameter.get} is required."))
        }

      override def unbind(key: String, chargeableGainModel: ChargeableGainModel): String =
        Seq(
          totalGainBinder.unbind("", chargeableGainModel.totalGainModel),
          optionDoubleBinder.unbind(queryKeys.allowableLosses, chargeableGainModel.allowableLosses),
          optionDoubleBinder.unbind(queryKeys.broughtForwardLosses, chargeableGainModel.broughtForwardLosses),
          doubleBinder.unbind(queryKeys.annualExemptAmount, chargeableGainModel.annualExemptAmount)
        ).filter(!_.isEmpty).mkString("&")

    }
}
