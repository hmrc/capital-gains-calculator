package models.nonResident

import org.joda.time.DateTime

case class NonPropertyGainModel(disposalValue: Double,
                                disposalCosts: Double,
                                acquisitionValue: Double,
                                acquisitionCosts: Double,
                                improvements: Double,
                                rebasedValue: Option[Double],
                                rebasedCosts: Double,
                                disposalDate: Option[DateTime],
                                acquisitionDate: Option[DateTime],
                                improvementsAfterTaxStarted: Double)
