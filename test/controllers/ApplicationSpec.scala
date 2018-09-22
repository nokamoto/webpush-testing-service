package controllers

import org.scalatest.FlatSpec
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ApplicationSpec extends FlatSpec {
  "Application#index" should "return not implemented yet" in {
    val sut = new Application(stubControllerComponents())
    val res = sut.index(FakeRequest())

    assert(status(res) === NOT_IMPLEMENTED)
  }
}
