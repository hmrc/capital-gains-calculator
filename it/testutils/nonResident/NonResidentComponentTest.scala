package testutils.nonResident

import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.libs.ws.{WSClient, WSResponse}
import uk.gov.hmrc.play.test.UnitSpec

class NonResidentComponentTest extends UnitSpec with GuiceOneServerPerSuite {

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .build()

  val calculateUrl = s"http://localhost:$port/calculate-your-capital-gains/non-resident/"

  lazy val ws: WSClient = app.injector.instanceOf[WSClient]

  "Hitting the /non-resident/calculate-total-gain route" should {
    s"return a $OK with a valid result" when {
      "non-residential" in {
        def request: WSResponse = ws.url(calculateUrl)
          .post(
            Json.parse(
              """
                |{
                |"disposalValue":"",
                |"disposalCosts":"",
                |"acquisitionValue":"",
                |"acquisitionCosts":"",
                |"improvements":"",
                |"rebasedValue":"",
                |"rebasedCosts":"",
                |"disposalDate":"",
                |"acquisitionDate":"",
                |"improvementsAfterTaxStarted":"",
                |}
              """.
                stripMargin)
          )

        val responseJson = Json.parse(
          """
            |{
            |
            |}
          """.stripMargin)

        request.status shouldBe OK
        request.json shouldBe responseJson
      }
    }

    s"return a $BAD_REQUEST" when {
      "Data is missing" in {
        //bad request / data is missing / 400
        def request: WSResponse = ws.url(calculateUrl)
          .post(
            Json.parse(
              """
                |{
                |
                |}
              """.
                stripMargin)
          )

        val responseJson = Json.parse(
          """
            |{
            |
            |}
          """.stripMargin)

        request.status shouldBe 400
        request.json shouldBe responseJson
      }
    }

    "return a 500 status" when {
      "an unexpected error occurs" in {
        //unexpected / server side / 500
        def request: WSResponse = ws.url(calculateUrl)
          .post(
            Json.parse(
              """
                |{
                |
                |}
              """.
                stripMargin)
          )

        val responseJson = Json.parse(
          """
            |{
            |
            |}
          """.stripMargin)

        request.status shouldBe 500
        request.json shouldBe responseJson
      }
    }
  }
}