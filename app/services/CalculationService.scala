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

import models.{DateModel, CalculationResultModel}
import scala.math.BigDecimal._

object CalculationService extends CalculationService

trait CalculationService {

  val maxAnnualExemptAmount = 11100
  val notVulnerableMaxannualExemptAmount = 5550
  val entrepreneursPercentage = 10
  val basicRatePercentage = 18
  val higherRatePercentage = 28
  val entrepreneursRate = entrepreneursPercentage / 100.toDouble
  val basicRate = basicRatePercentage / 100.toDouble
  val higherRate = higherRatePercentage / 100.toDouble
  val basicRateBand = 32000

  def calculateCapitalGainsTax(customerType: String,
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
                               entReliefClaimed: String)
  : CalculationResultModel = {

    val calculatedGain = calculateGain(disposalValue, disposalCosts, acquisitionValueAmt, acquisitionCostsAmt, improvementsAmt)
    val calculatedAEA = calculateAEA(customerType, priorDisposal, annualExemptAmount, isVulnerable)
    val calculatedChargeableGain = calculateChargeableGain(calculatedGain, reliefs, allowableLossesAmt, calculatedAEA)
    val taxableGain = negativeToZero(calculatedChargeableGain)

    entReliefClaimed match {
      case "Yes" => CalculationResultModel(
        taxOwed = round("result", taxableGain * entrepreneursRate),
        totalGain = calculatedGain,
        baseTaxGain = calculatedChargeableGain,
        baseTaxRate = entrepreneursPercentage)
      case _ => customerType match {
          case "individual" => CalculationResultModel(
            taxOwed = round("result",min(brRemaining(currentIncome, personalAllowanceAmt), taxableGain) * basicRate +
                      negativeToZero(taxableGain - brRemaining(currentIncome, personalAllowanceAmt)) * higherRate),
            totalGain = calculatedGain,
            baseTaxGain = min(brRemaining(currentIncome, personalAllowanceAmt), calculatedChargeableGain),
            baseTaxRate = basicRatePercentage,
            upperTaxGain = negativeToNone(taxableGain - brRemaining(currentIncome, personalAllowanceAmt)),
            upperTaxRate = if (negativeToZero(taxableGain - brRemaining(currentIncome, personalAllowanceAmt)) > 0 ) Some(higherRatePercentage) else None)
          case _ => CalculationResultModel(
            taxOwed = round("result",taxableGain * higherRate),
            totalGain = calculatedGain,
            baseTaxGain = 0,
            baseTaxRate = basicRatePercentage,
            upperTaxGain = Some(calculatedChargeableGain),
            upperTaxRate = Some(higherRatePercentage))
        }
    }
  }

  def calculateAEA(customerType: String,
                   priorDisposal: String,
                   annualExemptAmount: Option[Double] = None,
                   isVulnerable: Option[String] = None)
  : Double = {
    priorDisposal match {
      case "No" =>
        customerType match {
          case "individual" | "personalRep" => maxAnnualExemptAmount
          case "trustee" if isVulnerable.contains("Yes") => maxAnnualExemptAmount
          case _ => notVulnerableMaxannualExemptAmount
        }
      case _ => annualExemptAmount.getOrElse(0)
    }
  }

  def calculateGain(disposalValue: Double, disposalCosts: Double, acquisitionValueAmt: Double, acquisitionCostsAmt: Double, improvementsAmt: Double): Double = {
    round("down", disposalValue) -
    round("up", disposalCosts) -
    round("up", acquisitionValueAmt) -
    round("up", acquisitionCostsAmt) -
    round("up", improvementsAmt)
  }

  def calculateChargeableGain(gain: Double, reliefs: Double, allowableLossesAmt: Double, annualExemptAmount: Double): Double = {
    gain -
    round("up", reliefs) -
    round("up", allowableLossesAmt) -
    round("up", annualExemptAmount)
  }

  def brRemaining(currentIncome: Double, personalAllowanceAmt: Double): Double = {
    currentIncome match {
      case a if a < personalAllowanceAmt => basicRateBand
      case _ => negativeToZero(basicRateBand - (currentIncome - personalAllowanceAmt))
    }
  }

  def round(roundMethod: String, x: Double): Double = {
    roundMethod match {
      case "down" => BigDecimal.valueOf(x).setScale(0, RoundingMode.DOWN).toDouble
      case "up" => BigDecimal.valueOf(x).setScale(0, RoundingMode.UP).toDouble
      case "result" => BigDecimal.valueOf(x).setScale(2, RoundingMode.DOWN).toDouble
      case _ => x
    }
  }

  def min(x: Double, y:Double): Double = {
    if (x < y) x else y
  }

  def negativeToNone(x: Double): Option[Double] = {
    if (x < 0) None else Some(x)
  }

  def negativeToZero(x: Double): Double = {
    if (x < 0) 0 else x
  }
}
