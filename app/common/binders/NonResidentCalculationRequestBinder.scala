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

import models.nonResident.CalculationRequest
import play.api.mvc.QueryStringBindable
import common.Validation
import common.QueryStringKeys.{NonResidentCalculationKeys => keys}

trait NonResidentCalculationRequestBinder {

  val requiredParams = Seq(keys.customerType, keys.priorDisposal)

  implicit def requestBinder(implicit stringBinder: QueryStringBindable[String],
                             optionalStringBinder: QueryStringBindable[Option[String]],
                             optionalDoubleBinder: QueryStringBindable[Option[Double]]): QueryStringBindable[CalculationRequest] =
    new QueryStringBindable[CalculationRequest] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, CalculationRequest]] = {

        val missingParam = requiredParams.find(p => params.get(p).isEmpty)

        if (missingParam.isDefined) Some(Left(s"${missingParam.get} is required."))
        else for {
          customerTypeParam <- stringBinder.bind(keys.customerType, params)
          priorDisposalParam <- stringBinder.bind(keys.priorDisposal, params)
          aeaParam <- optionalDoubleBinder.bind(keys.annualExemptAmount, params)
          otherPropertiesParam <- optionalDoubleBinder.bind(keys.otherPropertiesAmount, params)
          vulnerableParam <- optionalStringBinder.bind(keys.vulnerable, params)
        } yield {
          (customerTypeParam,
            priorDisposalParam,
            aeaParam,
            otherPropertiesParam,
            vulnerableParam) match {
            case (
              Right(customerType),
              Right(priorDisposal),
              Right(aea),
              Right(otherProperties),
              Right(vulnerable)) => Right(CalculationRequest(customerType,
                                                              priorDisposal,
                                                              aea,
                                                              otherProperties,
                                                              vulnerable))
            case fail => Left(Validation.getFirstErrorMessage(Seq(customerTypeParam,
                                                                  priorDisposalParam,
                                                                  aeaParam,
                                                                  otherPropertiesParam,
                                                                  vulnerableParam)))
          }
        }
      }

      override def unbind(key: String, request: CalculationRequest): String =
        Seq(
          stringBinder.unbind(keys.customerType, request.customerType),
          stringBinder.unbind(keys.priorDisposal, request.priorDisposal),
          optionalDoubleBinder.unbind(keys.annualExemptAmount, request.annualExemptAmount),
          optionalDoubleBinder.unbind(keys.otherPropertiesAmount, request.otherPropertiesAmount),
          optionalStringBinder.unbind(keys.vulnerable, request.isVulnerable)
        ).filterNot(_.isEmpty).mkString("&")

    }
}
