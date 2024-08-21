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

import sbt.*

object AppDependencies {
  private val jsoupVersion     = "1.18.1"
  private val bootstrapVersion = "9.3.0"
  private val scalaTestVersion = "7.0.1"
  private val playSuffix = "-play-30"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-backend$playSuffix" % bootstrapVersion
  )

  def test(scope: String = "test, it"): Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% s"bootstrap-test$playSuffix" % bootstrapVersion % scope,
    "org.mockito"            %% "mockito-scala-scalatest"    % "1.17.37" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play"         % scalaTestVersion % scope,
    "org.jsoup"               % "jsoup"                      % jsoupVersion % scope,
    "org.scalatestplus"      %% "scalacheck-1-17"            % "3.2.18.0" % scope
  )
}
