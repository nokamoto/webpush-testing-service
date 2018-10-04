package models

import play.api.mvc.PathBindable

sealed abstract class Driver(val value: String)

object Driver {
  case object Firefox extends Driver("firefox")
  case object Chrome extends Driver("chrome")

  private[this] val all: Set[Driver] = Set(Firefox, Chrome)

  implicit val bindable: PathBindable[Driver] = new PathBindable[Driver] {
    override def bind(key: String, value: String): Either[String, Driver] = {
      all
        .find(_.value == value.toLowerCase)
        .map(Right(_))
        .getOrElse(Left(s"undefined driver: $value"))
    }

    override def unbind(key: String, value: Driver): String = value.value
  }
}
