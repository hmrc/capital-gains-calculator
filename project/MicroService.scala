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

import sbt._
import sbt.Keys._
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import play.sbt.routes.RoutesKeys._
import uk.gov.hmrc.{DefaultBuildSettings, SbtAutoBuildPlugin}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.versioning.SbtGitVersioning
import play.sbt.routes.RoutesKeys.routesGenerator
import play.routes.compiler.StaticRoutesGenerator
import com.timushev.sbt.updates.UpdatesKeys._
import com.timushev.sbt.updates.UpdatesPlugin.autoImport.moduleFilterRemoveValue

trait MicroService {

  import DefaultBuildSettings._
  import com.typesafe.sbt.web.Import.pipelineStages
  import com.typesafe.sbt.web.Import.Assets

  val appName: String

  lazy val appDependencies : Seq[ModuleID] = ???
  lazy val plugins : Seq[Plugins] = Seq(play.sbt.PlayScala)
  lazy val playSettings : Seq[Setting[_]] = Seq.empty

  lazy val scoverageSettings = {
    import scoverage.ScoverageKeys
    Seq(
      // Semicolon-separated list of regexs matching classes to exclude
      ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;uk.gov.hmrc.BuildInfo;app.*;prod.*;config.*;com.*",
      ScoverageKeys.coverageMinimum := 90,
      ScoverageKeys.coverageFailOnMinimum := false,
      ScoverageKeys.coverageHighlighting := true
    )
  }

  lazy val microservice = Project(appName, file("."))
    .enablePlugins(Seq(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory) ++ plugins : _*)
    .settings(scoverageSettings : _*)
    .settings(majorVersion := 2)
    .settings(playSettings : _*)
    .settings(scalaSettings: _*)
    .settings(publishingSettings: _*)
    .settings(defaultSettings(): _*)
    .settings(
      scalaVersion := "2.11.12",
      libraryDependencies ++= appDependencies,
      retrieveManaged := true,
      evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)
    )
    .configs(IntegrationTest)
    .settings(integrationTestSettings())
    .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
    .settings(
      resolvers += Resolver.bintrayRepo("hmrc", "releases"),
      resolvers += Resolver.jcenterRepo)
    .settings(routesImport += "models.nonResident._")
    .settings(routesImport += "common.binders._")
    .settings(routesImport += "common.binders.CommonBinders._")
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "org.scala-lang"))
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "com.typesafe.play"))
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "org.scalatest"))
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "org.scalatestplus.play"))
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "org.scalameta"))
    .settings(dependencyUpdatesFilter -= moduleFilter(organization = "org.scoverage"))
    .settings(dependencyUpdatesFailBuild := true)
}