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
import play.api.mvc.QueryStringBindable

case class ChargeableGainModel (totalGainModel: TotalGainModel,
                                allowableLosses: Option[Double],
                                broughtForwardLosses: Option[Double],
                                annualExemptAmount: Double)

object ChargeableGainModel {

  implicit def chargeableGainBinder(implicit doubleBinder: QueryStringBindable[Double],
                                    totalGainBinder: QueryStringBindable[TotalGainModel],
                                    optionDoubleBinder: QueryStringBindable[Option[Double]]): QueryStringBindable[ChargeableGainModel] =
    new QueryStringBindable[ChargeableGainModel] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, ChargeableGainModel]] = {
        for {
          totalGainModelEither <- totalGainBinder.bind("totalGain", params)
          annualExemptAmountEither <- doubleBinder.bind("annualExemptAmount", params)
          allowableLossesEither <- optionDoubleBinder.bind("allowableLosses", params)
          broughtForwardLossesEither <- optionDoubleBinder.bind("broughtForwardLosses", params)
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

      override def unbind(key: String, chargeableGainModel: ChargeableGainModel): String =
        s"${totalGainBinder.unbind("totalGain", chargeableGainModel.totalGainModel)}&" +
          s"allowableLosses=${optionDoubleBinder.unbind("allowableLosses", chargeableGainModel.allowableLosses)}&" +
          s"broughtForwardLosses=${optionDoubleBinder.unbind("broughtForwardLosses", chargeableGainModel.broughtForwardLosses)}&" +
          s"annualExemptAmount=${doubleBinder.unbind("annualExemptAmount", chargeableGainModel.annualExemptAmount)}"

    }
}