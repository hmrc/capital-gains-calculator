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

import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings}

lazy val appName = "capital-gains-calculator"
lazy val plugins : Seq[Plugins] = Seq(play.sbt.PlayScala)
lazy val playSettings : Seq[Setting[_]] = Seq.empty

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins : _*)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(CodeCoverageSettings.settings : _*)
  .settings(majorVersion := 2)
  .settings(playSettings : _*)
  .settings(PlayKeys.playDefaultPort := 9985)
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    scalaVersion := "2.13.12",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,

    retrieveManaged := true,
    update / evictionWarningOptions :=
      EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    scalacOptions += "-P:silencer:pathFilters=routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.7.14" cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % "1.7.14" % Provided cross CrossVersion.full
    ),
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings())
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / fork := false,
    IntegrationTest / unmanagedResourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it" / "resources")).value,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "it")).value,
    IntegrationTest / parallelExecution := false)
  .settings(
    resolvers += Resolver.jcenterRepo)
  .settings(routesImport += "models.nonResident._")
  .settings(routesImport += "common.binders._")
  .settings(routesImport += "common.binders.CommonBinders._")