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

import connectors.ThirdPartyDeveloperConnector
import play.api.Play.current
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import service._

import scala.concurrent.Future

trait DeleteApplication extends ApplicationController {

  val applicationService: ApplicationService
  val developerConnector: ThirdPartyDeveloperConnector
  val auditService: AuditService

  def deleteApplication(applicationId: String, error: Option[String] = None) = teamMemberOnStandardApp(applicationId) { implicit request =>
    val view = views.html.deleteApplication(request.application, request.role)
    Future(error.map(_ => BadRequest(view)).getOrElse(Ok(view)))
  }

  def deleteApplicationConfirm(applicationId: String, error: Option[String] = None) = adminIfStandardProductionApp(applicationId) { implicit request =>
    val view = views.html.deleteApplicationConfirm(request.application, DeleteApplicationForm.form.fill(DeleteApplicationForm(None)))
    Future(error.map(_ => BadRequest(view)).getOrElse(Ok(view)))
  }

  def deleteApplicationAction(applicationId: String) = adminIfStandardProductionApp(applicationId) { implicit request =>
    val application = request.application

    def handleInvalidForm(formWithErrors: Form[DeleteApplicationForm]) =
      Future(BadRequest(views.html.deleteApplicationConfirm(application, formWithErrors)))

    def handleValidForm(validForm: DeleteApplicationForm) = {
      validForm.deleteConfirm match {
        case Some("Yes") => applicationService.requestApplicationDeletion(request.user, application).map(_ => Ok(views.html.deleteApplicationComplete(application)))
        case _ => Future(Redirect(routes.Details.details(applicationId)))
      }
    }

    DeleteApplicationForm.form.bindFromRequest.fold(handleInvalidForm, handleValidForm)
  }
}

object DeleteApplication extends DeleteApplication with WithAppConfig {
  override val sessionService = SessionService
  override val applicationService = ApplicationServiceImpl
  override val developerConnector = ThirdPartyDeveloperConnector
  override val auditService = AuditService
}
