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
import models.resident.shares.TotalGainModel
import play.api.mvc.QueryStringBindable

trait ResidentSharesBinders {

  implicit def totalGainBinder(implicit doubleBinder: QueryStringBindable[Double]): QueryStringBindable[TotalGainModel] =
    new QueryStringBindable[TotalGainModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, TotalGainModel]] = {
        val parameters = Seq(queryKeys.disposalValue, queryKeys.disposalCosts, queryKeys.acquisitionValue, queryKeys.acquisitionCosts)

        val missingParameter = parameters.find(element => params.get(element).isEmpty)

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
}
