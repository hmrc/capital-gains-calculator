/*
 * Copyright 2021 HM Revenue & Customs
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

import common.Date._
import common.Math._
import config.{PrivateResidenceReliefDateUtils, TaxRatesAndBands}
import models.CalculationResultModel
import org.joda.time.DateTime

class CalculationService {

  def calculateCapitalGainsTax(
                                calculationType: String,
                                priorDisposal: String,
                                annualExemptAmount: Option[Double] = None,
                                otherPropertiesAmt: Option[Double] = None,
                                currentIncome: Double,
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

    val calculatedAEA = calculateAEA(priorDisposal, annualExemptAmount, disposalDate)
    val calculatedChargeableGain = calculateChargeableGain(gain, reliefs + prrAmount, allowableLossesAmt, calculatedAEA)
    val usedAEA = annualExemptAmountUsed(calculatedAEA, gain, reliefs + prrAmount, allowableLossesAmt)
    val aeaRemaining = annualExemptAmountLeft(calculatedAEA, usedAEA)
    val taxableGain = negativeToZero(calculatedChargeableGain)
    val basicRateRemaining = brRemaining(currentIncome, personalAllowanceAmt.getOrElse(0), otherPropertiesAmt.getOrElse(0), calcTaxYear)

    calculationResult(gain, taxableGain, calculatedChargeableGain, basicRateRemaining, prrAmount, isClaimingPRR.getOrElse("No"), usedAEA, aeaRemaining, calcTaxYear, isProperty)
  }

  def calculationResult(gain: Double, taxableGain: Double, chargeableGain: Double,
                        basicRateRemaining: Double, prrAmount: Double, isClaimingPRR: String, usedAEA: Double,
                        aeaLeft: Double, taxYear: Int, isProperty: Boolean): CalculationResultModel = {
    val taxRates = TaxRatesAndBands.getRates(taxYear)
    val basicRate = if (isProperty) taxRates.basicRate else taxRates.shareBasicRate
    val higherRate = if (isProperty) taxRates.higherRate else taxRates.shareHigherRate
    val basicRatePercentage = if (isProperty) taxRates.basicRatePercentage else taxRates.shareBasicRatePercentage
    val higherRatePercentage = if (isProperty) taxRates.higherRatePercentage else taxRates.shareHigherRatePercentage

    val basicRateOwed = round("result", min(basicRateRemaining, taxableGain) * basicRate)
    val upperRateOwed = round("result", negativeToZero(taxableGain - basicRateRemaining) * higherRate)

    CalculationResultModel(
      taxOwed = round("result", min(basicRateRemaining, taxableGain) * basicRate + negativeToZero(taxableGain - basicRateRemaining) *
        higherRate),
      totalGain = gain,
      baseTaxGain = {
        if(gain <= 0) 0
        else min(basicRateRemaining, chargeableGain)
      },
      baseTaxRate = {
        if(min(basicRateRemaining, chargeableGain) <= 0) 0
        else basicRatePercentage
      },
      usedAnnualExemptAmount = usedAEA,
      aeaRemaining = aeaLeft,
      upperTaxGain = negativeToNone(round("result", taxableGain - basicRateRemaining)), //rounding to be removed when refactored into BigDecimals
      upperTaxRate = if (negativeToZero(taxableGain - basicRateRemaining) > 0) Some(higherRatePercentage) else None,
      simplePRR = if (isClaimingPRR == "Yes") Some(prrAmount) else None,
      baseRateTotal = basicRateOwed,
      upperRateTotal = upperRateOwed)
  }

  def calculateGainFlat(
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

  def calculateGainRebased(
                            disposalValue: Double,
                            disposalCosts: Double,
                            revaluedAmount: Double,
                            revaluationCost: Double,
                            improvementsAmt: Double
                          ): Double = calculateGainFlat(disposalValue, disposalCosts, revaluedAmount, revaluationCost, improvementsAmt)

  def calculateGainTA(
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

  def calculateAEA(priorDisposal: String, annualExemptAmount: Option[Double] = None, disposalDate: DateTime): Double = {

    priorDisposal match {
      case "No" => TaxRatesAndBands.getRates(TaxRatesAndBands.getClosestTaxYear(disposalDate.getYear)).maxAnnualExemptAmount
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
  ): Double = {
    if(gain <= 0) gain
    else gain - round("up", reliefs) match {
      //gain greater than 0 so deduct the reliefs
      case gainMinusReliefs if gainMinusReliefs <= 0 => 0 //Reliefs cannot turn gain into a loss, hence return 0
      case gainMinusReliefs => gainMinusReliefs - round("up", allowableLossesAmt) match {
        //Gain greater than 0 still so deduct allowable loses
        case gainMinusLosses if gainMinusLosses <= 0 => gainMinusLosses - round("up", broughtForwardLosses) //Allowable losses turns gain into a loss so return the loss and finally, subtract any brought forward losses.
        case gainMinusLosses => negativeToZero(gainMinusLosses - round("up", annualExemptAmount)) - round("up", broughtForwardLosses) //deduct AEA, if amount less than 0 return 0 else return amount and finally, subtract any brought forward losses.
      }
    }
  }

  def brRemaining(currentIncome: Double, personalAllowanceAmt: Double, otherPropertiesAmt: Double, taxYear: Int): Double = {
    negativeToZero(TaxRatesAndBands.getRates(taxYear).basicRateBand - negativeToZero(round("down", currentIncome) - round("up", personalAllowanceAmt)) - round("down", otherPropertiesAmt))
  }

  def calculateFlatPRR
  (disposalDate: DateTime,
   acquisitionDate: DateTime,
   daysClaimed: Double,
   gain: Double): Double = {

    //val taxYear = getTaxYear(disposalDate)
    //val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
    //val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)

    val pRRDateDetails = PrivateResidenceReliefDateUtils(disposalDate).pRRMonthDeductionApplicable()

    min(round("up", gain * ((daysClaimed + daysBetween(disposalDate.minusMonths(pRRDateDetails.months), disposalDate)) /
      daysBetween(acquisitionDate, disposalDate))), gain)
  }

  def calculateRebasedPRR
  (disposalDate: DateTime,
   daysClaimedAfter: Double,
   gain: Double): Double = {

    val taxYear = getTaxYear(disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)

    val pRRDateDetails = PrivateResidenceReliefDateUtils(disposalDate).pRRMonthDeductionApplicable()

    min(round("up", gain * ((daysClaimedAfter + daysBetween(disposalDate.minusMonths(pRRDateDetails.months), disposalDate)) /
      daysBetween(taxRatesAndBands.startOfTaxDateTime, disposalDate))), gain)

  }

  def calculateTimeApportionmentPRR
  (disposalDate: DateTime,
   daysClaimedAfter: Double,
   gain: Double): Double = {

    val taxYear = getTaxYear(disposalDate)
    val calcTaxYear = TaxRatesAndBands.getClosestTaxYear(taxYear)
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)

    val pRRDateDetails = PrivateResidenceReliefDateUtils(disposalDate).pRRMonthDeductionApplicable()

    min(round("up", gain * ((daysClaimedAfter + daysBetween(disposalDate.minusMonths(pRRDateDetails.months), disposalDate)) /
      daysBetween(taxRatesAndBands.startOfTaxDateTime, disposalDate))), gain)

  }

  def determineReliefsUsed(gain: Double, prrValueOpt: Option[Double]): Double = {
    prrValueOpt.map {
      prrValue => if (prrValue < gain) round("up", prrValue) else gain
    }.getOrElse(0)
  }

  def annualExemptAmountUsed(available: Double, totalGain: Double, reliefs: Double, allowableLossesAmt: Double): Double = {
    totalGain - reliefs - allowableLossesAmt match {
      case finalGain if finalGain <= 0 => 0.toDouble
      case finalGain if finalGain >= available => round("down", available)
      case _ => partialAEAUsed(totalGain, reliefs, allowableLossesAmt)
    }
  }

  def partialAEAUsed(totalGain: Double, reliefs: Double, allowableLossesAmt: Double): Double = {
    negativeToZero(round("down", totalGain - round("up", reliefs) - round("up", allowableLossesAmt)))
  }

  def annualExemptAmountLeft(available: Double, aeaUsed: Double): Double = {
    round("up", available) - aeaUsed
  }

  def determineLossLeft(gain: Double, loss: Double): Double = {
    round("down", round("up", loss) match {
      case roundedLoss if gain > roundedLoss => 0.toDouble
      case roundedLoss if gain < 0 => roundedLoss
      case roundedLoss => roundedLoss - gain
    })
  }

  def determineLettingsReliefsUsed(gain: Double, prrUsed: Double, reliefs: Option[Double], calcTaxYear: Int): Double = {
    val taxRatesAndBands = TaxRatesAndBands.getRates(calcTaxYear)
    val maxLettingRelief = taxRatesAndBands.maxLettingsRelief
    round("up", List(gain - prrUsed, prrUsed, reliefs.getOrElse(0.0), maxLettingRelief).min)
  }

  def calculateAmountUsed(total: Double, remaining: Double): Double = negativeToZero(round("up", total - remaining))

  def calculateTotalCosts(disposalCosts: Double, acquisitionCosts: Double, improvements: Double = 0): Double = {
    round("up", disposalCosts) +
      round("up", acquisitionCosts) +
      round("up", improvements)
  }
}
