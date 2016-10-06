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

import common.QueryStringKeys.{ResidentPropertiesCalculationKeys => queryKeys}
import common.Validation
import models.resident.properties.{PropertyChargeableGainModel, PropertyTotalGainModel}
import models.resident.shares.TotalGainModel
import play.api.mvc.QueryStringBindable

object ResidentPropertyBinders extends ResidentPropertyBinders

trait ResidentPropertyBinders extends CommonBinders {

  val totalGainParameters = Seq(queryKeys.disposalValue, queryKeys.disposalCosts, queryKeys.acquisitionValue, queryKeys.acquisitionCosts)
  val propertyTotalGainParameters = totalGainParameters ++ Seq(queryKeys.improvements)
  val chargeableGainParameters = propertyTotalGainParameters ++ Seq(queryKeys.annualExemptAmount, queryKeys.disposalDate)

  implicit def propertyTotalGainBinder(implicit totalGainBinder: QueryStringBindable[TotalGainModel],
                                       doubleBinder: QueryStringBindable[Double]) : QueryStringBindable[PropertyTotalGainModel] =
    new QueryStringBindable[PropertyTotalGainModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PropertyTotalGainModel]] = {

        val missingParameter = propertyTotalGainParameters.find(element => params.get(element).isEmpty)

        if(missingParameter.isEmpty) {
          for {
            totalGainModelEither <- totalGainBinder.bind("", params)
            improvementsEither <- doubleBinder.bind(queryKeys.improvements, params)
          } yield {
            val inputs = (totalGainModelEither, improvementsEither)
            inputs match {
              case (Right(totalGainModel), Right(improvements)) =>
                Right(PropertyTotalGainModel(totalGainModel, improvements))
              case _ =>
                val inputs = Seq(totalGainModelEither, improvementsEither)
                Left(Validation.getFirstErrorMessage(inputs))
            }
          }
        }
        else Some(Left(s"${missingParameter.get} is required."))
      }

      override def unbind(key: String, propertyTotalGainModel: PropertyTotalGainModel): String = {
        Seq(
          totalGainBinder.unbind("", propertyTotalGainModel.totalGainModel),
          doubleBinder.unbind(queryKeys.improvements, propertyTotalGainModel.improvements)
        ).filter(!_.isEmpty).mkString("&")
      }
    }

  implicit def propertyChargeableGainBinder(implicit propertyTotalGainBinder: QueryStringBindable[PropertyTotalGainModel],
                                            optionDoubleBinder: QueryStringBindable[Option[Double]],
                                            doubleBinder: QueryStringBindable[Double]) : QueryStringBindable[PropertyChargeableGainModel] = {
    new QueryStringBindable[PropertyChargeableGainModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PropertyChargeableGainModel]] = {

        val missingParameter = chargeableGainParameters.find(element => params.get(element).isEmpty)

        if(missingParameter.isEmpty) {
          for {
            propertyTotalGainModelEither <- propertyTotalGainBinder.bind("", params)
            prrValueEither <- optionDoubleBinder.bind(queryKeys.prrValue, params)
            lettingReliefsEither <- optionDoubleBinder.bind(queryKeys.lettingReliefs, params)
            allowableLossesEither <- optionDoubleBinder.bind(queryKeys.allowableLosses, params)
            broughtForwardLossesEither <- optionDoubleBinder.bind(queryKeys.broughtForwardLosses, params)
            annualExemptAmountEither <- doubleBinder.bind(queryKeys.annualExemptAmount, params)
            disposalDateEither <- dateTimeBinder.bind(queryKeys.disposalDate, params)
          } yield {

            val inputs = (propertyTotalGainModelEither, prrValueEither, lettingReliefsEither, allowableLossesEither,
              broughtForwardLossesEither, annualExemptAmountEither, disposalDateEither)
            inputs match {
              case (Right(propertyTotalGain), Right(prrValue), Right(lettingReliefs), Right(allowableLosses), Right(broughtForwardLosses),
                Right(annualExemptAmount), Right(disposalDate)) =>
                Right(PropertyChargeableGainModel(propertyTotalGain, prrValue, lettingReliefs, allowableLosses, broughtForwardLosses,
                  annualExemptAmount, disposalDate))
              case _ =>
                val inputs = Seq(propertyTotalGainModelEither, prrValueEither, lettingReliefsEither, allowableLossesEither,
                  broughtForwardLossesEither, annualExemptAmountEither, disposalDateEither)
                Left(Validation.getFirstErrorMessage(inputs))
            }
          }
        }

        else Some(Left(s"${missingParameter.get} is required."))
      }

      override def unbind(key: String, propertyChargeableGainModel: PropertyChargeableGainModel): String =
        Seq(
          propertyTotalGainBinder.unbind("", propertyChargeableGainModel.propertyTotalGainModel),
          optionDoubleBinder.unbind(queryKeys.prrValue, propertyChargeableGainModel.prrValue),
          optionDoubleBinder.unbind(queryKeys.lettingReliefs, propertyChargeableGainModel.lettingReliefs),
          optionDoubleBinder.unbind(queryKeys.allowableLosses, propertyChargeableGainModel.allowableLosses),
          optionDoubleBinder.unbind(queryKeys.broughtForwardLosses, propertyChargeableGainModel.broughtForwardLosses),
          doubleBinder.unbind(queryKeys.annualExemptAmount, propertyChargeableGainModel.annualExemptAmount),
          dateTimeBinder.unbind(queryKeys.disposalDate, propertyChargeableGainModel.disposalDate)
        ).filter(!_.isEmpty).mkString("&")
    }
  }
}
