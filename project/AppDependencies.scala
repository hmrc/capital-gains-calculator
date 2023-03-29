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


  private val jsoupVersion = "1.13.1"
  val bootstrapVersion = "7.15.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapVersion,
    "joda-time" % "joda-time" % "2.10.10",
    "com.typesafe.play" %% "play-json-joda" % "2.9.2",
    "org.mockito" %% "mockito-scala-scalatest" % "1.17.12"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc"                 %% "bootstrap-test-play-28"     % bootstrapVersion,
        "org.mockito" % "mockito-core" % "3.12.4" % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.jsoup" % "jsoup" % jsoupVersion % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply(): Seq[ModuleID] = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "uk.gov.hmrc"                 %% "bootstrap-test-play-28"     % bootstrapVersion,
        "org.mockito" % "mockito-core" % "5.2.0" % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.jsoup" % "jsoup" % jsoupVersion % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}

