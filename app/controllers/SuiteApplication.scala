package controllers

import javax.inject.Inject
import models.Driver
import models.SuiteService
import modules.BrowserModule.TestingBrowser
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.ControllerComponents

import scala.util.control.NonFatal

class SuiteApplication @Inject()(service: SuiteService,
                                 browser: TestingBrowser,
                                 cc: ControllerComponents)
    extends AbstractController(cc) {
  def start(driver: Driver) = Action { req =>
    try {
      val url = s"http://${req.host}"
      val suite = service.start(url, browser.timeout, driver)
      Created(Json.toJson(suite))
    } catch {
      case NonFatal(e) =>
        Logger.error("failed to start", e)
        InternalServerError(Json.obj("error" -> e.getMessage))
    }
  }

  def quit(id: String) = Action { _ =>
    try {
      service.quit(id)
      new Status(NO_CONTENT)(Json.obj())
    } catch {
      case NonFatal(e) =>
        Logger.error(s"failed to quit: $id", e)
        InternalServerError(Json.obj("error" -> e.getMessage))
    }
  }

  def get(id: String) = Action { _ =>
    try {
      service.get(id) match {
        case Some(v) => Ok(Json.toJson(v))
        case None    => NotFound(Json.obj("error" -> s"$id not found"))
      }
    } catch {
      case NonFatal(e) =>
        Logger.error(s"failed to get: $id", e)
        InternalServerError(Json.obj("error" -> e.getMessage))
    }
  }

  def addEvent(id: String): Action[String] = Action(parse.text) { req =>
    try {
      service.addEvent(id, req.body)
      Ok(Json.obj())
    } catch {
      case e: NoSuchElementException =>
        NotFound(Json.obj("error" -> e.getMessage))
      case NonFatal(e) =>
        Logger.error(s"failed to add event: $id ${req.body}", e)
        InternalServerError(Json.obj("error" -> e.getMessage))
    }
  }
}
