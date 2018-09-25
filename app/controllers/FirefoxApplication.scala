package controllers

import javax.inject.Inject
import models.PushSubscription
import modules.BrowserModule.TestingBrowser
import org.openqa.selenium.By
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.remote.DesiredCapabilities
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

import scala.concurrent.duration.FiniteDuration
import scala.util.Failure
import scala.util.Success
import scala.util.Try

class FirefoxApplication @Inject()(browser: TestingBrowser,
                                   cc: ControllerComponents)
    extends AbstractController(cc) {
  private[this] def withDriver[A](f: FirefoxDriver => A): A = {
    val options = new FirefoxOptions()
    options.addPreference("permissions.default.desktop-notification", 1)
    val driver = new FirefoxDriver(options)
    try {
      f(driver)
    } finally {
      driver.quit()
    }
  }

  private[this] def ready(driver: FirefoxDriver,
                          timeout: FiniteDuration,
                          n: Int,
                          maxTrial: Int,
                          expected: Boolean): Option[PushSubscription] = {
    if (n >= maxTrial) return None

    Thread.sleep(timeout.toMillis / maxTrial)

    val endpoint = driver.findElement(By.id("endpoint")).getAttribute("value")
    val auth = driver.findElement(By.id("auth")).getAttribute("value")
    val p256dh = driver.findElement(By.id("p256dh")).getAttribute("value")

    if (endpoint.isEmpty || auth.isEmpty || p256dh.isEmpty)
      return ready(driver, timeout, n + 1, maxTrial, expected)

    Some(PushSubscription(endpoint = endpoint, auth = auth, p256dh = p256dh))
  }

  private[this] def browserSubscribe(
      url: String,
      timeout: FiniteDuration): Try[PushSubscription] = {
    withDriver { driver =>
      driver.get(url)

      val b = driver.findElement(By.id("subscribe"))
      b.click()

      ready(driver, timeout, n = 0, maxTrial = 10, expected = true) match {
        case Some(v) => Success(v)
        case None =>
          Failure(new RuntimeException(s"operation timed out $timeout"))
      }
    }
  }

  def subscribe: Action[AnyContent] = Action { req =>
    browserSubscribe(s"http://${req.host}", browser.timeout) match {
      case Success(value) => Ok(Json.toJson(value))
      case Failure(exception) =>
        Logger.error("failed to subscribe", exception)
        InternalServerError(Json.obj("error" -> exception.getMessage))
    }
  }
}
