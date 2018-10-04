package modules

import com.google.inject.AbstractModule
import models.SuiteService
import modules.BrowserModule.TestingBrowser
import play.api.Configuration
import play.api.Environment

import scala.collection.concurrent.TrieMap
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class BrowserModule(environment: Environment, configuration: Configuration)
    extends AbstractModule {
  override def configure(): Unit = {
    val timeout = configuration.getMillis("browser.timeout").millis
    bind(classOf[TestingBrowser]).toInstance(TestingBrowser(timeout = timeout))
    bind(classOf[SuiteService]).toInstance(new SuiteService(TrieMap.empty))
  }
}

object BrowserModule {
  case class TestingBrowser(timeout: FiniteDuration)
}
