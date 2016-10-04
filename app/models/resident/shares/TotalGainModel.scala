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

import play.api.mvc.QueryStringBindable
import common.Validation

case class TotalGainModel (disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double)

object TotalGainModel {

  implicit def totalGainBinder(implicit doubleBinder: QueryStringBindable[Double]): QueryStringBindable[TotalGainModel] =
    new QueryStringBindable[TotalGainModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, TotalGainModel]] = {
        for {
          disposalValueEither <- doubleBinder.bind("disposalValue", params)
          disposalCostsEither <- doubleBinder.bind("disposalCosts", params)
          acquisitionValueEither <- doubleBinder.bind("acquisitionValue", params)
          acquisitionCostsEither <- doubleBinder.bind("acquisitionCosts", params)
        } yield {
          val inputs = Seq(disposalValueEither, disposalCostsEither, acquisitionValueEither, acquisitionCostsEither)
          inputs match {
            case Seq(Right(disposalValue), Right(disposalCosts), Right(acquisitionValue), Right(acquisitionCosts)) =>
              Right(TotalGainModel(disposalValue, disposalCosts, acquisitionValue, acquisitionCosts))
            case fail => Left(Validation.getFirstErrorMessage(fail))
          }
        }
      }

      override def unbind(key: String, totalGainModel: TotalGainModel): String = {
        s"disposalValue=${doubleBinder.unbind("disposalValue", totalGainModel.disposalValue)}&" +
          s"disposalCosts=${doubleBinder.unbind("disposalCosts", totalGainModel.disposalCosts)}&" +
          s"acquisitionValue=${doubleBinder.unbind("acquisitionValue", totalGainModel.acquisitionValue)}&" +
          s"acquisitionCosts=${doubleBinder.unbind("acquisitionCosts", totalGainModel.acquisitionCosts)}"
      }
    }
}