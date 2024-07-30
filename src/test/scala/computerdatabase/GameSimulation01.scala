package computerdatabase
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class GameSimulation01 extends Simulation {

  val nbUsers = Integer.getInteger("users")
  val rampDuration = Integer.getInteger( "rampduration")

  val feeder = csv( "gameCsvFile.csv").random

  val httpProtocols = http
    .baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")


  val searchAllGames = http("Search All Games")
    .get("/videogame")
    .check(status.is(200))
    .check( jsonPath("$[?(@.name=='#{name}')].id").saveAs("gameId"))

  val searchGameById = http("Search by Id")
    .get("/videogame/#{gameId}")
    .check(status.is(200))
    .check( jsonPath("$.name").is("#{name}"))

  def debug() = exec(
    session => {
      val name = session("name").as[String]
      val gameId = session("gameId").as[String]
      println(s"$name")
      println(s"$gameId")
      session
    }
  )

  val scn = scenario("search by name by id").exec(
    repeat( 10 ) (
      feed(feeder),
      searchAllGames,
      searchGameById,
      debug()
    )
  )

  setUp(
    scn.inject( rampUsers(nbUsers) during( rampDuration ))
  ).protocols( httpProtocols )
}
