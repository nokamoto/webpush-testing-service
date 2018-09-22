package controllers

import javax.inject.Inject
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

class Application @Inject()(cc: ControllerComponents)
    extends AbstractController(cc) {
  def index: Action[AnyContent] = TODO
}
