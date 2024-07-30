package computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class MultiUsersSimulationHAR extends Simulation {

  private val httpProtocol = http
    .baseUrl("https://computer-database.gatling.io")
    .inferHtmlResources(AllowList(), DenyList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*\.svg""", """.*detectportal\.firefox\.com.*"""))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7")
    .doNotTrackHeader("1")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36")
  
  private val headers_0 = Map(
  		"cache-control" -> "max-age=0",
  		"origin" -> "https://computer-database.gatling.io",
  		"priority" -> "u=0, i",
  		"sec-ch-ua" -> """Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows",
  		"sec-fetch-dest" -> "document",
  		"sec-fetch-mode" -> "navigate",
  		"sec-fetch-site" -> "same-origin",
  		"sec-fetch-user" -> "?1"
  )
  
  private val headers_1 = Map(
  		"priority" -> "u=0, i",
  		"sec-ch-ua" -> """Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows",
  		"sec-fetch-dest" -> "document",
  		"sec-fetch-mode" -> "navigate",
  		"sec-fetch-site" -> "none",
  		"sec-fetch-user" -> "?1"
  )
  
  private val headers_2 = Map(
  		"priority" -> "u=0, i",
  		"sec-ch-ua" -> """Not/A)Brand";v="8", "Chromium";v="126", "Google Chrome";v="126""",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-ch-ua-platform" -> "Windows",
  		"sec-fetch-dest" -> "document",
  		"sec-fetch-mode" -> "navigate",
  		"sec-fetch-site" -> "same-origin",
  		"sec-fetch-user" -> "?1"
  )


  private val scn = scenario("MultiUsersSimulationHAR")
    .exec(
      http("request_0")
        .post("/computers/310/delete")
        .headers(headers_0),
      pause(56),
      http("request_1")
        .get("/")
        .headers(headers_1),
      pause(6),
      http("request_2")
        .get("/computers?f=Apple")
        .headers(headers_2),
      pause(5),
      http("request_3")
        .get("/computers?f=IBM")
        .headers(headers_2),
      pause(2),
      http("request_4")
        .get("/computers/310")
        .headers(headers_2),
      pause(2),
      http("request_5")
        .post("/computers/310/delete")
        .headers(headers_0)
    )

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
