import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, integrationTestSettings, scalaSettings, targetJvm}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import uk.gov.hmrc.DefaultBuildSettings.targetJvm

lazy val appName = "capital-gains-calculator"
lazy val appDependencies : Seq[ModuleID] = ???
lazy val plugins : Seq[Plugins] = Seq(play.sbt.PlayScala)
lazy val playSettings : Seq[Setting[_]] = Seq.empty


lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;uk.gov.hmrc.BuildInfo;app.*;prod.*;config.*;com.*",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins : _*)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(scoverageSettings : _*)
  .settings(majorVersion := 2)
  .settings(playSettings : _*)
  .settings(PlayKeys.playDefaultPort := 9985)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    scalaVersion := "2.13.8",
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    update / evictionWarningOptions :=
      EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    scalacOptions += "-P:silencer:pathFilters=routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.7.12" cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % "1.7.12" % Provided cross CrossVersion.full
    ),
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings())
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    resolvers += Resolver.jcenterRepo)
  .settings(routesImport += "models.nonResident._")
  .settings(routesImport += "common.binders._")
  .settings(routesImport += "common.binders.CommonBinders._")