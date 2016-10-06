/**
  * Copyright 2016 HM Revenue & Customs
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package common.validation

import common.QueryStringKeys.{ResidentPropertiesCalculationKeys => residentPropertyKeys}
import common.validation.CommonValidation._
import common.validation.SharesValidation.validateSharesTotalGain
import models.resident.properties.PropertyTotalGainModel

object PropertyValidation {

  def validatePropertyTotalGain(propertyTotalGainModel: PropertyTotalGainModel): Either[String, PropertyTotalGainModel] = {
    val totalGainModel = validateSharesTotalGain(propertyTotalGainModel.totalGainModel)
    val improvements = validateDouble(propertyTotalGainModel.improvements, residentPropertyKeys.improvements)

    (totalGainModel, improvements) match {
      case (Right(_), Right(_)) => Right(propertyTotalGainModel)
      case _ => Left(getFirstErrorMessage(Seq(totalGainModel, improvements)))
    }
  }
}
