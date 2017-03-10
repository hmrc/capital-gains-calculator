/*
 * Copyright 2017 HM Revenue & Customs
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

import common.QueryStringKeys.{NonResidentCalculationKeys => keys}
import common.validation.{CommonValidation, NonResidentValidation}
import models.nonResident.TimeApportionmentCalculationRequestModel
import play.api.mvc.QueryStringBindable

trait NonResidentTimeApportionmentCalculationRequestBinder extends CommonBinders {

  val requiredParams = Seq(
    keys.customerType,
    keys.priorDisposal,
    keys.disposalValue,
    keys.disposalCosts,
    keys.initialValue,
    keys.initialCosts,
    keys.improvementsAmount,
    keys.reliefsAmount,
    keys.allowableLosses,
    keys.disposalDate,
    keys.acquisitionDate)

  implicit def requestBinder(implicit stringBinder: QueryStringBindable[String],
                             optionalStringBinder: QueryStringBindable[Option[String]],
                             optionalDoubleBinder: QueryStringBindable[Option[Double]],
                             doubleBinder: QueryStringBindable[Double]): QueryStringBindable[TimeApportionmentCalculationRequestModel] =
    new QueryStringBindable[TimeApportionmentCalculationRequestModel] {

      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, TimeApportionmentCalculationRequestModel]] = {

        val missingParam = requiredParams.find(p => params.get(p).isEmpty)

        if (missingParam.isDefined) Some(Left(s"${missingParam.get} is required."))
        else for {
          customerTypeParam <- stringBinder.bind(keys.customerType, params)
          priorDisposalParam <- stringBinder.bind(keys.priorDisposal, params)
          aeaParam <- optionalDoubleBinder.bind(keys.annualExemptAmount, params)
          otherPropertiesParam <- optionalDoubleBinder.bind(keys.otherPropertiesAmount, params)
          vulnerableParam <- optionalStringBinder.bind(keys.vulnerable, params)
          currentIncomeParam <- optionalDoubleBinder.bind(keys.currentIncome, params)
          personalAllowanceParam <- optionalDoubleBinder.bind(keys.personalAllowanceAmount, params)
          disposalValueParam <- doubleBinder.bind(keys.disposalValue, params)
          disposalCostsParam <- doubleBinder.bind(keys.disposalCosts, params)
          initialValueParam <- doubleBinder.bind(keys.initialValue, params)
          initialCostsParam <- doubleBinder.bind(keys.initialCosts, params)
          improvementsParam <- doubleBinder.bind(keys.improvementsAmount, params)
          reliefsParam <- doubleBinder.bind(keys.reliefsAmount, params)
          allowableLossesParam <- doubleBinder.bind(keys.allowableLosses, params)
          acquisitionDateParam <- dateTimeBinder.bind(keys.acquisitionDate, params)
          disposalDateParam <- dateTimeBinder.bind(keys.disposalDate, params)
          isClaimingPRRParam <- optionalStringBinder.bind(keys.isClaimingPRR, params)
          daysClaimedParam <- optionalDoubleBinder.bind(keys.daysClaimed, params)
        } yield {
          (customerTypeParam,
            priorDisposalParam,
            aeaParam,
            otherPropertiesParam,
            vulnerableParam,
            currentIncomeParam,
            personalAllowanceParam,
            disposalValueParam,
            disposalCostsParam,
            initialValueParam,
            initialCostsParam,
            improvementsParam,
            reliefsParam,
            allowableLossesParam,
            acquisitionDateParam,
            disposalDateParam,
            isClaimingPRRParam,
            daysClaimedParam) match {
            case (
              Right(customerType),
              Right(priorDisposal),
              Right(aea),
              Right(otherProperties),
              Right(vulnerable),
              Right(currentIncome),
              Right(personalAllowance),
              Right(disposalValue),
              Right(disposalCosts),
              Right(initialValue),
              Right(initialCosts),
              Right(improvements),
              Right(reliefs),
              Right(allowableLosses),
              Right(acquisitionDate),
              Right(disposalDate),
              Right(isClaimingPRR),
              Right(daysClaimed)) =>
              NonResidentValidation.validateNonResidentTimeApportioned(
                TimeApportionmentCalculationRequestModel(customerType,
                  priorDisposal,
                  aea,
                  otherProperties,
                  vulnerable,
                  currentIncome,
                  personalAllowance,
                  disposalValue,
                  disposalCosts,
                  initialValue,
                  initialCosts,
                  improvements,
                  reliefs,
                  allowableLosses,
                  acquisitionDate,
                  disposalDate,
                  isClaimingPRR,
                  daysClaimed
                )
              )
            case fail =>
              Left(CommonValidation.getFirstErrorMessage(Seq(customerTypeParam,
                priorDisposalParam,
                aeaParam,
                otherPropertiesParam,
                vulnerableParam,
                currentIncomeParam,
                personalAllowanceParam,
                disposalValueParam,
                disposalCostsParam,
                initialValueParam,
                initialCostsParam,
                improvementsParam,
                reliefsParam,
                allowableLossesParam,
                acquisitionDateParam,
                disposalDateParam,
                isClaimingPRRParam,
                daysClaimedParam)))
          }
        }
      }

      override def unbind(key: String, request: TimeApportionmentCalculationRequestModel): String =
        Seq(
          stringBinder.unbind(keys.customerType, request.customerType),
          stringBinder.unbind(keys.priorDisposal, request.priorDisposal),
          optionalDoubleBinder.unbind(keys.annualExemptAmount, request.annualExemptAmount),
          optionalDoubleBinder.unbind(keys.otherPropertiesAmount, request.otherPropertiesAmount),
          optionalStringBinder.unbind(keys.vulnerable, request.isVulnerable),
          optionalDoubleBinder.unbind(keys.currentIncome, request.currentIncome),
          optionalDoubleBinder.unbind(keys.personalAllowanceAmount, request.personalAllowanceAmount),
          doubleBinder.unbind(keys.disposalValue, request.disposalValue),
          doubleBinder.unbind(keys.disposalCosts, request.disposalCosts),
          doubleBinder.unbind(keys.initialValue, request.initialValue),
          doubleBinder.unbind(keys.initialCosts, request.initialCosts),
          doubleBinder.unbind(keys.improvementsAmount, request.improvementsAmount),
          doubleBinder.unbind(keys.reliefsAmount, request.reliefsAmount),
          doubleBinder.unbind(keys.allowableLosses, request.allowableLosses),
          dateTimeBinder.unbind(keys.acquisitionDate, request.acquisitionDate),
          dateTimeBinder.unbind(keys.disposalDate, request.disposalDate),
          optionalStringBinder.unbind(keys.isClaimingPRR, request.isClaimingPRR),
          optionalDoubleBinder.unbind(keys.daysClaimed, request.daysClaimed)
        ).filterNot(_.isEmpty).mkString("&")

    }
}
