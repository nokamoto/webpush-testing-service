package models

import models.Suite.SuiteID
import play.api.libs.json.Json
import play.api.libs.json.OFormat

case class Suite(id: SuiteID,
                 driver: String,
                 subscription: PushSubscription,
                 events: List[String])

object Suite {
  type SuiteID = String

  implicit val format: OFormat[Suite] = Json.format[Suite]
}
