package computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class MultiUsersSimulation extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io")
    .inferHtmlResources(AllowList(), DenyList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*\.svg""", """.*detectportal\.firefox\.com.*"""))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0")
  
  private val headers_0 = Map("Priority" -> "u=0, i")
  
  private val headers_4 = Map(
  		"Origin" -> "http://computer-database.gatling.io",
  		"Priority" -> "u=0, i"
  )


  private val scn = scenario("MultiUsersSimulation")
    .exec(
      http("request_0")
        .get("/")
        .headers(headers_0),
      pause(6),
      http("request_1")
        .get("/computers?f=Apple")
        .headers(headers_0),
      pause(4),
      http("request_2")
        .get("/computers?f=IBM")
        .headers(headers_0),
      pause(3),
      http("request_3")
        .get("/computers/310")
        .headers(headers_0),
      pause(2),
      http("request_4")
        .post("/computers/310/delete")
        .headers(headers_4)
    )

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
