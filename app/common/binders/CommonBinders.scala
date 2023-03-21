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

import org.joda.time.DateTime
import play.api.mvc.QueryStringBindable

import scala.util.{Success, Try}

object CommonBinders extends CommonBinders

trait CommonBinders {

  implicit def dateTimeBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[DateTime] = {
    new QueryStringBindable[DateTime] {

      override def unbind(key: String, value: DateTime): String = s"$key=${value.getYear}-${value.getMonthOfYear}-${value.getDayOfMonth}"

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, DateTime]] = {
        stringBinder.bind(key, params) match {
          case Some(Right(dateString)) => Try {
            DateTime.parse(dateString)
          } match {
            case Success(date) => Some(Right(date))
            case _ => Some(Left(s"""Cannot parse parameter $key as DateTime: For input string: "${params.get(key).get.head}""""))
          }
          case _ => None
        }
      }
    }
  }

  implicit def optionalDateTimeBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[Option[DateTime]] = {
    new QueryStringBindable[Option[DateTime]] {

      override def unbind(key: String, value: Option[DateTime]): String = {

        if (value.isDefined) {
          s"$key=${value.get.getYear}-${value.get.getMonthOfYear}-${value.get.getDayOfMonth}"
        } else {
          ""
        }
      }

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Option[DateTime]]] = {

        if (params.get(key).isDefined) {
          stringBinder.bind(key, params) match {
            case Some(Right(dateString)) => Try {
              DateTime.parse(dateString)
            } match {
              case Success(date) => Some(Right(Some(date)))
              case _ => Some(Left(s"""Cannot parse parameter $key as DateTime: For input string: "${params.get(key).get.head}""""))
            }
            case _ => None
          }
        } else {
          Some(Right(None))
        }
      }
    }
  }
}
