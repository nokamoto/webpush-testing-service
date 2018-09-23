package controllers

import javax.inject.Inject
import modules.ApplicationServerKeyModule.ApplicationServerKey
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

class Application @Inject()(applicationServerKey: ApplicationServerKey,
                            cc: ControllerComponents)
    extends AbstractController(cc) {
  def index: Action[AnyContent] =
    Action(_ => Ok(views.html.index(applicationServerKey.value)))
}
