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

import java.time.LocalDate
import play.api.mvc.QueryStringBindable

import scala.util.{Success, Try}

trait CommonBinders {

  implicit def localDateBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[LocalDate] = {
    new QueryStringBindable[LocalDate] {

      override def unbind(key: String, value: LocalDate): String = s"$key=${value.getYear}-${value.getMonthValue}-${value.getDayOfMonth}"

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, LocalDate]] = {
        stringBinder.bind(key, params) match {
          case Some(Right(dateString)) => Try {
            LocalDate.parse(dateString)
          } match {
            case Success(date) => Some(Right(date))
            case _ => Some(Left(s"""Cannot parse parameter $key as LocalDate: For input string: "${params.get(key).get.head}""""))
          }
          case _ => None
        }
      }
    }
  }
}
