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

package unit.connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import config.WSHttp
import connectors.{PayloadEncryption, ThirdPartyDeveloperConnector}
import domain._
import play.api.http.Status
import uk.gov.hmrc.play.http.metrics.NoopMetrics
import uk.gov.hmrc.http.HeaderCarrier

class ThirdPartyDeveloperConnectorEncryptionSpec extends BaseConnectorSpec {

  trait Setup {
    implicit val hc = HeaderCarrier()

    val underTest = new ThirdPartyDeveloperConnector {
      val http = WSHttp
      val serviceBaseUrl = wireMockUrl
      val payloadEncryption: PayloadEncryption = PayloadEncryption
      val metrics = NoopMetrics
    }
  }

  "register" should {
    "send request with encrypted payload" in new Setup {
      stubFor(post(urlEqualTo("/developer"))
        .willReturn(
          aResponse()
            .withStatus(201)
            .withHeader("Content-Type", "application/json")
        ))
      val result = await(underTest.register(new Registration("first", "last", "email@example.com", "password")))
      verify(1, postRequestedFor(urlMatching("/developer"))
        .withRequestBody(equalTo("""{"data":"Zw/sYw13nx6BrF3uAK8Qssm3sNZrI5pcJIVgTd8CdGY/o0GzlHwXqUVXKmx7QQ1avPtuPt45IYrVojwrPSGL9A13AqF0PAL6obBTOpqpGFzrgSkMl6uIAcdsBeop+gby"}""")))
    }
  }

  "reset-password" should {
    "send request with encrypted payload" in new Setup {
      stubFor(post(urlEqualTo("/reset-password"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
        ))
      val result = await(underTest.reset(new PasswordReset("email@example.com","newPassword")))
      verify(1, postRequestedFor(urlMatching("/reset-password"))
        .withRequestBody(equalTo("""{"data":"k7hutBek3t8KfWDBIKTCQ0l+TimQW7kD9CjSsGHSaAQbeQeXeipY+TaP5PL7E7Td64EpDJeriAHHbAXqKfoG8g=="}""")))
    }
  }

  "change-password" should {
    "send request with encrypted payload" in new Setup {
      stubFor(post(urlEqualTo("/change-password"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
        ))
      val result = await(underTest.changePassword(new ChangePassword("email@example.com","oldPassword","newPassword")))
      verify(1, postRequestedFor(urlMatching("/change-password"))
        .withRequestBody(equalTo("""{"data":"k7hutBek3t8KfWDBIKTCQ55JG4p9+fMdvQ0VkN8bpmd90WmD+EXjWAYekKvTLLYUNsoQzP0MXHS23JgkFZItsxfqZsCc76lJQmpWm/p0te4+5+KbrSWmSBVuTBFkPkrD"}""")))
    }

    "return a locked response when the account is locked" in new Setup {
      stubFor(post(urlEqualTo("/change-password"))
        .willReturn(
          aResponse()
            .withStatus(Status.LOCKED)
        ))

      intercept[LockedAccount] {
        await(underTest.changePassword(ChangePassword("email@email.com", "oldPassword", "newPassword")))
      }
    }

    "return an unverified account response when the account is forbidden" in new Setup {
      stubFor(post(urlEqualTo("/change-password"))
        .willReturn(
          aResponse()
            .withStatus(Status.FORBIDDEN)
        ))

      intercept[UnverifiedAccount] {
        await(underTest.changePassword(ChangePassword("email@email.com", "oldPassword", "newPassword")))
      }
    }

    "return an invalid credentials response when the account is unauthorised" in new Setup {
      stubFor(post(urlEqualTo("/change-password"))
        .willReturn(
          aResponse()
            .withStatus(Status.UNAUTHORIZED)
        ))

      intercept[InvalidCredentials] {
        await(underTest.changePassword(ChangePassword("email@email.com", "oldPassword", "newPassword")))
      }
    }
  }

  "check-password" should {
    "send request with encrypted payload" in new Setup {
      stubFor(post(urlEqualTo("/check-password"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
        ))
      val result = await(underTest.checkPassword(new PasswordCheckRequest("email@example.com","password")))
      verify(1, postRequestedFor(urlMatching("/check-password"))
        .withRequestBody(equalTo("""{"data":"k7hutBek3t8KfWDBIKTCQ9BO8Louz2xXjyXu2ERSQ9l7E5dh1C0hI7iuNNT7kfgfFZOK/NlNGY89EBB/xaaxiA=="}""")))
    }
  }
}

