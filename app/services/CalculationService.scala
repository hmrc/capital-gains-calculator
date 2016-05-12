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

package services

import models.CalculationResultModel
import common.Math._
import common.Date._
import org.joda.time.{DateTime, Days}

object CalculationService extends CalculationService

trait CalculationService {

  val maxAnnualExemptAmount = 11100
  val notVulnerableMaxAnnualExemptAmount = 5550
  val entrepreneursPercentage = 10
  val basicRatePercentage = 18
  val higherRatePercentage = 28
  val entrepreneursRate = entrepreneursPercentage / 100.toDouble
  val basicRate = basicRatePercentage / 100.toDouble
  val higherRate = higherRatePercentage / 100.toDouble
  val basicRateBand = 32000
  val startOfTax = "2015-04-06"
  val startOfTaxDateTime = DateTime.parse("2015-04-06")
  val disposalDateTimePRR = DateTime.parse("2016-10-06")
  val months = 18

  def calculateCapitalGainsTax
  (
    calculationType: String,
    customerType: String,
    priorDisposal: String,
    annualExemptAmount: Option[Double] = None,
    otherPropertiesAmt: Option[Double] = None,
    isVulnerable: Option[String] = None,
    currentIncome: Option[Double] = None,
    personalAllowanceAmt: Option[Double] = None,
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValueAmt: Double,
    acquisitionCostsAmt: Double,
    revaluedAmount: Double,
    revaluationCost: Double,
    improvementsAmt: Double,
    reliefs: Double,
    allowableLossesAmt: Double,
    entReliefClaimed: String,
    acquisitionDate: Option[String] = None,
    disposalDate: Option[String] = None,
    isClaimingPRR: Option[String] = None,
    daysClaimed: Option[Double] = None,
    daysClaimedAfter: Option[Double] = None
  ): CalculationResultModel = {

    val gain: Double = calculationType match {
      case "flat" => calculateGainFlat(disposalValue, disposalCosts, acquisitionValueAmt, acquisitionCostsAmt, improvementsAmt)
      case "rebased" => calculateGainRebased(disposalValue, disposalCosts, revaluedAmount, revaluationCost, improvementsAmt)
      case "time" => calculateGainTA(disposalValue, disposalCosts, acquisitionValueAmt, acquisitionCostsAmt, improvementsAmt,
                     acquisitionDate.getOrElse(""), disposalDate.getOrElse(""))
    }
    val calculatedAEA = calculateAEA(customerType, priorDisposal, annualExemptAmount, isVulnerable)

    val reliefsPRR: Double = isClaimingPRR.getOrElse("") match {
      case "Yes" => calculateFlatPRR(disposalDate, acquisitionDate, daysClaimed, gain) match {
        case a if a >= gain => gain
        case b if b < gain => b
        case _ => 0
      }
      case _ => 0
    }

    val calculatedChargeableGain = calculateChargeableGain(gain, reliefs + reliefsPRR, allowableLossesAmt, calculatedAEA)
    val taxableGain = negativeToZero(calculatedChargeableGain)
    val basicRateRemaining = if (customerType == "individual") brRemaining(currentIncome.getOrElse(0), personalAllowanceAmt.getOrElse(0),
        otherPropertiesAmt.getOrElse(0)) else 0

    calculationResult(entReliefClaimed, customerType, gain, taxableGain, calculatedChargeableGain, basicRateRemaining)

  }

  def calculationResult
  (
    entReliefClaimed: String,
    customerType: String,
    gain: Double,
    taxableGain: Double,
    chargeableGain: Double,
    basicRateRemaining: Double
  ): CalculationResultModel = {

    entReliefClaimed match {
      case "Yes" => CalculationResultModel(
        taxOwed = round("result", taxableGain * entrepreneursRate),
        totalGain = gain,
        baseTaxGain = chargeableGain,
        baseTaxRate = entrepreneursPercentage)
      case _ => customerType match {
        case "individual" => CalculationResultModel(
          taxOwed = round("result", min(basicRateRemaining, taxableGain) * basicRate + negativeToZero(taxableGain - basicRateRemaining) * higherRate),
          totalGain = gain,
          baseTaxGain = min(basicRateRemaining, chargeableGain),
          baseTaxRate = basicRatePercentage,
          upperTaxGain = negativeToNone(taxableGain - basicRateRemaining),
          upperTaxRate = if (negativeToZero(taxableGain - basicRateRemaining) > 0) Some(higherRatePercentage) else None)
        case _ => CalculationResultModel(
          taxOwed = round("result", taxableGain * higherRate),
          totalGain = gain,
          baseTaxGain = 0,
          baseTaxRate = basicRatePercentage,
          upperTaxGain = Some(chargeableGain),
          upperTaxRate = Some(higherRatePercentage))
      }
    }
  }

