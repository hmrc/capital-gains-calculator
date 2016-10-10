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

import java.time.format.DateTimeFormatter

import config.TaxRatesAndBands
import models.CalculationResultModel
import common.Math._
import common.Date._
import org.joda.time.{DateTime, Days}

object CalculationService extends CalculationService {

}

trait CalculationService {

  //scalastyle:off
  def calculateCapitalGainsTax (
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
    acquisitionDate: Option[DateTime] = None,
    disposalDate: DateTime,
    isClaimingPRR: Option[String] = None,
    daysClaimed: Option[Double] = None,
    daysClaimedAfter: Option[Double] = None,
    isProperty: Boolean
  ): CalculationResultModel = {

    val taxYear = getTaxYear(disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)

    val gain: Double = calculationType match {
      case "flat" => calculateGainFlat(disposalValue, disposalCosts, acquisitionValueAmt, acquisitionCostsAmt, improvementsAmt)
      case "rebased" => calculateGainRebased(disposalValue, disposalCosts, revaluedAmount, revaluationCost, improvementsAmt)
      case "time" => calculateGainTA(disposalValue, disposalCosts, acquisitionValueAmt, acquisitionCostsAmt, improvementsAmt,
        acquisitionDate, disposalDate)
    }

    val prrAmount: Double = isClaimingPRR match {
      case Some("Yes") => calculationType match {
        case "flat" => calculateFlatPRR(disposalDate, acquisitionDate.get,
                                        daysClaimed.getOrElse(0), gain)
        case "rebased" => calculateRebasedPRR(disposalDate, daysClaimedAfter.getOrElse(0), gain)
        case "time" => calculateTimeApportionmentPRR(disposalDate, daysClaimedAfter.getOrElse(0), gain)
      }
      case _ => 0
    }

    val calculatedAEA = calculateAEA(customerType, priorDisposal, annualExemptAmount, isVulnerable, disposalDate)
    val calculatedChargeableGain = calculateChargeableGain(gain, reliefs + prrAmount, allowableLossesAmt, calculatedAEA)
    val usedAEA = annualExemptAmountUsed(calculatedAEA, gain, calculatedChargeableGain, reliefs + prrAmount, allowableLossesAmt)
    val aeaRemaining = annualExemptAmountLeft(calculatedAEA, usedAEA)
    val taxableGain = negativeToZero(calculatedChargeableGain)
    val basicRateRemaining = customerType match {
      case "individual" => brRemaining(currentIncome.getOrElse(0), personalAllowanceAmt.getOrElse(0), otherPropertiesAmt.getOrElse(0), calcTaxYear)
      case _ => 0
    }

    calculationResult(customerType, gain, taxableGain, calculatedChargeableGain, basicRateRemaining, prrAmount, isClaimingPRR.getOrElse("No"), usedAEA, aeaRemaining, calcTaxYear, isProperty)
  }

  def calculationResult (customerType: String, gain: Double, taxableGain: Double, chargeableGain: Double,
                         basicRateRemaining: Double, prrAmount: Double, isClaimingPRR: String, usedAEA: Double,
                         aeaLeft: Double, taxYear: Int, isProperty: Boolean): CalculationResultModel = {
    val taxRates = TaxRatesAndBands.getRates(taxYear)
    val basicRate = if(isProperty) taxRates.basicRate else taxRates.shareBasicRate
    val higherRate = if(isProperty) taxRates.higherRate else taxRates.shareHigherRate
    val basicRatePercentage = if(isProperty) taxRates.basicRatePercentage else taxRates.shareBasicRatePercentage
    val higherRatePercentage = if(isProperty) taxRates.higherRatePercentage else taxRates.shareHigherRatePercentage
    customerType match {
      case "individual" => CalculationResultModel(
        taxOwed = round("result", min(basicRateRemaining, taxableGain) * basicRate + negativeToZero(taxableGain - basicRateRemaining) *
          higherRate),
        totalGain = gain,
        baseTaxGain = gain match {
          case x if x > 0 => min(basicRateRemaining, chargeableGain)
          case _ => 0
        },
        baseTaxRate = min(basicRateRemaining, chargeableGain) match {
          case x if x > 0 => basicRatePercentage
          case _ => 0
        },
        usedAnnualExemptAmount = usedAEA,
        aeaRemaining = aeaLeft,
        upperTaxGain = negativeToNone(round("result", taxableGain - basicRateRemaining)), //rounding to be removed when refactored into BigDecimals
        upperTaxRate = if (negativeToZero(taxableGain - basicRateRemaining) > 0) Some(higherRatePercentage) else None,
        simplePRR = if (isClaimingPRR == "Yes") Some(prrAmount) else None)
      case _ => CalculationResultModel(
        taxOwed = round("result", taxableGain * higherRate),
        totalGain = gain,
        baseTaxGain = 0,
        baseTaxRate = 0,
        usedAnnualExemptAmount = usedAEA,
        aeaRemaining = aeaLeft,
        upperTaxGain = Some(chargeableGain),
        upperTaxRate = Some(higherRatePercentage),
        simplePRR = if (isClaimingPRR == "Yes") Some(prrAmount) else None)
    }
  }

  def calculateGainFlat (
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValueAmt: Double,
    acquisitionCostsAmt: Double,
    improvementsAmt: Double
  ): Double = {

    round("down", disposalValue) -
      round("up", disposalCosts) -
      round("up", acquisitionValueAmt) -
      round("up", acquisitionCostsAmt) -
      round("up", improvementsAmt)
  }

  def calculateGainRebased (
    disposalValue: Double,
    disposalCosts: Double,
    revaluedAmount: Double,
    revaluationCost: Double,
    improvementsAmt: Double
  ): Double = calculateGainFlat(disposalValue, disposalCosts, revaluedAmount, revaluationCost, improvementsAmt)

