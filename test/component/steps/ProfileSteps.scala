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

package component.steps

import component.pages._
import component.stubs.{DeveloperStub, Stubs}
import cucumber.api.scala.{EN, ScalaDsl}
import domain.UpdateProfileRequest
import org.openqa.selenium.WebDriver
import org.scalatest.Matchers

class ProfileSteps extends ScalaDsl with EN with Matchers with NavigationSugar {

  implicit val webDriver: WebDriver = Env.driver

  Given( """^I want to successfully change my profile$""") { () =>
    DeveloperStub.update("john.smith@example.com", UpdateProfileRequest("Joe", "Bloggs", None), 200)
  }

  Given( """^I want to successfully change my password""") { () =>
    Stubs.setupPostRequest("/change-password", 204)
  }
}
