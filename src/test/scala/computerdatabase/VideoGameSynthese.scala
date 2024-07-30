package computerdatabase


import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Random


class VideoGameSynthese extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // Custom Feeder
  var idNumbers = (1 to 10).iterator
  val rnd = new Random()
  val now = LocalDate.now()
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  def getRandomDate(startDate: LocalDate, random: Random): String = {
    startDate.minusDays(random.nextInt(30)).format(pattern)
  }

  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString(5)),
    "releaseDate" -> getRandomDate(now, rnd),
    "reviewScore" -> rnd.nextInt(100),
    "category" -> ("Category-" + randomString(6)),
    "rating" -> ("Rating-" + randomString(4))
  ))


  // ** VARIABLES FOR FEEDERS ** /
  // runtime variables
  val USERCOUNT = Integer.getInteger("users")
  val RAMPDURATION = Integer.getInteger("rampduration")
  val TESTDURATION = Integer.getInteger("testduration")

  val csvFeeder = csv("gameCsvFile.csv").random

  before{
    println(s"Running test with ${USERCOUNT} users")
    println(s"Ramping users over ${RAMPDURATION} seconds")
    println(s"Total test duration: ${TESTDURATION} seconds")
}

  /*** HTTP CALLS ***/
  def getAllVideoGames() =
    exec(
      http("Get all video games")
        .get("/videogame")
        .check(status.is(200))
    )

  def authenticate() =
    exec(http("Authenticate")
      .post("/authenticate")
      .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
      .check(jsonPath("$.token").saveAs("jwtToken")))

  def createNewGame() =
    feed(csvFeeder)
      .exec(http("Create New Game - #{name}")
        .post("/videogame")
        .header("authorization", "Bearer #{jwtToken}")
        .body(ElFileBody("newGameTemplate.json")).asJson)

  def getSingleGame() =
    exec(http("Get single game - #{name}")
      .get("/videogame/#{gameId}")
      .check(jsonPath("$.name").is("#{name}")))

  def deleteGame() =
    exec(http("Delete game - #{name}")
      .delete("/videogame/#{gameId}")
      .header("authorization", "Bearer #{jwtToken}")
      .check(bodyString.is("Video game deleted")))


  /** SCENARIO DESIGN */
  val scn = scenario("VideoGame Stress test")
    .forever(
      exec(authenticate())
        .pause(2)
        .exec(createNewGame())
        .pause(2)
        .exec(getSingleGame())
        .pause(2)
        .exec(deleteGame())
    )


  setUp(
    scn.inject(
      nothingFor(5),
      rampUsers(USERCOUNT).during(RAMPDURATION)
    ).protocols(httpProtocol)
  ).maxDuration(TESTDURATION)
    .normalPausesWithStdDevDuration(1)

  after {
    println("Stress test termine !")
  }

}