  def calculateGainFlat
  (
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValueAmt: Double,
    acquisitionCostsAmt: Double,
    improvementsAmt: Double
  ): Double = {

    round("result",round("down", disposalValue) -
      round("up", disposalCosts) -
      round("up", acquisitionValueAmt) -
      round("up", acquisitionCostsAmt) -
      round("up", improvementsAmt))
  }

  def calculateGainRebased
  (
    disposalValue: Double,
    disposalCosts: Double,
    revaluedAmount: Double,
    revaluationCost: Double,
    improvementsAmt: Double
  ): Double = calculateGainFlat(disposalValue, disposalCosts, revaluedAmount, revaluationCost, improvementsAmt)

  def calculateGainTA
  (
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValueAmt: Double,
    acquisitionCostsAmt: Double,
    improvementsAmt: Double,
    acquisitionDate: String,
    disposalDate: String
  ): Double = {

    val flatGain = calculateGainFlat(disposalValue, disposalCosts, acquisitionValueAmt,acquisitionCostsAmt,improvementsAmt)
    val fractionOfOwnership = daysBetween(startOfTax, disposalDate) / daysBetween(acquisitionDate, disposalDate)

    round("result",flatGain * fractionOfOwnership)

  }

  def calculateAEA
  (
    customerType: String,
    priorDisposal: String,
    annualExemptAmount: Option[Double] = None,
    isVulnerable: Option[String] = None
  ): Double = {

    priorDisposal match {
      case "No" =>
        customerType match {
          case "individual" | "personalRep" => maxAnnualExemptAmount
          case "trustee" => if (isVulnerable.contains("Yes")) maxAnnualExemptAmount else notVulnerableMaxAnnualExemptAmount
        }
      case _ => annualExemptAmount.getOrElse(0)
    }
  }

  def calculateChargeableGain
  (
    gain: Double,
    reliefs: Double,
    allowableLossesAmt: Double,
    annualExemptAmount: Double
  ): Double = {

    round("result",gain -
      round("up", reliefs) -
      round("up", allowableLossesAmt) -
      round("up", annualExemptAmount))
  }

  def brRemaining(currentIncome: Double, personalAllowanceAmt: Double, otherPropertiesAmt: Double): Double = {
    negativeToZero(basicRateBand - negativeToZero(currentIncome - personalAllowanceAmt) - otherPropertiesAmt)
  }

  def calculateFlatPRR
  (disposalDate: Option[String],
   acquisitionDate: Option[String],
   daysClaimed: Option[Double],
   gain: Double): Double = {

    (acquisitionDate, disposalDate) match {
      case (None, None) => 0
      case (Some(acquisitionDate), (Some(disposalDate))) =>
        val acqDateTime = DateTime.parse(acquisitionDate)
        val dispDateTime = DateTime.parse(disposalDate)
        dispDateTime match {
              case a if a.isBefore(disposalDateTimePRR) && (acqDateTime.isAfter(startOfTaxDateTime) ||
                acqDateTime.isEqual(startOfTaxDateTime)) =>
                round("up", gain * (daysBetween(dispDateTime.minusMonths(months), dispDateTime) /
                  daysBetween(acqDateTime, dispDateTime)))
              case _ =>
                round("up", gain * ((daysClaimed.get + daysBetween(dispDateTime.minusMonths(months), dispDateTime)) /
                  daysBetween(acqDateTime, dispDateTime)))
        }
      case _ => 0
    }
  }
}
