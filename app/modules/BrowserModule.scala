package modules

import com.google.inject.AbstractModule
import modules.BrowserModule.TestingBrowser
import play.api.Configuration
import play.api.Environment

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class BrowserModule(environment: Environment, configuration: Configuration)
    extends AbstractModule {
  override def configure(): Unit = {
    val timeout = configuration.getMillis("browser.timeout").millis
    bind(classOf[TestingBrowser]).toInstance(TestingBrowser(timeout = timeout))
  }
}

object BrowserModule {
  case class TestingBrowser(timeout: FiniteDuration)
}
