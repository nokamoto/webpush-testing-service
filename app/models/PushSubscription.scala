package models

import play.api.libs.json.Format
import play.api.libs.json.Json

case class PushSubscription(endpoint: String, auth: String, p256dh: String)

object PushSubscription {
  implicit val format: Format[PushSubscription] = Json.format[PushSubscription]
}
