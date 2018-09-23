package modules

import com.google.inject.AbstractModule
import modules.ApplicationServerKeyModule.ApplicationServerKey
import play.api.Configuration
import play.api.Environment

class ApplicationServerKeyModule(environment: Environment,
                                 configuration: Configuration)
    extends AbstractModule {
  override def configure(): Unit = {
    val applicationServerKey = configuration.get[String]("applicationServerKey")
    bind(classOf[ApplicationServerKey])
      .toInstance(ApplicationServerKey(applicationServerKey))
  }
}

object ApplicationServerKeyModule {
  case class ApplicationServerKey(value: String)
}
