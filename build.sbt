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

import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

lazy val appName = "capital-gains-calculator"

lazy val ItTest = config("it") extend Test

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(CodeCoverageSettings.settings *)
  .settings(majorVersion := 2)
  .settings(PlayKeys.playDefaultPort := 9985)
  .settings(
    scalaVersion := "2.13.12",
    scalafmtOnCompile := true,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test()
  )
  .settings(
    scalacOptions += "-Wconf:src=routes/.*:s"
  )
  .configs(ItTest)
  .settings(inConfig(ItTest)(Defaults.testSettings) *)
  .settings(
    ItTest / unmanagedSourceDirectories := (ItTest / baseDirectory)(base => Seq(base / "it")).value
  )
  .settings(routesImport += "models.nonResident._")
  .settings(routesImport += "common.binders._")
  .settings(routesImport += "common.binders.CommonBinders._")
