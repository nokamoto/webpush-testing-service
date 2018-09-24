package controllers

import modules.ApplicationServerKeyModule.ApplicationServerKey
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatest.FlatSpec
import org.scalatest.selenium.WebBrowser
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ApplicationSpec extends FlatSpec {
  val applicationServerKey = ApplicationServerKey(
    "BNuvjW90TpDawYyxhvK79QVyNEplaSQZOWo1CwXDmWwfya6qnyBvIx3tFvKEBetExvil4rNNRL0/ZR2WLjGEAbQ=")

  "Application#index" should "return return OK" in {
    val sut = new Application(applicationServerKey, stubControllerComponents())
    val res = sut.index(FakeRequest())

    assert(status(res) === OK)
  }
}

class test extends FlatSpec with WebBrowser {
  implicit val webDriver: WebDriver = new HtmlUnitDriver

}
