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

package common.validation

import models.resident.shares.{CalculateTaxOwedModel, ChargeableGainModel, TotalGainModel}
import org.joda.time.DateTime
import uk.gov.hmrc.play.test.UnitSpec

class SharesValidationSpec extends UnitSpec {

  "Calling validateSharesTotalGain" should {

    "return a Right with all validation passing" in {
      val model = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val result = SharesValidation.validateSharesTotalGain(model)

      result shouldBe Right(model)
    }

    "return a Left with disposalValue validation failing" in {
      val model = TotalGainModel(-1000.0, 1500.0, 2000.0, 2500.0)
      val result = SharesValidation.validateSharesTotalGain(model)

      result shouldBe Left("disposalValue cannot be negative.")
    }

    "return a Left with disposalCosts validation failing" in {
      val model = TotalGainModel(1000.0, -1500.0, 2000.0, 2500.0)
      val result = SharesValidation.validateSharesTotalGain(model)

      result shouldBe Left("disposalCosts cannot be negative.")
    }

    "return a Left with acquisitionValue validation failing" in {
      val model = TotalGainModel(1000.0, 1500.0, -2000.0, 2500.0)
      val result = SharesValidation.validateSharesTotalGain(model)

      result shouldBe Left("acquisitionValue cannot be negative.")
    }

    "return a Left with acquisitionCosts validation failing" in {
      val model = TotalGainModel(1000.0, 1500.0, 2000.0, -2500.0)
      val result = SharesValidation.validateSharesTotalGain(model)
      result shouldBe Left("acquisitionCosts cannot be negative.")
    }

    "return a Left with multiple failed validations" in {
      val model = TotalGainModel(1000.0, 132.067, -50.045, 1000.0)
      val result = SharesValidation.validateSharesTotalGain(model)

      result shouldBe Left("disposalCosts has too many decimal places.")
    }
  }

  "Calling validateSharesChargeableGain" should {

    "return a Right with all validation passing with no optional values" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, None, None, 4000.0)
      val result = SharesValidation.validateSharesChargeableGain(chargeableGainModel)

      result shouldBe Right(chargeableGainModel)
    }

    "return a Right with all validation passing with optional values" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), 4000.0)
      val result = SharesValidation.validateSharesChargeableGain(chargeableGainModel)

      result shouldBe Right(chargeableGainModel)
    }

    "return a Left with totalGainModel validation failing" in {
      val totalGainModel = TotalGainModel(1000.0, -1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), 4000.0)
      val result = SharesValidation.validateSharesChargeableGain(chargeableGainModel)

      result shouldBe Left("disposalCosts cannot be negative.")
    }

    "return a Left with allowableLosses validation failing" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(-3000.0), Some(3500.0), 4000.0)
      val result = SharesValidation.validateSharesChargeableGain(chargeableGainModel)

      result shouldBe Left("allowableLosses cannot be negative.")
    }

    "return a Left with broughtForwardLosses validation failing" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.045), 4000.0)
      val result = SharesValidation.validateSharesChargeableGain(chargeableGainModel)

      result shouldBe Left("broughtForwardLosses has too many decimal places.")
    }

    "return a Left with annualExemptAmount validation failing" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), -4000.0)
      val result = SharesValidation.validateSharesChargeableGain(chargeableGainModel)

      result shouldBe Left("annualExemptAmount cannot be negative.")
    }

    "return a Left with multiple failing validation containing a single message" in {
      val totalGainModel = TotalGainModel(1000.0, -1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(-3000.0), Some(3500.045), 4000.0)
      val result = SharesValidation.validateSharesChargeableGain(chargeableGainModel)

      result shouldBe Left("disposalCosts cannot be negative.")
    }
  }

  "Calling validateSharesTaxOwed" should {

    "return a Right when all validation passes with no optional values" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, None, None, 4000.0)
      val calculateTaxOwedModel = CalculateTaxOwedModel(chargeableGainModel, None, 5000.0, 5500.0, DateTime.parse("2016-05-04"))
      val result = SharesValidation.validateSharesTaxOwed(calculateTaxOwedModel)

      result shouldBe Right(calculateTaxOwedModel)
    }

    "return a Right when all validation passes with optional values" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), 4000.0)
      val calculateTaxOwedModel = CalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.0, 5500.0, DateTime.parse("2016-05-04"))
      val result = SharesValidation.validateSharesTaxOwed(calculateTaxOwedModel)

      result shouldBe Right(calculateTaxOwedModel)
    }

    "return a Left when validation fails on chargeableGain" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(-3000.0), Some(3500.0), 4000.0)
      val calculateTaxOwedModel = CalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.0, 5500.0, DateTime.parse("2016-05-04"))
      val result = SharesValidation.validateSharesTaxOwed(calculateTaxOwedModel)

      result shouldBe Left("allowableLosses cannot be negative.")
    }

    "return a Left when validation fails on previousTaxableGain" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), 4000.0)
      val calculateTaxOwedModel = CalculateTaxOwedModel(chargeableGainModel, Some(-4500.0), 5000.0, 5500.0, DateTime.parse("2016-05-04"))
      val result = SharesValidation.validateSharesTaxOwed(calculateTaxOwedModel)

      result shouldBe Left("previousTaxableGain cannot be negative.")
    }

    "return a Left when validation fails on previousIncome" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), 4000.0)
      val calculateTaxOwedModel = CalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.045, 5500.0, DateTime.parse("2016-05-04"))
      val result = SharesValidation.validateSharesTaxOwed(calculateTaxOwedModel)

      result shouldBe Left("previousIncome has too many decimal places.")
    }

    "return a Left when validation fails on personalAllowance" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), 4000.0)
      val calculateTaxOwedModel = CalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.0, 5500.076, DateTime.parse("2016-05-04"))
      val result = SharesValidation.validateSharesTaxOwed(calculateTaxOwedModel)

      result shouldBe Left("personalAllowance has too many decimal places.")
    }

    "return a Left when validation fails on disposalDate" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.0), Some(3500.0), 4000.0)
      val calculateTaxOwedModel = CalculateTaxOwedModel(chargeableGainModel, Some(4500.0), 5000.0, 5500.0, DateTime.parse("2013-05-04"))
      val result = SharesValidation.validateSharesTaxOwed(calculateTaxOwedModel)

      result shouldBe Left("disposalDate cannot be before 2015-04-06")
    }

    "return a Left with a single message when multiple validation failurs occur" in {
      val totalGainModel = TotalGainModel(1000.0, 1500.0, 2000.0, 2500.0)
      val chargeableGainModel = ChargeableGainModel(totalGainModel, Some(3000.056), Some(3500.0), -4000.0)
      val calculateTaxOwedModel = CalculateTaxOwedModel(chargeableGainModel, Some(4500.777), -5000.0, 5500.0, DateTime.parse("2013-05-04"))
      val result = SharesValidation.validateSharesTaxOwed(calculateTaxOwedModel)

      result shouldBe Left("allowableLosses has too many decimal places.")
    }
  }
}
