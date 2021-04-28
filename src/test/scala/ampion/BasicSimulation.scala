package ampion

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BasicSimulation extends Simulation {

	val httpProtocol = http
		.baseUrl("https://www.ampion.com.au")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.inferHtmlResources()
		.acceptHeader("image/webp,*/*")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.doNotTrackHeader("1")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
		"Accept-Encoding" -> "gzip, deflate, br",
		"Upgrade-Insecure-Requests" -> "1")
			
			
		// Contact Page HTTP Request
		object HomePage {
			val homePage = group("Home Page"){
			exec(http("HomePage")
				.get("/")
				.headers(headers_0)
				)
			}		
		// pause sets the think time in-between each request. The think time is randomized between the two declared integers to prevent harmonic regularity of the test steps
		.pause(3,12)
		}
		
		// Contact Page HTTP Request
		object ContactPage {
			val contactPage = group("Contact Page"){
			exec(http("ContactPage")
				.get("/contact")
				.headers(headers_0)
				)
			}
		.pause(3,12)
		}
		
	// Careers Page HTTP Request
		object CareersPage {
			val careersPage = group("Careers Page"){
			exec(http("CareersPage")
				.get("/careers")
				.headers(headers_0)
				)
			}
		.pause(3,12)
		}
		
	// About Page HTTP Request
		object AboutPage {
			val aboutPage = group("About Page"){
			exec(http("AboutPage")
				.get("/about")
				.headers(headers_0)
				)
			}
		.pause(3,12)
		}	

	// Services Page HTTP Request
		object ServicesPage {
			val servicesPage = group("Services Page"){
			exec(http("ServicesPage")
				.get("/services")
				.headers(headers_0)
				)
			}
		.pause(3,12)
		}	
		
		// Partners Page HTTP Request
		object PartnersPage {
			val partnersPage = group("Partners Page"){
			exec(http("PartnersPage")
				.get("/partners")
				.headers(headers_0)
				)
			}
		// Pause in the final request is longer to control pacing between iterations of the scenario	
		.pause(60,180)
		}
		
	// Parameters passed in from Jenkins. These control virtual user concurrency, ramp-up period, test duration and failure tolerance
	val pConcurrency = Integer.getInteger("users", 1).toInt
	val pRampUp = Integer.getInteger("rampup", 1).toInt
	val pDuration = Integer.getInteger("duration", 1).toInt
	val pFailureTolerance = Integer.getInteger("failure_tolerance", 1).toInt
	
	//2 Scenarios.  Chains together the HTTP request objects that represent the transaction steps. Includes duration per scenario
	val SiteBrowse1 = scenario("Ampion Browse Type 1").during(pDuration minutes){exec(HomePage.homePage, ContactPage.contactPage, CareersPage.careersPage)}
	val SiteBrowse2 = scenario("Ampion Browse Type 2").during(pDuration minutes){exec(AboutPage.aboutPage, ServicesPage.servicesPage, PartnersPage.partnersPage)}
	
	//The test which calls the multiple scenarios
	setUp(SiteBrowse2.inject(rampUsers(pConcurrency) during(pRampUp minutes)), SiteBrowse1.inject(rampUsers(pConcurrency)during(pRampUp minutes))).assertions(forAll.failedRequests.percent.lte(pFailureTolerance)).protocols(httpProtocol)

}
