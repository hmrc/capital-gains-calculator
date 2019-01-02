/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.libs.json.Json
import play.api.mvc.QueryStringBindable

case class OtherReliefsModel(flatReliefs: Double, rebasedReliefs: Double, timeApportionedReliefs: Double)

object OtherReliefsModel {
  implicit val formats = Json.format[OtherReliefsModel]

  implicit def otherReliefsBinder(implicit doubleBinder: QueryStringBindable[Double]): QueryStringBindable[OtherReliefsModel] = {
    new QueryStringBindable[OtherReliefsModel] {
      override def unbind(key: String, value: OtherReliefsModel): String = s"&otherReliefsFlat=${value.flatReliefs}" +
        s"&otherReliefsRebased=${value.rebasedReliefs}" +
        s"&otherReliefsTimeApportioned=${value.timeApportionedReliefs}"

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, OtherReliefsModel]] = {
        val flatReliefs = doubleBinder.bind("otherReliefsFlat", params) match {
          case Some(Right(value)) => value
          case _ => 0.0
        }
        val rebasedReliefs = doubleBinder.bind("otherReliefsRebased", params) match {
          case Some(Right(value)) => value
          case _ => 0.0
        }
        val timeApportionedReliefs = doubleBinder.bind("otherReliefsTimeApportioned", params) match {
          case Some(Right(value)) => value
          case _ => 0.0
        }

        Some(Right(OtherReliefsModel(flatReliefs, rebasedReliefs, timeApportionedReliefs)))
      }
    }
  }
}
