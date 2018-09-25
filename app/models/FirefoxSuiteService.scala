package models
import models.Suite.SuiteID
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

import scala.collection.concurrent.TrieMap

class FirefoxSuiteService(m: TrieMap[SuiteID, SuiteDriver])
    extends SuiteService(m) {
  override protected[this] def newDriver(): WebDriver = {
    val options = new FirefoxOptions()
    options.addPreference("permissions.default.desktop-notification", 1)
    new FirefoxDriver(options)
  }
}
