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
import domain._
import play.api.Play.current
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import service._
import views.html._

import scala.concurrent.Future

trait ManageApplications extends ApplicationController {

  val applicationService: ApplicationService
  val developerConnector: ThirdPartyDeveloperConnector
  val auditService: AuditService

  val detailsTab = "details"
  val credentialsTab = "credentials"
  val subscriptionsTab = "subscriptions"

  val rolesTab = "roles"

  def manageApps = loggedInAction { implicit request =>
    applicationService.fetchByTeamMemberEmail(loggedIn.email) map { apps =>
      Ok(views.html.manageApplications(apps.map(ApplicationSummary.from(_, loggedIn.email))))
    }
  }

  def addApplication() = loggedInAction { implicit request =>
    Future.successful(Ok(views.html.addApplication(AddApplicationForm.form)))
  }

  def addApplicationAction() = loggedInAction { implicit request =>
    val requestForm = AddApplicationForm.form.bindFromRequest

    def addApplicationWithFormErrors(errors: Form[AddApplicationForm]) =
      Future.successful(BadRequest(views.html.addApplication(errors)))

    def addApplicationWithValidForm(validForm: AddApplicationForm) = {

      applicationService.createForUser(CreateApplicationRequest.from(loggedIn, validForm))
        .map(appCreated => Created(addApplicationSuccess(validForm.applicationName, appCreated.id, validForm.environment.getOrElse(Environment.SANDBOX.toString))))
    }
    requestForm.fold(addApplicationWithFormErrors, addApplicationWithValidForm)
  }

  def editApplication(applicationId: String, error: Option[String] = None) = teamMemberOnApp(applicationId) { implicit request =>
    Future.successful(Redirect(routes.Details.details(applicationId)))
  }
}

object ManageApplications extends ManageApplications with WithAppConfig {
  override val sessionService = SessionService
  override val applicationService = ApplicationServiceImpl
  override val developerConnector = ThirdPartyDeveloperConnector
  override val auditService = AuditService
}
