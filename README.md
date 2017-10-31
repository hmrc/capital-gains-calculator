# Capital Gains Calculator protected microservice

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)[
![Build Status](https://travis-ci.org/hmrc/capital-gains-calculator.svg?branch=master)](https://travis-ci.org/hmrc/capital-gains-calculator)[
![Download](https://api.bintray.com/packages/hmrc/releases/capital-gains-calculator/images/download.svg) ](https://bintray.com/hmrc/releases/capital-gains-calculator/_latestVersion)


## Summary

This protected microservice provides RESTful endpoints to calculate the amount of Capital Gains Tax that is due for a Taxpayer based on a number of core input data items.

There is a frontend microservice [Capital-Gains-Calculator-Frontend](https://github.com/hmrc/capital-gains-calculator-frontend) that provides
the views and controllers which interact with this protected microservice.

## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs a [JRE] to run.

## Dependencies

* Audit - datastream

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")

## End points

These are the available end points for the service. The table below gives details on each one.

<table>
    <tr>
        <th>Path</th>
        <th>Supported Methods</th>
        <th>Description</th>
    </tr>
    <tr>
        <td><code>/non-resident/calculate-total-gain</code></td>
        <td>GET</td>
        <td>Returns a JSON object with a constructed total gains model. This requires the following parameters:
            disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double, improvements: Double, rebasedValue: Option[Double],
            rebasedCosts: Double, disposalDate: Option[org.joda.time.DateTime], acquisitionDate: Option[org.joda.time.DateTime], improvementsAfterTaxStarted: Double</td>
    </tr>
    <tr>
        <td><code>/non-resident/calculate-gain-after-prr</code></td>
        <td>GET</td>
        <td>Returns a JSON object which contains the results for the flat, rebased and time apportioned gains with prr applied. This requires the following parameters:
            disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double, improvements: Double, rebasedValue: Option[Double],
            rebasedCosts: Double, disposalDate: Option[org.joda.time.DateTime], acquisitionDate: Option[org.joda.time.DateTime], improvementsAfterTaxStarted: Double,
            claimingPRR: Boolean, daysClaimed: Double, daysClaimedAfter: Double</td>
    </tr>
    <tr>
        <td><code>/non-resident/calculate-tax-owed</code></td>
        <td>GET</td>
        <td>Returns a JSON object which contains the results for the flat, rebased and time apportioned tax owed values. This requires the following parameters:
            disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double, improvements: Double, rebasedValue: Option[Double],
            rebasedCosts: Double, disposalDate: org.joda.time.DateTime, acquisitionDate: Option[org.joda.time.DateTime], improvementsAfterTaxStarted: Double,
            claimingPRR: Boolean, daysClaimed: Double, daysClaimedAfter: Double, customerType: String, isVulnerable: Option[String], currentIncome: Option[Double],
            personalAllowanceAmt: Option[Double], allowableLoss: Option[Double], previousGain: Option[Double], annualExemptAmount: Double, broughtForwardLoss: Option[Double]</td>
    </tr>
    <tr>
        <td><code>/non-resident/calculate-total-costs</code></td>
        <td>GET</td>
        <td>Returns a JSON object which contains the total costs for a given total gain calculation. This requires the following parameters:
            disposalCosts: Double, acquisitionCosts: Double, improvements: Double</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/calculate-total-gain</code></td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the property total gain calculation. This requires a propertyTotalGainModel which is made of
            the following variables: disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double,
            improvements: Double</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/calculate-chargeable-gain</code></td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the property chargeable gain calculation. This requires a propertyChargeableGainModel which is
            made of the following variables: disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double,
            improvements: Double, prrValue: Option[Double], lettingReliefs: Option[Double], allowableLosses: Option[Double],
            broughtForwardLosses: Option[Double], annualExemptAmount: Double, disposalDate: DateTime</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/calculate-resident-capital-gains-tax</code></td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the property tax owed calculation. This requires a taxOwedModel which is made of the
            following variables:disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double,
            improvements: Double, prrValue: Option[Double], lettingReliefs: Option[Double], allowableLosses: Option[Double],
            broughtForwardLosses: Option[Double], annualExemptAmount: Double, previousTaxableGain: Option[Double],
            previousIncome: Double, personalAllowance: Double, disposalDate: String, otherReliefsFlat: Option[Double],
            otherReliefsRebased: Option[Double], otherReliefsTimeApportioned: Option[Double]</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/calculate-total-costs</code></td>
        <td>GET</td>
        <td>Returns a JSON object which contains the total costs for a given property total gain calculation. This requires a propertyTotalGainModel which is made of
            the following variables: disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double,
            improvements: Double</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/shares/calculate-total-gain</code></td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the shares total gain calculation. This requires a totalGainModel which is made of
            the following variables: disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/shares/calculate-chargeable-gain</code></td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the shares chargeable gain calculation. This requires a chargeableGainModel which is
            made of the following variables: disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double,
            allowableLosses: Option[Double], broughtForwardLosses: Option[Double], annualExemptAmount: Double</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/shares/calculate-resident-capital-gains-tax</code></tax>
        <td>GET</td>
        <td>Returns a JSON object with the results from the shares tax owed calculation. This requires a chargeableGainModel which is made of
            the following variables: disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double,
            allowableLosses: Option[Double], broughtForwardLosses: Option[Double], annualExemptAmount: Double, previousTaxableGain: Option[Double],
            previousIncome: Double, personalAllowance: Double, disposalDate: DateTime</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/shares/calculate-total-costs</code></td>
        <td>GET</td>
        <td>Returns a JSON object which contains the total costs for a given shares total gain calculation. This requires a totalGainModel which is made of
            the following variables: disposalValue: Double, disposalCosts: Double, acquisitionValue: Double, acquisitionCosts: Double</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/tax-rates-and-bands/max-full-aea</code></td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the get max Annual Exempt Amount. This method has one argument which is: year: Int. </td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/tax-rates-and-bands/max-partial-aea</code></td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the get max partial Annual Exempt Amount calculation. This method has one argument which is: 
            taxYear: Int</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/tax-rates-and-bands/max-pa</code></td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the get max personal allowance calculation. This method has two arguments which
            are: taxYear: Int, isEligibleBlindPersonsAllowance: Option[Boolean]</td>
    </tr>
    <tr>
        <td><code>/capital-gains-calculator/tax-year </td>
        <td>GET</td>
        <td>Returns a JSON object with the results from the get tax year method. This method has one argument which is: dateString: String</td>
    </tr>
</table>

## GET /capital-gains-calculator/non-resident/calculate-total-gain

Calculates the basic amount of gain for a non-resident capital gains user

### Example of usage

**Request Body**

```json
{
    "disposalValue":1000.0,
    "disposalCosts":55.0,
    "acquisitionValue":750.0,
    "acquisitionCosts":50.0,
    "improvements":2.0,
    "rebasedValue":150.0,
    "rebasedCosts":5.0,
    "disposalDate":"2017-01-02",
    "acquisitionDate":"2005-10-16",
    "improvementsAfterTaxStarted":4.0
}
```

**Response**

```json
{
    "flatGain":6.0,
    "rebasedGain":100.0,
    "timeApportionedGain":50.0
}
```

## GET /capital-gains-calculator/non-resident/calculate-gain-after-prr

Calculates the basic amount of gain for a non-resident capital gains user including prr

### Example of usage

**Request Body**

```json
{
    "disposalValue":1000.0,
    "disposalCosts":55.0,
    "acquisitionValue":750.0,
    "acquisitionCosts":50.0,
    "improvements":2.0,
    "rebasedValue":150.0,
    "rebasedCosts":5.0,
    "disposalDate":"2017-01-02",
    "acquisitionDate":"2005-10-16",
    "improvementsAfterTaxStarted":4.0,
    "claimingPRR":true,
    "daysClaimed":2847,
    "daysClaimedAfter":1
}
```

**Response**

```json
{
    "flatResult":{
        "totalGain":6.0,
        "taxableGain":1.0,
        "prrUsed":1.0
    },
    "rebasedResult":{
        "totalGain":100.0,
        "taxableGain":13.0,
        "prrUsed":87.0
    },
    "timeApportionedResult":{
        "totalGain":50.0,
        "taxableGain":6.0,
        "prrUsed":44.0
    }
}
```

## GET /capital-gains-calculator/non-resident/calculate-tax-owed

Calculates the tax owed for a non-resident capital gains user

### Example of usage

**Request Body**

```json
{
    "disposalValue":1000.0,
    "disposalCosts":55.0,
    "acquisitionValue":750.0,
    "acquisitionCosts":50.0,
    "improvements":2.0,
    "rebasedValue":150.0,
    "rebasedCosts":5.0,
    "disposalDate":"2017-01-02",
    "acquisitionDate":"2005-10-16",
    "improvementsAfterTaxStarted":4.0,
    "claimingPRR":true,
    "daysClaimed":2847,
    "daysClaimedAfter":1,
    "customerType":"individual",
    "currentIncome":25000,
    "personalAllowanceAmt":11000,
    "allowableLoss":50000,
    "previousGain":0,
    "annualExemptAmount":11000,
    "broughtForwardLoss":5000,
    "otherReliefsFlat":100,
    "otherReliefsRebased":100,
    "otherReliefsTimeApportioned":100
}
```

**Response**

```json
{
    "flatResult":{
        "taxOwed":600.0,
        "taxGain":1000.0,
        "taxRate":18,
        "upperTaxGain":500.0,
        "upperTaxRate":28,
        "totalGain":2500.0,
        "taxableGain":1500.0,
        "prrUsed":100.0,
        "otherReliefsUsed":100.0,
        "allowableLossesUsed":100.0,
        "aeaUsed":4000.0,
        "aeaRemaining":0.0,
        "broughtForwardLossesUsed":800.0
    },
    "rebasedResult":{
        "taxOwed":600.0,
        "taxGain":1000.0,
        "taxRate":18,
        "upperTaxGain":500.0,
        "upperTaxRate":28,
        "totalGain":2500.0,
        "taxableGain":1500.0,
        "prrUsed":100.0,
        "otherReliefsUsed":100.0,
        "allowableLossesUsed":100.0,
        "aeaUsed":4000.0,
        "aeaRemaining":0.0,
        "broughtForwardLossesUsed":800.0
    },
    "timeApportionedResult":{
        "taxOwed":600.0,
        "taxGain":1000.0,
        "taxRate":18,
        "upperTaxGain":500.0,
        "upperTaxRate":28,
        "totalGain":2500.0,
        "taxableGain":1500.0,
        "prrUsed":100.0,
        "otherReliefsUsed":100.0,
        "allowableLossesUsed":100.0,
        "aeaUsed":4000.0,
        "aeaRemaining":0.0,
        "broughtForwardLossesUsed":800.0
    }
}
```

## GET /capital-gains-calculator/non-resident/calculate-total-costs

Calculates the total amount of costs in the total gain calculation for a non-resident capital gains user

### Example of usage

**Request Body**

```json
{
    "disposalCosts":55.0,
    "acquisitionCosts":50.0,
    "improvements":2.0
}
```

**Response**


107


## GET /capital-gains-calculator/calculate-total-gain 

Calculates the basic amount of gain for a resident capital gains user

### Example of usage

**Request Body**

```json
{
    "disposalValue": 450000.0,
    "disposalCosts": 500.0,
    "acquisitionValue": 500000.0,
    "acquisitionCosts": 200.0,
    "improvements": 25000.0
}
```

**Response**

-75700.0

## GET /capital-gains-calculator/calculate-chargeable-gain

Calculates the basic amount of gain minus deductions for a resident capital gains user 

### Example of usage

**Request Body**

```json
{
    "disposalValue": 450000.0,
    "disposalCosts": 500.0,
    "acquisitionValue": 500000.0,
    "acquisitionCosts": 200.0,
    "improvements": 25000.0,
    "prrValue": 50000.0,
    "allowableLosses": 2000.0,
    "broughtForwardLosses": 2000.0,
    "annualExemptAmount": 0,
    "disposalDate": "2016-12-12"
}
```

**Response**

```json
{
    "gain":-75700.0,
    "chargeableGain":-75700.0,
    "aeaUsed":0.0,
    "aeaRemaining":0.0,
    "deductions":-147400.0,
    "allowableLossesRemaining":0.0,
    "broughtForwardLossesRemaining":0.0,
    "lettingReliefsUsed":-75700.0,
    "prrUsed":-75700.0,
    "broughtForwardLossesUsed":2000.0,
    "allowableLossesUsed":2000.0
}
```

## GET /capital-gains-calculator/calculate-resident-capital-gains-tax

Calculates the amount of tax owed and the tax bands for a resident capital gains user 

### Example of usage

**Request Body**

```json
{
    "disposalValue": 450000.0,
    "disposalCosts": 500.0,
    "acquisitionValue": 500000.0,
    "acquisitionCosts": 200.0,
    "improvements": 25000.0,
    "prrValue": 50000.0,
    "allowableLosses": 2000.0,
    "broughtForwardLosses": 2000.0,
    "annualExemptAmount": 0,
    "disposalDate": "2016-12-12",
    "previousIncome": 28000.0,
    "personalAllowance":11000.0
}
```

**Response**

```json
{
    "gain":-95700.0,
    "chargeableGain":-95700.0,
    "aeaUsed":0.0,
    "deductions":-191400.0,
    "taxOwed":-17226.0,
    "firstBand":0.0,
    "firstRate":0,
    "lettingReliefsUsed":-95700.0,
    "prrUsed":-95700.0,
    "broughtForwardLossesUsed":0.0,
    "allowableLossesUsed":0.0
}
```

## GET /capital-gains-calculator/calculate-total-costs 

Calculates the total amount of costs in the total gain calculation for a resident capital gains user

### Example of usage

**Request Body**

```json
{
    "disposalValue": 450000.0,
    "disposalCosts": 500.0,
    "acquisitionValue": 500000.0,
    "acquisitionCosts": 200.0,
    "improvements": 25000.0
}
```

**Response**

25700

## GET /capital-gains-calculator/shares/calculate-total-gain

Calculates the basic amount of gain for a shares capital gains user

### Example of usage

**Request Body**

```json
{
    "disposalValue": 450000.0,
    "disposalCosts": 500.0,
    "acquisitionValue": 500000.0,
    "acquisitionCosts": 200.0
}
```

**Response**

-50700.0

## GET /capital-gains-calculator/shares/calculate-chargeable-gain

Calculates the basic amount of gain minus deductions for a shares capital gains user 

### Example of usage

**Request Body**

```json
{
    "disposalValue": 450000.0,
    "disposalCosts": 500.0,
    "acquisitionValue": 500000.0,
    "acquisitionCosts": 200.0,
    "allowableLosses": 2000.0,
    "broughtForwardLosses": 2000.0,
    "annualExemptAmount": 0
}
```

**Response**

```json
{
    "gain":-50700.0,
    "chargeableGain":-50700.0,
    "aeaUsed":0.0,
    "aeaRemaining":0.0,
    "deductions":0.0,
    "allowableLossesRemaining":2000.0,
    "broughtForwardLossesRemaining":0.0,
    "broughtForwardLossesUsed":0.0,
    "allowableLossesUsed":0.0
}
```

## GET /capital-gains-calculator/shares/calculate-resident-capital-gains-tax

Calculates the amount of tax owed and the tax bands for a resident capital gains user 

### Example of usage

**Request Body**

```json
{
    "disposalValue": 450000.0,
    "disposalCosts": 500.0,
    "acquisitionValue": 500000.0,
    "acquisitionCosts": 200.0,
    "allowableLosses": 2000.0,
    "broughtForwardLosses": 2000.0,
    "annualExemptAmount": 0,
    "previousTaxableGain" : 0,
    "previousIncome": 28000.0,
    "personalAllowance":11000.0,
    "disposalDate":"2016-12-12"
}
```

**Response**

```json
{
    "gain":-50700.0,
    "chargeableGain":-50700.0,
    "aeaUsed":0.0,
    "deductions":2000.0,
    "taxOwed":-5070.0,
    "firstBand":0.0,
    "firstRate":0,
    "broughtForwardLossesUsed":0.0,
    "allowableLossesUsed":2000.0
}
```

## GET /capital-gains-calculator/shares/calculate-total-costs

Calculates the total amount of costs in the total gain calculation for a shares capital gains user

### Example of usage

**Request Body**

```json
{
    "disposalValue": 450000.0,
    "disposalCosts": 500.0,
    "acquisitionValue": 500000.0,
    "acquisitionCosts": 200.0
}
```

**Response**

700

## GET /capital-gains-calculator/tax-rates-and-bands/max-full-aea

Returns the max Annual Exempt Amount an individual can claim for a tax year

### Example of usage

**Request Body**

```json
{
    "taxYear":2016
}
```

**Response**

11100

## GET /capital-gains-calculator/tax-rates-and-bands/max-pa

Returns the maximum personal allowance an individual can claim for a tax year

**Request Body**

```json
{
    "taxYear":2016
}
```

**Response**

11100

## GET /capital-gains-calculator/tax-year

Returns the tax year for a given date

**Request Body**

```json
{
    "disposalDate":"2016-12-12"
}
```

**Response**

```json
{
    "taxYearSupplied":"2016/17",
    "isValidYear":true,
    "calculationTaxYear":"2016/17"
}
```

## GET /capital-gains-calculator/minimum-date

Returns the start date for the earliest valid tax year

**Response**

    1428278400000


