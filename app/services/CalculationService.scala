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

import java.util.Date

import models.{DateModel, CalculationResultModel}
import org.joda.time.{DateTime, Days}
import scala.math.BigDecimal._

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

  def calculateCapitalGainsTax
  (
    calculationType: String,
    customerType: String,
    priorDisposal: String,
    annualExemptAmount: Option[Double],
    isVulnerable: Option[String],
    currentIncome: Double,
    personalAllowanceAmt: Double,
    disposalValue: Double,
    disposalCosts: Double,
    acquisitionValueAmt: Double,
    acquisitionCostsAmt: Double,
    improvementsAmt: Double,
    reliefs: Double,
    allowableLossesAmt: Double,
    entReliefClaimed: String
  ): CalculationResultModel = {

    val gain: Double = calculationType match {
      case "flat" => calculateGainFlat(disposalValue, disposalCosts, acquisitionValueAmt, acquisitionCostsAmt, improvementsAmt)
    }
    val calculatedAEA = calculateAEA(customerType, priorDisposal, annualExemptAmount, isVulnerable)
    val calculatedChargeableGain = calculateChargeableGain(gain, reliefs, allowableLossesAmt, calculatedAEA)
    val taxableGain = negativeToZero(calculatedChargeableGain)
    val basicRateRemaining = if (customerType == "individual") brRemaining(currentIncome, personalAllowanceAmt) else 0

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

    round("down", disposalValue) -
      round("up", disposalCosts) -
      round("up", acquisitionValueAmt) -
      round("up", acquisitionCostsAmt) -
      round("up", improvementsAmt)
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

    gain -
      round("up", reliefs) -
      round("up", allowableLossesAmt) -
      round("up", annualExemptAmount)
  }

  def brRemaining(currentIncome: Double, personalAllowanceAmt: Double): Double = {
    negativeToZero(basicRateBand - negativeToZero(currentIncome - personalAllowanceAmt))
  }

  def round(roundMethod: String, x: Double): Double = {
    roundMethod match {
      case "down" => BigDecimal.valueOf(x).setScale(0, RoundingMode.DOWN).toDouble
      case "up" => BigDecimal.valueOf(x).setScale(0, RoundingMode.UP).toDouble
      case "result" => BigDecimal.valueOf(x).setScale(2, RoundingMode.DOWN).toDouble
      case _ => x
    }
  }

  def min(x: Double, y: Double): Double = {
    if (x < y) x else y
  }

  def negativeToNone(x: Double): Option[Double] = {
    if (x < 0) None else Some(x)
  }

  def negativeToZero(x: Double): Double = {
    if (x < 0) 0 else x
  }

  def daysBetween(start: DateTime, end: DateTime): Int = {
    Days.daysBetween(start, end).getDays + 1
  }
}
