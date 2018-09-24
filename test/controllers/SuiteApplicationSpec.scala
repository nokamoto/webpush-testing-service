package controllers

import models.Suite
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.DefaultAwaitTimeout
import play.api.test.FutureAwaits

class SuiteApplicationSpec
    extends PlaySpec
    with GuiceOneServerPerTest
    with FutureAwaits
    with DefaultAwaitTimeout {
  "SuiteApplication start and quit" in {
    val ws = app.injector.instanceOf[WSClient]
    val start = await(
      ws.url(s"http://localhost:$port/testing")
        .post(Json.obj()))

    start.status mustBe 201
    noException should be thrownBy Json.parse(start.body).as[Suite]

    val suite = Json.parse(start.body).as[Suite]
    suite.id must not be empty
    suite.subscription.endpoint must not be empty
    suite.subscription.auth must not be empty
    suite.subscription.auth must not be empty

    val quit =
      await(ws.url(s"http://localhost:$port/testing/${suite.id}").delete())

    quit.status mustBe 204
  }
}
