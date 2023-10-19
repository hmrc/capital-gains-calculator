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
import play.sbt.PlayImport._
import play.core.PlayVersion

object AppDependencies {

  val jsoupVersion     = "1.15.4"
  val bootstrapVersion = "7.19.0"
  val scalaTestVersion = "5.1.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28" % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion,
    "org.mockito"             % "mockito-core"           % "3.12.4",
    "org.scalatestplus.play" %% "scalatestplus-play"     % scalaTestVersion,
    "com.typesafe.play"      %% "play-test"              % PlayVersion.current,
    "org.jsoup"               % "jsoup"                  % jsoupVersion,
    "org.mockito"            %% "mockito-scala-scalatest"% "1.17.12"
  ).map(_ % "test")

  val integrationTest: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapVersion,
    "org.scalatestplus.play" %% "scalatestplus-play"     % scalaTestVersion,
    "com.typesafe.play"      %% "play-test"              % PlayVersion.current
  ).map(_ % "it")

  def apply(): Seq[ModuleID] = compile ++ test ++ integrationTest
}

