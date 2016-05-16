# Capital Gains Calculator protected microservice

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)[
![Build Status](https://travis-ci.org/hmrc/capital-gains-calculator.svg?branch=master)](https://travis-ci.org/hmrc/capital-gains-calculator)[
![Download](https://api.bintray.com/packages/hmrc/releases/capital-gains-calculator/images/download.svg) ](https://bintray.com/hmrc/releases/capital-gains-calculator/_latestVersion)
]

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
