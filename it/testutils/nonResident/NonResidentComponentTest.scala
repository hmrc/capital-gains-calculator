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
                |"disposalValue":"500000",
                |"disposalCosts":"200000",
                |"acquisitionValue":"350000",
                |"acquisitionCosts":"200000",
                |"improvements":"9000",
                |"rebasedValue":"450000",
                |"rebasedCosts":"20000",
                |"disposalDate":"2017-05-12",
                |"acquisitionDate":"2014-08-14",
                |"improvementsAfterTaxStarted":"250000"
                |}
              """.
                stripMargin)
          )

        val responseJson = Json.parse(

          //TODO : This is the response you'd get on the frontend

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
        def request: WSResponse = ws.url(calculateUrl)
          .post(
            Json.parse(
              """
                |{
                |"disposalValue":"500000",
                |"disposalCosts":"200000",
                |"acquisitionValue":"350000",
                |"acquisitionCosts":"200000",
                |"improvements":"9000",
                |"rebasedValue":"450000",
                |"rebasedCosts":"20000",
                |"disposalDate":"2017-05-12",
                |"acquisitionDate":"2014-08-14",
                |"improvementsAfterTaxStarted":"250000"
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
        def request: WSResponse = ws.url(calculateUrl)
          .post(
            Json.parse(
              """
                |{
                |"disposalValue":"500000",
                |"disposalCosts":"200000",
                |"acquisitionValue":"350000",
                |"acquisitionCosts":"200000",
                |"improvements":"9000",
                |"rebasedValue":"450000",
                |"rebasedCosts":"20000",
                |"disposalDate":"2017-05-12",
                |"acquisitionDate":"2014-08-14",
                |"improvementsAfterTaxStarted":"250000"
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