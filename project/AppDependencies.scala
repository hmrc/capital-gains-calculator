/*
 * Copyright 2024 HM Revenue & Customs
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

import play.core.PlayVersion
import play.sbt.PlayImport.*
import sbt.*

object AppDependencies {

  val jsoupVersion     = "1.15.4"
  val bootstrapVersion = "8.3.0"
  val scalaTestVersion = "7.0.0"
  private val playSuffix = "-play-30"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"       %% s"bootstrap-backend$playSuffix" % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% s"bootstrap-test$playSuffix" % bootstrapVersion,
    "org.mockito"            %% "mockito-scala-scalatest"    % "1.17.30",
    "org.scalatestplus.play" %% "scalatestplus-play"         % scalaTestVersion,
    "org.jsoup"               % "jsoup"                      % jsoupVersion,
    "org.mockito"            %% "mockito-scala-scalatest"    % "1.17.12",
    "org.scalatestplus"      %% "scalacheck-1-17"            % "3.2.16.0"
  ).map(_ % "test")

  val integrationTest: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% s"bootstrap-test$playSuffix" % bootstrapVersion,
    "org.scalatestplus.play" %% "scalatestplus-play"         % scalaTestVersion,

  ).map(_ % "it")

  def apply(): Seq[ModuleID] = compile ++ test ++ integrationTest
}
