package models

import java.util.UUID

import models.Suite.SuiteID
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import play.api.Logger
import play.api.libs.json.Json

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.FiniteDuration

abstract class SuiteService(m: TrieMap[SuiteID, SuiteDriver]) {
  protected[this] def newDriver(): WebDriver

  private[this] def ready(driver: WebDriver,
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

  def start(url: String, timeout: FiniteDuration): Suite = {
    val id = UUID.randomUUID().toString
    val driver = newDriver()

    Logger.info(s"$id: start $driver - $url")

    driver.get(url)

    Logger.info(s"$id: click subscription")

    val b = driver.findElement(By.id("subscribe"))
    b.click()

    ready(driver, timeout, n = 0, maxTrial = 10, expected = true) match {
      case Some(v) =>
        Logger.info(s"$id: subscription=${Json.toJson(v)}")

        val suite = SuiteDriver(
          suite = Suite(id = id, subscription = v, logs = List.empty),
          driver = driver)
        m.putIfAbsent(id, suite)
          .foreach(duplicated =>
            throw new RuntimeException(s"duplicated suite id: $duplicated"))
        suite.suite

      case None =>
        throw new RuntimeException(s"operation timed out $timeout")
    }
  }

  def quit(id: SuiteID): Unit = {
    println(s"$id: quit")
    m.remove(id).foreach { suite =>
      suite.driver.quit()
    }
  }
}
