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

package domain

import play.api.libs.json.Json

case class AccountSetupRequest(roles: Option[Seq[String]] = None,
                               rolesOther: Option[String] = None,
                               services: Option[Seq[String]] = None,
                               servicesOther: Option[String] = None,
                               targets: Option[Seq[String]] = None,
                               targetsOther: Option[String] = None)

object AccountSetupRequest {
  implicit val format = Json.format[AccountSetupRequest]
}
