/*
 * Copyright 2023 HM Revenue & Customs
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
import common.validation.{CommonValidation, PropertyValidation}
import models.resident.properties.{PropertyCalculateTaxOwedModel, PropertyChargeableGainModel, PropertyTotalGainModel}
import models.resident.shares.TotalGainModel
import play.api.mvc.QueryStringBindable

object ResidentPropertyBinders extends ResidentPropertyBinders

trait ResidentPropertyBinders extends CommonBinders {

  val totalGainParameters = Seq(queryKeys.disposalValue, queryKeys.disposalCosts, queryKeys.acquisitionValue, queryKeys.acquisitionCosts)
  val propertyTotalGainParameters = totalGainParameters ++ Seq(queryKeys.improvements)
  val chargeableGainParameters = propertyTotalGainParameters ++ Seq(queryKeys.annualExemptAmount, queryKeys.disposalDate)
  val calculateTaxOwedParameters = chargeableGainParameters ++ Seq(queryKeys.previousIncome, queryKeys.personalAllowance, queryKeys.disposalDate)

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
                PropertyValidation.validatePropertyTotalGain(PropertyTotalGainModel(totalGainModel, improvements))
              case fail => Left(CommonValidation.getFirstErrorMessage(Seq(totalGainModelEither, improvementsEither)))
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
            disposalDateEither <- localDateBinder.bind(queryKeys.disposalDate, params)
          } yield {

            val inputs = (propertyTotalGainModelEither, prrValueEither, lettingReliefsEither, allowableLossesEither,
              broughtForwardLossesEither, annualExemptAmountEither, disposalDateEither)
            inputs match {
              case (Right(propertyTotalGain), Right(prrValue), Right(lettingReliefs), Right(allowableLosses), Right(broughtForwardLosses),
                Right(annualExemptAmount), Right(disposalDate)) =>
                  PropertyValidation.validatePropertyChargeableGain(PropertyChargeableGainModel(propertyTotalGain, prrValue, lettingReliefs,
                    allowableLosses, broughtForwardLosses, annualExemptAmount, disposalDate))

              case _ => Left(CommonValidation.getFirstErrorMessage(Seq(propertyTotalGainModelEither, prrValueEither, lettingReliefsEither,
                allowableLossesEither, broughtForwardLossesEither, annualExemptAmountEither, disposalDateEither)))
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
          localDateBinder.unbind(queryKeys.disposalDate, propertyChargeableGainModel.disposalDate)
        ).filter(!_.isEmpty).mkString("&")
    }
  }

  implicit def propertyCalculateTaxOwedBinder(implicit doubleBinder: QueryStringBindable[Double], optionDoubleBinder: QueryStringBindable[Option[Double]],
                                              propertyChargeableGainBinder: QueryStringBindable[PropertyChargeableGainModel]):
  QueryStringBindable[PropertyCalculateTaxOwedModel] =
    new QueryStringBindable[PropertyCalculateTaxOwedModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PropertyCalculateTaxOwedModel]] = {

        val missingParameter = calculateTaxOwedParameters.find(element => params.get(element).isEmpty)

        if(missingParameter.isEmpty) {
          for {
            propertyChargeableGainModelEither <- propertyChargeableGainBinder.bind("", params)
            previousTaxableGainEither <- optionDoubleBinder.bind(queryKeys.previousTaxableGain, params)
            previousIncomeEither <- doubleBinder.bind(queryKeys.previousIncome, params)
            personalAllowanceEither <- doubleBinder.bind(queryKeys.personalAllowance, params)
          } yield {
            val inputs = (propertyChargeableGainModelEither, previousTaxableGainEither, previousIncomeEither, personalAllowanceEither)
            inputs match {
              case (Right(chargeableGain), Right(previousTaxableGain), Right(previousIncome), Right(personalAllowance)) =>
                PropertyValidation.validatePropertyTaxOwed(
                  PropertyCalculateTaxOwedModel(chargeableGain, previousTaxableGain, previousIncome, personalAllowance)
                )
              case _ =>
                val inputs = Seq(propertyChargeableGainModelEither, previousTaxableGainEither, previousIncomeEither, personalAllowanceEither)
                Left(CommonValidation.getFirstErrorMessage(inputs))
            }
          }
        }

        else Some(Left(s"${missingParameter.get} is required."))
      }

      override def unbind(key: String, propertyCalculateTaxOwedModel: PropertyCalculateTaxOwedModel): String =
        Seq(
          propertyChargeableGainBinder.unbind("", propertyCalculateTaxOwedModel.propertyChargeableGainModel),
          optionDoubleBinder.unbind(queryKeys.previousTaxableGain, propertyCalculateTaxOwedModel.previousTaxableGain),
          doubleBinder.unbind(queryKeys.previousIncome, propertyCalculateTaxOwedModel.previousIncome),
          doubleBinder.unbind(queryKeys.personalAllowance, propertyCalculateTaxOwedModel.personalAllowance)
        ).filter(!_.isEmpty).mkString("&")

    }
}
