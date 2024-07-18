/*
 * Copyright 2024 HM Revenue & Customs
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

package models.nonResident

import common.validation.CommonValidation
import play.api.mvc.QueryStringBindable

case class PrivateResidenceReliefModel(prrClaimed: Option[Double])

object PrivateResidenceReliefModel {
  implicit def prrBinder(implicit optionDoubleBinder: QueryStringBindable[Option[Double]],
                         booleanBinder: QueryStringBindable[Boolean]): QueryStringBindable[PrivateResidenceReliefModel] = {
    new QueryStringBindable[PrivateResidenceReliefModel] {

      override def unbind(key: String, value: PrivateResidenceReliefModel): String =
        s"claimingPRR=${value.claimingPRR}&daysClaimed=${value.daysClaimed.getOrElse(0)}&daysClaimedAfter=${value.daysClaimedAfter.getOrElse(0)}"

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, PrivateResidenceReliefModel]] = {
        for {
          isClaiming <- booleanBinder.bind("claimingPRR", params)
          daysClaimed <- optionDoubleBinder.bind("daysClaimed", params)
          daysClaimedAfter <- optionDoubleBinder.bind("daysClaimedAfter", params)
        } yield {
          (isClaiming, daysClaimed, daysClaimedAfter) match {
            case (Right(a), Right(b), Right(c)) => Right(PrivateResidenceReliefModel(a, b, c))
            case _ => Left(CommonValidation.getFirstErrorMessage(Seq(isClaiming, daysClaimed, daysClaimedAfter)))
          }
        }
      }
    }
  }
}
