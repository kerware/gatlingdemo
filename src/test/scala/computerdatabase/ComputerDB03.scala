package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ComputerDB03 extends Simulation  {

  // Http Protocol
  val httpProtocol = http.baseUrl("http://computer-database.gatling.io").disableCaching

  val home = http("home").get("/")

  val p =  pause( 5 )

  val searchAllComputers = http("Computers").get("/computers")

  val queryField = "Apple"
  val computerToSearch = "Apple Lisa"

  val computersData = csv( "searchComputerData.csv").random

  def searchAComputer() =
    http("Search a computer")
      .get("/computers").queryParam("f", "#{query}")
      .check( substring( "#{computer}" ))
      .check( css("a:contains('#{computer}')" , "href"))

  // Def des 2 scenarios
  val scnAllComputers = scenario( "Get all computers").exec(
    home,
    p,
    searchAllComputers
  )

  val scnSearchAComputers = scenario("Get Apple computers").exec(
    feed( computersData),
    home,
    p,
    searchAComputer()
  )


  // Injection de charge dans le scenario

  setUp(
    scnAllComputers.inject( rampUsers( 10 ) during(5)),
    scnSearchAComputers.inject( rampUsers(20 ) during(5))
  ).protocols( httpProtocol ).assertions(
      global.responseTime.max.lt( 500) ,
      global.successfulRequests.percent.gt( 95 )
    )

}
