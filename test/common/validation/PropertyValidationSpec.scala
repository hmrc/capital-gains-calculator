/*
 * Copyright 2022 HM Revenue & Customs
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

package common.validation

import models.resident.properties.{PropertyCalculateTaxOwedModel, PropertyChargeableGainModel, PropertyTotalGainModel}
import models.resident.shares.TotalGainModel
import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec

class PropertyValidationSpec extends PlaySpec {

  "Calling validatePropertyTotalGain" must {

    val validTotalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
    val inValidTotalGainModel = TotalGainModel(-1000.0, 1500.0, 2000.0, 2500.0)

    "return a Right with all validation passing" in {
      val model = PropertyTotalGainModel(validTotalGainModel, 1000.0)
      val result = PropertyValidation.validatePropertyTotalGain(model)

      result mustBe Right(model)
    }

    "return a Left with an invalid TotalGainModel" in {
      val model = PropertyTotalGainModel(inValidTotalGainModel, 1000.0)
      val result = PropertyValidation.validatePropertyTotalGain(model)

      result mustBe Left("disposalValue cannot be negative.")
    }

    "return a Left with improvements validation failing" in {
      val model = PropertyTotalGainModel(validTotalGainModel, -1000.0)
      val result = PropertyValidation.validatePropertyTotalGain(model)

      result mustBe Left("improvements cannot be negative.")
    }

    "return a Left with a the TotalGainsModel and improvements both failing validation" in {
      val model = PropertyTotalGainModel(inValidTotalGainModel, -1000.0)
      val result = PropertyValidation.validatePropertyTotalGain(model)

      result mustBe Left("disposalValue cannot be negative.")
    }
  }

  "Calling validatePropertyChargeableGain" must {

    val validPropertyTotalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), 3000.0)
    val invalidPropertyTotalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), -3000.0)

    "return a Right with all validation passing" in {
      val model = PropertyChargeableGainModel(validPropertyTotalGainModel, Some(3500.0), Some(4000.0), Some(4500.0),
        Some(5000.0), 5500.0, DateTime.parse("2016-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Right(model)
    }

    "return a Left with an invalid PropertyTotalGainModel" in {
      val model = PropertyChargeableGainModel(invalidPropertyTotalGainModel, Some(3500.0), Some(4000.0), Some(4500.0),
        Some(5000.0), 5500.0, DateTime.parse("2016-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Left("improvements cannot be negative.")
    }

    "return a Left with prrValue validation failing" in {
      val model = PropertyChargeableGainModel(validPropertyTotalGainModel, Some(-3500.0), Some(4000.0), Some(4500.0),
        Some(5000.0), 5500.0, DateTime.parse("2016-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Left("prrValue cannot be negative.")
    }

    "return a Left with lettingReliefs validation failing" in {
      val model = PropertyChargeableGainModel(validPropertyTotalGainModel, Some(3500.0), Some(-4000.0), Some(4500.0),
        Some(5000.0), 5500.0, DateTime.parse("2016-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Left("lettingReliefs cannot be negative.")
    }

    "return a Left with allowableLosses validation failing" in {
      val model = PropertyChargeableGainModel(validPropertyTotalGainModel, Some(3500.0), Some(4000.0), Some(-4500.0),
        Some(5000.0), 5500.0, DateTime.parse("2016-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Left("allowableLosses cannot be negative.")
    }

    "return a Left with broughtForwardLosses validation failing" in {
      val model = PropertyChargeableGainModel(validPropertyTotalGainModel, Some(3500.0), Some(4000.0), Some(4500.0),
        Some(-5000.0), 5500.0, DateTime.parse("2016-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Left("broughtForwardLosses cannot be negative.")
    }

    "return a Left with annualExemptAmount validation failing" in {
      val model = PropertyChargeableGainModel(validPropertyTotalGainModel, Some(3500.0), Some(4000.0), Some(4500.0),
        Some(5000.0), -5500.0, DateTime.parse("2016-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Left("annualExemptAmount cannot be negative.")
    }

    "return a Left with disposalDate validation failing" in {
      val model = PropertyChargeableGainModel(validPropertyTotalGainModel, Some(3500.0), Some(4000.0), Some(4500.0),
        Some(5000.0), 5500.0, DateTime.parse("2014-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Left("disposalDate cannot be before 2015-04-06")
    }

    "return a Left with multiple arguments failing validation" in {
      val model = PropertyChargeableGainModel(validPropertyTotalGainModel, Some(-3500.0), Some(4000.0), Some(-4500.0),
        Some(5000.0), 5500.0, DateTime.parse("2014-09-09"))
      val result = PropertyValidation.validatePropertyChargeableGain(model)

      result mustBe Left("prrValue cannot be negative.")
    }
  }

  "Calling validatePropertyTaxOwed" must {

    "return a Right when all validation passes with no optional values" in {
      val totalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), 3000.0)
      val chargeableGainModel = PropertyChargeableGainModel(totalGainModel, None, None, None, None, 4000.0, DateTime.parse("2016-05-04"))
      val calculateTaxOwedModel = PropertyCalculateTaxOwedModel(chargeableGainModel, None, 5000.0, 5500.0)
      val result = PropertyValidation.validatePropertyTaxOwed(calculateTaxOwedModel)

      result mustBe Right(calculateTaxOwedModel)
    }

    "return a Right when all validation passes with optional values" in {
      val totalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), 3000.0)
      val chargeableGainModel = PropertyChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), Some(4000.0), Some(4500.0), 5000.0,
        DateTime.parse("2016-05-04"))
      val calculateTaxOwedModel = PropertyCalculateTaxOwedModel(chargeableGainModel, Some(5500.0), 6000.0, 6500.0)
      val result = PropertyValidation.validatePropertyTaxOwed(calculateTaxOwedModel)

      result mustBe Right(calculateTaxOwedModel)
    }

    "return a Left when validation fails on chargeableGain" in {
      val totalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), 3000.0)
      val chargeableGainModel = PropertyChargeableGainModel(totalGainModel, Some(-3000.0), Some(3500.0), Some(4000.0), Some(4500.0), 5000.0,
        DateTime.parse("2016-05-04"))
      val calculateTaxOwedModel = PropertyCalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.0, 5500.0)
      val result = PropertyValidation.validatePropertyTaxOwed(calculateTaxOwedModel)

      result mustBe Left("prrValue cannot be negative.")
    }

    "return a Left when validation fails on previousTaxableGain" in {
      val totalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), 3000.0)
      val chargeableGainModel = PropertyChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), Some(4000.0), Some(4500.0), 5000.0,
        DateTime.parse("2016-05-04"))
      val calculateTaxOwedModel = PropertyCalculateTaxOwedModel(chargeableGainModel, Some(-4500.0), 5000.0, 5500.0)
      val result = PropertyValidation.validatePropertyTaxOwed(calculateTaxOwedModel)

      result mustBe Left("previousTaxableGain cannot be negative.")
    }

    "return a Left when validation fails on previousIncome" in {
      val totalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), 3000.0)
      val chargeableGainModel = PropertyChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), Some(4000.0), Some(4500.0), 5000.0,
        DateTime.parse("2016-05-04"))
      val calculateTaxOwedModel = PropertyCalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.045, 5500.0)
      val result = PropertyValidation.validatePropertyTaxOwed(calculateTaxOwedModel)

      result mustBe Left("previousIncome has too many decimal places.")
    }

    "return a Left when validation fails on personalAllowance" in {
      val totalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), 3000.0)
      val chargeableGainModel = PropertyChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), Some(4000.0), Some(4500.0), 5000.0,
        DateTime.parse("2016-05-04"))
      val calculateTaxOwedModel = PropertyCalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.0, 5500.076)
      val result = PropertyValidation.validatePropertyTaxOwed(calculateTaxOwedModel)

      result mustBe Left("personalAllowance has too many decimal places.")
    }

    "return a Left with a single message when multiple validation failures occur" in {
      val totalGainModel = PropertyTotalGainModel(TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0), 3000.0)
      val chargeableGainModel = PropertyChargeableGainModel(totalGainModel, Some(3000.123), Some(3500.0), Some(4000.0), Some(4500.0), 5000.0,
        DateTime.parse("2016-05-04"))
      val calculateTaxOwedModel = PropertyCalculateTaxOwedModel(chargeableGainModel, Some(4500.777), -5000.0, 5500.0)
      val result = PropertyValidation.validatePropertyTaxOwed(calculateTaxOwedModel)

      result mustBe Left("prrValue has too many decimal places.")
    }
  }
}
