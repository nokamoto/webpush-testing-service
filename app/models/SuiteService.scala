package models

import java.util.UUID

import models.Driver.Chrome
import models.Driver.Firefox
import models.Suite.SuiteID
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import play.api.Logger
import play.api.libs.json.Json

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.FiniteDuration
import scala.collection.JavaConverters._

class SuiteService(m: TrieMap[SuiteID, SuiteDriver]) {
  private[this] def firefox(): FirefoxDriver = {
    val options = new FirefoxOptions()
    options.addPreference("permissions.default.desktop-notification", 1)
    new FirefoxDriver(options)
  }

  private[this] def chrome(): ChromeDriver = {
    val options = new ChromeOptions()
    options.setExperimentalOption(
      "prefs",
      Map("profile.default_content_setting_values.notifications" -> 1).asJava)
    // https://stackoverflow.com/questions/50642308/org-openqa-selenium-webdriverexception-unknown-error-devtoolsactiveport-file-d
    options.addArguments("--disable-dev-shm-usage")
    options.addArguments("--no-sandbox")
    new ChromeDriver(options)
  }

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

  private[this] def start(url: String,
                          timeout: FiniteDuration,
                          name: Driver,
                          newDriver: () => WebDriver): Suite = {
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

        val suite = SuiteDriver(suite = Suite(id = id,
                                              driver = name.value,
                                              subscription = v,
                                              events = List.empty),
                                driver = driver)
        m.putIfAbsent(id, suite)
          .foreach(duplicated =>
            throw new RuntimeException(s"duplicated suite id: $duplicated"))
        suite.suite

      case None =>
        throw new RuntimeException(s"operation timed out $timeout")
    }
  }

  def start(url: String, timeout: FiniteDuration, driver: Driver): Suite = {
    driver match {
      case Firefox => start(url, timeout, driver, () => firefox())
      case Chrome  => start(url, timeout, driver, () => chrome())
    }
  }

  def quit(id: SuiteID): Unit = {
    Logger.info(s"$id: quit")
    m.remove(id).foreach { suite =>
      suite.driver.quit()
    }
  }

  def get(id: SuiteID): Option[Suite] = m.get(id).map(_.suite)

  def addEvent(id: SuiteID, event: String): Unit = {
    Logger.info(s"$id: add $event")
    val prev =
      m.getOrElse(id, throw new NoSuchElementException(s"suite $id not found"))
    m.update(
      id,
      prev.copy(suite = prev.suite.copy(events = event :: prev.suite.events)))
  }
}
