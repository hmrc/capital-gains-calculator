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
import models.resident.properties.PropertyTotalGainModel
import models.resident.shares.TotalGainModel
import play.api.mvc.QueryStringBindable

trait ResidentPropertyBinders {

  val totalGainParameters = Seq(queryKeys.disposalValue, queryKeys.disposalCosts, queryKeys.acquisitionValue, queryKeys.acquisitionCosts)
  val propertyTotalGainParameters = totalGainParameters ++ Seq(queryKeys.improvements)

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
}
