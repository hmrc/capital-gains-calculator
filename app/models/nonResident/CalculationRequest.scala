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

package models.nonResident

import org.joda.time.DateTime

case class CalculationRequest(customerType: String,
                              priorDisposal: String,
                              annualExemptAmount: Option[Double],
                              otherPropertiesAmount: Option[Double],
                              isVulnerable: Option[String],
                              currentIncome: Option[Double],
                              personalAllowanceAmount: Option[Double],
                              disposalValue: Double,
                              disposalCosts: Double,
                              acquisitionValue: Double,
                              acquisitionCosts: Double,
                              improvementsAmount: Double,
                              reliefsAmount: Double,
                              allowableLosses: Double,
                              acquisitionDate: Option[DateTime])
//                              disposalDate: String,
//                              isClaimingPRR: Option[String],
//                              daysClaimed: Option[Double])
