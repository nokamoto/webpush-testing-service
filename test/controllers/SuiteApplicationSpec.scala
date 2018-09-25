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
  private[this] def get(id: String): Suite = {
    val ws = app.injector.instanceOf[WSClient]
    val found = await(ws.url(s"http://localhost:$port/testing/$id").get())

    found.status mustBe 200
    noException should be thrownBy Json.parse(found.body).as[Suite]

    Json.parse(found.body).as[Suite]
  }

  "SuiteApplication all" in {
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

    get(suite.id) mustBe suite

    val data = "test event"
    val event = await(
      ws.url(s"http://localhost:$port/testing/${suite.id}/events").post(data))

    event.status mustBe 200
    get(suite.id).events mustBe data :: Nil

    val quit =
      await(ws.url(s"http://localhost:$port/testing/${suite.id}").delete())

    quit.status mustBe 204

    val removed =
      await(ws.url(s"http://localhost:$port/testing/${suite.id}").get())

    removed.status mustBe 404
  }
}
