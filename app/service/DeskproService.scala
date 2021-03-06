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

package service

import config.ApplicationConfig
import connectors.DeskproConnector
import controllers.{SignOutSurveyForm, SupportEnquiryForm}
import domain.{DeskproTicket, Feedback, TicketId, TicketResult}
import play.api.mvc.Request

import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

trait DeskproService {

  val applicationConfig: ApplicationConfig
  val deskproConnector: DeskproConnector

  def submitSurvey(survey: SignOutSurveyForm)(implicit request: Request[AnyRef], hc: HeaderCarrier): Future[TicketId] = {
    val feedback = Feedback.createFromSurvey(survey, Option(applicationConfig.title))
    deskproConnector.createFeedback(feedback)
  }

  def submitSupportEnquiry(supportEnquiry: SupportEnquiryForm)(implicit request: Request[AnyRef], hc: HeaderCarrier): Future[TicketResult] = {
    val ticket = DeskproTicket.createFromSupportEnquiry(supportEnquiry, applicationConfig.title)
    deskproConnector.createTicket(ticket)
  }
}

object DeskproService extends DeskproService{
  val deskproConnector = DeskproConnector
  val applicationConfig = ApplicationConfig
}
