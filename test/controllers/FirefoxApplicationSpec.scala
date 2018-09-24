package controllers

import models.PushSubscription
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.DefaultAwaitTimeout
import play.api.test.FutureAwaits

class FirefoxApplicationSpec
    extends PlaySpec
    with GuiceOneServerPerTest
    with FutureAwaits
    with DefaultAwaitTimeout {
  "FirefoxApplication#subscribe" in {
    val ws = app.injector.instanceOf[WSClient]
    val res = await(
      ws.url(s"http://localhost:$port/testing/firefox/subscribe.json")
        .post(Json.obj()))

    res.status mustBe 200
    noException should be thrownBy Json.parse(res.body).as[PushSubscription]

    val subscription = Json.parse(res.body).as[PushSubscription]
    subscription.endpoint must not be empty
    subscription.auth must not be empty
    subscription.auth must not be empty
  }
}
