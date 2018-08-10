/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import jp.t2v.lab.play2.auth.LoginLogout
import play.api.Logger
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc.{Action, AnyContent, Request}
import service.{DeskproService, SessionService}
import views.html.signoutSurvey

import scala.concurrent.Future

trait UserLogoutAccount extends LoggedInController with LoginLogout {

  val deskproService: DeskproService

  def logoutSurvey = loggedInAction { implicit request =>
    val page = signoutSurvey("Give feedback", SignOutSurveyForm.form)

    Future.successful(Ok(page))
  }

  def logoutSurveyAction = loggedInAction { implicit request =>
    SignOutSurveyForm.form.bindFromRequest.value match {
      case Some(form) => deskproService.submitSurvey(form)
      case None => Logger.error(s"Survey form invalid.")
    }

    Future.successful(Redirect(controllers.routes.UserLogoutAccount.logout))
  }

  def logout = Action.async { implicit request: Request[AnyContent]  =>
    gotoLogoutSucceeded {
      for {
        _ <- tokenAccessor.extract(request)
          .map(sessionService.destroy)
          .getOrElse(Future.successful(()))
      } yield Ok(views.html.logoutConfirmation())
    }
  }
}

object UserLogoutAccount extends UserLogoutAccount with WithAppConfig {
  override val sessionService = SessionService
  override val deskproService = DeskproService
}