  def calculateGainTA (
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValueAmt: Double,
    acquisitionCostsAmt: Double,
    improvementsAmt: Double,
    acquisitionDate: Option[DateTime],
    disposalDate: DateTime
  ): Double = {

    val taxYear = getTaxYear(disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)
    val flatGain = calculateGainFlat(disposalValue, disposalCosts, acquisitionValueAmt, acquisitionCostsAmt, improvementsAmt)
    val fractionOfOwnership = daysBetween(taxRatesAndBands.startOfTax, disposalDate.toString) / daysBetween(acquisitionDate.get, disposalDate)

    round("gain", flatGain * fractionOfOwnership)

  }

  def calculateAEA (customerType: String, priorDisposal: String, annualExemptAmount: Option[Double] = None,
                    isVulnerable: Option[String] = None, disposalDate: DateTime): Double = {

    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(disposalDate.getYear)
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)

    priorDisposal match {
      case "No" =>
        customerType match {
          case "individual" | "personalRep" => taxRatesAndBands.maxAnnualExemptAmount
          case "trustee" => if (isVulnerable.contains("Yes")) taxRatesAndBands.maxAnnualExemptAmount else taxRatesAndBands.notVulnerableMaxAnnualExemptAmount
        }
      case _ => annualExemptAmount.getOrElse(0)
    }
  }

  def calculateChargeableGain
  (
    gain: Double,
    reliefs: Double,
    allowableLossesAmt: Double,
    annualExemptAmount: Double,
    broughtForwardLosses: Double = 0
  ): Double =
    gain match {
      case a if a <= 0 => a //gain less than 0, no need to deduct reliefs, losses or aea
      case b => b - round("up", reliefs) match { //gain greater than 0 so deduct the reliefs
        case c if c <= 0 => 0 //Reliefs cannot turn gain into a loss, hence return 0
        case d => d - round("up", allowableLossesAmt) match { //Gain greater than 0 still so deduct allowable loses
          case e if e <= 0 => e - round("up", broughtForwardLosses) //Allowable losses turns gain into a loss so return the loss and finally, subtract any brought forward losses.
          case f => negativeToZero(f - round("up", annualExemptAmount)) - round("up", broughtForwardLosses) //deduct AEA, if amount less than 0 return 0 else return amount and finally, subtract any brought forward losses.
        }
      }
    }

  def brRemaining(currentIncome: Double, personalAllowanceAmt: Double, otherPropertiesAmt: Double, taxYear: Int): Double = {
    negativeToZero(TaxRatesAndBands.getRates(taxYear).basicRateBand - negativeToZero(round("down",currentIncome) - round("up",personalAllowanceAmt)) - round("down",otherPropertiesAmt))
  }

  def calculateFlatPRR
  (disposalDate: DateTime,
   acquisitionDate: DateTime,
   daysClaimed: Double,
   gain: Double): Double = {

    val taxYear = getTaxYear(disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)

    min(round("up", gain * ((daysClaimed + daysBetween(disposalDate.minusMonths(taxRatesAndBands.eighteenMonths), disposalDate)) /
      daysBetween(acquisitionDate, disposalDate))), gain)
  }

  def calculateRebasedPRR
  (disposalDate: DateTime,
   daysClaimedAfter: Double,
   gain: Double): Double = {

    val taxYear = getTaxYear(disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)

    min(round("up", gain * ((daysClaimedAfter + daysBetween(disposalDate.minusMonths(taxRatesAndBands.eighteenMonths), disposalDate)) /
      daysBetween(taxRatesAndBands.startOfTaxDateTime, disposalDate))), gain)

  }

  def calculateTimeApportionmentPRR
  (disposalDate: DateTime,
   daysClaimedAfter: Double,
   gain: Double): Double = {

    val taxYear = getTaxYear(disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)

    min(round("up", gain * ((daysClaimedAfter + daysBetween(disposalDate.minusMonths(taxRatesAndBands.eighteenMonths), disposalDate)) /
      daysBetween(taxRatesAndBands.startOfTaxDateTime, disposalDate))), gain)

  }

  def determinePRRUsed (gain: Double, prrValue: Option[Double]): Double = {
    prrValue match {
      case (Some(a)) if a < gain => round("up", a)
      case (Some(a)) => gain
      case _ => 0
    }
  }

  def annualExemptAmountUsed (available: Double, totalGain: Double, chargeableGain: Double, reliefs: Double, allowableLossesAmt: Double) = {
    chargeableGain match {
      case a if a < 0 => 0.toDouble
      case b if b > 0 => round("down", available)
      case _ => partialAEAUsed(totalGain, reliefs, allowableLossesAmt)
    }
  }

  def partialAEAUsed (totalGain: Double, reliefs: Double, allowableLossesAmt: Double) = {
    negativeToZero(round("down", totalGain - round("up", reliefs) - round("up", allowableLossesAmt)))
  }

  def annualExemptAmountLeft (available: Double, aeaUsed: Double)  = {
    round("up", available) - aeaUsed
  }

  def determineLossLeft(gain: Double, loss: Double) = {
    round("down", round("up", loss) match {
      case a if gain > a => 0.toDouble
      case b if gain < 0 => b
      case c => c - gain
    })
  }

  def determineLettingsReliefsUsed(gain: Double, prrUsed: Double, reliefs: Option[Double], calcTaxYear: Int): Double = {
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)
    val maxLettingRelief = taxRatesAndBands.maxLettingsRelief
    round("up", List(gain - prrUsed, prrUsed, reliefs.getOrElse(0.0), maxLettingRelief).min)
  }

  def calculateAmountUsed(total: Double, remaining: Double): Double = negativeToZero(round("up", total - remaining))
}
